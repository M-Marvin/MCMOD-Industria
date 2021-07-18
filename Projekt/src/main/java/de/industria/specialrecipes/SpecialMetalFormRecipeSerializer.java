package de.industria.specialrecipes;

import com.google.gson.JsonObject;

import de.industria.recipetypes.MetalFormRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SpecialMetalFormRecipeSerializer<T extends RecipeFormCladding> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

	private IFactory<T> factory;
	
	public SpecialMetalFormRecipeSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		int processTime = json.get("processTime").getAsInt();
		return this.factory.create(recipeId, processTime);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		int processTime = buffer.readInt();
		return this.factory.create(recipeId, processTime);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeInt(recipe.getProcessTime());
	}
	
	public interface IFactory<T extends MetalFormRecipe> {
		T create(ResourceLocation id, int processTime);
	}
	
}
