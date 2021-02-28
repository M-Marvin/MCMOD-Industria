package de.redtec.recipetypes;

import de.redtec.registys.ModRecipeTypes;
import de.redtec.registys.ModSerializer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class WashingRecipe implements IRecipe<IInventory> {
	
	public ResourceLocation id;
	public Ingredient itemIn;
	public ItemStack itemOut1;
	public ItemStack itemOut2;
	public ItemStack itemOut3;
	public int washingTime;
	
	public WashingRecipe(ResourceLocation id, ItemStack itemOut1, Ingredient itemIn, ItemStack itemOut2, ItemStack itemOut3, int washingTime) {
		this.id = id;
		this.itemIn = itemIn;
		this.itemOut1 = itemOut1;
		this.itemOut2 = itemOut2;
		this.itemOut3 = itemOut3;
		this.washingTime = washingTime;
	}
	
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return this.itemIn.test(inv.getStackInSlot(0));
	}
	
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return this.itemOut1.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width == 1 && height == 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.itemOut1.copy();
	}
	
	public ItemStack getRecipeOutput2() {
		return this.itemOut2.copy();
	}
	
	public ItemStack getRecipeOutput3() {
		return this.itemOut3.copy();
	}
	
	public int getWashingTime() {
		return this.washingTime;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.WASHING;
	}
	
	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.WASHING;
	}
	
}