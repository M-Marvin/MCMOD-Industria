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

public class BlockBase extends Block {
	
	public BlockBase(String name, Properties properties) {
		super(properties);
		this.setRegistryName(RedTec.MODID, name);
	}
	
	public BlockBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound));
		this.setRegistryName(RedTec.MODID, name);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.getItemFromBlock(this), 1));
		return drops;
	}
	
}
