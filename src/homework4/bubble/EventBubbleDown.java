package homework4.bubble;

import engine.event.Event;

public class EventBubbleDown extends Event {
	
	@Override
	public Event clone() {
		EventBubbleDown evt = new EventBubbleDown();
		evt.copyDefaultData(this);
		return evt;
	}

}
