package engine.component;

import java.io.Serializable;

import engine.network.sync.SyncField;

public class CollisionData extends Component implements Serializable {
	
	private static final long serialVersionUID = -5186690186205960700L;
	
	public boolean onGround;
	public boolean gravity = true;
	
	public boolean solid = true;
	public boolean stopOnCollide = true;
	
	@SyncField
	public Position pos;
	
	@SyncField
	public Velocity vel;
	
	public Size size;
	
	public CollisionData() {
		pos = new Position(0,0);
		vel = new Velocity(0,0);
		size = new Size(0,0);
	}
}
