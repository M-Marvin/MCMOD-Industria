package de.industria.blocks;

import de.industria.util.blockfeatures.IBurnableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockWoodenSlabBase extends BlockSlabBase implements IBurnableBlock {

	public BlockWoodenSlabBase(String name, Properties properties) {
		super(name, properties);
	}
	
	public BlockWoodenSlabBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(name, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
	}
	
	public BlockWoodenSlabBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(name, Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
	}

	public BlockWoodenSlabBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(name, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)).setRequiresTool());
	}
	
	public BlockWoodenSlabBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(name, Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)).setRequiresTool());
	}
	
	public static ToolType getDefaultToolType(Material material) {
		if (material == Material.ROCK || material == Material.IRON) return ToolType.PICKAXE;
		if (material == Material.SAND || material == Material.EARTH) return ToolType.SHOVEL;
		if (material == Material.WOOD) return ToolType.AXE;
		if (material == Material.ORGANIC || material == Material.LEAVES) return ToolType.HOE;
		return null;
	}

	@Override
	public int getBurnTime() {
		return 375;
	}
	
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}
	
}
