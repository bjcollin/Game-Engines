package homework4.breakout;

import java.awt.Color;
import java.awt.Rectangle;

import engine.DrawHelper;
import engine.Game;
import engine.GameEngine;
import engine.KeyboardHelper;
import engine.MouseHelper;
import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.GameObjectDraw;
import engine.component.GameObjectOnTick;
import engine.event.EventHandler;
import engine.event.EventKeyboard;
import engine.network.sync.SyncField;
import engine.object.GameObject;

public class GamePaddle extends GameObject {
	
	private static final long serialVersionUID = 9127973413592774966L;
	
	@SyncField
	@GameObjectCollisionData
	public CollisionData collision;
	
	public Color color;

	@SyncField
	public boolean canFireBall = true; //is the ball able to be fired?
	
	public GamePaddle(double x, double y, Color c) {
		collision = new CollisionData();
		collision.gravity = false;
		collision.pos.x = x;
		collision.pos.y = y;
		collision.size.width = 64;
		collision.size.height = 32;
		color = c;
	}
	
	@EventHandler
	public void onKey(EventKeyboard evt) {
		if (GameEngine.getEngine().server != null && evt.release && evt.code == KeyboardHelper.SPACE && canFireBall) {
			GameBall proj = new GameBall(collision.pos.getX() + collision.size.width/4, collision.pos.getY() - 32);
			proj.collision.vel.x = Breakout.rng.nextInt(5) - 2;
			proj.collision.vel.y = -2;
			GameEngine.getEngine().addObject(proj);
			canFireBall = false;
			hasChanged = true;
		}
	}
	
	@GameObjectDraw
	public void onDraw(Game g) {
		g.fill(color.getRGB());
		DrawHelper.drawRectangle(g, collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
		g.stroke(Color.BLACK.getRGB());
		g.strokeWeight(1.0f);
		g.line(collision.pos.getX(), collision.pos.getY(), collision.pos.getX(), collision.pos.getY()+collision.size.height); //Left
		g.line(collision.pos.getX(), collision.pos.getY(), collision.pos.getX()+collision.size.width, collision.pos.getY()); //Top
		g.line(collision.pos.getX()+collision.size.width, collision.pos.getY(), collision.pos.getX()+collision.size.width, collision.pos.getY()+collision.size.height); //Right
		g.line(collision.pos.getX(), collision.pos.getY()+collision.size.height, collision.pos.getX()+collision.size.width, collision.pos.getY()+collision.size.height); //Bottom
		
		if (canFireBall) {
			g.image(((Breakout)g).ballTexture, collision.pos.getX()+collision.size.width/4, collision.pos.getY() - 32);
		}
	}
	
	@GameObjectBoundingBox
	public Rectangle getBoundingBox() {
		return new Rectangle(collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
	}
	
	@GameObjectOnTick
	public void onTick(Game g) {
		double startx = collision.pos.x;
		MouseHelper mouse = GameEngine.getEngine().getControllerMouse(this);
		int newx = mouse.x - collision.size.width/2;
		newx = Math.min(newx, g.width - collision.size.width);
		newx = Math.max(0, newx);
		collision.pos.x = newx;
		if (Math.abs(startx-newx) >= 1) {
			hasChanged = true;
		}
	}

}
