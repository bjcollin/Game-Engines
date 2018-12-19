package engine;

import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.GameObjectDraw;
import engine.component.GameObjectOnTick;
import engine.event.Event;
import engine.event.EventHandler;
import engine.exception.InvalidAnnotationException;
import engine.object.GameObject;
import engine.script.ScriptEventHandler;
import engine.script.ScriptOnTick;

@SuppressWarnings("rawtypes")
public class AnnotationHelper {
	
	private static HashMap<Class, Field> collisionDataFields = new HashMap<Class, Field>();
	private static HashMap<Class, Method> drawFunctions = new HashMap<Class, Method>();
	private static HashMap<Class, Method> tickFunctions = new HashMap<Class, Method>();
	private static HashMap<Class, Method> boundingBoxFunctions = new HashMap<Class, Method>();
	private static HashMap<Class, HashMap<Class<? extends Event>, Method>> eventHandlers = new HashMap<Class, HashMap<Class<? extends Event>, Method>>();
	private static HashMap<Class, String> scriptTickFunctions = new HashMap<Class, String>();
	private static HashMap<Class, HashMap<Class<? extends Event>, String>> scriptEventHandlers = new HashMap<Class, HashMap<Class<? extends Event>, String>>();
	
	public static void onDraw(GameObject o, Game g) {
		if (!drawFunctions.containsKey(o.getClass())) {
			registerDrawMethod(o.getClass());
		}
		
		if (drawFunctions.get(o.getClass()) != null) {
			try {
				drawFunctions.get(o.getClass()).invoke(o, g);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static CollisionData getCollisionData(GameObject o) {
		if (!collisionDataFields.containsKey(o.getClass())) {
			registerCollisionDataField(o.getClass());
		}
		
		if (collisionDataFields.get(o.getClass()) != null) {
			try {
				return (CollisionData)collisionDataFields.get(o.getClass()).get(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void onTick(GameObject o, Game g) {
		if (!tickFunctions.containsKey(o.getClass())) {
			registerTickMethod(o.getClass());
		}
		
		if (tickFunctions.get(o.getClass()) != null) {
			try {
				tickFunctions.get(o.getClass()).invoke(o, g);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static Rectangle getBoundingBox(GameObject o) {
		if (!boundingBoxFunctions.containsKey(o.getClass())) {
			registerBoundingBoxMethod(o.getClass());
		}
		
		if (boundingBoxFunctions.get(o.getClass()) != null) {
			try {
				return (Rectangle) boundingBoxFunctions.get(o.getClass()).invoke(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static HashMap<Class<? extends Event>, Method> getEventHandlers(Object o) {
		if (!eventHandlers.containsKey(o.getClass())) {
			registerEventHandlers(o.getClass());
		}
		return eventHandlers.get(o.getClass());
	}
	
	public static void onScriptTick(GameObject o, Game g) {
		if (!scriptTickFunctions.containsKey(o.getClass())) {
			registerScriptTickMethod(o.getClass());
		}
		
		if (scriptTickFunctions.get(o.getClass()) != null) {
			try {
				String file = scriptTickFunctions.get(o.getClass());
				g.engine.scriptManager.executeOnTick(file, o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static HashMap<Class<? extends Event>, String> getScriptedEventHandlers(Object o) {
		if (!scriptEventHandlers.containsKey(o.getClass())) {
			registerScriptedEventHandlers(o.getClass());
		}
		return scriptEventHandlers.get(o.getClass());
	}
	
	//---------------------REGISTRATION--------------------------
	
	private static void registerDrawMethod(Class c) {
		Method methods[] = c.getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(GameObjectDraw.class)) {
				Class[] params = m.getParameterTypes();
				if (params.length == 1 && params[0] == Game.class) {
					drawFunctions.put(c, m);
					Debug.log("Registered onDraw method: "+m.getName()+" for GameObject "+c);
					return;
				} else {
					throw new InvalidAnnotationException("Invalid parameters for GameObjectDraw method: "+m.getName()+" in "+c);
				}
			}
		}
		drawFunctions.put(c, null);
		Debug.log("No draw function found for GameObject: "+c);
	}
	
	private static void registerCollisionDataField(Class c) {
		Field fields[] = c.getFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(GameObjectCollisionData.class)) {
				if (f.getType() == CollisionData.class) {
					collisionDataFields.put(c, f);
					Debug.log("Registered GameObjectCollisionData field "+f+" for "+c);
					return;
				} else {
					throw new InvalidAnnotationException("Invalid type for GameObjectCollisionData field: "+f.getName()+" in "+c);
				}
			}
		}
		collisionDataFields.put(c, null);
		Debug.log("No collision data field found for GameObject: "+c);
	}
	
	private static void registerTickMethod(Class c) {
		Method methods[] = c.getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(GameObjectOnTick.class)) {
				Class[] params = m.getParameterTypes();
				if (params.length == 1 && params[0] == Game.class) {
					tickFunctions.put(c, m);
					Debug.log("Registered onTick method: "+m.getName()+" for GameObject "+c);
					return;
				} else {
					throw new InvalidAnnotationException("Invalid parameters for GameObjectOnTick method: "+m.getName()+" in "+c);
				}
			}
		}
		tickFunctions.put(c, null);
		Debug.log("No tick function found for GameObject: "+c);
	}
	
	private static void registerBoundingBoxMethod(Class c) {
		Method methods[] = c.getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(GameObjectBoundingBox.class)) {
				Class[] params = m.getParameterTypes();
				if (params.length == 0 && m.getReturnType() == Rectangle.class) {
					boundingBoxFunctions.put(c, m);
					Debug.log("Registered boundingBox method: "+m.getName()+" for GameObject "+c);
					return;
				} else {
					throw new InvalidAnnotationException("Invalid format for GameObjectOnTick method: "+m.getName()+" in "+c);
				}
			}
		}
		boundingBoxFunctions.put(c, null);
		Debug.log("No bounding box function found for GameObject: "+c);
	}
	

	@SuppressWarnings("unchecked")
	private static void registerEventHandlers(Class c) {
		HashMap<Class<? extends Event>, Method> result = new HashMap<Class<? extends Event>, Method>();
		Method methods[] = c.getDeclaredMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(EventHandler.class)) {
				Class[] params = m.getParameterTypes();
				if (params.length == 1 && (Event.class.isAssignableFrom(params[0]) || params[0] == Event.class)) {
					result.put(params[0], m);
					Debug.log("Registered event handler method: "+m.getName()+" for GameObject "+c);
				} else {
					throw new InvalidAnnotationException("Invalid format for EventHandler method: "+m.getName()+" in "+c);
				}
			}
		}
		if (result.size() == 0) {
			result = null;
			Debug.log("No event handler functions found for GameObject: "+c);
		}
		eventHandlers.put(c, result);
	}
	
	@SuppressWarnings("unchecked")
	private static void registerScriptTickMethod(Class c) {
		if (c.isAnnotationPresent(ScriptOnTick.class)) {
			ScriptOnTick t = (ScriptOnTick) c.getAnnotation(ScriptOnTick.class);
			String file = t.file();
			if (new File("./scripts/"+file+".js").exists()) {
				scriptTickFunctions.put(c, "./scripts/"+file+".js");
				Debug.log("Registered scripted tick: "+file+".js for GameObject "+c);
			} else {
				scriptTickFunctions.put(c, null);
				Debug.err("Script file: "+file+" not found!");
			}
		} else {
			scriptTickFunctions.put(c, null);
			Debug.log("No scripted tick function found for GameObject: "+c);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void registerScriptedEventHandlers(Class c) {
		if (c.isAnnotationPresent(ScriptEventHandler.class)) {
			ScriptEventHandler[] handlers = (ScriptEventHandler[]) c.getAnnotationsByType(ScriptEventHandler.class);
			HashMap<Class<? extends Event>, String> result = new HashMap<Class<? extends Event>, String>();
			for (ScriptEventHandler handle : handlers) {
				String fname = "./scripts/"+handle.file()+".js";
				if (new File(fname).exists()) {
					result.put(handle.eventType(), fname);
					Debug.log("Registered scripted event handler: "+handle.file()+".js for Event "+handle.eventType()+" for GameObject "+c);
				} else {
					Debug.err("Script file: "+handle.file()+" not found!");
				}
			}
			scriptEventHandlers.put(c, result);
		} else {
			scriptEventHandlers.put(c, null);
			Debug.log("No scripted event handlers found for GameObject: "+c);
		}
	}
}
