package de.m_marvin.industria.core.util.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

@Mixin(BlockModel.Deserializer.class)
public abstract class BlockModelAlternateAtlasParsing {
	
	// TODO should not be necessary in future, atlas should be decided by context of model loading
	
	@ModifyExpressionValue(
		method = "net/minecraft/client/renderer/block/model/BlockModel$Deserializer.getTextureMap(Lcom/google/gson/JsonObject;)Ljava/util/Map;",
		at = @At(value = "FIELD", target = "net/minecraft/client/renderer/texture/TextureAtlas.LOCATION_BLOCKS : Lnet/minecraft/resources/ResourceLocation;")
	)
	private ResourceLocation getAlternateAtlasIfSpecified(ResourceLocation originalAtlas, JsonObject modelJson) {
		
		if (modelJson.has("atlas"))
			return new ResourceLocation(modelJson.get("atlas").getAsString());
		return originalAtlas;
		
	}
	
}
