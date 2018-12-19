package homework;

import java.awt.Color;
import java.awt.Rectangle;

import engine.CollisionHelper;
import engine.DrawHelper;
import engine.Game;
import engine.GameEngine;
import engine.KeyboardHelper;
import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.GameObjectDraw;
import engine.component.GameObjectOnTick;
import engine.component.Position;
import engine.component.Velocity;
import engine.event.EventCollision;
import engine.event.EventDeath;
import engine.event.EventHandler;
import engine.event.EventSpawn;
import engine.network.sync.SyncField;
import engine.object.GameObject;
import processing.core.PFont;

public class GameRectangle extends GameObject {
		
	private static final long serialVersionUID = 5713770339983175560L;

	@SyncField
	public Color color;
	
	@SyncField
	@GameObjectCollisionData
	public CollisionData collision;
	
	public boolean controllable = false;

	
	public GameRectangle(double x, double y, int w, int h, Color col) {
		this.collision = new CollisionData();
		this.collision.size.width = w;
		this.collision.size.height = h;
		this.color = col;
		this.collision.pos = new Position(x,y);
		this.collision.vel = new Velocity(0,0);
	}

	@GameObjectDraw
	public void onDraw(Game g) {
		g.fill(color.getRGB());
		DrawHelper.drawRectangle(g, collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
		if (controllable) {
			g.textAlign(PFont.CENTER, PFont.TOP);
			String name = g.engine.getControllerName(this);
			if (name != null) g.text(name, collision.pos.getX()+(collision.size.width/2), collision.pos.getY()-16);
		}
		//
	}
	
	@GameObjectOnTick
	public void onTick(Game g) {
		KeyboardHelper helper = g.engine.getControllerKeyboard(this);
		if (controllable && helper != null) {
			if (helper.isKeyDown(KeyboardHelper.LEFT)) {
				collision.vel.x = -7;
			} else if (helper.isKeyDown(KeyboardHelper.RIGHT)) {
				collision.vel.x = 7;
			}
			if (helper.isKeyDown(KeyboardHelper.SPACE) && collision.onGround && !CollisionHelper.checkForCollision(this, 0, -14)) {
				collision.vel.y = -14;
				collision.onGround = false;
			}
		}
	}
	

	
	@EventHandler
	public void onCollide(EventCollision evt) {
		if (evt.obj1 != getGUID()) return;
		if (CollisionHelper.checkForCollision(this, (int)Math.floor(collision.vel.x), -1)) {
			collision.vel.x = 0;
		}
		
		CollisionHelper.moveToEdge(this, (int)Math.floor(collision.vel.x), (int)Math.floor(collision.vel.y));
	}
	
	@GameObjectBoundingBox
	public Rectangle getBoundingBox() {
		return new Rectangle(collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
	}
	
	@EventHandler
	public void handleDeath(EventDeath evt) {
		if (evt.player == getGUID()) {
			GameEngine.getEngine().raiseEvent(new EventSpawn(this, GameEngine.getEngine().getRandomSpawn()));
		}
	}
	
	@EventHandler
	public void handleSpawn(EventSpawn evt) {
		if (evt.player == getGUID()) {
			GameEngine.getEngine().setPosToSpawn(this, evt.respawnIndex);
			hasChanged = true;
		}
	}
	
	public GameObject clone() {
		GameRectangle result = new GameRectangle(collision.pos.x, collision.pos.y, collision.size.width, collision.size.height, color);
		result.GUID = GUID;
		result.collision.vel = new Velocity(collision.vel.x, collision.vel.y);
		result.controllable = controllable;
		result.collision.gravity = collision.gravity;
		return result;
	}
}
 