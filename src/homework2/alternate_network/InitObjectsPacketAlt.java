package homework2.alternate_network;

import java.util.ArrayList;

import engine.GameEngine;
import engine.object.GameObject;
import engine.object.MessageDisplayer;

public class InitObjectsPacketAlt extends PacketAlt {
	
	private static final long serialVersionUID = -7553399228509584303L;
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	
	public InitObjectsPacketAlt() {
		super(PacketTypeAlt.INIT_OBJECTS);
		for (GameObject obj : GameEngine.getEngine().objects) {
			if (!(obj instanceof MessageDisplayer)) {
				objects.add(obj);
			}
		}
		
	}
}
