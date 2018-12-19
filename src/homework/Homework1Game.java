package homework;

import java.awt.Color;
import java.util.Random;

import engine.Game;
import processing.event.KeyEvent;

public class Homework1Game extends Game {
	
	public GameRectangle sq;
	
	@Override
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);
		if (event.getKey() == 's') {
			engine.startServer();
		} else if (event.getKey() == 'c') {
			sq.controllable = false;
			engine.startClient();
		}
	}
	
	@Override
	public void initGameObjects() {
		Random rng = new Random();
		Color c = new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
		sq = new GameRectangle(1, 1, 100, 100, c);
		sq.controllable = true;
		engine.addObject(sq);
		
		
		GameRectangle sq2 = new GameRectangle(320, 1, 100, 50, Color.PINK);
		engine.addObject(sq2);
	
		GameRectangle sq3 = new GameRectangle(600, 250, 50, 100, Color.GREEN);
		sq3.collision.gravity = false;
		engine.addObject(sq3);
		
	}
	
	public static void main(String[] args) {
		Homework1Game game = new Homework1Game();
		game.startApplet();
	}
}
