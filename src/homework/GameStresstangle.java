package homework;

import java.awt.Color;

import engine.DrawHelper;
import engine.Game;
import engine.component.GameObjectDraw;
import engine.component.GameObjectOnTick;
import engine.component.Position;
import engine.object.GameObject;

public class GameStresstangle extends GameObject { //Used to stress-test the game engine
	
	private static final long serialVersionUID = -1912253729375573294L;

	public Color color;
	public int dir = 1;
	
	public Position pos;
	
	public int width;
	public int height;

	public GameStresstangle() {
		pos = new Position(0,0);
		color = Color.RED;
	}
	public GameStresstangle(int x, int y, int w, int h, Color col) {
		this.width = w;
		this.height = h;
		this.color = col;
		this.pos = new Position(x,y);
	}
	
	@GameObjectOnTick
	public void onTick(Game g) {
		pos.x += dir*5;
		dir *= -1;
		hasChanged = true; //Muhahahah
	}

	@GameObjectDraw
	public void onDraw(Game g) {
		g.fill(color.getRGB());
		DrawHelper.drawRectangle(g, pos.getX(), pos.getY(), width, height);
	}
}
 