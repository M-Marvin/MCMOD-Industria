package de.industria.util.handler;

public class MathHelper {
	
	public static double castBounds(double bounds, double value) {
		if (bounds < 0) bounds = -bounds;
		return Math.min(bounds, Math.max(-bounds, value));
	}
	
	public static double makePositive(double value) {
		return value < 0 ? -value : value;
	}
	
	public static double getDistance(double value1, double value2) {
		return Math.max(value1, value2) - Math.min(value1, value2);
	}
	
}
