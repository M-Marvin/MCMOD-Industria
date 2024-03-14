package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;

import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.tileentity.TileEntityRHarvester;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockRHarvester extends BlockContainerBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty SILKTOUCH = BooleanProperty.create("silktouch");
	
	public BlockRHarvester() {
		super("harvester", Material.STONE, 3.5F, SoundType.STONE);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(SILKTOUCH, false));
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, SILKTOUCH);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(POWERED, false).setValue(SILKTOUCH, false);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.getItemInHand(handIn).getItem() == ModItems.hammer) {
			worldIn.setBlockAndUpdate(pos, state.setValue(SILKTOUCH, !state.getValue(SILKTOUCH)));
			return ActionResultType.SUCCESS;
		}
		
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof TileEntityRHarvester) {
			
			if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
			return ActionResultType.CONSUME;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		boolean powered = state.getValue(POWERED);
		boolean power = worldIn.getBestNeighborSignal(fromPos) > 0 || worldIn.hasNeighborSignal(pos);
		
		if (powered != power) {
			worldIn.blockEvent(pos, this, 1, power ? 1 : 0);
		}
		
	}
	
	@Override
	public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		
		if (id == 1) {

			boolean power = param == 1;
			
			worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, power));
			
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			
			if (power && tileEntity instanceof TileEntityRHarvester) {
				
				List<ItemStack> items = harvest(pos, pos.relative(state.getValue(FACING)), worldIn, state.getValue(SILKTOUCH));
				
				if (items != null) {
					
					List<ItemStack> drops = ((TileEntityRHarvester) tileEntity).tryToInser(items);
					
					for (ItemStack stack : drops) {
						ItemEntity item = new ItemEntity(worldIn, pos.relative(state.getValue(FACING)).getX(), pos.relative(state.getValue(FACING)).getY(), pos.relative(state.getValue(FACING)).getZ(), stack);
						item.setDefaultPickUpDelay();
						worldIn.addFreshEntity(item);
					}
					
				}
				
			}
			
		}
		
		return super.triggerEvent(state, worldIn, pos, id, param);
	}
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public List<ItemStack> harvest(BlockPos pos, BlockPos harvestPos, World world, boolean useSilkTouch) {

		BlockState harvestState = world.getBlockState(harvestPos);
		
		if (harvestState.getBlock() == Blocks.MOVING_PISTON || harvestState.getBlock() == ModItems.advanced_moving_block) {
			TileEntity moving = world.getBlockEntity(harvestPos);
			if (moving instanceof PistonTileEntity) {
				harvestState = ((PistonTileEntity) moving).getMovedState();
			} else if (moving instanceof TileEntityAdvancedMovingBlock) {
				harvestState = ((TileEntityAdvancedMovingBlock) moving).getPistonState();
			}
		}
		
		if (harvestState.isAir()) {
			return null;
		} else if (harvestState.getBlock() == Blocks.BEDROCK || harvestState.getBlock() == Blocks.BARRIER || harvestState.getBlock() == Blocks.HOPPER) {
			return null;
		} else {
			
			if (!harvestState.hasTileEntity() && useSilkTouch) {
				
				Item item = harvestState.getBlock().asItem();
				List<ItemStack> drops = new ArrayList<>();
				drops.add(new ItemStack(item, 1));

				world.destroyBlock(harvestPos, false);
				
				return drops;
				
			} else {

				TileEntity tileEntity = world.getBlockEntity(harvestPos);
				List<ItemStack> drops = harvestState.getBlock().getDrops(harvestState, (ServerWorld) world, harvestPos, tileEntity);
				
				world.destroyBlock(harvestPos, false);
				
				return drops;
				
			}
			
		}
		
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityRHarvester();
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
