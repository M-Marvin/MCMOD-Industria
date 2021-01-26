package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;

import de.redtec.RedTec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraftforge.common.ToolType;

public class BlockBase extends Block {
	
	public BlockBase(String name, Properties properties) {
		super(properties);
		this.setRegistryName(RedTec.MODID, name);
	}
	
	public BlockBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(RedTec.MODID, name);
	}
	
	public static ToolType getDefaultToolType(Material material) {
		if (material == Material.ROCK) return ToolType.PICKAXE;
		if (material == Material.SAND) return ToolType.SHOVEL;
		if (material == Material.EARTH ||material == Material.SAND) return ToolType.AXE;
		if (material == Material.ORGANIC) return ToolType.HOE;
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.getItemFromBlock(this), 1));
		return drops;
	}
	
}
