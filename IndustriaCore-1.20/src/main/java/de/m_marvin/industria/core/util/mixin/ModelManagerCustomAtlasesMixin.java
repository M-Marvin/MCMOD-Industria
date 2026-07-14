package de.m_marvin.industria.core.util.mixin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import de.m_marvin.industria.core.util.events.ModelAtlasRegisterEvent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

@Debug(export = true)
@Mixin(ModelManager.class)
public class ModelManagerCustomAtlasesMixin {
	
	private static Map<ResourceLocation, ResourceLocation> vanillaAndCustomAtlases = null;
	
	@ModifyExpressionValue(
			method = "net/minecraft/client/resources/model/ModelManager.<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/color/block/BlockColors;I)V",
			at = @At(value = "FIELD", target = "net/minecraft/client/resources/model/ModelManager.VANILLA_ATLASES : Ljava/util/Map;")
	)
	private Map<ResourceLocation, ResourceLocation> getAtlases(Map<ResourceLocation, ResourceLocation> originalAtlases) {
		if (vanillaAndCustomAtlases == null) {
			Map<ResourceLocation, ResourceLocation> atlases = new HashMap<ResourceLocation, ResourceLocation>();
			ModelAtlasRegisterEvent registerCustomAtlasesEvent = new ModelAtlasRegisterEvent(atlases);
			ModList.get().forEachModInOrder(mc -> { if (mc instanceof FMLModContainer fml) fml.getEventBus().post(registerCustomAtlasesEvent); });
			atlases.putAll(originalAtlases);
			vanillaAndCustomAtlases = Collections.unmodifiableMap(atlases);
		}
		return vanillaAndCustomAtlases;
	}
	
	@Shadow
	private static Logger LOGGER;
	
	@SuppressWarnings("deprecation")
	@Inject(
			method = "net/minecraft/client/resources/model/ModelManager.lambda$loadModels$15(Ljava/util/Map;Lcom/google/common/collect/Multimap;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/Material;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void textureLoadingLambda(Map<ResourceLocation, AtlasSet.StitchResult> atlases, Multimap<ResourceLocation, Material> missingTexturesMap, ResourceLocation textureLocation, Material material, CallbackInfoReturnable<TextureAtlasSprite> callback) {
		
		if (!atlases.containsKey(material.atlasLocation())) {
			
			LOGGER.warn("Texture reffers to unknown alternate atlas: {} {}", textureLocation, material.atlasLocation());
			
			missingTexturesMap.put(textureLocation, material);
			callback.setReturnValue(atlases.get(TextureAtlas.LOCATION_BLOCKS).missing());
			
		}
		
	}
	
}
