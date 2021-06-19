package de.industria.blocks;

import java.util.function.Supplier;

import de.industria.Industria;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockStairsBase extends StairsBlock {
	
	public BlockStairsBase(Supplier<BlockState> modelBlock, String name, Properties properties) {
		super(modelBlock, properties);
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockStairsBase(Supplier<BlockState> modelBlock, String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(modelBlock, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockStairsBase(Supplier<BlockState> modelBlock, String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(modelBlock, Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}

	public BlockStairsBase(Supplier<BlockState> modelBlock, String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(modelBlock, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)).setRequiresTool());
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockStairsBase(Supplier<BlockState> modelBlock, String name, Material material, float hardness, float resistance, SoundType sound) {
		super(modelBlock, Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)).setRequiresTool());
		this.setRegistryName(Industria.MODID, name);
	}
	
	public static ToolType getDefaultToolType(Material material) {
		if (material == Material.ROCK || material == Material.IRON) return ToolType.PICKAXE;
		if (material == Material.SAND || material == Material.EARTH) return ToolType.SHOVEL;
		if (material == Material.WOOD) return ToolType.AXE;
		if (material == Material.ORGANIC || material == Material.LEAVES) return ToolType.HOE;
		return null;
	}
	
}
