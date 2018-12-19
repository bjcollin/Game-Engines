package engine.event;

import engine.AnnotationHelper;
import engine.object.GameObject;

public class EventCollision extends Event {
	
	public long obj1;
	public long obj2;
	public boolean solid;
	public boolean edge;

	public EventCollision(GameObject obj1, GameObject obj2) {
		this.obj1 = obj1.getGUID();
		if (obj2 != null) {
			this.obj2 = obj2.getGUID();
			solid = AnnotationHelper.getCollisionData(obj2).solid;
		}
		edge = (obj2 == null);
	}
	
	private EventCollision(long g1, long g2, boolean s, boolean e) {
		obj1 = g1;
		obj2 = g2;
		solid = s;
	}
	
	@Override
	public Event clone() {
		Event clone = new EventCollision(obj1, obj2, solid, edge);
		clone.copyDefaultData(this);
		return clone;
	}
}
