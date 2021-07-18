package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;

public class BlockPillarBase extends RotatedPillarBlock {
	
	public BlockPillarBase(String name, Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	public BlockPillarBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)));
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	public BlockPillarBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)));
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}

	public BlockPillarBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)).requiresCorrectToolForDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	public BlockPillarBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)).requiresCorrectToolForDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
}
