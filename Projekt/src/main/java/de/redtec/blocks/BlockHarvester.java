package de.redtec.blocks;

import java.util.List;

import de.redtec.tileentity.TileEntityHarvester;
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
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockHarvester extends BlockContainerBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockHarvester() {
		super("harvester", Material.ROCK, 1, SoundType.STONE);
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, false));
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite()).with(POWERED, false);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof TileEntityHarvester) {
			
			if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
			return ActionResultType.CONSUME;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		boolean powered = state.get(POWERED);
		boolean power = worldIn.getRedstonePowerFromNeighbors(fromPos) > 0 || worldIn.isBlockPowered(pos);
		
		if (powered != power) {
			
			worldIn.setBlockState(pos, state.with(POWERED, power));
			
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			
			if (power && tileEntity instanceof TileEntityHarvester) {
				
				List<ItemStack> items = harvest(pos.offset(state.get(FACING)), worldIn);
				
				if (items != null) {
					
					List<ItemStack> drops = ((TileEntityHarvester) tileEntity).tryToInser(items);
					
					for (ItemStack stack : drops) {
						ItemEntity item = new ItemEntity(worldIn, pos.offset(state.get(FACING)).getX(), pos.offset(state.get(FACING)).getX(), pos.offset(state.get(FACING)).getX(), stack);
						item.setDefaultPickupDelay();
						worldIn.addEntity(item);
					}
					
				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public List<ItemStack> harvest(BlockPos harvestPos, World world) {

		BlockState harvestState = world.getBlockState(harvestPos);
		
		if (harvestState.isAir()) {
			return null;
		} else if (harvestState.getBlock() == Blocks.BEDROCK || harvestState.getBlock() == Blocks.BARRIER || harvestState.getBlock() == Blocks.HOPPER) {
			return null;
		} else {
			
			TileEntity tileEntity = world.getTileEntity(harvestPos);
			List<ItemStack> drops = harvestState.getBlock().getDrops(harvestState, (ServerWorld) world, harvestPos, tileEntity);
			
			world.destroyBlock(harvestPos, false);
			
			return drops;
			
		}
		
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityHarvester();
	}
	
}
