package homework4.breakout;

import java.awt.Rectangle;

import javax.swing.JOptionPane;

import engine.Game;
import engine.GameEngine;
import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.GameObjectDraw;
import engine.event.EventCollision;
import engine.network.sync.SyncField;
import engine.object.GameObject;
import engine.script.ScriptEventHandler;

@ScriptEventHandler(file = "Breakout_BallCollide", eventType = EventCollision.class)
public class GameBall extends GameObject {

	private static final long serialVersionUID = 9051910814123756388L;
	
	@SyncField
	@GameObjectCollisionData
	public CollisionData collision;
	
	public GameBall(int x, int y) {
		collision = new CollisionData();
		collision.gravity = false;
		collision.pos.x = x;
		collision.pos.y = y;
		collision.size.width = 32;
		collision.size.height = 32;
		collision.stopOnCollide = false;
	}

	
	/*@EventHandler
	public void onCollide(EventCollision evt) {
		if (evt.obj1 == getGUID()) {
			GameBall obj1 = (GameBubble)GameEngine.getEngine().getObjectFromGUID(evt.obj1);
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
		return new Rectangle(collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
	}
	
	public void fail() {
		JOptionPane.showMessageDialog(null, "Game Over!", "Game Over", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	
	public void breakBrick() {
		Breakout game = (Breakout)GameEngine.getEngine().screen;
		game.numBricks -= 1;
		if (game.numBricks <= 0) {
			JOptionPane.showMessageDialog(null, "You win!", "Winner!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void bounceLeft() {
		if (collision.vel.x > 0) collision.vel.x *= -1;
		else collision.vel.x = Math.max(-2, collision.vel.x - 1);
	}
	
	public void bounceRight() {
		if (collision.vel.x < 0) collision.vel.x *= -1;
		else collision.vel.x = Math.min(2, collision.vel.x + 1);
	}
	
	@GameObjectDraw
	public void onDraw(Game g) {
		g.image(((Breakout)g).ballTexture, collision.pos.getX(), collision.pos.getY());
	}

}
