package de.industria.recipetypes;

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

public class RifiningRecipeSerializer<T extends RifiningRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public RifiningRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T read(ResourceLocation recipeId, JsonObject json) {
		JsonArray resultItem = json.get("resultItems").getAsJsonArray();
		ItemStack[] itemsOut = new ItemStack[3];
		itemsOut[0] = ShapedRecipe.deserializeItem(resultItem.get(0).getAsJsonObject());
		itemsOut[1] = resultItem.size() > 1 ? ShapedRecipe.deserializeItem(resultItem.get(1).getAsJsonObject()) : ItemStack.EMPTY;
		itemsOut[2] = resultItem.size() > 2 ? ShapedRecipe.deserializeItem(resultItem.get(2).getAsJsonObject()) : ItemStack.EMPTY;
		FluidStack fluidIn = RifiningRecipeSerializer.deserializeFluidStack(json.get("ingredientFluid").getAsJsonObject());
		FluidStack fluidOut = RifiningRecipeSerializer.deserializeFluidStack(json.get("resultFluid").getAsJsonObject());
		int rifiningTime = json.get("rifiningTime").getAsInt();
		
		return this.factory.create(recipeId, itemsOut, fluidIn, fluidOut, rifiningTime);
	}
	
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack[] itemsOut = new ItemStack[3];
		for (int i = 0; i < 3; i++) {
			itemsOut[i] = buffer.readItemStack();
		}
		FluidStack fluidIn = buffer.readFluidStack();
		FluidStack fluidOut = buffer.readFluidStack();
		int rifiningTime = buffer.readInt();
		return factory.create(recipeId, itemsOut, fluidIn, fluidOut, rifiningTime);
	}
	
	@Override
	public void write(PacketBuffer buffer, T recipe) {
		for (int i = 0; i < 3; i++) {
			buffer.writeItemStack(recipe.itemsOut[i]);
		}
		buffer.writeFluidStack(recipe.fluidIn);
		buffer.writeFluidStack(recipe.fluidOut);
		buffer.writeInt(recipe.rifiningTime);
	}
	
	public interface IFactory<T extends RifiningRecipe> {
		T create(ResourceLocation id, ItemStack[] itemsOut, FluidStack fluidIn, FluidStack fluidOut, int rifiningTime);
	}
	
	@SuppressWarnings("deprecation")
	public static FluidStack deserializeFluidStack(JsonObject json) {
		
		ResourceLocation fluidName = new ResourceLocation(json.get("fluid").getAsString());
		Fluid fluid = Registry.FLUID.getOrDefault(fluidName);
		int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
		return new FluidStack(fluid, amount);
		
	}
	
}
