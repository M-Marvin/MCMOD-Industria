package de.industria.recipetypes;

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
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack itemIn = ShapedRecipe.itemFromJson(json.get("ingredientItem").getAsJsonObject());
		FluidStack fluidIn = BlendingRecipeSerializer.deserializeFluidStack(json.get("ingredientFluid").getAsJsonObject());
		ItemStack itemOut = ShapedRecipe.itemFromJson(json.get("resultItem").getAsJsonObject());
		FluidStack fluidOut = BlendingRecipeSerializer.deserializeFluidStack(json.get("wasteFluid").getAsJsonObject());
		int processTime = json.get("processTime").getAsInt();
		
		return this.factory.create(recipeId, itemOut, itemIn, fluidOut, fluidIn, processTime);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack itemOut = buffer.readItem();
		ItemStack itemIn = buffer.readItem();
		FluidStack fluidOut = buffer.readFluidStack();
		FluidStack fluidIn = buffer.readFluidStack();
		int processTime = buffer.readInt();
		return this.factory.create(recipeId, itemOut, itemIn, fluidOut, fluidIn, processTime);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeItem(recipe.itemOut);
		buffer.writeItem(recipe.itemIn);
		buffer.writeFluidStack(recipe.fluidOut);
		buffer.writeFluidStack(recipe.fluidIn);
		buffer.writeInt(recipe.processTime);
	}
	
	public interface IFactory<T extends FluidBathRecipe> {
		T create(ResourceLocation id, ItemStack itemOut, ItemStack itemIn, FluidStack fluidOut, FluidStack fluidIn, int processTime);
	}
	
}
