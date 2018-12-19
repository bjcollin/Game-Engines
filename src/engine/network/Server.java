package engine.network;

import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;

import engine.GameEngine;
import engine.network.packet.InitObjectsPacket;
import engine.network.packet.Packet;
import engine.network.packet.UpdateObjectPacket;
import engine.network.packet.UpdateObjectPacket.UpdateType;
import engine.object.GameObject;
import engine.object.MessageDisplayer;

public class Server implements Runnable {
	
	public MessageDisplayer console;
	
	public Server() {}
	public Server(MessageDisplayer con) {
		this.console = con;
	}

	private ServerSocket socket;
	public CopyOnWriteArrayList<ServerClient> clients = new CopyOnWriteArrayList<ServerClient>();
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(25565);
			while (true) {
				ServerClient newClient = new ServerClient(this, socket.accept(), clients.size());
				clients.add(newClient);
				
				
				GameObject play = GameEngine.getEngine().screen.onClientConnect(newClient);
				if (play != null) {
					newClient.playerGUID = play.getGUID();
					GameEngine.getEngine().addObject(play);
					UpdateObjectPacket p = new UpdateObjectPacket(play, UpdateType.CREATE);
					newClient.sendPacket(p);
				}
				InitObjectsPacket initpack = new InitObjectsPacket();
				newClient.sendPacket(initpack);
				
				
				Thread t = new Thread(newClient);
				t.setName("Server: Client "+newClient.clientId+" read");
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendPacketToAll(Packet p) {
		for (ServerClient sc : clients) {
			sc.sendPacket(p);
		}
	}
	
	public void disconnectClient(ServerClient sc) {
		try {
			sc.dataInput.close();
		} catch (Exception e) {}
		try {
			sc.dataOutput.close();
		} catch (Exception e) {}
		try {
			sc.socket.close();
		} catch (Exception e) {}
		
		if (clients.contains(sc)) {
			clients.remove(sc);
		}
		GameEngine.getEngine().removeObject(sc.playerGUID);
	}
	
	public void consoleMessage(String msg) {
		if (console != null) {
			console.addMessage(msg);
		}
	}
	
	public ServerClient getClientById(int id) {
		synchronized(clients) {
			for (ServerClient sc : clients) {
				if (sc.clientId == id) return sc;
			}
		}
		return null;
	}
}
