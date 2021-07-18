package de.industria.recipetypes;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MetalFormRecipeSerializer<T extends MetalFormRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public MetalFormRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack itemIn = ShapedRecipe.itemFromJson(json.get("ingredientItem").getAsJsonObject());
		ItemStack itemOut = ShapedRecipe.itemFromJson(json.get("resultItem").getAsJsonObject());
		int processTime = json.get("processTime").getAsInt();
		
		return this.factory.create(recipeId, itemOut, itemIn, processTime);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack itemOut = buffer.readItem();
		ItemStack itemIn = buffer.readItem();
		int processTime = buffer.readInt();
		return this.factory.create(recipeId, itemOut, itemIn, processTime);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeItem(recipe.itemOut);
		buffer.writeItem(recipe.itemIn);
		buffer.writeInt(recipe.processTime);
	}
	
	public interface IFactory<T extends MetalFormRecipe> {
		T create(ResourceLocation id, ItemStack itemOut, ItemStack itemIn, int processTime);
	}
	
}
