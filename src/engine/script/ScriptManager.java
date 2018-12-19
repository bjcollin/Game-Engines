package engine.script;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import engine.event.Event;
import engine.object.GameObject;

public class ScriptManager {

	private static ScriptEngine js_engine;
	
	private static ScriptHelper helper = new ScriptHelper();
	
	public ScriptManager() {
		js_engine = new ScriptEngineManager().getEngineByName("JavaScript");
	}
	
	public void executeOnTick(String file, GameObject obj) {
		try {
			js_engine.eval(new java.io.FileReader(file));
			js_engine.put("obj", obj);
			js_engine.put("helper", helper);
			((Invocable)js_engine).invokeFunction("onTick");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeEventHandler(String file, Object obj, Event evt) {
		try {
			js_engine.eval(new java.io.FileReader(file));
			js_engine.put("obj", obj);
			js_engine.put("helper", helper);
			js_engine.put("event_type", evt.getClass().toString().replace("class ", ""));
			((Invocable)js_engine).invokeFunction("onEvent", evt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
