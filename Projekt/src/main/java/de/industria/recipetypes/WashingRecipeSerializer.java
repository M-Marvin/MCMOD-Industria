package de.industria.recipetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class WashingRecipeSerializer<T extends WashingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public WashingRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		Ingredient itemIn = Ingredient.fromJson(json.get("ingredient").getAsJsonObject());
		JsonArray resultArray = json.get("resultItems").getAsJsonArray();
		ItemStack itemOut1 = ShapedRecipe.itemFromJson(resultArray.get(0).getAsJsonObject());
		ItemStack itemOut2 = resultArray.size() > 1 ? ShapedRecipe.itemFromJson(resultArray.get(1).getAsJsonObject()) : ItemStack.EMPTY;
		ItemStack itemOut3 = resultArray.size() > 2 ? ShapedRecipe.itemFromJson(resultArray.get(2).getAsJsonObject()) : ItemStack.EMPTY;
		int washingTime = json.get("washingTime").getAsInt();
		return this.factory.create(recipeId, itemOut1, itemIn, itemOut2, itemOut3, washingTime);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		Ingredient itemIn = Ingredient.fromNetwork(buffer);
		ItemStack itemOut1 = buffer.readItem();
		ItemStack itemOut2 = buffer.readItem();
		ItemStack itemOut3 = buffer.readItem();
		int washingTime = buffer.readInt();
		return factory.create(recipeId, itemOut1, itemIn, itemOut2, itemOut3, washingTime);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		recipe.itemIn.toNetwork(buffer);
		buffer.writeItem(recipe.itemOut1);
		buffer.writeItem(recipe.itemOut2);
		buffer.writeItem(recipe.itemOut3);
		buffer.writeInt(recipe.washingTime);
	}
	
	public interface IFactory<T extends WashingRecipe> {
		T create(ResourceLocation id, ItemStack itemOut1, Ingredient itemIn, ItemStack itemOut2, ItemStack itemOut3, int washingTime);
	}
	
}
