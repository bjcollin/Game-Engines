package engine.script;

import engine.GameEngine;
import engine.object.GameObject;

public class ScriptHelper {

	public GameObject getObjFromID(long guid) {
		return GameEngine.getEngine().getObjectFromGUID(guid);
	}
	
	public String getObjType(long guid) {
		GameObject obj = GameEngine.getEngine().getObjectFromGUID(guid);
		if (obj != null) {
			return obj.getClass().toString().replace("class ", "");
		}
		return "null";
	}
}
