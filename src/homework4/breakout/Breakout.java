package homework4.breakout;

import java.awt.Color;
import java.util.Random;

import engine.Game;
import engine.GameEngine;
import engine.network.ServerClient;
import engine.object.GameObject;
import homework4.bubble.EventBubbleDown;
import processing.core.PImage;
import processing.event.KeyEvent;

public class Breakout extends Game {
	
	public static final int ROWS = 15;
	public static final int COLS = 20;
	
	public PImage ballTexture;
	
	public static Random rng = new Random();
	
	public int numBricks = 0;
	
	@Override
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);
		if (event.getKey() == 's') {
			if (engine.server != null) throw new IllegalStateException();
			engine.startServer();
		} else if (event.getKey() == 'c') {
			loadTextures();
			engine.startClient();
		}
	}
	
	private void loadTextures() {
		ballTexture = loadImage("sprites/None.png");
	}
	
	@Override
	public void initGameObjects() {
		loadTextures();
		
		//engine.addObject(new GameGrid());

		for (int r = 0; r<4; r++) {
			for (int c = 0; c<COLS; c++) {
				int sz = (c < COLS-1 ? rng.nextInt(2) + 1 : 1);
				engine.addObject(new GameBrick(c*32, r*32, 32*sz, 32, getRandomColor()));
				numBricks++;
				if (sz == 2) c++;
			}
		}
		EventBubbleDown bd = new EventBubbleDown();
		bd.timestep += 15000;
		GameEngine.getEngine().raiseEvent(bd);
	}
	
	@Override
	public GameObject onClientConnect(ServerClient sc) {
		return new GamePaddle(320+(32*sc.clientId), 416, getRandomColor());
	}
	
	public static void main(String[] args) {
		Breakout game = new Breakout();
		game.startApplet();
	}
	
	private Color getRandomColor() {
		return new Color(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
	}
}
