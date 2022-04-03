package de.m_marvin.industria.registries;

import de.m_marvin.industria.blocks.ConduitClampBlock.ConduitClampType;
import de.m_marvin.industria.types.WallOrientations;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockStateProperties {
	
	public static final EnumProperty<ConduitClampType> CLAMP_TYPE = EnumProperty.create("clamp", ConduitClampType.class);
	public static final IntegerProperty CLAMP_OFFSET = IntegerProperty.create("offset", 0, 2);
	public static final EnumProperty<WallOrientations> ORIENTATION = EnumProperty.create("orientation", WallOrientations.class);
	
}
