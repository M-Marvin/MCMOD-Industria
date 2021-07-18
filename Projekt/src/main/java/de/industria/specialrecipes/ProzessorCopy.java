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
			
			ItemStack stack = inv.getItem(i);
			
			if (stack.getItem() instanceof ItemProcessor)  {
				
				for (String line : ((ItemProcessor) stack.getItem()).getCodeLinesFromProcessor(stack)) {
					if (!line.equals("")) hasOneCode = true;
				}
				
				prozessors++;
				
			} else if (!stack.isEmpty()) {
				invalidItem = true;
			}
			
		}
		
		return hasOneCode && prozessors == 2 && !invalidItem;
		
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		
		String[] codeToCopy = null;
		int copyIndex = 0;
		
		for (int i = 0; i < 9; i++) {
			
			ItemStack stack = inv.getItem(i);
			
			if (stack.getItem() instanceof ItemProcessor)  {
				
				if (codeToCopy != null && i != copyIndex) {
					
					ItemStack stack2 = stack.copy();
					((ItemProcessor) stack2.getItem()).storeCodeLinesInProcessor(stack2, codeToCopy);
					return stack2;
					
				} else {

					for (String line : ((ItemProcessor) stack.getItem()).getCodeLinesFromProcessor(stack)) {
						if (!line.equals("")) {
							codeToCopy = ((ItemProcessor) stack.getItem()).getCodeLinesFromProcessor(stack);
							copyIndex = i;
							i = 0;
						}
					}
					
				}
				
			}
			
		}
		
		return ItemStack.EMPTY;
		
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		
		return width * height > 1;
		
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
	      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

	      for(int i = 0; i < nonnulllist.size(); ++i) {
	         ItemStack itemstack = inv.getItem(i);
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
