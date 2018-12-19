package engine.event;

public class EventReplayStart extends Event {

	@Override
	public Event clone() {
		Event clone = new EventReplayStart();
		clone.copyDefaultData(this);
		return clone;
	}

}
