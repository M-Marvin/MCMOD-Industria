package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.types.MotorMode;
import de.m_marvin.industria.content.types.WallOrientations;
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
	public static final BooleanProperty CORE = BooleanProperty.create("core");
	public static final DirectionProperty CONNECT = DirectionProperty.create("connect");
	
}
