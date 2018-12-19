package engine.event;

import engine.object.GameObject;

public class EventDeath extends Event {
	
	public long player;

	public EventDeath(GameObject player) {
		this.player = player.getGUID();
	}
	
	private EventDeath(long pl) {
		player = pl;
	}

	@Override
	public Event clone() {
		Event clone = new EventDeath(player);
		clone.copyDefaultData(this);
		return clone;
	}
}
