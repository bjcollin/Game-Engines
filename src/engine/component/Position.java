package engine.component;

import java.io.Serializable;

import engine.MathHelper;
import engine.network.sync.SyncField;

public class Position extends Component implements Serializable {
	
	private static final long serialVersionUID = 769046036773782775L;
	
	@SyncField
	public double x;
	@SyncField
	public double y;
	
	public Position() {
		
	}
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Position copy() {
		return new Position(x, y);
	}
	
	public boolean equals(Position other) {
		return x == other.x && y == other.y;
	}
	
	public int getX() {
		return MathHelper.floor(x);
	}
	
	public int getY() {
		return MathHelper.floor(y);
	}
}
