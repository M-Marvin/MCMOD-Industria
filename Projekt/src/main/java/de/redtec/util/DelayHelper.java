package de.redtec.util;

public class DelayHelper {
	
	protected Task task;
	protected int delay;
	protected Thread thread;
	
	protected DelayHelper(Task task, int delay) {
		this.task = task;
		this.delay = delay;
	}
	
	protected void start() {
		this.thread = new Thread("timer_thread") {
			public void run() {
				try {
					Thread.sleep(DelayHelper.this.delay);
				} catch (InterruptedException e) {
					System.out.println("Interrupt Exception in Delayed Task!");
					e.printStackTrace();
				}
				try {
					DelayHelper.this.task.execute();
				} catch (Exception e) {
					System.out.println("Exception in Delayed Task!");
					e.printStackTrace();
				}
			};
		};
		this.thread.start();
	}
		
	public static void executeDelayed(Task executeTask, int delay) {
		new DelayHelper(executeTask, delay).start();
	}
	
	public static interface Task {
		public void execute();
	}
	
}
