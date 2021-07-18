package de.industria.fluids.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class GasFluid extends Fluid implements IBucketPickupHandler {
	
	public static final BooleanProperty FALLING_NOT_IN_USE = BlockStateProperties.FALLING;
	
	@Override
	protected boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return false;
	}
	
	@Override
	protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
		builder.add(FALLING_NOT_IN_USE);
		super.createFluidStateDefinition(builder);
	}
	
	@Override
	public Vector3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_) {
		return new Vector3d(0, 0, 0);
	}

	@Override
	public int getTickDelay(IWorldReader p_205569_1_) {
		return 1;
	}

	@Override
	protected float getExplosionResistance() {
		return 100F;
	}

	@Override
	public float getHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
		return getOwnHeight(p_215662_1_);
	}

	@Override
	public float getOwnHeight(FluidState p_223407_1_) {
		return 1F;
	}
	
	@Override
	public boolean isSource(FluidState state) {
		return true;
	}
	
	@Override
	public VoxelShape getShape(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
		return Block.box(0, 0, 0, 16, 16, 16);
	}
	
	@Override
	public int getAmount(FluidState p_207192_1_) {
		return 8;
	}
	
	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}
	
	@Override
	public void tick(World worldIn, BlockPos pos, FluidState state) {}
	
	public void onMoved(World worldIn, BlockPos pos, Direction moveDirection, FluidState state, Random random) {};
	
//	@Override
//	public Fluid getFlowingFluid() {
//		return this;
//	}
//
//	@Override
//	public Fluid getStillFluid() {
//		return this;
//	}
//
//	@Override
//	protected boolean canSourcesMultiply() {
//		return false;
//	}
//
//	@Override
//	public void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}
//	
//	@Override
//	protected int getSlopeFindDistance(IWorldReader worldIn) {
//		return 0;
//	}
//
//	@Override
//	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
//		return 0;
//	}
	
}
