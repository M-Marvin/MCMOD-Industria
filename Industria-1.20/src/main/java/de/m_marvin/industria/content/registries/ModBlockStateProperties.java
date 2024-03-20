package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.types.MotorMode;
import de.m_marvin.industria.content.types.WallOrientations;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockStateProperties {
	
	public static final IntegerProperty CLAMP_OFFSET = IntegerProperty.create("offset", 0, 2);
	public static final EnumProperty<WallOrientations> ORIENTATION = EnumProperty.create("orientation", WallOrientations.class);
	public static final EnumProperty<MotorMode> MOTOR_MODE = EnumProperty.create("mode", MotorMode.class);
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 4);
	public static final BooleanProperty TOP = BooleanProperty.create("top");
//	public static final BooleanProperty CORE = BooleanProperty.create("core");
	public static final DirectionProperty CONNECT = DirectionProperty.create("connect");
	public static final EnumProperty<HorizontalConnection> SHAPE = EnumProperty.create("shape", HorizontalConnection.class);
	
	public static enum HorizontalConnection implements StringRepresentable {
		NONE("none", false, false, false, false),
		NORTH("north", true, false, false, false),
		SOUTH("south", false, true, false, false),
		EAST("east", false, false, true, false),
		WEST("west", false, false, false, true),
		NORTH_SOUTH("north_south", true, true, false, false),
		EAST_WEST("east_west", false, false, true, true),
		NORTH_EAST("north_east", true, false, true, false),
		NORTH_WEST("north_west", true, false, false, true),
		SOUTH_EAST("south_east", false, true, true, false),
		SOUTH_WEST("south_west", false, true, false, true),
		NORHT_EAST_WEST("north_east_west", true, false, true, true),
		SOUTH_EAST_WEST("south_east_west", false, true, true, true),
		NORHT_SOUTH_EAST("north_south_east", true, true, true, false),
		NORTH_SOUTH_WEST("north_south_west", true, true, false, true),
		ALL("all", true, true, true, true);
		
		private final String name;
		private final boolean north;
		private final boolean south;
		private final boolean east;
		private final boolean west;
		
		private HorizontalConnection(String name, boolean north, boolean south, boolean east, boolean west) {
			this.name = name;
			this.north = north;
			this.south = south;
			this.east = east;
			this.west = west;
		}
		
		@Override
		public String getSerializedName() {
			return name;
		}
		
		public boolean hasNorth() {
			return north;
		}
		
		public boolean hasSouth() {
			return south;
		}
		
		public boolean hasEast() {
			return east;
		}
		
		public boolean hasWest() {
			return west;
		}
		
		public static HorizontalConnection fromFaces(boolean north, boolean south, boolean east, boolean west) {
			for (HorizontalConnection facing : values()) {
				if (facing.hasNorth() == north &&
					facing.hasSouth() == south &&
					facing.hasEast() == east &&
					facing.hasWest() == west)
						return facing;
			}
			return NONE;
		}
		
	}
	
}
