package homework2.alternate_network;

import engine.network.packet.UpdateObjectPacket;
import engine.object.GameObject;

public class UpdateObjectPacketAlt extends PacketAlt {
	
	private static final long serialVersionUID = 639434594997252591L;

	public static enum UpdateType {
		SYNC_FIELD(0),
		CREATE(1),
		DESTROY(2);
		
		public byte id;
		private UpdateType(int id) {
			this.id = (byte) id;
		}
		
		public static UpdateType fromId(byte id) {
			for (UpdateType t : UpdateType.values()) {
				if (t.id == id) return t;
			}
			return null;
		}
	}

	public UpdateType type;
	public GameObject updateObj;
	
	protected UpdateObjectPacketAlt(UpdateObjectPacket p) {//Used only for reading!
		super(PacketTypeAlt.UPDATE_OBJECT);
		this.updateObj = p.updateObj;
		this.type = UpdateType.fromId(p.type.id);
	}
	
	public UpdateObjectPacketAlt(GameObject obj, UpdateType type) {
		super(PacketTypeAlt.UPDATE_OBJECT);
		this.type = type;
		this.updateObj = obj;
	}
}
