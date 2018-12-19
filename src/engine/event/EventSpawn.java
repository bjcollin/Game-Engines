package engine.event;

import engine.object.GameObject;

public class EventSpawn extends Event {

	public long player;
	public int respawnIndex;
	
	public EventSpawn(GameObject player, int point) {
		this.player = player.getGUID();
		this.respawnIndex = point;
	}
	
	private EventSpawn(long pl, int point) {
		player = pl;
		respawnIndex = point;
	}

	@Override
	public Event clone() {
		Event clone = new EventSpawn(player, respawnIndex);
		clone.copyDefaultData(this);
		return clone;
	}
}
