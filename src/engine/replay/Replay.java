package engine.replay;

import java.util.ArrayList;
import java.util.HashMap;

import engine.event.Event;
import engine.object.GameObject;

public class Replay {

	public ArrayList<GameObject> initial;
	public ArrayList<Event> events;
	public HashMap<Integer, Long> clientToGUID;
}
