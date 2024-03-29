package de.industria.blocks;

import java.util.Random;

import de.industria.Industria;
import de.industria.typeregistys.ModItems;
import de.industria.util.handler.ItemStackHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.PlantType;

public class BlockSwampAlgae extends BushBlock implements IGrowable {
	
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
	
	public BlockSwampAlgae() {
		super(Properties.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundType.WET_GRASS));
		this.setRegistryName(new ResourceLocation(Industria.MODID, "swamp_algae"));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return state.getValue(HALF) == Half.BOTTOM ? Block.box(2, 6, 2, 14, 16, 14) : Block.box(2, 0, 2, 14, 16, 14);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(HALF);
	}
	
	@Override
	public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		ItemStackHelper.spawnItemStack(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, new ItemStack(Item.byBlock(ModItems.swamp_algae)));
	}
	
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.PLAINS;
	}
	
	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (state.getValue(HALF) == Half.BOTTOM) {
			worldIn.getBlockTicks().scheduleTick(pos.above(), this, 1);
		} else {
			worldIn.getBlockTicks().scheduleTick(pos.below(), this, 1);
		}
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		worldIn.destroyBlock(pos, true);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.defaultBlockState().setValue(HALF, Half.TOP);
		if (canSurvive(state, context.getLevel(), context.getClickedPos()) && context.getLevel().getBlockState(context.getClickedPos().below()).canBeReplaced(context)) {
			context.getLevel().setBlockAndUpdate(context.getClickedPos().below(), this.defaultBlockState().setValue(HALF, Half.BOTTOM));
			return state;
		}
		return context.getLevel().getBlockState(context.getClickedPos());
	}
	
	@Override
	protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.isSolidRender(worldIn, pos) || state.getBlock() instanceof LeavesBlock;
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (state.getValue(HALF) == Half.BOTTOM) {
			return worldIn.getBlockState(pos.above()).getBlock() == this;
		} else {
			return mayPlaceOn(worldIn.getBlockState(pos.above()), worldIn, pos.above());
		}
	}
	
}
