package de.redtec.recipetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BlendingRecipeSerializer<T extends BlendingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public BlendingRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T read(ResourceLocation recipeId, JsonObject json) {
		JsonArray ingredientsItem = json.get("ingredientItems").getAsJsonArray();
		ItemStack[] itemsIn = new ItemStack[ingredientsItem.size()];
		for (int i = 0; i < itemsIn.length; i++) itemsIn[i] = ShapedRecipe.deserializeItem(ingredientsItem.get(i).getAsJsonObject());
		JsonArray ingredientsFluid = json.get("ingredientFluids").getAsJsonArray();
		FluidStack[] fluidsIn = new FluidStack[ingredientsFluid.size()];
		for (int i = 0; i < fluidsIn.length; i++) fluidsIn[i] = BlendingRecipeSerializer.deserializeFluidStack(ingredientsFluid.get(i).getAsJsonObject());
		FluidStack fluidOut = BlendingRecipeSerializer.deserializeFluidStack(json.get("resultFluid").getAsJsonObject());
		int mixingTime = json.get("mixingTime").getAsInt();
		
		return this.factory.create(recipeId, itemsIn, fluidsIn, fluidOut, mixingTime);
	}
	
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack[] itemsIn = new ItemStack[buffer.readInt()];
		for (int i = 0; i < itemsIn.length; i++) {
			itemsIn[i] = buffer.readItemStack();
		}
		FluidStack[] fluidsIn = new FluidStack[buffer.readInt()];
		for (int i = 0; i < fluidsIn.length; i++) {
			fluidsIn[i] = buffer.readFluidStack();
		}
		FluidStack fluidOut = buffer.readFluidStack();
		int mixingTime = buffer.readInt();
		return factory.create(recipeId, itemsIn, fluidsIn, fluidOut, mixingTime);
	}
	
	@Override
	public void write(PacketBuffer buffer, T recipe) {
		buffer.writeInt(recipe.itemsIn.length);
		for (int i = 0; i < recipe.itemsIn.length; i++) {
			buffer.writeItemStack(recipe.itemsIn[i]);
		}
		buffer.writeInt(recipe.fluidsIn.length);
		for (int i = 0; i < recipe.fluidsIn.length; i++) {
			buffer.writeFluidStack(recipe.fluidsIn[i]);
		}
		buffer.writeFluidStack(recipe.fluidOut);
		buffer.writeInt(recipe.mixingTime);
	}
	
	public interface IFactory<T extends BlendingRecipe> {
		T create(ResourceLocation id, ItemStack[] itemsIn, FluidStack[] fluidsIn, FluidStack fluidOut, int mixingTime);
	}
	
	@SuppressWarnings("deprecation")
	public static FluidStack deserializeFluidStack(JsonObject json) {
		
		if (json.has("fluid")) {
			ResourceLocation fluidName = new ResourceLocation(json.get("fluid").getAsString());
			Fluid fluid = Registry.FLUID.getOrDefault(fluidName);
			int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
			return new FluidStack(fluid, amount);
		} else {
			return FluidStack.EMPTY;
		}
		
	}
	
}
