package homework2.alternate_network;

import java.awt.Color;

import javax.swing.JOptionPane;

import engine.Game;
import engine.object.GameSpawn;
import engine.object.MessageDisplayer;
import homework.GameDeathZone;
import homework.GameMovingPlatform;
import homework.GameMovingPlatform.PlatformType;
import homework.GameRectangle;
import processing.event.KeyEvent;

public class Homework2GameAlt extends Game {
	
	
	@Override
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);
		if (event.getKey() == 's') {
			initGameObjects();
			System.out.println("Alternative Server Started");
			MessageDisplayer msg = new MessageDisplayer(1, 1, this);
			msg.addMessage("Alternative Server Started");
			engine.addObject(msg);
			ServerAlt server = new ServerAlt(msg);
			engine.server = server;
			Thread t = new Thread(server);
			t.setName("Server: Socket accept thread");
			t.start();
		} else if (event.getKey() == 'c') {
			System.out.println("Alternative Client Started");
			String name = JOptionPane.showInputDialog(null, "What is your name?", "Player Name", JOptionPane.QUESTION_MESSAGE);
			ClientAlt client = new ClientAlt(name);
			Thread t = new Thread(client);
			engine.client = client;
			t.setName("Client: read");
			t.start();
		}
	}
	
	@Override
	public void initGameObjects() {
		GameRectangle sq2 = new GameRectangle(320, 10, 100, 50, Color.PINK);
		engine.addObject(sq2);
	
		GameRectangle sq3 = new GameRectangle(600, 250, 50, 100, Color.GREEN);
		sq3.collision.gravity = false;
		engine.addObject(sq3);
		
		GameMovingPlatform pl = new GameMovingPlatform(0, 250, 100, 50, Color.BLUE, PlatformType.HORIZONTAL);
		engine.addObject(pl);
		
		GameMovingPlatform p2 = new GameMovingPlatform(480, 190, 100, 50, Color.YELLOW, PlatformType.VERTICAL);
		engine.addObject(p2);
		
		engine.addSpawnPoint(new GameSpawn(200,1));
		engine.addSpawnPoint(new GameSpawn(1,1));
		engine.addSpawnPoint(new GameSpawn(300,1));
		
		engine.addObject(new GameDeathZone(600, 400, 100, 100));
		/*
		for (int i=1; i<600; i+=3) {
			engine.addObject(new GameStresstangle(i, 1, 2, 2, Color.CYAN));
		}
		for (int i=1; i<600; i+=3) {
			engine.addObject(new GameStresstangle(i, 5, 2, 2, Color.CYAN));
		}
		for (int i=1; i<600; i+=3) {
			engine.addObject(new GameStresstangle(i, 8, 2, 2, Color.CYAN));
		}*/
		
		/*engine.addSpawnPoint(new GameSpawn(1,1)); //Used for stress testing
		
		for (int i=0; i<500; i++) {
			engine.addObject(new GameStresstangle(i, 300, 2, 2, Color.CYAN));
		}*/
		
	}
	
	public static void main(String[] args) {
		Homework2GameAlt game = new Homework2GameAlt();
		game.startApplet();
	}
}
