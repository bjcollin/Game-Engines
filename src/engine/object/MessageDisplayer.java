package engine.object;

import java.awt.Color;
import java.util.ArrayList;

import engine.DrawHelper;
import engine.Game;
import engine.component.GameObjectDraw;
import engine.component.Position;
import processing.core.PFont;

public class MessageDisplayer extends GameObject {
	
	private static final long serialVersionUID = -5998341508385357100L;

	public Position pos;
	
	private ArrayList<String> messages = new ArrayList<String>();
	private static final int MAX_MESSAGES = 10;
	private static final Color BACKGROUND_COLOR = new Color(127, 127, 127, 127);
	private static PFont font;
	
	public MessageDisplayer(double x, double y, Game g) {
		super();
		pos = new Position(x, y);
		if (font == null) font = g.createFont("lucida console", 16);
	}
	
	private MessageDisplayer(double x, double y) {
		pos = new Position(x, y);
	}
	
	public void addMessage(String m) {
		if (messages.size() == MAX_MESSAGES) {
			messages.remove(0);
		}
		messages.add(m);
	}

	@GameObjectDraw
	public void drawMessageBox(Game g) {
		g.fill(BACKGROUND_COLOR.getRGB());
		g.stroke(Color.BLACK.getRGB());
		DrawHelper.drawRectangle(g, pos.getX(), pos.getY(), 440, 160);
		
		g.fill(Color.WHITE.getRGB());
		g.textFont(font);
		g.textAlign(PFont.LEFT, PFont.TOP);
		for (int i=0; i<messages.size(); i++) {
			g.text(messages.get(i), pos.getX() + 4, pos.getY() + (16*i) + 4);
		}
	}
	
	public GameObject clone() {
		return new MessageDisplayer(pos.x, pos.y);
	}
}
