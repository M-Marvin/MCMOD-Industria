package de.redtec.recipetypes;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FluidBathRecipeSerializer<T extends FluidBathRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public FluidBathRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T read(ResourceLocation recipeId, JsonObject json) {
		ItemStack itemIn = ShapedRecipe.deserializeItem(json.get("ingredientItem").getAsJsonObject());
		FluidStack fluidIn = BlendingRecipeSerializer.deserializeFluidStack(json.get("ingredientFluid").getAsJsonObject());
		ItemStack itemOut = ShapedRecipe.deserializeItem(json.get("resultItem").getAsJsonObject());
		FluidStack fluidOut = BlendingRecipeSerializer.deserializeFluidStack(json.get("wasteFluid").getAsJsonObject());
		int processTime = json.get("processTime").getAsInt();
		
		return this.factory.create(recipeId, itemOut, itemIn, fluidOut, fluidIn, processTime);
	}
	
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack itemOut = buffer.readItemStack();
		ItemStack itemIn = buffer.readItemStack();
		FluidStack fluidOut = buffer.readFluidStack();
		FluidStack fluidIn = buffer.readFluidStack();
		int processTime = buffer.readInt();
		return this.factory.create(recipeId, itemOut, itemIn, fluidOut, fluidIn, processTime);
	}
	
	@Override
	public void write(PacketBuffer buffer, T recipe) {
		buffer.writeItemStack(recipe.itemOut);
		buffer.writeItemStack(recipe.itemIn);
		buffer.writeFluidStack(recipe.fluidOut);
		buffer.writeFluidStack(recipe.fluidIn);
	}
	
	public interface IFactory<T extends FluidBathRecipe> {
		T create(ResourceLocation id, ItemStack itemOut, ItemStack itemIn, FluidStack fluidOut, FluidStack fluidIn, int processTime);
	}
	
}
