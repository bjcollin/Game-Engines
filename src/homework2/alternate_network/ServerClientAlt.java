package homework2.alternate_network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import engine.KeyboardHelper;
import homework2.alternate_network.UpdateObjectPacketAlt.UpdateType;
import homework2.alternate_network.PacketAlt.PacketTypeAlt;

public class ServerClientAlt implements Runnable {

	public String name;
	public boolean initialized = false;
	
	public ServerAlt server;
	public Socket socket;
	public ObjectOutputStream dataOutput;
	public ObjectInputStream dataInput;
	public int clientId;
	
	public KeyboardHelper keyboard = new KeyboardHelper();
	public long playerGUID = 0;
	
	private ArrayList<PacketAlt> outgoing;
	
	public ServerClientAlt(ServerAlt server, Socket sock, int clientId) throws IOException {
		socket = sock;
		dataOutput = new ObjectOutputStream(sock.getOutputStream());
		dataInput = new ObjectInputStream(sock.getInputStream());
		this.clientId = clientId;
		this.server = server;
		outgoing = new ArrayList<PacketAlt>();
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
								for (PacketAlt p : outgoing) {
									try {
										p.writePacket(dataOutput);
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
				try {
					PacketAlt p = (PacketAlt)dataInput.readObject();
					//System.out.println("READ: "+p);
					if (p.type == PacketTypeAlt.CONNECT) {
						name = ((ConnectPacketAlt)p).name;
						server.consoleMessage("Alt Client: "+name+" connected!");
					} else if (p.type == PacketTypeAlt.KEYBOARD) {
						KeyboardPacketAlt kb = (KeyboardPacketAlt)p;
						if (kb.released) {
							keyboard.onRelease(kb.code);
						} else {
							keyboard.onPressed(kb.code);
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
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
		System.out.println("Client: "+name+" disconnected!");
		server.consoleMessage("Client: "+name+" disconnected!");
		server.disconnectClient(this);
	}
	
	public void sendPacket(PacketAlt p) {
		//System.out.println("Attempt send: "+p);
		synchronized(outgoing) {
			boolean canSend = true;
			if (p.type == PacketTypeAlt.INIT_OBJECTS) {
				initialized = true;
			} else if (p.type == PacketTypeAlt.UPDATE_OBJECT) {
				UpdateObjectPacketAlt objup = (UpdateObjectPacketAlt) p;
				if (objup.type == UpdateType.SYNC_FIELD && !initialized) {
					canSend = false; //Don't send Updates until the basics have arrived
				}
			}
			
			if (canSend) { 
				outgoing.add(p);
				outgoing.notify();
			} else {
				System.out.println("REJECT: "+p);
			}
		}
	}
}
