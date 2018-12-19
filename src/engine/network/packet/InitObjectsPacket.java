package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import engine.GameEngine;
import engine.object.GameObject;

public class InitObjectsPacket extends Packet {
	
	public ArrayList<GameObject> objs;
	
	public InitObjectsPacket() {
		super(PacketType.INIT_OBJECTS);
		objs = new ArrayList<GameObject>();
		synchronized(GameEngine.getEngine().objects) {
			for (GameObject obj : GameEngine.getEngine().objects) {
				objs.add(obj);
			}
		}
	}

	@Override
	public void onPacketWrite(DataOutputStream ds) throws IOException {
		//GameEngine engine = GameEngine.getEngine();
		
		//synchronized(engine.objects) {
			//System.out.println("Start write, sz="+engine.objects.size());
			
			ds.writeInt(objs.size() - 1);
			
			ObjectOutputStream objout = new ObjectOutputStream(ds);
			for (GameObject o : objs) {
				if (o == GameEngine.getEngine().server.console) continue;
				objout.writeObject(o);
				//System.out.println("Wrote: "+o);
			}
			//System.out.println("Send init");
		//}
	}

	@Override
	public void onPacketRead(DataInputStream ds) throws IOException {
		GameEngine engine = GameEngine.getEngine();
		synchronized(engine.objects) {
			engine.objects.clear();
			int num = ds.readInt();
			//System.out.println("Expecting "+num+" objects");
			
			ObjectInputStream objin = new ObjectInputStream(ds);
			for (int i=0; i<num; i++) {
				try {
					GameObject obj = (GameObject)objin.readObject();
					//System.out.println("Read: "+obj);
					engine.objects.add(obj);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//ds.readByte(); //To remove extraneous input
			//System.out.println(ds.available());
		}

	}

}
