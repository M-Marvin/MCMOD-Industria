package de.m_marvin.industria.core.util.types;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.StringRepresentable;

public enum PlanarDirection implements StringRepresentable {
	
	Y_POS(	"y_positive",	PlanarDirection.PlanarAxis.Y,	new Vec2i(+1, 0),	1,	1, 90),
	Y_NEG(	"y_negative",	PlanarDirection.PlanarAxis.Y,	new Vec2i(-1, 0),	0,	0, -90),
	X_POS(	"x_positive",	PlanarDirection.PlanarAxis.X,	new Vec2i(0, +1),	3,	2, 0),
	X_NEG(	"x_negative",	PlanarDirection.PlanarAxis.X,	new Vec2i(0, -1),	2,	3, 180);
	
	private final String name;
	private final Vec2i normal;
	private final int data2d;
	private final int oposite;
	private final PlanarDirection.PlanarAxis axis;
	private final int angle;

	private static final PlanarDirection[] BY_2D_DATA = Stream.of(values())
			.sorted(Comparator.comparingInt(PlanarDirection::get2DDataValue))
			.toArray(PlanarDirection[]::new);
	
	private PlanarDirection(String name, PlanarDirection.PlanarAxis axis, Vec2i normal, int data2d, int oposite, int angle) {
		this.name = name;
		this.normal = normal;
		this.data2d = data2d;
		this.oposite = oposite;
		this.axis = axis;
		this.angle = angle;
	}
	
	public int get2DDataValue() {
		return data2d;
	}
	
	int getAngleFromPositiveX() {
		return this.angle;
	}
	
	public static PlanarDirection from2DDataValue(int value) {
		return BY_2D_DATA[value % BY_2D_DATA.length];
	}
	
	public Vec2i getNormal() {
		return normal;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String getSerializedName() {
		return name;
	}
	
	public PlanarDirection.PlanarAxis getAxis() {
		return axis;
	}
	
	public PlanarDirection getOposite() {
		return values()[this.oposite];
	}
	
	public static PlanarDirection fromAxisAndDirection(PlanarDirection.PlanarAxis axis, AxisDirection direction) {
		switch (axis) {
		case X: return direction == AxisDirection.POSITIVE ? X_POS : X_NEG;
		case Y: return direction == AxisDirection.POSITIVE ? Y_POS : Y_NEG;
		default: throw new IncompatibleClassChangeError();
		}
	}
	
	public static PlanarDirection getNearest(double x, double y) {
		return getNearest((float) x, (float) y);
	}
	
	public static PlanarDirection getNearest(float x, float y) {
		PlanarDirection direction = X_POS;
		float f = Float.MIN_VALUE;
		
		for (PlanarDirection d : values()) {
			float f1 = d.getNormal().x() * x + d.getNormal().y * y;
			if (f1 > f) {
				f = f1;
				direction = d;
			}
		}
		
		return direction;
	}
	
	public static enum PlanarAxis implements Predicate<PlanarDirection>, StringRepresentable {
		X("x") {
			@Override
			public int choose(int x, int y) {
				return y;
			}
			
			@Override
			public double choose(double x, double y) {
				return x;
			}
		},
		Y("y") {
			@Override
			public int choose(int x, int y) {
				return y;
			}
			
			@Override
			public double choose(double x, double y) {
				return y;
			}
		};
		
		private final String name;
		
		private PlanarAxis(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String getSerializedName() {
			return name;
		}
		
		@Override
		public boolean test(PlanarDirection t) {
			return t != null && t.getAxis() == this;
		}
		
		public abstract int choose(int x, int y);
		public abstract double choose(double x, double y);
		
	}
	
}
