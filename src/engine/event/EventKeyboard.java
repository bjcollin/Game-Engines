package engine.event;

import engine.network.ServerClient;

public class EventKeyboard extends Event {

	public int clientID;
	public boolean release;
	public int code;
	
	public EventKeyboard(ServerClient who, boolean rel, int code) {
		this.clientID = who != null ? who.clientId : -1;
		this.release = rel;
		this.code = code;
	}
	
	private EventKeyboard(int clid, boolean r, int c) {
		this.clientID = clid;
		this.release = r;
		this.code = c;
	}

	@Override
	public Event clone() {
		Event clone = new EventKeyboard(clientID, release, code);
		clone.copyDefaultData(this);
		return clone;
	}
}
