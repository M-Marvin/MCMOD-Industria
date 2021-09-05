package de.industria.specialrecipes;

import com.google.gson.JsonObject;

import de.industria.blocks.BlockBurnedCable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SpecialMacerateBurnedCableSerializer<T extends RecipeMacerateBurnedCable> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

	private IFactory<T> factory;
	
	public SpecialMacerateBurnedCableSerializer(IFactory<T> factory) {
		this.factory = factory;
	}
	
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack burnedCableItem = ShapedRecipe.itemFromJson(json.get("burnedCable").getAsJsonObject());
		ItemStack materialItem = ShapedRecipe.itemFromJson(json.get("material").getAsJsonObject());
		return this.factory.create(recipeId, BlockBurnedCable.getBurnedCable(Block.byItem(burnedCableItem.getItem())), materialItem);
	}
	
	public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack burnedCableItem = buffer.readItem();
		ItemStack materialItem = buffer.readItem();
		return this.factory.create(recipeId, burnedCableItem, materialItem);
	}
	
	@Override
	public void toNetwork(PacketBuffer buffer, T recipe) {
		buffer.writeItem(recipe.getIngredient().getItems()[0]);
		buffer.writeItem(recipe.getResultItem());
	}
	
	public interface IFactory<T extends RecipeMacerateBurnedCable> {
		T create(ResourceLocation id, ItemStack burnedCableItem, ItemStack materialItem);
	}
}
