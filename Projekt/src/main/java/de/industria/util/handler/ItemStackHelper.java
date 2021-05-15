package de.industria.util.handler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class ItemStackHelper {
	
	public static boolean isItemStackItemEqual(ItemStack stack, ItemStack other, boolean whenEmpty) {
		
		if (stack == null) return whenEmpty;
		
        if (stack.isEmpty())
            return false;
        else if (other.isEmpty())
        	return whenEmpty;
        else
            return !other.isEmpty() && stack.getItem() == other.getItem() &&
            (other.getDisplayName().getUnformattedComponentText().equals(stack.getDisplayName().getUnformattedComponentText()) || stack.getDisplayName().getUnformattedComponentText().length() == 0);
		
	}
	
	public static boolean canMergeRecipeStacks(ItemStack stack, ItemStack recipeItem) {
		
		if (stack.isEmpty()) return true;
		if (stack.getItem() == recipeItem.getItem() && stack.getMaxStackSize() - stack.getCount() >= recipeItem.getCount()) return true;
		return false;
		
	}
	
	public static boolean canMergeRecipeFluidStacks(FluidStack fluidStack, FluidStack recipeStack, int maxFluidStorage) {
		
		if (fluidStack.isEmpty()) return true;
		if (fluidStack.getFluid() == recipeStack.getFluid() && fluidStack.getAmount() + recipeStack.getAmount() <= maxFluidStorage) return true;
		return false;
		
	}
	
	public static String getBlockStateString(BlockState state) {
		String s = state.toString();
		if (s.startsWith("Block{")) {
			s = s.split("Block\\{")[1];
			if (s.contains("}")) {
				return s.split("\\}").length > 1 ? s.split("\\}")[0] + s.split("\\}")[1] : s.split("\\}")[0];
			}
		}
		return "minecraft:air";
	}

	public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
		double d0 = (double)EntityType.ITEM.getWidth();
		double d1 = 1.0D - d0;
		double d2 = d0 / 2.0D;
		double d3 = Math.floor(x) + worldIn.rand.nextDouble() * d1 + d2;
		double d4 = Math.floor(y) + worldIn.rand.nextDouble() * d1;
		double d5 = Math.floor(z) + worldIn.rand.nextDouble() * d1 + d2;
		
		while(!stack.isEmpty()) {
			ItemEntity itementity = new ItemEntity(worldIn, d3, d4, d5, stack.split(worldIn.rand.nextInt(21) + 10));
			itementity.setMotion(worldIn.rand.nextGaussian() * (double)0.05F, worldIn.rand.nextGaussian() * (double)0.05F + (double)0.2F, worldIn.rand.nextGaussian() * (double)0.05F);
			worldIn.addEntity(itementity);
		}
	}

	public static void spawnItemStackWithPickupDelay(World worldIn, double x, double y, double z, ItemStack stack) {
		double d0 = (double)EntityType.ITEM.getWidth();
		double d1 = 1.0D - d0;
		double d2 = d0 / 2.0D;
		double d3 = Math.floor(x) + worldIn.rand.nextDouble() * d1 + d2;
		double d4 = Math.floor(y) + worldIn.rand.nextDouble() * d1;
		double d5 = Math.floor(z) + worldIn.rand.nextDouble() * d1 + d2;
		
		while(!stack.isEmpty()) {
			ItemEntity itementity = new ItemEntity(worldIn, d3, d4, d5, stack.split(worldIn.rand.nextInt(21) + 10));
			itementity.setMotion(worldIn.rand.nextGaussian() * (double)0.05F, worldIn.rand.nextGaussian() * (double)0.05F + (double)0.2F, worldIn.rand.nextGaussian() * (double)0.05F);
			itementity.setPickupDelay(15);
			worldIn.addEntity(itementity);
		}
	}
	
}
