package de.redtec.recipetypes;

import de.redtec.typeregistys.ModRecipeTypes;
import de.redtec.typeregistys.ModSerializer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AlloyRecipe implements IRecipe<IInventory> {
	
	protected int smeltingTime;
	protected ItemStack[] itemsIn;
	protected ItemStack itemOut;
	protected ResourceLocation id;
	
	public AlloyRecipe(ResourceLocation id, ItemStack itemOut, ItemStack[] itemsIn, int smeltingTime) {
		this.smeltingTime = smeltingTime;
		this.itemsIn = itemsIn;
		this.itemOut = itemOut;
		this.id = id;
	}
	
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		
		// Check items
		ItemStack[] neededItems = this.itemsIn.clone();
		for (int i = 0; i < 3; i++) {
			ItemStack itemIn = inv.getStackInSlot(i);
			int i1;
			for (i1 = 0; i1 < neededItems.length; i1++) {
				if (neededItems[i1] == null) continue;
				if (neededItems[i1].getItem().equals(itemIn.getItem()) && neededItems[i1].getCount() <= itemIn.getCount()) {
					neededItems[i1] = null;
					i1 = -1;
					break;
				}
			}				
			// Too many items
			if (i1 != -1 && !itemIn.isEmpty()) return false;
		}
		
		for (ItemStack item : neededItems) {
			// To low items
			if (item != null) return false;
		}
		
		return true;
		
	}
	
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return itemOut.copy();
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width == 1 && height == 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		 return this.itemOut.copy();
	}
		
	public int getSmeltingTime() {
		return smeltingTime;
	}
	
	public ItemStack[] getItemsIn() {
		return itemsIn;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.ALLOY;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.ALLOY;
	}
	
}
