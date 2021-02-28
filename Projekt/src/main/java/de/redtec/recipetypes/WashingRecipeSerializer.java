package de.redtec.recipetypes;

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
	
	public T read(ResourceLocation recipeId, JsonObject json) {
		Ingredient itemIn = Ingredient.deserialize(json.get("ingredient").getAsJsonObject());
		JsonArray resultArray = json.get("resultItems").getAsJsonArray();
		ItemStack itemOut1 = ShapedRecipe.deserializeItem(resultArray.get(0).getAsJsonObject());
		ItemStack itemOut2 = resultArray.size() > 1 ? ShapedRecipe.deserializeItem(resultArray.get(1).getAsJsonObject()) : ItemStack.EMPTY;
		ItemStack itemOut3 = resultArray.size() > 2 ? ShapedRecipe.deserializeItem(resultArray.get(2).getAsJsonObject()) : ItemStack.EMPTY;
		int washingTime = json.get("washingTime").getAsInt();
		return this.factory.create(recipeId, itemOut1, itemIn, itemOut2, itemOut3, washingTime);
	}
	
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		Ingredient itemIn = Ingredient.read(buffer);
		ItemStack itemOut1 = buffer.readItemStack();
		ItemStack itemOut2 = buffer.readItemStack();
		ItemStack itemOut3 = buffer.readItemStack();
		int washingTime = buffer.readInt();
		return factory.create(recipeId, itemOut1, itemIn, itemOut2, itemOut3, washingTime);
	}
	
	@Override
	public void write(PacketBuffer buffer, T recipe) {
		recipe.itemIn.write(buffer);
		buffer.writeItemStack(recipe.itemOut1);
		buffer.writeItemStack(recipe.itemOut2);
		buffer.writeItemStack(recipe.itemOut3);
		buffer.writeInt(recipe.washingTime);
	}
	
	public interface IFactory<T extends WashingRecipe> {
		T create(ResourceLocation id, ItemStack itemOut1, Ingredient itemIn, ItemStack itemOut2, ItemStack itemOut3, int washingTime);
	}
	
}
