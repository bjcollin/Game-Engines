package engine.object;

import java.io.Serializable;

import engine.GUIDProvider;
import engine.IHasGUID;

public class GameObject implements IHasGUID, Serializable {

	private static final long serialVersionUID = -8342503957472827797L;
	
	public long GUID;
	public boolean hasChanged = false;
	public boolean markForDelete = false;
	
	public GameObject() {
		GUIDProvider.assignGUID(this);
	}

	@Override
	public long getGUID() {
		return GUID;
	}

	@Override
	public void assignGUID(long guid) {
		GUID = guid;
	}
	
	public void markChanged() {
		hasChanged = true;
	}
	
	public GameObject clone() {
		return null;
	}
}

