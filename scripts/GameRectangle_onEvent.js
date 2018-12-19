function onEvent(evt) {
	if (evt.obj1 == obj.getGUID() && (evt.solid || evt.edge)) {
		obj.dir *= -1;
	}
}