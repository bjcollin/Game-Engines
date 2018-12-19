package engine.event;

public class EventReplayEnd extends Event {

	@Override
	public Event clone() {
		Event clone = new EventReplayEnd();
		clone.copyDefaultData(this);
		return clone;
	}

}
