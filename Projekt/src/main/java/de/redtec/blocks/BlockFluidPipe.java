package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.redtec.tileentity.TileEntityFluidPipe;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IFluidConnective;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockFluidPipe extends BlockWiring implements ITileEntityProvider, IAdvancedBlockInfo {
	
	public BlockFluidPipe() {
		super("fluid_pipe", Material.IRON, 2F, SoundType.METAL, 8);
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
		return new TileEntityFluidPipe();
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityFluidPipe();
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
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.maxMB", 2000));
		return info;
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

}
