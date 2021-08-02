package de.industria.util.handler;

import de.industria.items.ItemFluidCannister;
import de.industria.tileentity.TileEntityFluidCannister;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidBucketHelper {
	
	public static FluidStack transferBuckets(IInventory inventory, int soltOffset, FluidStack storage, int maxStorage) {
		
		ItemStack stackIn = inventory.getItem(soltOffset);
		ItemStack stackOut = inventory.getItem(soltOffset + 1);
		
		if (stackIn.getItem() instanceof BucketItem) {
			
			FluidStack fluid = new FluidStack(((BucketItem) stackIn.getItem()).getFluid(), 1000);
			
			if (!fluid.isEmpty() && ItemStackHelper.canMergeRecipeFluidStacks(storage, fluid, maxStorage)) {
				
				ItemStack resultingBucket = stackIn.getContainerItem();
				
				if (ItemStackHelper.canMergeRecipeStacks(stackOut, resultingBucket)) {
					
					if (storage.isEmpty()) {
						storage = new FluidStack(fluid, 1000);
					} else {
						storage.grow(1000);
					}
					
					if (stackOut.isEmpty()) {
						stackOut = resultingBucket.copy();
					} else {
						stackOut.grow(resultingBucket.getCount());
					}
					
					stackIn.shrink(1);
					
					inventory.setItem(soltOffset, stackIn);
					inventory.setItem(soltOffset + 1, stackOut);
					
				}
				
			} else if (fluid.isEmpty() && !storage.isEmpty()) {
				
				Item bucket = storage.getFluid().getBucket();
				ItemStack bucketStack = new ItemStack(bucket, 1);
				
				if (ItemStackHelper.canMergeRecipeStacks(stackOut, bucketStack)) {
					
					if (storage.getAmount() < 1000) bucketStack = new ItemStack(stackIn.getItem(), 1);
					storage.shrink(Math.min(1000, storage.getAmount()));
					
					if (stackOut.isEmpty()) {
						stackOut = bucketStack;
					} else {
						stackOut.grow(bucketStack.getCount());
					}
					
					stackIn.shrink(1);

					inventory.setItem(soltOffset, stackIn);
					inventory.setItem(soltOffset + 1, stackOut);
					
				}
				
			}
			
		}
		
		if (stackIn.getItem() instanceof ItemFluidCannister) {
			
			ItemFluidCannister cannisterItem = (ItemFluidCannister) stackIn.getItem();
			FluidStack content = cannisterItem.getContent(stackIn);
			
			if (!content.isEmpty() && storage.getAmount() < maxStorage) {
				
				if ((storage.isEmpty() || storage.getFluid() == content.getFluid()) && stackOut.isEmpty()) {
					
					int transfer = Math.min(content.getAmount(), maxStorage - storage.getAmount());
					Fluid fluid = content.getFluid();
					
					content.shrink(transfer);
					if (storage.isEmpty()) {
						storage = new FluidStack(fluid, transfer);
					} else {
						storage.grow(transfer);
					}
					
					cannisterItem.setContent(stackIn, content);
					
					stackOut = stackIn.copy();
					stackIn = ItemStack.EMPTY;
					
					inventory.setItem(soltOffset, stackIn);
					inventory.setItem(soltOffset + 1, stackOut);
					
				}
				
			} else if (content.getAmount() < TileEntityFluidCannister.MAX_CONTENT && !storage.isEmpty()) {
				
				if ((content.getFluid() == storage.getFluid() || content.isEmpty()) && stackOut.isEmpty()) {

					int transfer = Math.min(TileEntityFluidCannister.MAX_CONTENT - content.getAmount(), storage.getAmount());
					Fluid fluid = storage.getFluid();
					
					storage.shrink(transfer);
					if (content.isEmpty()) {
						content = new FluidStack(fluid, transfer);
					} else {
						content.grow(transfer);
					}
					
					cannisterItem.setContent(stackIn, content);
					
					stackOut = stackIn.copy();
					stackIn = ItemStack.EMPTY;

					inventory.setItem(soltOffset, stackIn);
					inventory.setItem(soltOffset + 1, stackOut);
					
				}
				
			}
			
		}
		
		return storage;
		
	}
	
}
