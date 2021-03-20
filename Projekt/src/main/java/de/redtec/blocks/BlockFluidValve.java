package de.redtec.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.tileentity.TileEntityFluidPipe;
import de.redtec.tileentity.TileEntityFluidValve;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IFluidConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockFluidValve extends BlockWiring implements ITileEntityProvider, IAdvancedBlockInfo {
	
	public static final BooleanProperty OPEN = BooleanProperty.create("open");
	
	public BlockFluidValve() {
		super("fluid_valve", Material.IRON, 2F, SoundType.METAL, 8);
		this.setDefaultState(this.stateContainer.getBaseState().with(OPEN, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(OPEN);
		super.fillStateContainer(builder);
	}
		
	@Override
	public boolean canConnectTo(BlockState wireState, World worldIn, BlockPos wirePos, BlockPos connectPos, Direction direction) {
		
		TileEntity te = worldIn.getTileEntity(connectPos);
		if (te instanceof IFluidConnective) {
			return ((IFluidConnective) te).canConnect(direction);
		}
		return false;
		
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityFluidValve();
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityFluidValve();
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Nullable
	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("redtec.block.info.maxMB", 2000));
			info.add(new TranslationTextComponent("redtec.block.info.fluidValve"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.isSneaking()) {
			
			TileEntity te = worldIn.getTileEntity(pos);
			
			if (te instanceof TileEntityFluidPipe) {
				((TileEntityFluidPipe) te).clear();
				return ActionResultType.SUCCESS;
			}
			
		} else {
			
			worldIn.setBlockState(pos, state.with(OPEN, !state.get(OPEN)));
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityFluidValve) {
				((TileEntityFluidValve) tileEntity).updateFlowRate();
			}
			return ActionResultType.SUCCESS;
			
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityFluidValve) {
			((TileEntityFluidValve) tileEntity).updateFlowRate();
		}
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape wireShape = super.getShape(state, worldIn, pos, context);
		return VoxelShapes.or(wireShape, Block.makeCuboidShape(4, 0, 4, 12, 9, 12));
	}
	
}
