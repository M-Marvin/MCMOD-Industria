package de.industria.recipetypes;

import de.industria.tileentity.TileEntityMBlastFurnace;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class BlastFurnaceRecipe implements IRecipe<TileEntityMBlastFurnace> {
	
	protected int smeltingTime;
	protected ItemStack[] itemsIn;
	protected ItemStack itemOut;
	protected ItemStack wasteOut;
	protected FluidStack consumtionFluid;
	protected ResourceLocation id;
	
	public BlastFurnaceRecipe(ResourceLocation id, ItemStack wasteOut, ItemStack itemOut, ItemStack[] itemsIn, FluidStack consumtionFluid, int smeltingTime) {
		this.smeltingTime = smeltingTime;
		this.itemsIn = itemsIn;
		this.itemOut = itemOut;
		this.wasteOut = wasteOut;
		this.consumtionFluid = consumtionFluid;
		this.id = id;
	}
	
	@Override
	public boolean matches(TileEntityMBlastFurnace inv, World worldIn) {
		
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
		
		return inv.fluidStorage.getFluid() == this.consumtionFluid.getFluid() && this.consumtionFluid.getAmount() <= inv.fluidStorage.getAmount();
		
	}
	
	@Override
	public ItemStack getCraftingResult(TileEntityMBlastFurnace inv) {
		return itemOut.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		 return this.itemOut.copy();
	}
		
	public ItemStack getWasteOut() {
		return this.wasteOut;
	}
	
	public FluidStack getConsumtionFluid() {
		return consumtionFluid;
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width == 1 && height == 3;
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
		return ModSerializer.BLAST_FURNACE;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.BLAST_FURNACE;
	}
	
}