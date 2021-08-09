package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockFlax extends CropsBlock {
	
	public BlockFlax() {
		super(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
		this.setRegistryName(new ResourceLocation(Industria.MODID, "flax"));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected IItemProvider getBaseSeedId() {
		return Item.byBlock(this);
	}
	
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
		switch(p_220053_1_.getValue(this.getAgeProperty())) {
		case 0:
			return Block.box(0, 0, 0, 16, 4, 16);
		case 1:
			return Block.box(0, 0, 0, 16, 4, 16);
		case 2:
			return Block.box(0, 0, 0, 16, 12, 16);
		case 3:
		case 4:
		case 5:
			return Block.box(0, 0, 0, 16, 16, 16);
		case 6:
			return Block.box(0, 0, 0, 16, 14, 16);
		case 7:
			return Block.box(0, 0, 0, 16, 16, 16);
		}
		return Block.box(0, 0, 0, 16, 16, 16);
	}
	
}
