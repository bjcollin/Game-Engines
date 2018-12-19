package engine.replay;

import java.util.ArrayList;
import java.util.HashMap;

import engine.GameEngine;
import engine.KeyboardHelper;
import engine.event.Event;
import engine.event.EventHandler;
import engine.event.EventKeyboard;
import engine.event.EventReplayEnd;
import engine.event.EventReplayStart;
import engine.network.ServerClient;
import engine.object.GameObject;
import homework.GameRectangle;

public class ReplayManager {
	
	public enum ReplayState {
		NEW,
		RECORDING,
		READY,
		PLAYBACK,
		PAUSED
	}
	//Used while Replay is playing
	public ArrayList<GameObject> preState;
	
	public ReplayState state = ReplayState.NEW;
	public long start = -1;
	public Replay active;
	
	public HashMap<Long, KeyboardHelper> keyboards;

	
	public ReplayManager() {
		GameEngine.getEngine().eventManager.registerExtraHandler(this);
	}
	
	public void startRecording() {
		active = new Replay();
		active.initial = new ArrayList<GameObject>();
		active.clientToGUID = new HashMap<Integer, Long>();
		synchronized(GameEngine.getEngine().objects) {
			for (GameObject obj : GameEngine.getEngine().objects) {
				GameObject newobj = obj.clone();
				if (newobj == null) {
					throw new IllegalStateException("GameObject was not cloned properly! GameObject: "+obj.getClass());
				}
				if (obj instanceof GameRectangle && ((GameRectangle)obj).controllable) {
					for (ServerClient sc : GameEngine.getEngine().server.clients) {
						if (sc.playerGUID == obj.getGUID()) {
							active.clientToGUID.put(sc.clientId, sc.playerGUID);
						}
					}
				}
				active.initial.add(obj.clone());
			}
		}
		
		
		active.events = new ArrayList<Event>();
		start = GameEngine.getEngine().globalTime.tick;
		state = ReplayState.RECORDING;
	}
	
	public void stopRecording() {
		EventReplayEnd endevt = new EventReplayEnd();
		endevt.timestep -= start;
		active.events.add(endevt);
		state = ReplayState.READY;
	}
	
	@EventHandler
	public void onEvent(Event e) {
		if (state == ReplayState.RECORDING) {
			if (isReplayKeyEvent(e)) {
				//System.out.println("Discarded Replay key event");
			} else {
				Event evt = e.clone();
				evt.timestep -= start;
				active.events.add(evt);
			}
		}
	}
	
	private boolean isReplayKeyEvent(Event e) {
		if (e instanceof EventKeyboard) {
			EventKeyboard evt = (EventKeyboard)e;
			if (evt.code == 82 || evt.code == 84 || evt.code == 90) return true;
		}
		return false;
	}
	
	@EventHandler
	public void onReplayEnd(EventReplayEnd evt) {
		synchronized(GameEngine.getEngine().objects) {
			GameEngine.getEngine().objects.clear();
			for (GameObject obj : preState) {
				GameEngine.getEngine().objects.add(obj);
				obj.hasChanged = true;
			}
			preState = null;
		}
		GameEngine.getEngine().globalTime.scale = 1;
		state = ReplayState.READY;
	}
	
	@EventHandler
	public void play(EventReplayStart evt) {
		preState = new ArrayList<GameObject>();
		keyboards = new HashMap<Long, KeyboardHelper>();
		
		synchronized(GameEngine.getEngine().objects) {
			for (GameObject obj : GameEngine.getEngine().objects) {
				preState.add(obj);
			}
			GameEngine.getEngine().objects.clear();
			for (GameObject obj : active.initial) {
				GameObject newobj = obj.clone();
				GameEngine.getEngine().objects.add(newobj);
				if (obj instanceof GameRectangle && ((GameRectangle)obj).controllable) {
					keyboards.put(obj.getGUID(), new KeyboardHelper());
					newobj.hasChanged = true;
				}
			}
		}
		for (Event e : active.events) {
			Event e2 = e.clone();
			e2.timestep += GameEngine.getEngine().globalTime.tick + 1;
			GameEngine.getEngine().eventManager.raiseEvent(e2);
		}
		state = ReplayState.PLAYBACK;
	}
	
	public static boolean isSpeedInput(Event e) {
		if (e instanceof EventKeyboard) {
			EventKeyboard evt = (EventKeyboard)e;
			if (evt.code == 51 || evt.code == 50 || evt.code == 49 || evt.code == 48) return true;
		}
		return false;
	}
	
	@EventHandler
	public void onKeyEvent(EventKeyboard evt) {
		if (!evt.release || state != ReplayState.PLAYBACK) return;
		if (evt.code == 51) { //3
			GameEngine.getEngine().globalTime.scale = 2;
			GameEngine.getEngine().globalTime.paused = false;
		} else if (evt.code == 50) { // 2
			GameEngine.getEngine().globalTime.scale = 1;
			GameEngine.getEngine().globalTime.paused = false;
		} else if (evt.code == 49) { // 1
			GameEngine.getEngine().globalTime.scale = 0.5;
			GameEngine.getEngine().globalTime.paused = false;
		} else if (evt.code == 48) { //0
			GameEngine.getEngine().globalTime.paused = true;
		}
	}
}
