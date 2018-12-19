package homework2.alternate_network;

import java.awt.Color;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import engine.GameEngine;
import engine.network.Server;
import engine.network.packet.Packet;
import engine.network.packet.Packet.PacketType;
import engine.network.packet.UpdateObjectPacket;
import engine.object.MessageDisplayer;
import homework.GameRectangle;

public class ServerAlt extends Server implements Runnable {
	
	public MessageDisplayer console;
	
	public ServerAlt() {}
	public ServerAlt(MessageDisplayer con) {
		this.console = con;
	}

	private ServerSocket socket;
	public CopyOnWriteArrayList<ServerClientAlt> clients = new CopyOnWriteArrayList<ServerClientAlt>();
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(25565);
			while (true) {
				ServerClientAlt newClient = new ServerClientAlt(this, socket.accept(), clients.size());
				clients.add(newClient);
				
				Random rng = new Random();
				Color c = new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
				GameRectangle player = new GameRectangle(1, 1, 100, 100, c);
				player.controllable = true;
				newClient.playerGUID = player.getGUID();
				GameEngine.getEngine().setPosToSpawn(player, 0);
				GameEngine.getEngine().addObject(player);
				InitObjectsPacketAlt initpack = new InitObjectsPacketAlt();
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
		if (p.type == PacketType.UPDATE_OBJECT) {
			sendPacketToAll(new UpdateObjectPacketAlt((UpdateObjectPacket)p));
		} else 
		System.out.println("Default packet rejected: "+p);
	}
	
	public void sendPacketToAll(PacketAlt p) {
		for (ServerClientAlt sc : clients) {
			sc.sendPacket(p);
		}
	}
	
	public void disconnectClient(ServerClientAlt sc) {
		try {
			sc.dataInput.close();
		} catch (Exception e) {}
		try {
			sc.dataOutput.close();
		} catch (Exception e) {}
		try {
			sc.socket.close();
		} catch (Exception e) {}
		clients.remove(sc);
		
		GameEngine.getEngine().removeObject(sc.playerGUID);
		
	}
	
	public void consoleMessage(String msg) {
		if (console != null) {
			console.addMessage(msg);
		}
	}
}
