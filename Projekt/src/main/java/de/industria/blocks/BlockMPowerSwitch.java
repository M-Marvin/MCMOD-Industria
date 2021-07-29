package de.industria.blocks;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockMPowerSwitch extends BlockBase implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 11.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.box(5.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 11.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0D, 0.0D, 5.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.box(0.0D, 5.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public BlockMPowerSwitch() {
		super("power_switch", Material.METAL, 1.5F, SoundType.METAL);
		this.registerDefaultState(this.stateDefinition.any().setValue(CLOSED, false).setValue(POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED, CLOSED, FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace());
		return updateState(state, context.getLevel(), context.getClickedPos());
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState stateNew = updateState(state, worldIn, pos);
		
		if (stateNew != state) {
			
			worldIn.setBlockAndUpdate(pos, stateNew);
			
		}
		
	}
	
	public BlockState updateState(BlockState state, World world, BlockPos pos) {
		
		boolean powered = state.getValue(POWERED);
		boolean power = world.hasNeighborSignal(pos);
		
		if (powered != power) {
			
			state = state.setValue(POWERED, power).setValue(CLOSED, power);
			world.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.5F);
			
		}
		
		return state;
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		switch (state.getValue(FACING)) {
		case NORTH: return NORTH_OPEN_AABB;
		case SOUTH: return SOUTH_OPEN_AABB;
		case EAST: return EAST_OPEN_AABB;
		case WEST: return WEST_OPEN_AABB;
		case DOWN: return TOP_AABB;
		case UP: return BOTTOM_AABB;
		default: return NORTH_OPEN_AABB;
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(CLOSED)) {
		Direction direction = stateIn.getValue(FACING).getOpposite();
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D);
			double d1 = (double)pos.getY() + 0.5D + (rand.nextDouble() - 0.5D);
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D);
	        float f = -5.0F;
	        f = f / 16.0F;
	        double d3 = (double)(f * (float)direction.getStepX());
	        double d4 = (double)(f * (float)direction.getStepZ());
	        worldIn.addParticle(RedstoneParticleData.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}	
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		worldIn.setBlockAndUpdate(pos, state.setValue(CLOSED, !state.getValue(CLOSED)));
		worldIn.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.5F);
		return ActionResultType.SUCCESS;
		
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NoLimit;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side != state.getValue(FACING).getOpposite();
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}
	
	@Override
	public boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		return state.getValue(CLOSED);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side != state.getValue(FACING).getOpposite();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.powerSwitch"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
}
