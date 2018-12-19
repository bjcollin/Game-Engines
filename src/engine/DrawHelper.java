package engine;

public class DrawHelper {

	public static void drawSquare(Game g, int x, int y, int sz) {
		drawRectangle(g, x, y, sz, sz);
	}
	
	public static void drawRectangle(Game g, int x, int y, int w, int h) {
		g.quad(x, y, x, y+h, x+w, y+h, x+w, y);
	}
}
