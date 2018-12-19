package engine.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import engine.AnnotationHelper;
import engine.GameEngine;
import engine.object.GameObject;
import engine.replay.ReplayManager;
import engine.replay.ReplayManager.ReplayState;

public class EventManager {
	
	public PriorityQueue<Event> events;
	
	private ArrayList<Object> extraHandlers;
	
	public EventManager() {
		events = new PriorityQueue<Event>();
		extraHandlers = new ArrayList<Object>();
	}
	
	public void registerExtraHandler(Object obj) {
		extraHandlers.add(obj);
	}
	
	public void unregisterExtraHandler(Object obj) {
		extraHandlers.remove(obj);
	}

	public synchronized void raiseEvent(Event e) {
		if (GameEngine.getEngine().replayManager.state == ReplayState.PLAYBACK && !ReplayManager.isSpeedInput(e)) return; //No new events during playback
		synchronized(events) {
			events.add(e);
		}
	}
	
	public synchronized void handleEvents(GameEngine eng) {
		if (!events.isEmpty()) {
			Event top = events.peek();
			if (top == null) {return;}
			while (top.timestep <= eng.globalTime.tick) {
				handleEvent(top);
				try {
					events.remove();
				} catch (Exception e) {}
				if (events.isEmpty()) break;
				top = events.peek();
			}
		}
	}
	
	private void handleEvent(Event e) {
		synchronized(GameEngine.getEngine().objects) {
			for (GameObject o : GameEngine.getEngine().objects) {
				HashMap<Class<? extends Event>, Method> objHandles = AnnotationHelper.getEventHandlers(o);
				if (objHandles != null) {
					if (objHandles.containsKey(e.getClass())) {
						Method handler = objHandles.get(e.getClass());
						try {
							handler.invoke(o, e);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					if (e.getClass() != Event.class && objHandles.containsKey(Event.class)) { //Wildcard
						Method handler = objHandles.get(Event.class);
						try {
							handler.invoke(o, e);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				
				HashMap<Class<? extends Event>, String> scriptHandles = AnnotationHelper.getScriptedEventHandlers(o);
				if (scriptHandles != null) {
					if (scriptHandles.containsKey(e.getClass())) {
						String handler = scriptHandles.get(e.getClass());
						GameEngine.getEngine().scriptManager.executeEventHandler(handler, o, e);
					}
					if (e.getClass() != Event.class && scriptHandles.containsKey(Event.class)) { //Wildcard
						String handler = scriptHandles.get(Event.class);
						GameEngine.getEngine().scriptManager.executeEventHandler(handler, o, e);
					}
				}
			}
		}
		for (Object o : extraHandlers) {
			HashMap<Class<? extends Event>, Method> objHandles = AnnotationHelper.getEventHandlers(o);
			if (objHandles != null) {
				if (objHandles.containsKey(e.getClass())) {
					Method handler = objHandles.get(e.getClass());
					try {
						handler.invoke(o, e);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if (e.getClass() != Event.class && objHandles.containsKey(Event.class)) { //Wildcard
					Method handler = objHandles.get(Event.class);
					try {
						handler.invoke(o, e);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
			HashMap<Class<? extends Event>, String> scriptHandles = AnnotationHelper.getScriptedEventHandlers(o);
			if (scriptHandles != null) {
				if (scriptHandles.containsKey(e.getClass())) {
					String handler = scriptHandles.get(e.getClass());
					GameEngine.getEngine().scriptManager.executeEventHandler(handler, o, e);
				}
				if (e.getClass() != Event.class && scriptHandles.containsKey(Event.class)) { //Wildcard
					String handler = scriptHandles.get(Event.class);
					GameEngine.getEngine().scriptManager.executeEventHandler(handler, o, e);
				}
			}
		}
	}
}
