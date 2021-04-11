package de.industria.util.blockfeatures;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

public interface IAdvancedBlockInfo {
	
	public IBlockToolType getBlockInfo();
	
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER();
	
	public default int getStackSize() {
		return 64;
	}
	
}
