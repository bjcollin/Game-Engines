package engine;

import engine.event.Event;

public class Debug {

	private static final boolean DEBUG_MODE = true;
	public static final boolean CLIENT_EVENT_HANDLING = true;
	
	public static void log(String msg) {
		if (DEBUG_MODE) {
			System.out.println(msg);
		}
	}
	
	public static void err(String msg) {
		if (DEBUG_MODE) {
			System.err.println(msg);
		}
	}
	
	public static void main(String[] args) {
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				testTime();				
			}
		}).start();*/
		System.out.println(Event.class.isAssignableFrom(Event.class));
	}
	
	@SuppressWarnings("unused")
	private static void testTime() {
		long start = System.currentTimeMillis();
		long time = start;
		int loop = 0;
		while (time < start + 2000) {
			long nextStep = time + 100;
			loop++;
			System.out.println(loop);
			time = System.currentTimeMillis();
			try {
				Thread.sleep(nextStep - time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}