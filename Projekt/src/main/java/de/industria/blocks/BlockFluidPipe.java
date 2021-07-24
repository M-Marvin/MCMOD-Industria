package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityFluidPipe;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.blockfeatures.IFluidConnective;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockFluidPipe extends BlockWiring implements ITileEntityProvider, IAdvancedBlockInfo {
	
	public BlockFluidPipe(String name) {
		super(name, Material.METAL, 2F, SoundType.METAL, 8);
	}
	
	public BlockFluidPipe() {
		this("fluid_pipe");
	}
	
	@Override
	public boolean canConnectTo(BlockState wireState, World worldIn, BlockPos wirePos, BlockPos connectPos, Direction direction) {
		
		TileEntity te = worldIn.getBlockEntity(connectPos);
		if (te instanceof IFluidConnective) {
			return ((IFluidConnective) te).canConnect(direction);
		}
		return false;
		
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityFluidPipe();
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityFluidPipe();
	}
	
	public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.triggerEvent(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Nullable
	public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.maxMB", 2000));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.isShiftKeyDown()) {
			
			TileEntity te = worldIn.getBlockEntity(pos);
			
			if (te instanceof TileEntityFluidPipe) {
				((TileEntityFluidPipe) te).clear();
				return ActionResultType.SUCCESS;
			}
			
		}
		
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

}
