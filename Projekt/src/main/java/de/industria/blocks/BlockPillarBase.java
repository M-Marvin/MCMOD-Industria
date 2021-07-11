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
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	public BlockPillarBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)));
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	public BlockPillarBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)));
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}

	public BlockPillarBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)).setRequiresTool());
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	public BlockPillarBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)).setRequiresTool());
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
		setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
}
