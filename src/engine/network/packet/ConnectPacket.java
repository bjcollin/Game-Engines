package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConnectPacket extends Packet {

	public String name;
	
	public ConnectPacket() {
		super(PacketType.CONNECT);
	}
	public ConnectPacket(String name) {
		super(PacketType.CONNECT);
		this.name = name;
	}

	@Override
	public void onPacketWrite(DataOutputStream ds) throws IOException {
		//ds.writeInt(name.length());	
		ds.writeUTF(name);
	}

	@Override
	public void onPacketRead(DataInputStream ds) throws IOException {
		name = ds.readUTF();
	}

}
