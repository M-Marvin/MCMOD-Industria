package de.redtec.recipetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SchredderRecipeSerializer<T extends SchredderRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public SchredderRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T read(ResourceLocation recipeId, JsonObject json) {
		JsonElement jsonelement = (JsonElement)(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
		Ingredient itemIn = Ingredient.deserialize(jsonelement);
		JsonArray resultArray = json.get("results").getAsJsonArray();
		ItemStack itemOut1 = ShapedRecipe.deserializeItem(resultArray.get(0).getAsJsonObject());
		ItemStack itemOut2 = resultArray.size() > 1 ? ShapedRecipe.deserializeItem(resultArray.get(1).getAsJsonObject()) : ItemStack.EMPTY;
		ItemStack itemOut3 = resultArray.size() > 2 ? ShapedRecipe.deserializeItem(resultArray.get(2).getAsJsonObject()) : ItemStack.EMPTY;
		int schredderTime = json.get("schreddertime").getAsInt();
		Item schredderTool = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("schreddertool").getAsString()));
		int schredderDamage = json.get("schredderdamage").getAsInt();
		return this.factory.create(recipeId, itemIn, itemOut1, itemOut2, itemOut3, schredderTime, schredderTool, schredderDamage);
	}
	
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		Ingredient itemIn = Ingredient.read(buffer);
		ItemStack itemOut1 = buffer.readItemStack();
		ItemStack itemOut2 = buffer.readItemStack();
		ItemStack itemOut3 = buffer.readItemStack();
		int schredderTime = buffer.readInt();
		Item schredderTool = buffer.readItemStack().getItem();
		int schredderDamage = buffer.readInt();
		return factory.create(recipeId, itemIn, itemOut1, itemOut2, itemOut3, schredderTime, schredderTool, schredderDamage);
	}
	
	@Override
	public void write(PacketBuffer buffer, T recipe) {
		recipe.itemIn.write(buffer);
		buffer.writeItemStack(recipe.itemOut1);
		buffer.writeItemStack(recipe.itemOut2);
		buffer.writeItemStack(recipe.itemOut3);
		buffer.writeInt(recipe.schredderTime);
		buffer.writeItemStack(new ItemStack(recipe.schredderTool));
		buffer.writeInt(recipe.schredderDamage);
	}
	
	public interface IFactory<T extends SchredderRecipe> {
		T create(ResourceLocation id, Ingredient itemIn, ItemStack itemOut1, ItemStack itemOut2, ItemStack itemOut3, int schredderTime, Item schredderTool, int schredderDamage);
	}
	
}
