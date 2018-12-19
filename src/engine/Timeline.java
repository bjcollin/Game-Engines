package engine;

public class Timeline {
	
	public Timeline anchor = null;
	
	public long tick = 0;
	public long lastTick = 0;
	public double scale = 1;
	public boolean paused = false;
	//private long pauseTicks = 0;
	
	private long lastTime; //Used for realTime only

	public Timeline(Timeline anch) {
		anchor = anch;
		if (anch == null) {
			lastTime = System.currentTimeMillis();
		}
		update(); //Set the initial time
	}
	
	public void update() {
		if (anchor != null) {
			long elapsed = (anchor.tick - anchor.lastTick);
			//System.out.println("Elapsed: "+elapsed+" anch="+anchor.tick);
			
			tick += (long)(elapsed * scale);
			lastTick = tick;
		} else {
			long curr = System.currentTimeMillis();
			long elapsed = curr - lastTime;
			//System.out.println("Realtime ticks: "+(elapsed*scale));
			double mult = scale > 1 ? 1.6 * scale : scale;
			if (!paused) tick += Math.floor(elapsed * mult);
			//else pauseTicks += (elapsed * scale);
			lastTime = curr;
		}
	}

}
