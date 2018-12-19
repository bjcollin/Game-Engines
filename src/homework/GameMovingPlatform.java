package homework;

import java.awt.Color;

import engine.component.Velocity;
import engine.event.EventCollision;
import engine.object.GameObject;
import engine.script.ScriptEventHandler;
import engine.script.ScriptOnTick;

@ScriptOnTick(file = "GameRectangle_onTick")
@ScriptEventHandler(file = "GameRectangle_onEvent", eventType = EventCollision.class)
public class GameMovingPlatform extends GameRectangle {
	
	private static final long serialVersionUID = 7092352258410975551L;

	public enum PlatformType {
		HORIZONTAL(1),
		VERTICAL(2);
		
		public byte id;
		private PlatformType(int type) {
			this.id = (byte)type;
		}
	}
	
	public int dir = 1;
	public PlatformType type;

	public GameMovingPlatform(double x, double y, int w, int h, Color col, PlatformType type) {
		super(x, y, w, h, col);
		this.type = type;
		this.collision.gravity = false;
	}

	
	public GameObject clone() {
		GameMovingPlatform result = new GameMovingPlatform(collision.pos.x, collision.pos.y, collision.size.width, collision.size.height, color, type);
		result.GUID = GUID;
		result.collision.vel = new Velocity(collision.vel.x, collision.vel.y);
		result.controllable = controllable;
		result.collision.gravity = collision.gravity;
		result.dir = dir;
		return result;
	}

}
