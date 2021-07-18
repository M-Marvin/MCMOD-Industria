package de.industria.recipetypes;

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
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		JsonElement jsonelement = (JsonElement)(JSONUtils.isArrayNode(json, "ingredient") ? JSONUtils.getAsJsonArray(json, "ingredient") : JSONUtils.getAsJsonObject(json, "ingredient"));
		Ingredient itemIn = Ingredient.fromJson(jsonelement);
		JsonArray resultArray = json.get("results").getAsJsonArray();
		ItemStack itemOut1 = ShapedRecipe.itemFromJson(resultArray.get(0).getAsJsonObject());
		ItemStack itemOut2 = resultArray.size() > 1 ? ShapedRecipe.itemFromJson(resultArray.get(1).getAsJsonObject()) : ItemStack.EMPTY;
		ItemStack itemOut3 = resultArray.size() > 2 ? ShapedRecipe.itemFromJson(resultArray.get(2).getAsJsonObject()) : ItemStack.EMPTY;
		int schredderTime = json.get("schreddertime").getAsInt();
		Item schredderTool = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("schreddertool").getAsString()));
		int schredderDamage = json.get("schredderdamage").getAsInt();
		return this.factory.create(recipeId, itemIn, itemOut1, itemOut2, itemOut3, schredderTime, schredderTool, schredderDamage);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		Ingredient itemIn = Ingredient.fromNetwork(buffer);
		ItemStack itemOut1 = buffer.readItem();
		ItemStack itemOut2 = buffer.readItem();
		ItemStack itemOut3 = buffer.readItem();
		int schredderTime = buffer.readInt();
		Item schredderTool = buffer.readItem().getItem();
		int schredderDamage = buffer.readInt();
		return factory.create(recipeId, itemIn, itemOut1, itemOut2, itemOut3, schredderTime, schredderTool, schredderDamage);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		recipe.itemIn.toNetwork(buffer);
		buffer.writeItem(recipe.itemOut1);
		buffer.writeItem(recipe.itemOut2);
		buffer.writeItem(recipe.itemOut3);
		buffer.writeInt(recipe.schredderTime);
		buffer.writeItem(new ItemStack(recipe.schredderTool));
		buffer.writeInt(recipe.schredderDamage);
	}
	
	public interface IFactory<T extends SchredderRecipe> {
		T create(ResourceLocation id, Ingredient itemIn, ItemStack itemOut1, ItemStack itemOut2, ItemStack itemOut3, int schredderTime, Item schredderTool, int schredderDamage);
	}
	
}
