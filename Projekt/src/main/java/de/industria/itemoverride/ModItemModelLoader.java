package de.industria.itemoverride;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

//public class ModItemModelLoader implements IModelLoader<ModItemModelLoader.Geometry> {
//	
//	
//	
//	public static class Geometry implements IModelGeometry<Geometry> {
//
//		@Override
//		public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
//			
//			return null;
//		}
//
//		@Override
//		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
//			
//			return null;
//		}
//		
//		public static class Baked implements IBakedModel {
//			
//			private BlockModel base;
//			private ModelBakery bakery;
//			private Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
//			private IModelTransform modelTransform;
//			private ItemOverrideList itemOverrideList;
//			
//			private List<IBakedModel> cache = new ArrayList<IBakedModel>();
//			
//			private IModelData getModel(IModelData extraData) {
//				if (extraData instanceof ModItemOverride) {
//					
//				}
//				return getModel();
//			}
//			
//			private IModelData getModel() {
//				
//				
//				
//			}
//			
//			
//			
//			@Override
//			public List<BakedQuad> getQuads(BlockState p_200117_1_, Direction p_200117_2_, Random p_200117_3_, IModelData extraData) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			@Override
//			public boolean useAmbientOcclusion() {
//				return getBakedModel().useAmbientOcclusion();
//			}
//			@Override
//			public boolean isGui3d() {
//				return getBakedModel().isGui3d();
//			}
//			@Override
//			public boolean usesBlockLight() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			@Override
//			public boolean isCustomRenderer() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			@Override
//			public TextureAtlasSprite getParticleIcon() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			@Override
//			public ItemOverrideList getOverrides() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//		}
//		
//	}
//
//	@Override
//	public void onResourceManagerReload(IResourceManager resourceManager) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public ModItemModelLoader.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
//		
//	}
//	
//}


