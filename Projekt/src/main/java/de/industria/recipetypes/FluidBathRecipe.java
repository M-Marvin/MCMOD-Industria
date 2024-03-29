package de.industria.recipetypes;

import de.industria.tileentity.TileEntityMFluidBath;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class FluidBathRecipe implements IRecipe<TileEntityMFluidBath> {
	
	protected int processTime;
	protected ItemStack itemIn;
	protected ItemStack itemOut;
	protected FluidStack fluidIn;
	protected FluidStack fluidOut;
	protected ResourceLocation id;
	
	public FluidBathRecipe(ResourceLocation id, ItemStack itemOut, ItemStack itemIn, FluidStack fluidOut, FluidStack fludiIn, int processTime) {
		this.processTime = processTime;
		this.itemIn = itemIn;
		this.itemOut = itemOut;
		this.fluidIn = fludiIn;
		this.fluidOut = fluidOut;
		this.id = id;
	}
	
	@Override
	public boolean matches(TileEntityMFluidBath inv, World worldIn) {
		return	inv.fluidIn.getFluid() == this.fluidIn.getFluid() && inv.fluidIn.getAmount() >= this.fluidIn.getAmount() &&
				inv.getItem(0).getItem() == this.itemIn.getItem() && inv.getItem(0).getCount() >= this.itemIn.getCount();
	}
	
	@Override
	public ItemStack assemble(TileEntityMFluidBath inv) {
		return itemOut.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width == 1 && height == 1;
	}
	
	@Override
	public ItemStack getResultItem() {
		 return this.itemOut.copy();
	}
		
	public int getProcessTime() {
		return processTime;
	}
	
	public ItemStack getItemIn() {
		return itemIn;
	}
	
	public FluidStack getFluidIn() {
		return fluidIn;
	}
	
	public FluidStack getFluidOut() {
		return fluidOut;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.FLUID_BATH;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.FLUID_BATH;
	}
	
}