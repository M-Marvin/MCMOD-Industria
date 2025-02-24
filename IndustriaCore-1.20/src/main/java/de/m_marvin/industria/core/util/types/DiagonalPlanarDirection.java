package de.m_marvin.industria.core.util.types;

import java.util.Comparator;
import java.util.stream.Stream;

import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.util.StringRepresentable;

public enum DiagonalPlanarDirection implements StringRepresentable {
	
	Y_POS(			"y_positive",	new Vec2i(0, +1),	1,	1, 90),
	Y_NEG(			"y_negative",	new Vec2i(0, -1),	0,	0, -90),
	X_POS(			"x_positive",	new Vec2i(+1, 0),	3,	3, 0),
	X_NEG(			"x_negative",	new Vec2i(-1, 0),	2,	2, 180),
	X_POS_Y_POS(	"x_pos_y_pos",	new Vec2i(+1, +1),	4,	7, 45),
	X_NEG_Y_POS(	"x_neg_y_pos",	new Vec2i(-1, +1),	5,	6, 135),
	X_POS_Y_NEG(	"x_pos_y_neg",	new Vec2i(+1, -1),	6,	5, -45),
	X_NEG_Y_NEG(	"x_neg_y_neg",	new Vec2i(-1, -1),	7,	4, -135);
	
	private final String name;
	private final Vec2i normal;
	private final Vec2f normalized;
	private final int data2d;
	private final int oposite;
	private final int angle;
	
	private static final DiagonalPlanarDirection[] BY_2D_DATA = Stream.of(values())
			.sorted(Comparator.comparingInt(DiagonalPlanarDirection::get2DDataValue))
			.toArray(DiagonalPlanarDirection[]::new);
	
	private DiagonalPlanarDirection(String name, Vec2i normal, int data2d, int oposite, int angle) {
		this.name = name;
		this.normal = normal;
		this.normalized = new Vec2f(normal).normalize();
		this.data2d = data2d;
		this.oposite = oposite;
		this.angle = angle;
	}
	
	public int get2DDataValue() {
		return data2d;
	}
	
	public int getAngleFromPositiveX() {
		return angle;
	}
	
	public static DiagonalPlanarDirection from2DDataValue(int value) {
		return BY_2D_DATA[value % BY_2D_DATA.length];
	}
	
	public boolean isDiagonal() {
		return data2d > 3;
	}
	
	public Vec2i getNormal() {
		return normal;
	}
	
	public Vec2f getNormalized() {
		return normalized;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String getSerializedName() {
		return name;
	}
	
	public PlanarDirection getPlanarDirection() {
		if (this.data2d > 3) return null;
		return PlanarDirection.from2DDataValue(this.data2d);
	}
	
	public static DiagonalPlanarDirection fromPlanarDirection(PlanarDirection direction) {
		return BY_2D_DATA[direction.get2DDataValue()];
	}
	
	public DiagonalPlanarDirection getOposite() {
		return values()[this.oposite];
	}

	public static DiagonalPlanarDirection getNearest(double x, double y) {
		return getNearest((float) x, (float) y);
	}
	
	public static DiagonalPlanarDirection getNearest(float x, float y) {
		DiagonalPlanarDirection direction = X_POS;
		float f = Float.MIN_VALUE;
		
		for (DiagonalPlanarDirection d : values()) {
			Vec2f normal = d.getNormalized();
			float f1 = normal.x() * x + normal.y * y;
			if (f1 > f) {
				f = f1;
				direction = d;
			}
		}
		
		return direction;
	}
	
}
