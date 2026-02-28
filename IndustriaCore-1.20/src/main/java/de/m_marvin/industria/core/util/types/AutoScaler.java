package de.m_marvin.industria.core.util.types;

import de.m_marvin.industria.core.client.util.widgets.AbstractScaleWidget;
import de.m_marvin.industria.core.client.util.widgets.StatusBar.BarSegment;
import de.m_marvin.industria.core.parametrics.BlockParametrics;

public class AutoScaler {
	
	private final int magnitude;
	private final float offset;
	private final float range;
	private final float scala1;
	private final float scala2;
	private final float zero;
	
	public AutoScaler(float minValue, float maxValue, boolean zeroAligned) {
		if (minValue > maxValue)
			throw new IllegalArgumentException("minValue > maxValue");

		this.offset = -minValue;
		
		if (zeroAligned) {

			this.magnitude = Math.max(autoRangeMagnitude(Math.abs(minValue)), autoRangeMagnitude(Math.abs(maxValue)));
			this.range = (Math.max(maxValue, 0F) - Math.min(minValue, 0F)) / (float) Math.pow(10F, this.magnitude);

			this.zero = minValue < 0 ? getScaleValue(-minValue) : 0F;
					
		} else {
			
			this.magnitude = autoRangeMagnitude(Math.abs(maxValue) - Math.abs(minValue));
			
			this.range = (maxValue - minValue) / (float) Math.pow(10F, this.magnitude);

			this.zero = 0F;
			
		}
		
		float rd = this.range;
		while (rd > 10) rd /= 10;
		while (rd < 1) rd *= 10;
		
		this.scala1 = rd;
		this.scala2 = this.scala1 > 5 ? 0 : 5;
		
	}
	
	public static AutoScaler autoRangeMeter(AbstractScaleWidget meter, float minValue, float maxValue, boolean zeroAligned) {
		float range = (zeroAligned ? Math.max(maxValue, 0) : maxValue) - (zeroAligned ? Math.min(minValue, 0) : minValue);
		float limitLow = minValue == 0.0F ? minValue : minValue - range * 0.1F;
		float limitHigh = maxValue == 0.0F ? maxValue : maxValue + range * 0.1F;
		AutoScaler scaler =  new AutoScaler(limitLow, limitHigh, zeroAligned);
		meter.setScale(scaler.getScala1(), scaler.getScala2(), scaler.getZero());
		float fl = (float) scaler.getScaleValue(minValue);
		float fh = (float) scaler.getScaleValue(maxValue);
		meter.setSegments(
				new BarSegment(0F, fl, 1F, 1F, 0F),
				new BarSegment(fl, fh, 0F, 1F, 0F),
				new BarSegment(fh, 1F, 1F, 0F, 0F)
		);
		return scaler;
	}
	
	public static AutoScaler autoRangeVoltMeter(AbstractScaleWidget meter, BlockParametrics parametrics) {
		return autoRangeMeter(meter, parametrics.getVoltageMin(), parametrics.getVoltageMax(), true);
	}
	
	public static AutoScaler autoRangePowerMeter(AbstractScaleWidget meter, BlockParametrics parametrics) {
		return autoRangeMeter(meter, parametrics.getPowerMin(), parametrics.getPowerMax(), true);
	}
	
	public float getRange() {
		return range;
	}

	public float getOffset() {
		return offset;
	}
	
	public int getMagnitude() {
		return magnitude;
	}
	
	public float getMagnitudeFactor() {
		return (float) Math.pow(10, -this.magnitude);
	}
	
	public char getMagnitudeSymbol() {
		switch (this.magnitude) {
			case -15: return 'f';
			case -12: return 'p';
			case -9: return 'n';
			case -6: return 'u';
			case -3: return 'm';
			case 0: return ' ';
			case +3: return 'k';
			case +6: return 'M';
			case +9: return 'G';
			case +12: return 'T';
			case +15: return 'P';
			default: return '#';
		}
	}
	
	public float getScala1() {
		return scala1;
	}
	
	public float getScala2() {
		return scala2;
	}
	
	public float getZero() {
		return zero;
	}
	
	public float getScaleValue(float value) {
		return Math.max(0F, Math.min(1F, (value - this.offset) * getMagnitudeFactor() / this.range));
	}
	
	public static int autoRangeMagnitude(double maxValue) {
		int i = 0;
		while (maxValue / Math.pow(10, i) > 1500.0 && i < +15)
			i += 3;
		while (maxValue / Math.pow(10, i) < 0.0015 && i > -15)
			i -= 3;
		return i;
	}
	
	public static float autoMultiplier(float range) {

		float rd = range;
		while (rd > 10) rd /= 10;
		while (rd < 1) rd *= 10;
		return rd / range;
		
	}
	
//	public static int[] autoRangeScale(double range) {
//		
//		int r = (int) Math.ceil(rd);
//		
//		return new int[] { r, 5 };
//		
//	}
	
}
