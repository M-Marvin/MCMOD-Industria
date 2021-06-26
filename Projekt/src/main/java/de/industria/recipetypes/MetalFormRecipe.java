package de.industria.recipetypes;

import de.industria.tileentity.TileEntityMMetalFormer;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MetalFormRecipe implements IRecipe<TileEntityMMetalFormer> {
	
	protected int processTime;
	protected ItemStack itemIn;
	protected ItemStack itemOut;
	protected ResourceLocation id;
	
	public MetalFormRecipe(ResourceLocation id, ItemStack itemOut, ItemStack itemIn, int processTime) {
		this.processTime = processTime;
		this.itemIn = itemIn;
		this.itemOut = itemOut;
		this.id = id;
	}
	
	@Override
	public boolean matches(TileEntityMMetalFormer inv, World worldIn) {
		return inv.getStackInSlot(0).getItem() == this.itemIn.getItem() && inv.getStackInSlot(0).getCount() >= this.itemIn.getCount();
	}
	
	@Override
	public ItemStack getCraftingResult(TileEntityMMetalFormer inv) {
		return itemOut.copy();
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width == 1 && height == 1;
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		 return this.itemOut.copy();
	}
		
	public int getProcessTime() {
		return processTime;
	}
	
	public ItemStack getItemIn() {
		return itemIn;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.METAL_FORM;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.METAL_FORM;
	}
	
}