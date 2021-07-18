package de.industria.blocks;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityRSignalProcessorContact;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.blockfeatures.INetworkDevice;
import de.industria.util.blockfeatures.ISignalConnectiveBlock;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockRSignalProcessorContact extends BlockContainerBase implements ISignalConnectiveBlock, IWaterLoggable, INetworkDevice, IAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty OPEN = BooleanProperty.create("open");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public BlockRSignalProcessorContact() {
		super("signal_processor_contact", Material.METAL, 1, SoundType.METAL);
		this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(OPEN, FACING, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		switch ((Direction) state.getValue(FACING)) {
		case NORTH: return VoxelShapes.or(Block.box(0, 0, 8, 16, 2, 16), VoxelShapes.or(Block.box(0, 2, 8, 2, 14, 16)), VoxelShapes.or(Block.box(14, 2, 8, 16, 14, 16)), VoxelShapes.or(Block.box(0, 14, 8, 16, 16, 16)), VoxelShapes.or(Block.box(0, 0, 15, 16, 16, 16)));
		case SOUTH: return VoxelShapes.or(Block.box(0, 0, 0, 16, 2, 8), VoxelShapes.or(Block.box(0, 2, 0, 2, 14, 8)), VoxelShapes.or(Block.box(14, 2, 0, 16, 14, 8)), VoxelShapes.or(Block.box(0, 14, 0, 16, 16, 8)), VoxelShapes.or(Block.box(0, 0, 0, 16, 16, 1)));
		case EAST: return VoxelShapes.or(Block.box(0, 0, 0, 8, 2, 16), VoxelShapes.or(Block.box(0, 2, 0, 8, 14, 2)), VoxelShapes.or(Block.box(0, 2, 14, 8, 14, 16)), VoxelShapes.or(Block.box(0, 14, 0, 8, 16, 16)), VoxelShapes.or(Block.box(0, 0, 0, 1, 16, 16)));
		case WEST: return VoxelShapes.or(Block.box(8, 0, 0, 16, 2, 16), VoxelShapes.or(Block.box(8, 2, 0, 16, 14, 2)), VoxelShapes.or(Block.box(8, 2, 14, 16, 14, 16)), VoxelShapes.or(Block.box(8, 14, 0, 16, 16, 16)), VoxelShapes.or(Block.box(15, 0, 0, 16, 16, 16)));
		case DOWN: return VoxelShapes.or(Block.box(0, 8, 0, 2, 16, 16), VoxelShapes.or(Block.box(2, 8, 0, 14, 16, 2)), VoxelShapes.or(Block.box(2, 8, 14, 14, 16, 16)), VoxelShapes.or(Block.box(14, 8, 0, 16, 16, 16)), VoxelShapes.or(Block.box(0, 15, 0, 16, 16, 16)));
		case UP: return VoxelShapes.or(Block.box(0, 0, 0, 2, 8, 16), VoxelShapes.or(Block.box(2, 0, 0, 14, 8, 2)), VoxelShapes.or(Block.box(2, 0, 14, 14, 8, 16)), VoxelShapes.or(Block.box(14, 0, 0, 16, 8, 16)), VoxelShapes.or(Block.box(0, 0, 0, 16, 1, 16)));
		default: return super.getShape(state, worldIn, pos, context);
		}
		
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (handIn == Hand.MAIN_HAND) {
			
			if (player.isShiftKeyDown()) {
				
				worldIn.playLocalSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, state.getValue(OPEN) ? SoundEvents.WOODEN_TRAPDOOR_CLOSE : SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1, 1, false);
				
				if (!worldIn.isClientSide()) {
					
					worldIn.setBlockAndUpdate(pos, state.setValue(OPEN, !state.getValue(OPEN)));
					
					TileEntity tileEntity = worldIn.getBlockEntity(pos);
					if (tileEntity instanceof TileEntityRSignalProcessorContact) ((TileEntityRSignalProcessorContact) tileEntity).getVariables().clear();
					
				}
				
				return ActionResultType.CONSUME;
				
			} else if (state.getValue(OPEN) && !worldIn.isClientSide()) {
				
				TileEntity tileEntity = worldIn.getBlockEntity(pos);
				ItemStack heldStack = player.getMainHandItem();
				boolean flag1 = false;
				
				if (tileEntity instanceof TileEntityRSignalProcessorContact) {

					if (!heldStack.isEmpty() && !((TileEntityRSignalProcessorContact) tileEntity).hasProcessor()) {
						
						ItemStack stackToSet = heldStack.copy();
						stackToSet.setCount(1);
						
						boolean flag = ((TileEntityRSignalProcessorContact) tileEntity).setProcessorStack(stackToSet);
						if (flag) {
							heldStack.shrink(1);
							flag1 = true;
						}
						
					} else if (((TileEntityRSignalProcessorContact) tileEntity).hasProcessor()) {
						
						ItemStack processorStack = ((TileEntityRSignalProcessorContact) tileEntity).getProcessorStack();
						
						if (ItemStackHelper.isItemStackItemEqual(processorStack, heldStack, true) && (heldStack.getCount() < heldStack.getMaxStackSize() || heldStack.isEmpty())) {
							
							((TileEntityRSignalProcessorContact) tileEntity).removeProcessor();
							if (heldStack.isEmpty()) {
								heldStack = processorStack.copy();
							} else {
								heldStack.grow(1);
							}
							flag1 = true;
							
						}
						
					}
					
				}
				
				player.inventory.items.set(player.inventory.selected, heldStack);
				return flag1 ? ActionResultType.CONSUME : ActionResultType.PASS;
				
			} else {
				
				TileEntity tileEntity = worldIn.getBlockEntity(pos);
				
				if (tileEntity instanceof TileEntityRSignalProcessorContact) {
					
					if (((TileEntityRSignalProcessorContact) tileEntity).hasProcessor()) {
						
						if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
						return ActionResultType.CONSUME;
						
					}
					
				}
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {

		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof TileEntityRSignalProcessorContact) {
			
			if (((TileEntityRSignalProcessorContact) tileEntity).hasProcessor()) {
				
				ItemStack drop = ((TileEntityRSignalProcessorContact) tileEntity).getProcessorStack();
				
				worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, drop.copy()));
				
			}
			
		}
		
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (rand.nextInt(5) == 0) {
			
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			
			if (tileEntity instanceof TileEntityRSignalProcessorContact ? ((TileEntityRSignalProcessorContact) tileEntity).hasProcessor() : false) {
				
				float f = 0.0625F;
				float width = 5.5F * f;
				float d1 = (rand.nextFloat() - 0.5F) * width;
				float d2 = (rand.nextFloat() - 0.5F) * width;
				Vector3f offVec = new Vector3f(0, 0, 0);
				
				switch (stateIn.getValue(FACING)) {
				case SOUTH: offVec = new Vector3f(d1, d2, -3.5F * f); break;
				case NORTH: offVec = new Vector3f(d1, d2, 3.5F * f); break;
				case EAST: offVec = new Vector3f(-3.5F * f, d1, d2); break;
				case WEST: offVec = new Vector3f(3.5F * f, d1, d2); break;
				case UP: offVec = new Vector3f(d1, -3.5F * f, d2); break;
				case DOWN: offVec = new Vector3f(d1, 3.5F * f, d2); break;
				default: offVec = new Vector3f(0, 0, 0); break;
				}
				
				worldIn.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, pos.getX() + 0.5F + offVec.x(), pos.getY() + 0.4F + offVec.y(), pos.getZ() + 0.5F + offVec.z(), 0, 0, 0);
				
			}
			
		}
		
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityRSignalProcessorContact();
	}

	@Override
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof TileEntityRSignalProcessorContact) {
			
			((TileEntityRSignalProcessorContact) tileEntity).setInput(signal.getChanelItem(), signal.isPowered());
			
		}
		
	}
	
	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this ? side != state.getValue(FACING) : false;
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
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public NetworkDeviceType getNetworkType() {
		return NetworkDeviceType.DEVICE;
	}

	@Override
	public NetworkDeviceIP getIP(BlockPos pos, BlockState state, World world) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityRSignalProcessorContact) {
			return ((TileEntityRSignalProcessorContact) tileEntity).deviceIP;
		}
		return NetworkDeviceIP.DEFAULT;
	}

	@Override
	public void setIP(NetworkDeviceIP ip, BlockPos pos, BlockState state, World world) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityRSignalProcessorContact) {
			((TileEntityRSignalProcessorContact) tileEntity).deviceIP = ip;
		}
	}

	@Override
	public boolean canConectNetworkWire(IWorldReader world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this ? side != state.getValue(FACING) : false;
	}
	
	@Override
	public void onMessageRecived(NetworkMessage message, World world, BlockPos pos, BlockState state) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityRSignalProcessorContact) {
			((TileEntityRSignalProcessorContact) tileEntity).onMessageRecived(message);
		}
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.signalProcessor"));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
}
