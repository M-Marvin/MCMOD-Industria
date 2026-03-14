package de.m_marvin.industria.core.client.util.widgets;

import java.util.stream.Stream;

import de.m_marvin.industria.core.client.util.widgets.StatusBar.BarSegment;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractScaleWidget extends AbstractTextureWidget implements Tickable {
	
	protected float scale1;
	protected float scale2;
	protected float zero;
	protected float state;
	protected float displayState;
	protected boolean initialized = false;
	protected BarSegment[] segments;
	protected Component info;

	public AbstractScaleWidget(int x, int y, int width, int height, Component message, ResourceLocation texture) {
		super(x, y, width, height, message, texture);
		this.info = Component.empty();
	}
	
	public void setScale(float scale1, float scale2, float zero) {
		this.scale1 = scale1;
		this.scale2 = scale2;
		this.zero = zero;
	}
	
	@Override
	public void tick() {
		
		this.displayState += (this.state - this.displayState) * 0.1F;
		
	}
	
	public float getScale1() {
		return scale1;
	}
	
	public float getScale2() {
		return scale2;
	}
	
	public void setSegments(BarSegment... segments) {
		this.segments = Stream.of(segments).sorted((a, b) -> Float.compare(a.start(), b.start())).toArray(BarSegment[]::new);
	}
	
	public BarSegment[] getSegments() {
		return segments;
	}
	
	public float getZero() {
		return zero;
	}
	
	public void setState(float state) {
		this.state = state;
		if (!this.initialized) {
			this.displayState = this.state;
			this.initialized = true;
		}
	}
	
	public float getState() {
		return state;
	}
	
	public void setInfo(Component info) {
		this.info = info;
	}
	
	public Component getInfo() {
		return info;
	}
	
}
