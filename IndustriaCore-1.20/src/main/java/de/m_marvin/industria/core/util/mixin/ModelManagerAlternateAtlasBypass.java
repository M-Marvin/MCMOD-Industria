//package de.m_marvin.industria.core.util.mixin;
//
//import org.spongepowered.asm.mixin.Debug;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//import com.llamalad7.mixinextras.sugar.Local;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.texture.AbstractTexture;
//import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
//import net.minecraft.client.renderer.texture.TextureAtlas;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.resources.model.AtlasSet;
//import net.minecraft.client.resources.model.Material;
//import net.minecraft.client.resources.model.ModelManager;
//import net.minecraft.client.resources.model.AtlasSet.StitchResult;
//import net.minecraft.resources.ResourceLocation;
//
//@Debug(export = true)
//@Mixin(ModelManager.class)
//public class ModelManagerAlternateAtlasBypass {
//	
////	@WrapOperation(
////			method = "net/minecraft/client/resources/model/ModelManager.<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/color/block/BlockColors;I)V",
////			at = @At(value = "NEW", target = "Lnet/minecraft/client/resources/model/AtlasSet;<init>(Ljava/util/Map;Lnet/minecraft/client/renderer/texture/TextureManager;)V;")
////	)
////	private static AtlasSet newAtlasSetWithCustomAtlas
////	
////	
////	
////	
////	@WrapOperation(
////			method = "net/minecraft/client/resources/model/ModelManager.lambda$loadModels$15(Ljava/util/Map;Lcom/google/common/collect/Multimap;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/Material;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
////			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/AtlasSet$StitchResult;getSprite(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;")
////    )
////	private static TextureAtlasSprite getSpriteFromCustomAtlasIfStichResultNull(AtlasSet.StitchResult stitchResult, ResourceLocation textureLocation, Operation<TextureAtlasSprite> originalGetSprite, @Local Material material) {
////		
////		if (stitchResult != null) return originalGetSprite.call(stitchResult, textureLocation);
////		
////		AbstractTexture atlasTexture = Minecraft.getInstance().getTextureManager().getTexture(material.atlasLocation());
////		if (atlasTexture instanceof TextureAtlas atlas) {
////
////			System.out.println("SPRITE FROM CUSTOM ATLAS > " + textureLocation + " > " + atlas.getSprite(textureLocation));
////			
////			return atlas.getSprite(textureLocation);
////		}
////		
////		System.out.println("TEXTURE NOT FOUND IN ATLAS: " + textureLocation + " in " + material.atlasLocation() + " > " + atlasTexture);
////		return null;
////		
////	}
////	
////	@SuppressWarnings("deprecation")
////	@WrapOperation(
////			method = "net/minecraft/client/resources/model/ModelManager.lambda$loadModels$15(Ljava/util/Map;Lcom/google/common/collect/Multimap;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/Material;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
////			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/AtlasSet$StitchResult;missing()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;")
////	)
////	private static TextureAtlasSprite getMissingTextureSpriteFromCustomAtlasIfStitchResultNull(AtlasSet.StitchResult stitchResult, Operation<TextureAtlasSprite> originalMissing, @Local Material material) {
////		
////		if (stitchResult != null) return originalMissing.call(stitchResult);
////		
////		AbstractTexture atlasTexture = Minecraft.getInstance().getTextureManager().getTexture(material.atlasLocation());
////		if (atlasTexture instanceof TextureAtlas atlas) {
////			
////
////			System.out.println("MISSINGNO FROM CUSTOM ATLAS > " + atlas.getSprite(MissingTextureAtlasSprite.getLocation()));
////			
////			
////			// get missingno from the custom atlas
////			return atlas.getSprite(MissingTextureAtlasSprite.getLocation());
////		} else {
////
////
////			System.out.println("MISSINGNO FROM BLOCK ATLAS > " + ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getSprite(MissingTextureAtlasSprite.getLocation()));
////			
////			
////			// fallback to missigno from the block atlas should the custom atlas be entirely not available
////			return ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getSprite(MissingTextureAtlasSprite.getLocation());
////		}
////	}
//	
//}
