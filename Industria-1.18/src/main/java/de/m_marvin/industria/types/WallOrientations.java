package de.m_marvin.industria.types;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;

public enum WallOrientations implements StringRepresentable {
	GROUND_NS("ground_ns", Direction.DOWN, Axis.Z),
	GROUND_EW("ground_ew", Direction.DOWN, Axis.X),
	NORHT_UD("north_ud", Direction.NORTH, Axis.Y),
	NORTH_EW("north_ew", Direction.NORTH, Axis.X),
	SOUTH_UD("south_ud", Direction.SOUTH, Axis.Y),
	SOUTH_EW("south_ew", Direction.SOUTH, Axis.X),
	EAST_UD("east_ud", Direction.EAST, Axis.Y),
	EAST_NS("east_ns", Direction.EAST, Axis.Z),
	WEST_UD("west_ud", Direction.WEST, Axis.Y),
	WEST_NS("west_ns", Direction.WEST, Axis.Z),
	CEILING_NS("ceiling_ns", Direction.UP, Axis.Z),
	CEILING_EW("ceiling_ew", Direction.UP, Axis.X);
		
	private String name;
	private Direction face;
	private Axis orientation;
	
	private WallOrientations(String name, Direction face, Axis orientation) {
		this.name = name;
		this.face = face;
		this.orientation = orientation;
	}
	
	@Override
	public String getSerializedName() {
		return name;
	}
	
	public Direction getFace() {
		return face;
	}
	
	public Axis getAxialOrientation() {
		return orientation;
	}
	
	public static WallOrientations fromFaceAndAxis(Direction face, Axis axis) {
		for (WallOrientations orientation : WallOrientations.values()) {
			if (orientation.getFace() == face && orientation.getAxialOrientation() == axis) return orientation;
		}
		return null;
	}
}
