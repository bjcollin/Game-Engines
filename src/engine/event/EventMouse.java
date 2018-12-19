package engine.event;

import engine.network.ServerClient;

public class EventMouse extends Event {

	public int clientID;
	public int x;
	public int y;
	
	public EventMouse(ServerClient who, int x, int y) {
		this.clientID = who != null ? who.clientId : -1;
		this.x = x;
		this.y = y;
	}
	
	private EventMouse(int clientId, int x, int y) {
		this.clientID = clientId;
		this.x = x;
		this.y = y;
	}

	@Override
	public Event clone() {
		Event clone = new EventMouse(clientID, x, y);
		clone.copyDefaultData(this);
		return clone;
	}

}
