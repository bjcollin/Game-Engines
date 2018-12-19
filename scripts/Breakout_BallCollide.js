function onEvent(evt) {
	if (evt.obj1 == obj.getGUID()) {
		obj1 = helper.getObjFromID(evt.obj1);
		if (evt.edge) {
			if (obj1.collision.pos.y >= 416) { //Bottom
				obj1.fail();
			} else if (obj.collision.pos.y <= 16) { //Top
				obj1.collision.vel.y *= -1;
			} else { //Sides
				obj1.collision.vel.x *= -1;
			}
		} else {
			obj2 = helper.getObjFromID(evt.obj2);
			if (helper.getObjType(evt.obj2) == "homework4.breakout.GameBrick" && !obj2.markForDelete) {
				obj2.markForDelete = true;
				obj1.breakBrick();
				obj1.collision.vel.y *= -1;
			} else if (helper.getObjType(evt.obj2) == "homework4.breakout.GamePaddle") {
				obj1.collision.vel.y *= -1;
				if (obj1.collision.pos.x + (obj1.collision.size.width/2) < obj2.collision.pos.x + (obj2.collision.size.width/4)) {
					obj1.bounceLeft();
					print("LEFT");
				} else if (obj1.collision.pos.x + (obj1.collision.size.width/2)> obj2.collision.pos.x + 3*(obj2.collision.size.width/4)) {
					obj1.bounceRight();
					print("RIGHT");
				}
			}
		}
	}
}