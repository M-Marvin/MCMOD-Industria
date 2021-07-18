package de.industria.blocks;

import de.industria.Industria;
import de.industria.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSalsolaSeeds extends SweetBerryBushBlock {
	
	public BlockSalsolaSeeds() {
		super(Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0).noOcclusion());
		this.setRegistryName(Industria.MODID, "salsola_seeds");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		int i = state.getValue(AGE);
		boolean flag = i == 3;
		
		if (!flag && player.getItemInHand(handIn).getItem() == Items.BONE_MEAL) {
			return ActionResultType.PASS;
		} else if (i > 1) {
			
			int j = 1 + worldIn.random.nextInt(2);
			popResource(worldIn, pos, new ItemStack(ModItems.salsola, j + (flag ? 1 : 0)));
			popResource(worldIn, pos, new ItemStack(Item.byBlock(this)));
			worldIn.playSound((PlayerEntity)null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.random.nextFloat() * 0.4F);
			worldIn.setBlock(pos, state.setValue(AGE, 1), 2);
			return ActionResultType.sidedSuccess(worldIn.isClientSide());
			
		} else {
			
			return super.use(state, worldIn, pos, player, handIn, hit);
			
		}
		
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.getValue(AGE) == 0) {
			return Block.box(3.0D, 0.0D, 3.0D, 13.0D, 5.0D, 13.0D);
		} else {
			return state.getValue(AGE) < 3 ? Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D) : Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(Item.byBlock(this));
	}
	
}
