package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {

	public static enum PacketType {
		CONNECT(0),
		UPDATE_OBJECT(1),
		KEYBOARD(2),
		INIT_OBJECTS(3),
		MOUSE(4);
		
		public byte id;
		private PacketType(int id) {
			this.id = (byte)id;
		}
		
		public static PacketType fromId(byte id) {
			for (PacketType t : PacketType.values()) {
				if (t.id == id) return t;
			}
			System.out.println("Unknown packet type id! "+id);
			return null;
		}
	}
	
	public PacketType type;
	
	public Packet(PacketType t) {
		type = t;
	}
	
	public void writePacket(DataOutputStream ds) throws IOException {
		ds.writeByte(type.id);
		onPacketWrite(ds);
	}
	
	public static Packet getPacketFromId(byte id) {
		PacketType type = PacketType.fromId(id);
		if (type == null) return null;
		switch (type) {
		case CONNECT:
			return new ConnectPacket();
		case UPDATE_OBJECT:
			return new UpdateObjectPacket();
		case KEYBOARD:
			return new KeyboardPacket();
		case INIT_OBJECTS:
			return new InitObjectsPacket();
		case MOUSE:
			return new MousePacket();
		default:
			throw new IllegalArgumentException("Unknown packet id!" +id);
		
		}
	}
	
	public abstract void onPacketWrite(DataOutputStream ds) throws IOException;
	public abstract void onPacketRead(DataInputStream ds) throws IOException;
}
