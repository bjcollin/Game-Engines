package engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JOptionPane;

import engine.component.CollisionData;
import engine.event.Event;
import engine.event.EventHandler;
import engine.event.EventKeyboard;
import engine.event.EventManager;
import engine.event.EventMouse;
import engine.event.EventReplayStart;
import engine.network.Client;
import engine.network.Server;
import engine.network.ServerClient;
import engine.network.packet.KeyboardPacket;
import engine.network.packet.MousePacket;
import engine.network.packet.UpdateObjectPacket;
import engine.network.packet.UpdateObjectPacket.UpdateType;
import engine.object.GameObject;
import engine.object.GameSpawn;
import engine.object.MessageDisplayer;
import engine.replay.ReplayManager;
import engine.replay.ReplayManager.ReplayState;
import engine.script.ScriptManager;

public class GameEngine {

	public Game screen;
	public static GameEngine instance;
	
	public Server server;
	public Client client;
	
	public ArrayList<GameObject> objects;
	public ArrayList<GameObject> toAdd;
	public ArrayList<GameSpawn> spawnPoints;
	public Timeline globalTime;
	
	public EventManager eventManager;
	public ReplayManager replayManager;
	public ScriptManager scriptManager;
	
	public int loops = -1;
	public long start = 0;
	
	public GameEngine(Game g) {
		screen = g;
		instance = this;
		objects = new ArrayList<GameObject>();
		toAdd = new ArrayList<GameObject>();
		spawnPoints = new ArrayList<GameSpawn>();
		globalTime = new Timeline(null);
		globalTime.scale = 1;
		globalTime.paused = false;
		eventManager = new EventManager();
		eventManager.registerExtraHandler(this);
		eventManager.registerExtraHandler(screen);
		replayManager = new ReplayManager();
		scriptManager = new ScriptManager();
		
		/*for (int i=0; i<1000; i++) { //Used for event system testing
			EventKeyboard e = new EventKeyboard(null, true, 76);
			e.timestep = globalTime.tick + i;
			eventManager.raiseEvent(e);
		}*/
	}
	
	public static GameEngine getEngine() {
		return instance;
	}
	
	public void startEngine() {
		Thread mainGame = new Thread(new Runnable() {
			@Override
			public void run() {
				//long expectedNext = globalTime.tick;
				while(true) {
					globalTime.update();
					//if (globalTime.tick >= expectedNext) {
						//long nextTime = globalTime.tick + (long)(10/globalTime.scale);
						onTick();
						if (loops > -1) loops++;
						//expectedNext = nextTime;
						try {
							/*long timeout = nextTime - globalTime.tick;
							if (timeout <= 0) timeout = 1;
							if (timeout > 0) Thread.sleep(timeout);*/
							Thread.sleep((long)(10/globalTime.scale));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					//}
					if (loops == 1000) {
						System.out.println("TIME: "+(System.currentTimeMillis() - start)+" ms");
						loops = -1;
					}
				}
			}
		});
		mainGame.setName("Main Game Loop");
		mainGame.start();
	}
	
	public void onTick() {
		if (!globalTime.paused) {
			synchronized(objects) {
				for (GameObject g : objects) {
					if (server != null) {
						CollisionHelper.preTick(g, screen);
						AnnotationHelper.onScriptTick(g, screen);
						AnnotationHelper.onTick(g, screen);
						CollisionHelper.postTick(g, screen);
					}
					if (server != null && g.hasChanged) {
						UpdateObjectPacket p = new UpdateObjectPacket(g, UpdateType.SYNC_FIELD);
						server.sendPacketToAll(p);
						g.hasChanged = false;
					}
				}
				Iterator<GameObject> iter = objects.iterator();
				while(iter.hasNext()) {
					GameObject obj = iter.next();
					if (obj.markForDelete) {
						iter.remove();
						if (server != null) {
							UpdateObjectPacket p = new UpdateObjectPacket(obj, UpdateType.DESTROY);
							server.sendPacketToAll(p);
						}
					}
				}
				
				synchronized(toAdd) {
					for (GameObject g : toAdd) {
						objects.add(g);
						if (server != null) {
							UpdateObjectPacket p = new UpdateObjectPacket(g, UpdateType.CREATE);
							server.sendPacketToAll(p);
						}
					}
					toAdd.clear();
				}
			}
		}
		eventManager.handleEvents(this);
	}
	
	public void onDrawStep() {
		synchronized(objects) {
			for (GameObject g : objects) {
				AnnotationHelper.onDraw(g, screen);
			}
		}
	}
	
	public void addObject(GameObject g) {
		addObject(g, true);
	}
	
	public void addObject(GameObject g, boolean sendToClients) {
		synchronized(toAdd) {toAdd.add(g);}
	}
	
	public void removeObject(GameObject g) {
		/*synchronized(objects) {
			objects.remove(g);
			if (server != null) {
				UpdateObjectPacket p = new UpdateObjectPacket(g, UpdateType.DESTROY);
				server.sendPacketToAll(p);
			}
		}*/
		g.markForDelete = true;
	}
	
	public void removeObject(long guid) {
		//removeObject(getObjectFromGUID(guid));
		getObjectFromGUID(guid).markForDelete = true;
	}
	
	public void addSpawnPoint(GameSpawn spawn) {
		spawnPoints.add(spawn);
		addObject(spawn);
	}
	
	public int getRandomSpawn() {
		Random rng = new Random();
		return rng.nextInt(spawnPoints.size());
	}
	
	public void setPosToSpawn(GameObject obj, int index) {
		GameSpawn point = spawnPoints.get(index);
		CollisionData data = AnnotationHelper.getCollisionData(obj);
		data.pos.x = point.pos.x;
		data.pos.y = point.pos.y;
	}
	
	public GameObject getObjectFromGUID(long guid) {
		synchronized(objects) {
			for (GameObject o : objects) {
				if (o.getGUID() == guid) {
					return o;
				}
			}
		}
		return null;
	}
	
	public void startServer() {
		screen.initGameObjects();
		System.out.println("Server Started");
		MessageDisplayer msg = new MessageDisplayer(1, 1, screen);
		msg.addMessage("Server Started");
		addObject(msg);
		server = new Server(msg);
		Thread t = new Thread(server);
		t.setName("Server: Socket accept thread");
		t.start();
	}

	public void startClient() {
		System.out.println("Client Started");
		String name = JOptionPane.showInputDialog(null, "What is your name?", "Player Name", JOptionPane.QUESTION_MESSAGE);
		client = new Client(name);
		Thread t = new Thread(client);
		t.setName("Client: read");
		t.start();
	}
	
	public KeyboardHelper getControllerKeyboard(GameObject obj) {
		if (server != null) {
			if (replayManager.state != ReplayState.PLAYBACK) {
				for (ServerClient sc : server.clients) {
					if (sc.playerGUID == obj.getGUID()) {
						return sc.keyboard;
					}
				}
			} else {
				return replayManager.keyboards.get(obj.getGUID());
			}
		}
		return null;
	}
	
	public MouseHelper getControllerMouse(GameObject obj) {
		if (server != null) {
			for (ServerClient sc : server.clients) {
				if (sc.playerGUID == obj.getGUID()) {
					return sc.mouse;
				}
			}
		} else if (client != null) {
			return client.mouse;
		}
		return null;
	}
	
	public String getControllerName(GameObject obj) {
		if (server != null) {
			for (ServerClient sc : server.clients) {
				if (sc.playerGUID == obj.getGUID()) {
					return sc.name;
				}
			}
		} else if (client != null) {
			return client.name;
		}
		return null;
	}
	
	public void keyPress(int code, boolean release) {
		if (client != null) {
			KeyboardPacket p = new KeyboardPacket(release, code);
			client.sendPacket(p);
		} else if (code == 81 && release) {
			start = System.currentTimeMillis();
			loops = 0;
			System.out.println("Started benchmark");
		}
	}
	
	@EventHandler
	public void onKeyEvent(EventKeyboard evt) {
		if (client != null) {
			KeyboardPacket p = new KeyboardPacket(evt.release, evt.code);
			client.sendPacket(p);
		} else if (server != null) {
			if (evt.code == 82 && evt.release == true && replayManager.state == ReplayState.NEW) {
				replayManager.startRecording();
				System.out.print("Recording...");
			} else if (evt.code == 84 && evt.release == true && replayManager.state == ReplayState.RECORDING) {
				replayManager.stopRecording();
				System.out.println("Done!");
			} else if (evt.code == 90 && evt.release == true && replayManager.state == ReplayState.READY) {
				eventManager.raiseEvent(new EventReplayStart());
				System.out.println("Playing...");
			} else if (evt.code == 81 && evt.release) {
				start = System.currentTimeMillis();
				loops = 0;
				System.out.println("Started benchmark");
			} else if (evt.clientID != -1) {
				KeyboardHelper keyboard;
				if (replayManager.state != ReplayState.PLAYBACK) {
					keyboard = server.getClientById(evt.clientID).keyboard;
				} else {
					keyboard = replayManager.keyboards.get(replayManager.active.clientToGUID.get(evt.clientID));
				}
				if (evt.release) {
					keyboard.onRelease(evt.code);
				} else {
					keyboard.onPressed(evt.code);
				}
			}
		}
	}
	
	@EventHandler
	public void onMouseEvent(EventMouse evt) {
		if (client != null) {
			MousePacket p = new MousePacket(evt.x, evt.y);
			client.sendPacket(p);
			client.mouse.x = evt.x;
			client.mouse.y = evt.y;
		} else if (server != null && evt.clientID != -1) {
			MouseHelper mouse = server.getClientById(evt.clientID).mouse;
			mouse.x = evt.x;
			mouse.y = evt.y;
		}
	}
	
	public void raiseEvent(Event e) {
		eventManager.raiseEvent(e);
	}
}
