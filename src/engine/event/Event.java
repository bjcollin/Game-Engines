package engine.event;

import java.util.Comparator;

import engine.GameEngine;

public abstract class Event implements Comparable<Event>, Comparator<Event> {

	public long timestep = 0;
	public int priority = 0;
	
	public Event() {
		timestep = GameEngine.getEngine().globalTime.tick;
	}
	
	
	@Override
	public int compare(Event o1, Event o2) {
		if (o1.timestep < o2.timestep) return -1;
		else if (o1.timestep > o2.timestep) return 1;
		else { //Same time, go to priority now
			if (o1.priority > o2.priority) return -1;
			else if (o1.priority < o2.priority) return 1;
			else return 0;
		}
	}

	@Override
	public int compareTo(Event o) {
		return this.compare(this, o);
	}
	
	public abstract Event clone();
	
	protected void copyDefaultData(Event source) {
		timestep = source.timestep;
		priority = source.priority;
	}
}
