package de.industria.recipetypes;

import de.industria.tileentity.TileEntityMRaffinery;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class RifiningRecipe implements IRecipe<TileEntityMRaffinery> {
	
	public ResourceLocation id;
	public ItemStack[] itemsOut;
	public FluidStack fluidIn;
	public FluidStack fluidOut;
	public int rifiningTime;
	
	public RifiningRecipe(ResourceLocation id, ItemStack[] itemsOut, FluidStack fluidIn, FluidStack fluidOut, int rifiningTime) {
		super();
		this.id = id;
		this.itemsOut = itemsOut;
		this.fluidIn = fluidIn;
		this.fluidOut = fluidOut;
		this.rifiningTime = rifiningTime;
	}
	
	@Override
	public boolean matches(TileEntityMRaffinery inv, World worldIn) {
		return inv.fluidIn.getFluid() == this.fluidIn.getFluid() && inv.fluidIn.getAmount() >= this.fluidIn.getAmount();		
	}
	
	@Override
	public ItemStack assemble(TileEntityMRaffinery inv) {
		return this.itemsOut[0].copy();
	}
	
	public FluidStack getFluidIn() {
		return fluidIn;
	}
	
	public FluidStack getFluidOut() {
		return fluidOut;
	}
	
	public ItemStack[] getItemsOut() {
		return itemsOut;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width == 1 && height == 1;
	}

	@Override
	public ItemStack getResultItem() {
		return this.itemsOut[0].copy();
	}
	
	public ItemStack getResultItem2() {
		return this.itemsOut[1].copy();
	}
	
	public ItemStack getResultItem3() {
		return this.itemsOut[2].copy();
	}
	
	public FluidStack getResultItemFluid() {
		return this.fluidOut.copy();
	}
	
	public int getRifiningTime() {
		return this.rifiningTime;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.RIFINING;
	}
	
	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.RIFINING;
	}
	
}