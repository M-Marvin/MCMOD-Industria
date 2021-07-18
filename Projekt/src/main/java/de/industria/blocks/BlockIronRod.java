package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.industria.Industria;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndRodBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockIronRod extends EndRodBlock {

	public BlockIronRod() {
		super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(1));
		this.setRegistryName(Industria.MODID, "iron_rod");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.byBlock(this), 1));
		return drops;
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 0;
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {}

}
