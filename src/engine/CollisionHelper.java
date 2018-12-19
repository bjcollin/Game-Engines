package engine;

import java.awt.Rectangle;
import java.util.ArrayList;

import engine.component.CollisionData;
import engine.component.Position;
import engine.component.Size;
import engine.component.Velocity;
import engine.event.EventCollision;
import engine.object.GameObject;

public class CollisionHelper {
	
	public static void preTick(GameObject obj, Game g) {
		CollisionData data = AnnotationHelper.getCollisionData(obj);
		
		if (data != null) {
			Position pos = data.pos;
			Velocity vel = data.vel;
			Size size = data.size;
			
			if (vel != null) {
				if (CollisionHelper.checkForCollision(obj, vel.x, vel.y)) {
					ArrayList<GameObject> collides = CollisionHelper.getCollidingObjects(obj, MathHelper.floor(vel.x), MathHelper.floor(vel.y));
					for (GameObject o2 : collides) {
						g.engine.raiseEvent(new EventCollision(obj, o2));
					}
				}
				if (checkEdge(g, pos, vel, size)) {
					g.engine.raiseEvent(new EventCollision(obj, null));
				}
			}
			
			if (data.gravity) {
				if (pos.y + size.height == g.height || CollisionHelper.checkForCollision(obj, 0, vel.y)) {
					data.onGround = true;
					vel.y = 0;
					vel.x *= 0.5;
				} else {
					data.onGround = false;
				}
			}
		}
	}
	
	public static void postTick(GameObject obj, Game g) {
		CollisionData data = AnnotationHelper.getCollisionData(obj);
		
		if (data != null) {
			Position pos = data.pos;
			Velocity vel = data.vel;
			Size size = data.size;
			Position start = pos.copy();
			
			if (data.stopOnCollide && CollisionHelper.checkForCollision(obj, (int)Math.floor(vel.x), -1)) {
				vel.x = 0;
			}
			
			if (data.stopOnCollide && CollisionHelper.checkForCollision(obj, 0, (int)Math.floor(vel.y))) {
				vel.y = 0;
			}
			
			//CollisionHelper.moveToEdge(obj, (int)Math.floor(vel.x), (int)Math.floor(vel.y));
			
			pos.x += vel.x;
			pos.y += vel.y;
			if (!data.gravity) {
				if (pos.y + size.height > g.height) pos.y = (g.height - size.height) - 2;
				if (pos.y < 0) pos.y = 2;
			}
			if (data.gravity) vel.y += 0.5;
			
			if (data.gravity) {
				if (pos.x + size.width >= g.width) {
					vel.x *= -0.5;
					vel.y *= 0.5;
					pos.x = g.width - size.width;
				}
				
				if (pos.x <= 0) {
					vel.x *= -0.5;
					vel.y *= 0.5;
					pos.x = 0;
				}
				
				if (pos.y + size.height >= g.height) {
					vel.x *= 0.5;
					vel.y *= -0.5;
					pos.y = g.height - size.height;
				}
				
				if (pos.y <= 0) {
					vel.x *= 0.5;
					vel.y *= -0.5;
					pos.y = 0;
				}
			
				if (Math.abs(vel.x) < 2) vel.x = 0;
				if (Math.abs(vel.y) < 2 && pos.y + size.height >= g.height) {
					vel.y = 0;
				}
			}
			
			if (!pos.equals(start)) obj.markChanged();
		}
		
	}
	
	private static boolean checkEdge(Game g, Position pos, Velocity vel, Size sz) {
		double newx = pos.x+vel.x;
		double newy = pos.y+vel.y;
		if (newx < 0 || newx > g.width-sz.width || newy < 0 || newy > g.height-sz.height) {
			return true;
		}
		return false;
	}

	public static boolean checkForCollision(GameObject me, double dx, double dy) {
		Rectangle box = AnnotationHelper.getBoundingBox(me);
		if (box == null) return false;
		box.setLocation(box.x+MathHelper.floor(dx), box.y+MathHelper.floor(dy));
		for (GameObject g : GameEngine.getEngine().objects) {
			if (g == me) continue;
			Rectangle boxOther = AnnotationHelper.getBoundingBox(g);
			if (boxOther == null) continue;
			if (box.intersects(boxOther)) return true;
		}
		return false;
	}
	
	public static void moveToEdge(GameObject me, int dx, int dy) {
		ArrayList<GameObject> collides = getCollidingObjects(me, dx, dy);
		if (collides.size() == 0) return;
		Rectangle box = AnnotationHelper.getBoundingBox(me);
		if (box == null) return;
		box.setLocation(box.x+dx, box.y+dy);
		
		for (GameObject g : collides) {
			Rectangle boxOther = AnnotationHelper.getBoundingBox(g);
			while (box.intersects(boxOther)) {
				box.setLocation((int)-Math.signum(dx), (int)-Math.signum(dy));
			}
		}
	}
	
	public static ArrayList<GameObject> getCollidingObjects(GameObject me, int dx, int dy) {
		ArrayList<GameObject> results = new ArrayList<GameObject>();
		
		Rectangle box = AnnotationHelper.getBoundingBox(me);
		if (box == null) return results;
		box.setLocation(box.x+dx, box.y+dy);
		for (GameObject g : GameEngine.getEngine().objects) {
			if (g == me) continue;
			Rectangle boxOther = AnnotationHelper.getBoundingBox(g);
			if (boxOther == null) continue;
			if (box.intersects(boxOther)) {
				results.add(g);
			}
		}
		return results;
	}
}
