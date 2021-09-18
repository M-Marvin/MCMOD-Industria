package de.industria.blocks;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntitySimpleBlockTicking;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBAreaLamp;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.UtilHelper;
import de.industria.util.handler.VoxelHelper;
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
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockMFloodlight extends BlockContainerBase implements IBAdvancedBlockInfo, IBElectricConnectiveBlock, IWaterLoggable, IBAreaLamp {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE_WALL = VoxelShapes.or(Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 6.0D), Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D));
	protected static final VoxelShape SHAPE_BOTTOM = VoxelShapes.or(Block.box(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D), Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D));
	protected static final VoxelShape SHAPE_UP = VoxelShapes.or(Block.box(1.0D, 10.0D, 1.0D, 15.0D, 16.0D, 15.0D), Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D));
	
	protected static final IAreaLightSupplier LIGHT_PLACEMENT = (lampState) -> {
		Direction hfacing= lampState.getValue(FACING);
		AttachFace face = lampState.getValue(FACE);
		Direction facing = face == AttachFace.FLOOR ? Direction.UP : (face  == AttachFace.CEILING) ? Direction.DOWN : hfacing.getOpposite();
		
		HashMap<BlockPos, Direction> lights = new HashMap<BlockPos, Direction>();
		for (int i = 0; i < 50; i++) {
			BlockPos p1 = BlockPos.ZERO.relative(facing, i);
			lights.put(p1, facing);
			
			int w = (int) (i * 0.2F);
			Direction d1 = UtilHelper.getDirectionOutOfAxis(facing.getAxis());
			Direction d2 = UtilHelper.rotateOnAxis(d1, 90, facing.getAxis());
			for (int j1 = 0; j1 < w; j1++) {
				
				BlockPos p21 = p1.relative(d1, j1);
				BlockPos p22 = p1.relative(d1, -j1);
				
				if (j1 != 0) lights.put(p21, lights.containsKey(p21.relative(facing.getOpposite())) ? facing : d1);
				if (j1 != 0) lights.put(p22, lights.containsKey(p22.relative(facing.getOpposite())) ? facing : d1.getOpposite());
				for (int j2 = 1; j2 < w; j2++) {
					
					BlockPos p31 = p21.relative(d2, j2);
					BlockPos p32 = p21.relative(d2, -j2);
					BlockPos p33 = p22.relative(d2, j2);
					BlockPos p34 = p22.relative(d2, -j2);
					
					lights.put(p31, lights.containsKey(p31.relative(facing.getOpposite())) ? facing : d2);
					lights.put(p32, lights.containsKey(p32.relative(facing.getOpposite())) ? facing : d2.getOpposite());
					lights.put(p33, lights.containsKey(p33.relative(facing.getOpposite())) ? facing : d2);
					lights.put(p34, lights.containsKey(p34.relative(facing.getOpposite())) ? facing : d2.getOpposite());
					
				}
			}
			
		}
		
		return lights;
	};
	
	public BlockMFloodlight() {
		super("floodlight", Properties.of(Material.METAL).strength(2.5F).sound(SoundType.METAL).harvestTool(getDefaultToolType(Material.METAL)).lightLevel((state) -> {return state.getValue(LIT) ? 15 : 0;}));
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(FACE, AttachFace.WALL).setValue(LIT, false));
	}
	
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		for(Direction direction : context.getNearestLookingDirections()) {
			BlockState blockstate;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, context.getHorizontalDirection());
			} else {
				blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction);
			}
			return blockstate;
		}
		return null;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, FACE, LIT, WATERLOGGED);
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
		return Voltage.NormalVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0.5F;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return (state.getValue(FACE) == AttachFace.WALL && side == state.getValue(FACING).getOpposite()) || (state.getValue(FACE) == AttachFace.FLOOR && side != Direction.DOWN) || (state.getValue(FACE) == AttachFace.CEILING && side == Direction.DOWN);
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		boolean active = state.getValue(LIT);
		boolean powered = network.canMachinesRun() == Voltage.NormalVoltage;
		
		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage()) {

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
		if (state.getValue(FACE) == AttachFace.CEILING) {
			return SHAPE_UP;
		} else if (state.getValue(FACE) == AttachFace.FLOOR) {
			return SHAPE_BOTTOM;
		} else {
			return VoxelHelper.rotateShape(SHAPE_WALL, state.getValue(FACING));
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
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 0.5F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 0.5F));
			info.add(new TranslationTextComponent("industria.block.info.floodlight"));
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
		if ((state.getBlock() != this || newState.getBlock() != this) ? true : state.getValue(LIT) != newState.getValue(LIT) || moved) {
			updateLight(state, world, pos, LIGHT_PLACEMENT);
		}
	}
	
}
