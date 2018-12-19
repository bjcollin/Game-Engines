package engine.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import engine.MouseHelper;
import engine.network.packet.ConnectPacket;
import engine.network.packet.Packet;

public class Client implements Runnable {
	
	public String name;
	public static Socket socket;
	
	public DataOutputStream dataOutput;
	public DataInputStream dataInput;
	
	public ArrayList<Packet> outgoing = new ArrayList<Packet>();
	
	public MouseHelper mouse = new MouseHelper();
	
	public Client(String name) {
		this.name = name;
	}
	
	public void sendPacket(Packet p) {
		synchronized(outgoing) {
			outgoing.add(p);
			outgoing.notify();
		}
	}
	

	@Override
	public void run() {
		try {
			socket = new Socket("127.0.0.1", 25565);
			dataOutput = new DataOutputStream(socket.getOutputStream());
			dataInput = new DataInputStream(socket.getInputStream());
			
			Thread t = new Thread(new Runnable() { //Start the writing thread, this will be the reading one
				@Override
				public void run() {
					while (true) {
						synchronized(outgoing) {
							if (outgoing.size() > 0) {
								for (Packet p : outgoing) {
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
			sendPacket(new ConnectPacket(name));
			
			while (socket.isConnected()) {
				byte packId = dataInput.readByte();
				if (packId == -1) continue;
				Packet p = Packet.getPacketFromId(packId);
				p.onPacketRead(dataInput);
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
