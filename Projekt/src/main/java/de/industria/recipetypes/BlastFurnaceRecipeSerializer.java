package de.industria.recipetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BlastFurnaceRecipeSerializer<T extends BlastFurnaceRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public BlastFurnaceRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		JsonArray ingredientsItem = json.get("ingredientItems").getAsJsonArray();
		ItemStack[] itemsIn = new ItemStack[ingredientsItem.size()];
		for (int i = 0; i < itemsIn.length; i++) itemsIn[i] = ShapedRecipe.itemFromJson(ingredientsItem.get(i).getAsJsonObject());
		ItemStack itemOut = ShapedRecipe.itemFromJson(json.get("resultItem").getAsJsonObject());
		ItemStack wasteOut = ShapedRecipe.itemFromJson(json.get("wasteItem").getAsJsonObject());
		FluidStack consumtionFluid = BlendingRecipeSerializer.deserializeFluidStack(json.get("consumtionFluid").getAsJsonObject());
		int smeltingTime = json.get("smeltingTime").getAsInt();
		
		return this.factory.create(recipeId, wasteOut, itemOut, itemsIn, consumtionFluid, smeltingTime);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack[] itemsIn = new ItemStack[buffer.readInt()];
		for (int i = 0; i < itemsIn.length; i++) {
			itemsIn[i] = buffer.readItem();
		}
		ItemStack itemOut = buffer.readItem();
		ItemStack wasteOut = buffer.readItem();
		FluidStack consumtionFluid = buffer.readFluidStack();
		int mixingTime = buffer.readInt();
		return factory.create(recipeId, wasteOut, itemOut, itemsIn, consumtionFluid, mixingTime);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeInt(recipe.itemsIn.length);
		for (int i = 0; i < recipe.itemsIn.length; i++) {
			buffer.writeItem(recipe.itemsIn[i]);
		}
		buffer.writeItem(recipe.itemOut);
		buffer.writeItem(recipe.wasteOut);
		buffer.writeFluidStack(recipe.consumtionFluid);
		buffer.writeInt(recipe.smeltingTime);
	}
	
	public interface IFactory<T extends BlastFurnaceRecipe> {
		T create(ResourceLocation id, ItemStack wasteOut, ItemStack itemOut, ItemStack[] itemsIn, FluidStack consumtionFluid, int smeltingTime);
	}
	
}
