package de.industria.util.handler;

public class MathHelper {
	
	public static double castBounds(double bounds, double value) {
		if (bounds < 0) bounds = -bounds;
		return Math.min(bounds, Math.max(-bounds, value));
	}
	
}
