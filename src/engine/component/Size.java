package engine.component;

import java.io.Serializable;

import engine.network.sync.SyncField;

public class Size extends Component implements Serializable {

	private static final long serialVersionUID = -889354032182195256L;

	@SyncField
	public int width;
	
	@SyncField
	public int height;
	
	public Size(int w, int h) {
		this.width = w;
		this.height = h;
	}
}
