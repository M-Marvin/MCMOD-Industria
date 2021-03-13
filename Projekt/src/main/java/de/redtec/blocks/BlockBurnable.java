package de.redtec.blocks;

import de.redtec.util.IBurnableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockBurnable extends BlockBase implements IBurnableBlock {
	
	private int flammability;
	private int fireSpreadChance;
	private int burnTime;
	
	public BlockBurnable(String name, Properties properties, int flammability, int fireSpreadTime, int burnTime) {
		super(name, properties);
		this.flammability = flammability;
		this.fireSpreadChance = fireSpreadTime;
		this.burnTime = burnTime;
	}
	
	public BlockBurnable(String name, Material material, float hardnessAndResistance, SoundType sound, int flammability, int fireSpreadTime, int burnTime, boolean dropsEver) {
		super(name, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.flammability = flammability;
		this.fireSpreadChance = fireSpreadTime;
		this.burnTime = burnTime;
	}
	
	public BlockBurnable(String name, Material material, float hardness, float resistance, SoundType sound, int flammability, int fireSpreadTime, int burnTime, boolean dropsEver) {
		super(name, Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.flammability = flammability;
		this.fireSpreadChance = fireSpreadTime;
		this.burnTime = burnTime;

	}

	public BlockBurnable(String name, Material material, float hardnessAndResistance, SoundType sound, int flammability, int fireSpreadTime, int burnTime) {
		super(name, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)).setRequiresTool());
		this.flammability = flammability;
		this.fireSpreadChance = fireSpreadTime;
		this.burnTime = burnTime;
	}
	
	public BlockBurnable(String name, Material material, float hardness, float resistance, SoundType sound, int flammability, int fireSpreadTime, int burnTime) {
		super(name, Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)).setRequiresTool());
		this.flammability = flammability;
		this.fireSpreadChance = fireSpreadTime;
		this.burnTime = burnTime;
	}
	
	@Override
	public int getBurnTime() {
		return this.burnTime;
	}
	
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return this.flammability;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return this.fireSpreadChance;
	}
	
}
