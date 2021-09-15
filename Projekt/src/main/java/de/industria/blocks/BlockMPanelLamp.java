package de.industria.blocks;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntitySimpleBlockTicking;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBAreaLamp;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockMPanelLamp extends BlockContainerBase implements IBElectricConnectiveBlock, IBAdvancedBlockInfo, IWaterLoggable, IBAreaLamp {
	
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public static final IAreaLightSupplier LIGHT_PLACEMENT = (lampState) -> {
		Direction facing = lampState.getValue(FACING);
		
		HashMap<BlockPos, Direction> lights = new HashMap<BlockPos, Direction>();
		for (Direction d : Direction.values()) {
			if (d != facing) {
				for (int i = 0; i < 9; i++) {
					lights.put(BlockPos.ZERO.relative(d, i), d);
				}
			}
		}
		
		return lights;
	};
	
	public BlockMPanelLamp() {
		super("panel_lamp", Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL).harvestTool(getDefaultToolType(Material.METAL)).lightLevel((state) -> {return state.getValue(LIT) ? 15 : 0;}));
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.LowVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0.1F;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side == state.getValue(FACING).getOpposite();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite()).setValue(LIT, false);
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		boolean active = state.getValue(LIT);
		boolean powered = network.canMachinesRun() == Voltage.LowVoltage;
		
		if (network.getVoltage().getVoltage() > Voltage.LowVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
		
		if (active != powered) worldIn.setBlockAndUpdate(pos, state.setValue(LIT, powered));
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		this.updateNetwork(worldIn, pos);
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

		switch (state.getValue(FACING)) {
		case SOUTH: return NORTH_OPEN_AABB;
		case NORTH: return SOUTH_OPEN_AABB;
		case WEST: return EAST_OPEN_AABB;
		case EAST: return WEST_OPEN_AABB;
		case UP: return TOP_AABB;
		case DOWN: return BOTTOM_AABB;
		default: return NORTH_OPEN_AABB;
		}
		
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 0.1F * Voltage.LowVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.LowVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 0.1F));
			info.add(new TranslationTextComponent("industria.block.info.panelLamp"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public boolean isLit(BlockState state, IWorldReader world, BlockPos pos) {
		return state.getBlock() == this ? state.getValue(LIT) : false;
	}
	
	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if ((state.getBlock() != this || newState.getBlock() != this) ? true : state.getValue(LIT) != newState.getValue(LIT)) {
			updateLight(state, world, pos, LIGHT_PLACEMENT);
		}
	}
	
}
