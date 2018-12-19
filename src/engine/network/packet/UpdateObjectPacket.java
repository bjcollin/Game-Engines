package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import engine.GameEngine;
import engine.network.sync.SyncHelper;
import engine.object.GameObject;

public class UpdateObjectPacket extends Packet {
	
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
	
	protected UpdateObjectPacket() {//Used only for reading!
		super(PacketType.UPDATE_OBJECT);
	}
	
	public UpdateObjectPacket(GameObject obj, UpdateType type) {
		super(PacketType.UPDATE_OBJECT);
		this.type = type;
		this.updateObj = obj;
	}

	@Override
	public void onPacketWrite(DataOutputStream ds) throws IOException {
		ds.writeByte(type.id);
		ds.writeLong(updateObj.getGUID());
		
		switch(type) {
		case CREATE:
			//System.out.println("Sending create, ID="+updateObj.getGUID());
			ObjectOutputStream out = new ObjectOutputStream(ds);
			out.writeObject(updateObj);
			out.reset();
			//ds.writeUTF(updateObj.getClass().toString().replace("class ", ""));
			//SyncHelper.writeAllSyncFields(updateObj, ds);
			break;
		case DESTROY:
			break;
		case SYNC_FIELD:
			//System.out.println("Sending update, ID="+updateObj.getGUID());
			SyncHelper.writeAllSyncFields(updateObj, ds);
			break;
		default:
			break;
		
		}
	}
	
	@Override
	public void onPacketRead(DataInputStream ds) throws IOException {
		type = UpdateType.fromId(ds.readByte());
		long GUID = ds.readLong();
		
		updateObj = GameEngine.getEngine().getObjectFromGUID(GUID);
		if (updateObj != null || type == UpdateType.CREATE) {
			switch (type) {
			case CREATE:
				try {
					/*Class c = Class.forName(ds.readUTF());
					GameObject obj = (GameObject)c.newInstance();
					GameEngine.getEngine().addObject(obj);*/
					//SyncHelper.readAllSyncFields(obj, ds);
					ObjectInputStream in = new ObjectInputStream(ds);
					GameObject obj = (GameObject)in.readObject();
					if (GameEngine.getEngine().getObjectFromGUID(obj.getGUID()) == null) {
						GameEngine.getEngine().objects.add(obj);
						//System.out.println("Added new object: "+obj.GUID);
					}// else
						//System.out.println("Duplicate create: id="+obj.getGUID());
					ds.readByte();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case DESTROY:
				GameEngine.getEngine().removeObject(updateObj);
				break;
			case SYNC_FIELD:
				SyncHelper.readAllSyncFields(updateObj, ds);
				break;
			default:
				throw new IllegalArgumentException("Unknown UpdateType!");
			}
		} else {
			//System.out.println("Unknown object in update packet! id="+GUID);
			//throw new IllegalArgumentException();
			throw new IllegalStateException("Unknown object in update packet! id="+GUID);
		}
		
	}
}
