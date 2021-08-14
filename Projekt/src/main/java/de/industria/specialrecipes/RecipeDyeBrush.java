package de.industria.specialrecipes;

import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeDyeBrush extends SpecialRecipe {

	public RecipeDyeBrush(ResourceLocation idIn) {
		super(idIn);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		
		boolean oneDye = false;
		boolean oneBrush = false;
		
		for (int i = 0; i < 9; i++) {
			
			ItemStack stack = inv.getItem(i);
			
			if (!stack.isEmpty()) {
				if (stack.getItem() == ModItems.brush) {
					if (oneBrush) {
						return false;
					} else {
						oneBrush = true;
					}
				} else if (stack.getItem() instanceof DyeItem) {
					if (oneDye) {
						return false;
					} else {
						oneDye = true;
					}
				} else {
					return false;
				}
			}
			
		}
		
		return oneBrush && oneDye;
		
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		
		Item dyeItem = null;
		
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() instanceof DyeItem) dyeItem = stack.getItem();
		}
		
		if (dyeItem != null) {
			String paintName = "dye." + ((DyeItem) dyeItem).getDyeColor().getSerializedName();
			ItemStack dyedBrush = new ItemStack(ModItems.brush);
			ModItems.brush.setPaintName(paintName, dyedBrush);
			return dyedBrush;
		}
		
		return new ItemStack(ModItems.brush);
		
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height > 1;
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.DYE_BRUSH;
	}
	
}
