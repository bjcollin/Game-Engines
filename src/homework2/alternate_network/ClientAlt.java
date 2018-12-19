package homework2.alternate_network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import engine.GameEngine;
import engine.network.Client;
import engine.network.packet.KeyboardPacket;
import engine.network.packet.Packet;
import engine.network.packet.Packet.PacketType;

public class ClientAlt extends Client implements Runnable {
	
	public String name;
	public static Socket socket;
	
	public ObjectOutputStream dataOutput;
	public ObjectInputStream dataInput;
	
	public ArrayList<PacketAlt> outgoing = new ArrayList<PacketAlt>();
	
	public ClientAlt(String name) {
		super(name);
		this.name = name;
	}
	
	public void sendPacket(Packet p) {
		if (p.type == PacketType.KEYBOARD) {
			sendPacket(new KeyboardPacketAlt((KeyboardPacket)p));
			System.out.println("Send keyboard");
		} else {
			System.out.println("REJECTED DEFAULT PACKET: "+p);
		}
	}
	
	public void sendPacket(PacketAlt p) {
		synchronized(outgoing) {
			outgoing.add(p);
			outgoing.notify();
		}
	}
	

	@Override
	public void run() {
		try {
			socket = new Socket("127.0.0.1", 25565);
			dataOutput = new ObjectOutputStream(socket.getOutputStream());
			dataInput = new ObjectInputStream(socket.getInputStream());
			
			Thread t = new Thread(new Runnable() { //Start the writing thread, this will be the reading one
				@Override
				public void run() {
					while (true) {
						synchronized(outgoing) {
							if (outgoing.size() > 0) {
								for (PacketAlt p : outgoing) {
									try {
										p.writePacket(dataOutput);
									} catch (IOException e) {
										e.printStackTrace();
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
			t.setName("Client: write");
			t.start();
			

			
			//Send Handshake
			sendPacket(new ConnectPacketAlt(name));
			
			while (socket.isConnected()) {
				PacketAlt p = (PacketAlt)dataInput.readObject();
				//System.out.println("READ: "+p);
				synchronized(GameEngine.getEngine().objects) {
				switch (p.type) {
				case INIT_OBJECTS:
					GameEngine.getEngine().objects.clear();
					GameEngine.getEngine().objects.addAll(((InitObjectsPacketAlt)p).objects);
					break;
				case UPDATE_OBJECT:
					UpdateObjectPacketAlt pack = (UpdateObjectPacketAlt)p;
					switch (pack.type) {
					case CREATE:
						GameEngine.getEngine().addObject(pack.updateObj);
						break;
					case DESTROY:
						GameEngine.getEngine().removeObject(pack.updateObj.getGUID());
						break;
					case SYNC_FIELD:
						GameEngine.getEngine().objects.set(GameEngine.getEngine().objects.indexOf(GameEngine.getEngine().getObjectFromGUID(pack.updateObj.getGUID())), pack.updateObj);
						break;
					default:
						break;
					}
					break;
				default:
					break;
				
				}
				}
			}
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
				System.err.println("Could not connect to server!");
			} else if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
				System.err.println("Lost connection to server!");
			} else {
				e.printStackTrace();
			}
		}
	}

}
