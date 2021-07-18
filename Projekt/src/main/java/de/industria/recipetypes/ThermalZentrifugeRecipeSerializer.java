package de.industria.recipetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ThermalZentrifugeRecipeSerializer<T extends ThermalZentrifugeRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
	
	private IFactory<T> factory;
	
	public ThermalZentrifugeRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	@Override
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack itemIn = ShapedRecipe.itemFromJson(json.get("ingredient").getAsJsonObject());
		JsonArray resultArr = json.getAsJsonArray("resultItems");
		ItemStack[] itemsOut = new ItemStack[3];
		for (int i = 0; i < 3; i++) {
			itemsOut[i] = resultArr.size() > i ? ShapedRecipe.itemFromJson(resultArr.get(i).getAsJsonObject()) : ItemStack.EMPTY;
		}
		int rifiningTime = json.get("rifiningTime").getAsInt();
		
		return this.factory.create(recipeId, itemIn, itemsOut, rifiningTime);
	}

	@Override
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack itemIn = buffer.readItem();
		ItemStack[] itemsOut = new ItemStack[3];
		for (int i = 0; i < 3; i++) {
			itemsOut[i] = buffer.readItem();
		}
		int rifiningTime = buffer.readInt();
		
		return this.factory.create(recipeId, itemIn, itemsOut, rifiningTime);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeItem(recipe.itemIn);
		for (int i = 0; i < 3; i++) {
			buffer.writeItem(recipe.itemsOut[0]);
		}
		buffer.writeInt(recipe.rifiningTime);
	}
	
	public interface IFactory<T extends ThermalZentrifugeRecipe> {
		T create(ResourceLocation id, ItemStack itemIn, ItemStack[] itemsOut, int rifiningTime);
	}
	
}
