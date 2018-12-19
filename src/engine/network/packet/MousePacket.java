package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MousePacket extends Packet {
	
	public int x;
	public int y;
	
	public MousePacket() {
		super(PacketType.MOUSE);
	}
	
	public MousePacket(int x, int y) {
		super(PacketType.MOUSE);
		this.x = x;
		this.y = y;
	}

	@Override
	public void onPacketWrite(DataOutputStream ds) throws IOException {
		ds.writeInt(x);
		ds.writeInt(y);
	}

	@Override
	public void onPacketRead(DataInputStream ds) throws IOException {
		x = ds.readInt();
		y = ds.readInt();
	}

}
