package engine.object;

import engine.component.Position;

public class GameSpawn extends GameObject {

	private static final long serialVersionUID = 1008860986494167331L;
	
	public Position pos = new Position();
	
	public GameSpawn(double x, double y) {
		pos.x = x;
		pos.y = y;
	}
	
	public GameObject clone() {
		return new GameSpawn(pos.x, pos.y);
	}
	
	/*@GameObjectDraw
	public void onDraw(Game g) { //For debug purposes
		java.awt.Color c = new java.awt.Color(0, 0, 255, 100);
		g.fill(c.getRGB());
		DrawHelper.drawRectangle(g, pos.x, pos.y, 20, 20);
	}*/
}
