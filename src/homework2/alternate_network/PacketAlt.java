package homework2.alternate_network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class PacketAlt implements Serializable {


	private static final long serialVersionUID = 4003203612516308990L;
	
	public static enum PacketTypeAlt implements Serializable {
		CONNECT(0),
		UPDATE_OBJECT(1),
		KEYBOARD(2),
		INIT_OBJECTS(3);
		
		public byte id;
		private PacketTypeAlt(int id) {
			this.id = (byte)id;
		}
		
		public static PacketTypeAlt fromId(byte id) {
			for (PacketTypeAlt t : PacketTypeAlt.values()) {
				if (t.id == id) return t;
			}
			return null;
		}
	}
	
	public PacketTypeAlt type;
	
	public PacketAlt(PacketTypeAlt t) {
		type = t;
	}
	
	public void writePacket(ObjectOutputStream ds) throws IOException {
		ds.writeObject(this);
		ds.reset();
		//System.out.println("Wrote: "+this.getClass());
	}
}
