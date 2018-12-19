package homework;

import java.awt.Color;
import java.util.Random;

import engine.Game;
import engine.GameEngine;
import engine.network.ServerClient;
import engine.object.GameObject;
import engine.object.GameSpawn;
import homework.GameMovingPlatform.PlatformType;
import processing.event.KeyEvent;

public class Homework2Game extends Game {
	
	@Override
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);
		if (event.getKey() == 's') {
			engine.startServer();
		} else if (event.getKey() == 'c') {
			engine.startClient();
		}
	}
	
	@Override
	public void initGameObjects() {
		GameRectangle sq2 = new GameRectangle(320, 1, 100, 50, Color.PINK);
		engine.addObject(sq2);
	
		GameRectangle sq3 = new GameRectangle(600, 250, 50, 100, Color.GREEN);
		sq3.collision.gravity = false;
		engine.addObject(sq3);
		
		GameRectangle sq4 = new GameRectangle(0, 250, 50, 100, Color.ORANGE);
		sq4.collision.gravity = false;
		engine.addObject(sq4);
		
		GameMovingPlatform pl = new GameMovingPlatform(51, 250, 100, 50, Color.BLUE, PlatformType.HORIZONTAL);
		engine.addObject(pl);
		
		GameMovingPlatform p2 = new GameMovingPlatform(480, 190, 100, 50, Color.YELLOW, PlatformType.VERTICAL);
		engine.addObject(p2);
		System.out.println(pl.getGUID());
		
		engine.addSpawnPoint(new GameSpawn(200,1));
		engine.addSpawnPoint(new GameSpawn(1,1));
		engine.addSpawnPoint(new GameSpawn(300,1));
		
		engine.addObject(new GameDeathZone(650, 400, 50, 100));
		
		/*for (int i=1; i<600; i+=3) {
			engine.addObject(new GameStresstangle(i, 1, 2, 2, Color.CYAN));
		}
		for (int i=1; i<600; i+=3) {
			engine.addObject(new GameStresstangle(i, 5, 2, 2, Color.CYAN));
		}
		for (int i=1; i<600; i+=3) {
			engine.addObject(new GameStresstangle(i, 8, 2, 2, Color.CYAN));
		}*/
		
		/*engine.addSpawnPoint(new GameSpawn(1,1)); //Used for stress testing
		
		for (int i=0; i<100; i++) {
			engine.addObject(new GameStresstangle(i, 300, 2, 2, Color.CYAN));
		}*/
	}
	
	public GameObject onClientConnect(ServerClient newClient) {
		Random rng = new Random();
		Color c = new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
		GameRectangle player = new GameRectangle(1, 1, 100, 100, c);
		player.controllable = true;
		GameEngine.getEngine().setPosToSpawn(player, GameEngine.getEngine().getRandomSpawn());
		return player;
	}
	
	
	public static void main(String[] args) {
		Homework2Game game = new Homework2Game();
		game.startApplet();
	}
}
