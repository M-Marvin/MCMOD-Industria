package de.redtec.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.util.text.ITextComponent;

public interface IAdvancedBlockInfo {
	
	public List<ITextComponent> getBlockInfo();
	
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER();
	
}
