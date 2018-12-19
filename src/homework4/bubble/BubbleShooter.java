package homework4.bubble;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import engine.Game;
import engine.GameEngine;
import engine.event.EventHandler;
import engine.network.ServerClient;
import engine.object.GameObject;
import processing.core.PImage;
import processing.event.KeyEvent;

public class BubbleShooter extends Game {
	
	public static final int ROWS = 15;
	public static final int COLS = 20;
	
	public PImage textures[] = new PImage[4];
	
	public static Random rng = new Random();
	
	public int bubbleCount = 0;
	
	public enum BubbleType {
		AIR(0),
		WATER(1),
		EARTH(2),
		FIRE(3);
		
		public byte id;
		private BubbleType(int b) {
			id = (byte)b;
		}
	}

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
		textures[0] = loadImage("sprites/Air.png");
		textures[1] = loadImage("sprites/Water.png");
		textures[2] = loadImage("sprites/Earth.png");
		textures[3] = loadImage("sprites/Fire.png");
	}
	
	@Override
	public void initGameObjects() {
		loadTextures();
		
		engine.addObject(new GameGrid());
		BubbleType firstRow[] = new BubbleType[COLS];
		BubbleType last = null;
		for (int r = 0; r<2; r++) {
			for (int c = 0; c<COLS; c++) {
				BubbleType above = (r == 0 ? null : firstRow[c]);
				BubbleType type = getTypeExcluding(last, above);
				GameBubble bub = new GameBubble(type, c*32, r*32);
				engine.addObject(bub);
				last = type;
				if (r == 0) firstRow[c] = type;
				bubbleCount++;
			}
		}
		EventBubbleDown bd = new EventBubbleDown();
		bd.timestep += 15000;
		GameEngine.getEngine().raiseEvent(bd);
	}
	
	@Override
	public GameObject onClientConnect(ServerClient sc) {
		return new GameShooter(320+(32*sc.clientId), 448);
	}
	
	public static void main(String[] args) {
		BubbleShooter game = new BubbleShooter();
		game.startApplet();
	}
	
	@EventHandler
	public void onBubbleDown(EventBubbleDown evt) {
		EventBubbleDown bd = new EventBubbleDown();
		bd.timestep += 15000;
		GameEngine.getEngine().raiseEvent(bd); //Raise a new event
	}
	
	public static void setToGrid(GameBubble bub) {
		bub.collision.pos.x -= bub.collision.pos.x % 32;
		bub.collision.pos.y -= bub.collision.pos.y % 32;
		if (bub.collision.pos.y >= (ROWS-1)*32) {
			JOptionPane.showMessageDialog(null, "Game Over!", "Game Over", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	private BubbleType getTypeExcluding(BubbleType excl, BubbleType excl2) {
		ArrayList<BubbleType> valid = new ArrayList<BubbleType>();
		for (BubbleType t : BubbleType.values()) {
			if (t != excl && t != excl2) {
				valid.add(t);
			}
		}
		return valid.get(rng.nextInt(valid.size()));
	}
	
	/*@Override
	public void mouseClicked(MouseEvent event) {
		super.mouseClicked(event);
		if (GameEngine.getEngine().server!= null && event.getButton() == LEFT) {
			GameBubble bub = new GameBubble(BubbleType.AIR, event.getX(), event.getY());
			setToGrid(bub);
			GameEngine.getEngine().addObject(bub);
		}
	}*/
}
