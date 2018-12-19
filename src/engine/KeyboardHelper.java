package engine;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyboardHelper {
	private HashMap<Integer, Boolean> keyPressed = new HashMap<Integer, Boolean>();
	
	public static final int LEFT = KeyEvent.VK_LEFT;
	public static final int RIGHT = KeyEvent.VK_RIGHT;
	public static final int SPACE = KeyEvent.VK_SPACE;
	
	
	public void onPressed(int keycode) {
		keyPressed.put(keycode, true);
	}
	
	public void onRelease(int keycode) {
		keyPressed.put(keycode, false);
	}
	
	public boolean isKeyDown(int keycode) {
		if (!keyPressed.containsKey(keycode)) return false;
		return keyPressed.get(keycode);
	}
}
