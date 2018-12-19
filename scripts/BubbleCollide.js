function onEvent(evt) {
	if (evt.obj1 == obj.getGUID()) {
		obj1 = helper.getObjFromID(evt.obj1);
		if (evt.edge) { //Wall
			obj1.collision.vel.x *= -1;
			if (obj1.collision.pos.y < 32) {
				obj1.stop();
			}
		} else {
			//print("collide!");
			if (obj1.type == helper.getObjFromID(evt.obj2).type) {
				//print("popped!");
				obj1.markForDelete = true;
				obj1.stop();
				helper.getObjFromID(evt.obj2).markForDelete = true;
				obj.score();
			} else {
				obj1.stop();
			}
		}
	}
}