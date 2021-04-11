package de.industria.util.handler;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidBucketHelper {
	
	public static FluidStack transferBuckets(IInventory inventory, int soltOffset, FluidStack storage, int maxStorage) {
		
		ItemStack stackIn = inventory.getStackInSlot(soltOffset);
		ItemStack stackOut = inventory.getStackInSlot(soltOffset + 1);
		
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
					
					inventory.setInventorySlotContents(soltOffset, stackIn);
					inventory.setInventorySlotContents(soltOffset + 1, stackOut);
					
				}
				
			} else if (fluid.isEmpty() && !storage.isEmpty()) {
				
				Item bucket = storage.getFluid().getFilledBucket();
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

					inventory.setInventorySlotContents(soltOffset, stackIn);
					inventory.setInventorySlotContents(soltOffset + 1, stackOut);
					
				}
				
			}
			
		}
		
		return storage;
		
	}
	
}
