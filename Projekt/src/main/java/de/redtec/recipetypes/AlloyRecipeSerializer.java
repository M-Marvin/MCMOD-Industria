package de.redtec.recipetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AlloyRecipeSerializer<T extends AlloyRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public AlloyRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T read(ResourceLocation recipeId, JsonObject json) {
		JsonArray ingredientsItem = json.get("ingredientItems").getAsJsonArray();
		ItemStack[] itemsIn = new ItemStack[ingredientsItem.size()];
		for (int i = 0; i < itemsIn.length; i++) itemsIn[i] = ShapedRecipe.deserializeItem(ingredientsItem.get(i).getAsJsonObject());
		ItemStack itemOut = ShapedRecipe.deserializeItem(json.get("resultItem").getAsJsonObject());
		int smeltingTime = json.get("smeltingTime").getAsInt();
		
		return this.factory.create(recipeId, itemOut, itemsIn, smeltingTime);
	}
	
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack[] itemsIn = new ItemStack[buffer.readInt()];
		for (int i = 0; i < itemsIn.length; i++) {
			itemsIn[i] = buffer.readItemStack();
		}
		ItemStack itemOut = buffer.readItemStack();
		int mixingTime = buffer.readInt();
		return factory.create(recipeId, itemOut, itemsIn, mixingTime);
	}
	
	@Override
	public void write(PacketBuffer buffer, T recipe) {
		buffer.writeInt(recipe.itemsIn.length);
		for (int i = 0; i < recipe.itemsIn.length; i++) {
			buffer.writeItemStack(recipe.itemsIn[i]);
		}
		buffer.writeItemStack(recipe.itemOut);
		buffer.writeInt(recipe.smeltingTime);
		buffer.writeInt(recipe.smeltingTime);
	}
	
	public interface IFactory<T extends AlloyRecipe> {
		T create(ResourceLocation id, ItemStack itemOut, ItemStack[] itemsIn, int smeltingTime);
	}
	
}
