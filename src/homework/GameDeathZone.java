package homework;

import java.awt.Rectangle;

import engine.GameEngine;
import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.Position;
import engine.event.EventCollision;
import engine.event.EventDeath;
import engine.event.EventHandler;
import engine.object.GameObject;

public class GameDeathZone extends GameObject {
		
	private static final long serialVersionUID = -8084493115648102941L;
	
	@GameObjectCollisionData
	public CollisionData collision;

	public GameDeathZone(int x, int y, int w, int h) {
		this.collision = new CollisionData();
		this.collision.size.width = w;
		this.collision.size.height = h;
		this.collision.pos = new Position(x,y);
		this.collision.solid = false;
	}

	/*@GameObjectDraw
	public void onDraw(Game g) { //For Debug purposes
		java.awt.Color c = new java.awt.Color(255, 0, 0, 100);
		g.fill(c.getRGB());
		DrawHelper.drawRectangle(g, pos.x, pos.y, width, height);
	}*/
	
	/*@GameObjectOnTick
	public void onTick(Game g) {
		ArrayList<GameObject> collides = CollisionHelper.getCollidingObjects(this, -10, 0);
		if (collides.size() > 0) {
			for (GameObject obj : collides) {
				if (obj instanceof GameRectangle) {
					g.engine.raiseEvent(new EventDeath(obj));
				}
			}
		}
	}*/
	
	@EventHandler
	public void onCollide(EventCollision evt) {
		if (!evt.edge && evt.obj2 == getGUID()) {
			GameObject other = GameEngine.getEngine().getObjectFromGUID(evt.obj1);
			if (other instanceof GameRectangle) {
				GameEngine.getEngine().raiseEvent(new EventDeath(other));
			}
		}
	}
	
	@GameObjectBoundingBox
	public Rectangle getBoundingBox() {
		return new Rectangle(collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
	}
	
	public GameObject clone() {
		return new GameDeathZone(collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
	}
}
 