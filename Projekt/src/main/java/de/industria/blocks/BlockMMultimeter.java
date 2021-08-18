package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockGaugeItemRenderer;
import de.industria.tileentity.TileEntityMMultimeter;
import de.industria.tileentity.TileEntityMMultimeter.DecimalUnit;
import de.industria.tileentity.TileEntityMMultimeter.MessurementType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMMultimeter extends BlockContainerBase implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<TileEntityMMultimeter.MessurementType> UNIT = EnumProperty.create("unit", TileEntityMMultimeter.MessurementType.class);
	
	public BlockMMultimeter() {
		super("multimeter", Material.METAL, 2F, SoundType.METAL);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, UNIT);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(UNIT, MessurementType.VOLT);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.isShiftKeyDown() && !worldIn.isClientSide()) {
			
			MessurementType unit = state.getValue(UNIT);
			if (unit == MessurementType.VOLT) {
				unit = MessurementType.AMPERE;
			} else if (unit == MessurementType.AMPERE) {
				unit = MessurementType.WATT;
			} else if (unit == MessurementType.WATT) {
				unit = MessurementType.VOLT;
			}
			
			worldIn.setBlockAndUpdate(pos, state.setValue(UNIT, unit));
			return ActionResultType.SUCCESS;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = Block.box(0, 0, 0, 16, 5, 16);
		shape = VoxelShapes.or(shape, Block.box(0, 14, 0, 16, 16, 16));
		
		switch(state.getValue(FACING)) {
		case NORTH:
			shape = VoxelShapes.or(shape, Block.box(0, 5, 2, 16, 16, 16));
			shape = VoxelShapes.or(shape, Block.box(0, 5, 0, 1, 16, 16));
			shape = VoxelShapes.or(shape, Block.box(15, 5, 0, 16, 16, 2));
			break;
		case SOUTH:
			shape = VoxelShapes.or(shape, Block.box(0, 5, 0, 16, 16, 14));
			shape = VoxelShapes.or(shape, Block.box(0, 5, 0, 1, 16, 16));
			shape = VoxelShapes.or(shape, Block.box(15, 5, 14, 16, 16, 16));
			break;
		case WEST:
			shape = VoxelShapes.or(shape, Block.box(2, 5, 0, 16, 16, 16));
			shape = VoxelShapes.or(shape, Block.box(0, 5, 0, 16, 16, 1));
			shape = VoxelShapes.or(shape, Block.box(0, 5, 15, 2, 16, 16));
			break;
		case EAST:
			shape = VoxelShapes.or(shape, Block.box(0, 5, 0, 14, 16, 16));
			shape = VoxelShapes.or(shape, Block.box(0, 5, 0, 16, 16, 1));
			shape = VoxelShapes.or(shape, Block.box(14, 5, 15, 16, 16, 16));
			break;
		default:
			break;
		
		}
		
		return shape;
		
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
	public NetworkChangeResult beforNetworkChanges(World world, BlockPos pos, BlockState state, ElectricityNetwork network, int lap) {
		
		TileEntity te = world.getBlockEntity(pos);
		
		if (te instanceof TileEntityMMultimeter) {
			
			float value = state.getValue(UNIT).getValue(world, pos, network);
			
			DecimalUnit unit = DecimalUnit.getUnitForValue(value);
			value = value / unit.getValue();
			
			((TileEntityMMultimeter) te).updateValue(value, unit);
			
		}
		
		return NetworkChangeResult.CONTINUE;
		
	}
	
	@Override
	public boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		
		return state.getValue(UNIT) != TileEntityMMultimeter.MessurementType.VOLT;
		
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.multimeter"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockGaugeItemRenderer::new;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMMultimeter();
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityMMultimeter) {
			switch (((TileEntityMMultimeter) tileEntity).decimalUnit) {
			case MILLI: return 0;
			case NONE: return 1;
			case KILO: return 2;
			case MEGA: return 3;
			}
		}
		return 0;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity tileEntity = blockAccess.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityMMultimeter) {
			return (int) (((TileEntityMMultimeter) tileEntity).getValue() / 1000 * 15);
		}
		return 0;
	}
	
}
