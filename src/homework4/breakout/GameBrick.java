package homework4.breakout;

import java.awt.Color;
import java.awt.Rectangle;

import engine.DrawHelper;
import engine.Game;
import engine.component.CollisionData;
import engine.component.GameObjectBoundingBox;
import engine.component.GameObjectCollisionData;
import engine.component.GameObjectDraw;
import engine.component.Position;
import engine.component.Velocity;
import engine.network.sync.SyncField;
import engine.object.GameObject;

public class GameBrick extends GameObject {

	private static final long serialVersionUID = -8315472865471012478L;

	@SyncField
	public Color color;
	
	@SyncField
	@GameObjectCollisionData
	public CollisionData collision;
	
	
	public GameBrick(double x, double y, int w, int h, Color col) {
		this.collision = new CollisionData();
		this.collision.size.width = w;
		this.collision.size.height = h;
		this.color = col;
		this.collision.pos = new Position(x,y);
		this.collision.vel = new Velocity(0,0);
		this.collision.gravity = false;
	}

	@GameObjectDraw
	public void onDraw(Game g) {
		g.fill(color.getRGB());
		DrawHelper.drawRectangle(g, collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
		g.stroke(Color.BLACK.getRGB());
		g.strokeWeight(1.0f);
		g.line(collision.pos.getX(), collision.pos.getY(), collision.pos.getX(), collision.pos.getY()+collision.size.height); //Left
		g.line(collision.pos.getX(), collision.pos.getY(), collision.pos.getX()+collision.size.width, collision.pos.getY()); //Top
		g.line(collision.pos.getX()+collision.size.width, collision.pos.getY(), collision.pos.getX()+collision.size.width, collision.pos.getY()+collision.size.height); //Right
		g.line(collision.pos.getX(), collision.pos.getY()+collision.size.height, collision.pos.getX()+collision.size.width, collision.pos.getY()+collision.size.height); //Bottom
	}
	
	@GameObjectBoundingBox
	public Rectangle getBoundingBox() {
		return new Rectangle(collision.pos.getX(), collision.pos.getY(), collision.size.width, collision.size.height);
	}

	public GameObject clone() {
		GameBrick result = new GameBrick(collision.pos.x, collision.pos.y, collision.size.width, collision.size.height, color);
		result.GUID = GUID;
		result.collision.vel = new Velocity(collision.vel.x, collision.vel.y);
		result.collision.gravity = collision.gravity;
		return result;
	}
}
 