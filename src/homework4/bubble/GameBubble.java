package homework4.bubble;

import java.awt.Rectangle;

import javax.swing.JOptionPane;

import engine.Game;
import engine.GameEngine;
import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.GameObjectDraw;
import engine.component.GameObjectOnTick;
import engine.event.EventCollision;
import engine.event.EventHandler;
import engine.network.sync.SyncField;
import engine.object.GameObject;
import engine.script.ScriptEventHandler;
import homework4.bubble.BubbleShooter.BubbleType;
import processing.core.PApplet;

@ScriptEventHandler(file = "BubbleCollide", eventType = EventCollision.class)
public class GameBubble extends GameObject {

	private static final long serialVersionUID = -2578381881579685105L;

	public byte type;
	
	@SyncField
	public boolean moving;
	
	@SyncField
	@GameObjectCollisionData
	public CollisionData collision;
	
	public GameBubble(BubbleType b, int x, int y) {
		this(b.id, x, y);	
	}
	
	public GameBubble(byte b, int x, int y) {
		type = b;
		collision = new CollisionData();
		collision.gravity = false;
		collision.pos.x = x;
		collision.pos.y = y;
		collision.solid = true;
		collision.size.width = 32;
		collision.size.height = 32;
	}
	
	@GameObjectOnTick
	public void onTick(Game g) {
		if (moving) {
			hasChanged = true;
		}
	}
	
	@EventHandler
	public void onBubbleDown(EventBubbleDown evt) {
		if (!moving) {
			collision.pos.y += 32;
			BubbleShooter.setToGrid(this);
			hasChanged = true;
		}
	}
	
	/*@EventHandler
	public void onCollide(EventCollision evt) {
		if (evt.obj1 == getGUID()) {
			GameBubble obj1 = (GameBubble)GameEngine.getEngine().getObjectFromGUID(evt.obj1);
			if (evt.edge) { //Wall
				obj1.collision.vel.x *= -1;
				if (obj1.collision.pos.y < 32) {
					obj1.stop();
				}
			} else {
				System.out.println("collide!");
				GameBubble obj2 = (GameBubble)GameEngine.getEngine().getObjectFromGUID(evt.obj2);
				if (obj1.type == obj2.type) {
					System.out.println("popped!");
					obj1.markForDelete = true;
					obj1.stop();
					obj2.markForDelete = true;
				} else {
					System.out.println("idk");
				}
			}
		}
	}*/
	
	@GameObjectBoundingBox
	public Rectangle getBoundingBox() {
		return new Rectangle(collision.pos.getX(), collision.pos.getY(),32,32);
	}
	
	public void stop() {
		moving = false;
		collision.vel.x = 0;
		collision.vel.y = 0;
		BubbleShooter.setToGrid(this);
		hasChanged = true;
	}
	
	public void score() {
		BubbleShooter game = (BubbleShooter)GameEngine.getEngine().screen;
		game.bubbleCount -= 2;
		if (game.bubbleCount <= 0) {
			JOptionPane.showMessageDialog(null, "You win!", "Winner!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	@GameObjectDraw
	public void onDraw(Game g) {
		g.pushMatrix();
		g.translate(collision.pos.getX() + 16, collision.pos.getY() + 16);
		//g.translate(collision.size.width/2, collision.size.height/2);
		g.rotate(PApplet.radians(System.currentTimeMillis() % 360));
		g.image(((BubbleShooter)g).textures[type], -16, -16);
		g.popMatrix();
	}

}
