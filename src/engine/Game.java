package engine;

import engine.event.EventKeyboard;
import engine.event.EventMouse;
import engine.network.ServerClient;
import engine.object.GameObject;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public abstract class Game extends PApplet {
	
	private final String className;
	public GameEngine engine;
	
	public Game() {
		className = this.getClass().toString().replace("class ", "");
		engine = new GameEngine(this);
		
	}

	public void settings() {
		size(640, 480);
		engine.startEngine();
	}
	
	public void initGameObjects() {}
	
	/**
	 * Called whenever a new client connects. If a GameObject is returned here, it will be used as the client's object.
	 * @return null or a GameObject
	 */
	public GameObject onClientConnect(ServerClient sc) {
		return null;
	}
	
	@Override
	public void draw() {
		clear();
		engine.onDrawStep();
	}
	
	@Override
	public void setup() {
		frameRate(60);
	}
	
	public void startApplet() {
		PApplet.main(this.className);
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (Debug.CLIENT_EVENT_HANDLING)  {
			engine.raiseEvent(new EventKeyboard(null, false, event.getKeyCode()));
		} else {
			engine.keyPress(event.getKeyCode(), false);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		if (Debug.CLIENT_EVENT_HANDLING)  {
			engine.raiseEvent(new EventKeyboard(null, true, event.getKeyCode()));
		} else {
			engine.keyPress(event.getKeyCode(), true);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent event) {
		engine.raiseEvent(new EventMouse(null, event.getX(), event.getY()));
	}
}
