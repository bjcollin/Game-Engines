package homework4.bubble;

import java.awt.Color;

import engine.Game;
import engine.component.GameObjectDraw;
import engine.object.GameObject;

public class GameGrid extends GameObject {

	private static final long serialVersionUID = 1834801822689596746L;
	
	@GameObjectDraw
	public void drawGrid(Game g) {
		g.stroke(Color.LIGHT_GRAY.getRGB());
		g.strokeWeight(1f);
		for (int x = 32; x<640; x+=32) {
			g.line(x, 0, x, 480);
		}
		for (int y = 32; y<480; y+=32) {
			g.line(0, y, 640, y);
		}
	}
}
