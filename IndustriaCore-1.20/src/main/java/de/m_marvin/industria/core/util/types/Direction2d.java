package de.m_marvin.industria.core.util.types;

public enum Direction2d {
	
	UP(0, 1),
	DOWN(0, -1),
	LEFT(1, 0),
	RIGHT(-1, 0);
	
	private final int stepX;
	private final int stepY;
	
	private Direction2d(int stepX, int stepY) {
		this.stepX = stepX;
		this.stepY = stepY;
	}
	
	public int getStepX() {
		return stepX;
	}

	public int getStepY() {
		return stepY;
	}
	
}
