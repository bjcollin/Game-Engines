package homework4.bubble;

import java.awt.Color;

import engine.Game;
import engine.GameEngine;
import engine.KeyboardHelper;
import engine.MouseHelper;
import engine.component.GameObjectDraw;
import engine.component.Position;
import engine.event.EventHandler;
import engine.event.EventKeyboard;
import engine.network.sync.SyncField;
import engine.object.GameObject;

public class GameShooter extends GameObject {

	private static final long serialVersionUID = 3465582184934980778L;

	@SyncField
	public byte current;
	
	public Position pos;
	
	public GameShooter(int x, int y) {
		pos = new Position(x, y);
	}
	
	@EventHandler
	public void onKey(EventKeyboard evt) {
		if (GameEngine.getEngine().server != null && evt.release && evt.code == KeyboardHelper.SPACE) {
			GameBubble proj = new GameBubble(current, pos.getX(), pos.getY());
			proj.moving = true;
			MouseHelper mouse = GameEngine.getEngine().getControllerMouse(this);
			if (mouse != null) {
				double velx = mouse.x - pos.x;
				double vely = mouse.y - pos.y;
				double vlen = Math.sqrt(velx * velx + vely * vely);
				proj.collision.vel.x = (velx/vlen)*4;
				proj.collision.vel.y = (vely/vlen)*4;
				GameEngine.getEngine().addObject(proj);
				BubbleShooter game = (BubbleShooter)GameEngine.getEngine().screen;
				game.bubbleCount++;
				current = BubbleShooter.BubbleType.values()[BubbleShooter.rng.nextInt(4)].id;
				hasChanged = true;
			}
		}
	}
	
	@GameObjectDraw
	public void onDraw(Game g) {
		g.image(((BubbleShooter)g).textures[current], pos.getX(), pos.getY());
		
		MouseHelper mouse = GameEngine.getEngine().getControllerMouse(this);
		if (mouse != null) {
			double dy = mouse.y - pos.y;
			double dx = mouse.x - pos.x;
			double theta = Math.atan2(dy, dx);
			//theta -= PApplet.radians(90);
			g.pushMatrix();
			g.translate(pos.getX()+16, pos.getY());
			g.rotate((float)theta);
			
			g.stroke(Color.RED.getRGB());
			g.strokeWeight(4f);
			g.line(0, 0, 64, 0);
			
			g.popMatrix();
		}
	}
}
