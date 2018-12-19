function onTick() {
	if (obj.type.id == 1) {
		obj.collision.vel.x = 2 * obj.dir;
	} else {
		obj.collision.vel.y = 2 * obj.dir;
	}
}