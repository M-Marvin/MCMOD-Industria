package de.industria.util.types;

public class ScannCounter {
	
	protected int maximum;
	protected int counter;
	
	public ScannCounter(int max) {
		this.maximum = max;
	}
	
	public void count() {
		this.counter++;
	}
	
	public boolean canRun() {
		return this.counter <= maximum;
	}
	
}
