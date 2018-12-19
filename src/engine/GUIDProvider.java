package engine;

public class GUIDProvider {
	
	private static long nextGUID = 1;
	
	public static void assignGUID(IHasGUID obj) {
		obj.assignGUID(nextGUID);
		nextGUID++;
	}
}
