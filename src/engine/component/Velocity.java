package engine.component;

import java.io.Serializable;

import engine.network.sync.SyncField;

public class Velocity extends Component implements Serializable {

	private static final long serialVersionUID = 4723672177882790126L;

	@SyncField
	public double x;
	
	@SyncField
	public double y;
	
	public Velocity(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
