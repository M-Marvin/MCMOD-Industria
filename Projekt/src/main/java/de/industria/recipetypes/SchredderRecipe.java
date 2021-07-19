package de.industria.recipetypes;

import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SchredderRecipe implements IRecipe<IInventory> {
	
	public ResourceLocation id;
	public Ingredient itemIn;
	public ItemStack itemOut1;
	public ItemStack itemOut2;
	public ItemStack itemOut3;
	public int schredderTime;
	public Item schredderTool;
	public int schredderDamage;
	
	public SchredderRecipe(ResourceLocation id, Ingredient itemIn, ItemStack itemOut1, ItemStack itemOut2, ItemStack itemOut3, int schredderTime, Item schredderTool, int schredderDamage) {
		this.id = id;
		this.itemIn = itemIn;
		this.itemOut1 = itemOut1;
		this.itemOut2 = itemOut2;
		this.itemOut3 = itemOut3;
		this.schredderTime = schredderTime;
		this.schredderTool = schredderTool;
		this.schredderDamage = schredderDamage;
	}
	
	public Ingredient getIngredient() {
		return this.itemIn;
	}
	
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return this.itemIn.test(inv.getItem(0)) && inv.getItem(4).getItem() == this.schredderTool;
	}
	
	@Override
	public ItemStack assemble(IInventory inv) {
		return this.itemOut1.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width == 1 && height == 1;
	}

	@Override
	public ItemStack getResultItem() {
		return this.itemOut1.copy();
	}
	
	public ItemStack getResultItem2() {
		return this.itemOut2.copy();
	}
	
	public ItemStack getResultItem3() {
		return this.itemOut3.copy();
	}
	
	public int getSchredderTime() {
		return schredderTime;
	}
	
	public int getSchredderDamage() {
		return schredderDamage;
	}
	
	public Item getSchredderTool() {
		return schredderTool;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.SCHREDDER;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.SCHREDDER;
	}
	
}
