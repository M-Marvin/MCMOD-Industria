package de.industria.specialrecipes;

import de.industria.items.ItemProcessor;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ProzessorCopy extends SpecialRecipe {
	
	public ProzessorCopy(ResourceLocation idIn) {
		super(idIn);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		
		boolean invalidItem = false;
		boolean hasOneCode = false;
		int prozessors = 0;
		
		for (int i = 0; i < 9; i++) {
			
			ItemStack stack = inv.getStackInSlot(i);
			
			if (stack.getItem() instanceof ItemProcessor)  {
				
				if (stack.hasTag()) {
					hasOneCode = !hasOneCode;
				}
				
				prozessors++;
				
			} else if (!stack.isEmpty()) {
				invalidItem = true;
			}
			
		}
		
		return hasOneCode && prozessors == 2 && !invalidItem;
		
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {

		for (int i = 0; i < 9; i++) {
			
			ItemStack stack = inv.getStackInSlot(i);
			
			if (stack.getItem() instanceof ItemProcessor)  {
				
				if (stack.hasTag()) {
					
					ItemStack stack2 = new ItemStack(stack.getItem());
					stack2.setTag(stack.getTag());
					return stack2;
					
				}
				
			}
			
		}
		
		return null;
		
	}

	@Override
	public boolean canFit(int width, int height) {
		
		return width * height > 1;
		
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
	      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

	      for(int i = 0; i < nonnulllist.size(); ++i) {
	         ItemStack itemstack = inv.getStackInSlot(i);
	         if (itemstack.hasTag()) {
	            ItemStack itemstack1 = itemstack.copy();
	            itemstack1.setCount(1);
	            nonnulllist.set(i, itemstack1);
	            break;
	         }
	      }
	      
	      return nonnulllist;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.CRAFTING_PROCESSOR_COPY;
	}

}
