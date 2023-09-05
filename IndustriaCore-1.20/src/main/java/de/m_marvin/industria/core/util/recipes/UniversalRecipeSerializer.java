package de.m_marvin.industria.core.util.recipes;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class UniversalRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

	protected final BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork;
	protected final BiConsumer<FriendlyByteBuf, T> toNetwork;
	protected final BiFunction<ResourceLocation, JsonObject, T> fromJson;
	
	public UniversalRecipeSerializer(BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork, BiConsumer<FriendlyByteBuf, T> toNetwork, BiFunction<ResourceLocation, JsonObject, T> fromJson) {
		this.fromJson = fromJson;
		this.fromNetwork = fromNetwork;
		this.toNetwork = toNetwork;
	}
	
	@Override
	public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
		return this.fromJson.apply(pRecipeId, pSerializedRecipe);
	}

	@Override
	public @Nullable T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
		return this.fromNetwork.apply(pRecipeId, pBuffer);
	}

	@Override
	public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
		this.toNetwork.accept(pBuffer, pRecipe);
	}
	
}
