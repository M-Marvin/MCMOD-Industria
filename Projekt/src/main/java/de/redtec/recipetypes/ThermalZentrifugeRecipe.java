package de.redtec.recipetypes;

import de.redtec.registys.ModRecipeTypes;
import de.redtec.registys.ModSerializer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ThermalZentrifugeRecipe implements IRecipe<IInventory> {
	
	protected int rifiningTime;
	protected ItemStack itemIn;
	protected ItemStack[] itemsOut;
	protected ResourceLocation id;
	
	public ThermalZentrifugeRecipe(ResourceLocation id, ItemStack itemIn, ItemStack[] itemsOut, int rifiningTime) {
		this.rifiningTime = rifiningTime;
		this.itemIn = itemIn;
		this.itemsOut = itemsOut;
		this.id = id;
	}
	
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return itemIn.getItem() == inv.getStackInSlot(0).getItem() && itemIn.getCount() <= inv.getStackInSlot(0).getCount();
	}
	
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return itemsOut[0].copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width == 1 && height == 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		 return this.itemsOut[0].copy();
	}
	
	public ItemStack getRecipeOutput2() {
		 return this.itemsOut[1].copy();
	}
	
	public ItemStack getRecipeOutput3() {
		 return this.itemsOut[2].copy();
	}
	
	public int getRifiningTime() {
		return rifiningTime;
	}
	
	public ItemStack getIngredient() {
		return this.itemIn;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.THERMAL_ZENTRIFUGE;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.THERMAL_ZENTRIFUGE;
	}
	
}
