package engine.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import engine.GameEngine;
import engine.KeyboardHelper;
import engine.MouseHelper;
import engine.event.EventKeyboard;
import engine.event.EventMouse;
import engine.network.packet.ConnectPacket;
import engine.network.packet.KeyboardPacket;
import engine.network.packet.MousePacket;
import engine.network.packet.Packet;
import engine.network.packet.Packet.PacketType;
import engine.network.packet.UpdateObjectPacket;
import engine.network.packet.UpdateObjectPacket.UpdateType;

public class ServerClient implements Runnable {

	public String name;
	public boolean initialized = false;
	
	public Server server;
	public Socket socket;
	public DataOutputStream dataOutput;
	public DataInputStream dataInput;
	public int clientId;
	
	public KeyboardHelper keyboard = new KeyboardHelper();
	public MouseHelper mouse = new MouseHelper();
	public long playerGUID = 0;
	
	private ArrayList<Packet> outgoing;
	
	public ServerClient(Server server, Socket sock, int clientId) throws IOException {
		socket = sock;
		dataOutput = new DataOutputStream(sock.getOutputStream());
		dataInput = new DataInputStream(sock.getInputStream());
		this.clientId = clientId;
		this.server = server;
		outgoing = new ArrayList<Packet>();
	}
	
	@Override
	public void run() {
		try {
			Thread t = new Thread(new Runnable() { //Start the writing thread, this will be the reading one
				@Override
				public void run() {
					while (true) {
						synchronized(outgoing) {
							if (name != null && outgoing.size() > 0) {
								for (Packet p : outgoing) {
									try {
										p.writePacket(dataOutput);
										//if (p.type == PacketType.INIT_OBJECTS) System.out.println("Wrote init");
									} catch (IOException e) {
										if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
											onDisconnect();
										} else {
											e.printStackTrace();
										}
									}
								}
								outgoing.clear();
							}
							try {
								outgoing.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			t.setName("Server: Client "+clientId+" write");
			t.start();
			while (socket.isConnected()) {
				byte packId = dataInput.readByte();
				Packet p = Packet.getPacketFromId(packId);
				p.onPacketRead(dataInput);
				if (p.type == PacketType.CONNECT) {
					name = ((ConnectPacket)p).name;
					server.consoleMessage("Client: "+name+" connected!");
					synchronized(outgoing) {outgoing.notify();}
				} else if (p.type == PacketType.KEYBOARD) {
					KeyboardPacket kb = (KeyboardPacket)p;
					GameEngine.getEngine().raiseEvent(new EventKeyboard(this, kb.released, kb.code));
				} else if (p.type == PacketType.MOUSE) {
					MousePacket mp = (MousePacket)p;
					GameEngine.getEngine().raiseEvent(new EventMouse(this, mp.x, mp.y));
				}
			}
		} catch (IOException e) {
			if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
				onDisconnect();
			} else {
				e.printStackTrace();
			}
		}
	}
	
	public void onDisconnect() {
		if (server.clients.contains(this)) {
			System.out.println("Client: "+name+" disconnected!");
			server.consoleMessage("Client: "+name+" disconnected!");
			server.disconnectClient(this);
		}
	}
		
	public void sendPacket(Packet p) {
		synchronized(outgoing) {
			boolean canSend = true;
			if (p.type == PacketType.INIT_OBJECTS) {
				initialized = true;
			} else if (p.type == PacketType.UPDATE_OBJECT) {
				UpdateObjectPacket objup = (UpdateObjectPacket) p;
				if (objup.type == UpdateType.SYNC_FIELD && !initialized) {
					canSend = false; //Don't send Updates until the basics have arrived
				}
			}
			
			if (canSend) { 
				outgoing.add(p);
				//if (p.type == PacketType.INIT_OBJECTS) System.out.println("Queued init");
				outgoing.notify();
			}
		}
	}
}
