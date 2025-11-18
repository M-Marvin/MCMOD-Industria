package de.m_marvin.industria.core.client.conduits;

import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.util.events.ModelAtlasRegisterEvent;
import dev.engine_room.flywheel.api.material.Material;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.model.ModelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConduitModelManager {

	public static final int TEXTURE_MAP_WIDTH = 64;
	public static final int TEXTURE_MAP_HEIGHT = 32;
	
	private static final Supplier<TextureManager> TEXTURE_MANAGER = Minecraft.getInstance()::getTextureManager; 
	private static final Supplier<ModelManager> MODEL_MANAGER = Minecraft.getInstance()::getModelManager;
	
	public static final ResourceLocation LOCATION_CONDUITS = new ResourceLocation(IndustriaCore.MODID, "textures/atlas/conduits.png");
	
	@SubscribeEvent
	public static void onRegisterAtlases(ModelAtlasRegisterEvent event) {
		// the primary conduit texture atlas used for all conduit models
		event.register(LOCATION_CONDUITS, new ResourceLocation(IndustriaCore.MODID, "conduits"));
	}

	@SubscribeEvent
	public static void test(ModelEvent.RegisterAdditional event) {
		
		// TODO dynamic conduit model resolution
		event.register(new ResourceLocation("industriacore:conduit/electric_conduit"));
		
	}
	
	public static TextureAtlas getAtlas() {
		if (TEXTURE_MANAGER.get().getTexture(LOCATION_CONDUITS) instanceof TextureAtlas atlas)
			return atlas;
		throw new RuntimeException("conduit texture atlas not found!");
	}
	
	public static Material getMaterial(RenderType chunkRenderType, boolean shaded, boolean ambientOcclusion) {
		Material blockBaseMaterial = ModelUtil.getMaterial(chunkRenderType, shaded, ambientOcclusion);
		return SimpleMaterial.builderOf(blockBaseMaterial).texture(LOCATION_CONDUITS);
	}
	
	public static BakedModel getModel(ConduitState conduit) {
		// FIXME
		ResourceLocation modelLocation = new ResourceLocation("industriacore:conduit/electric_conduit");
		return MODEL_MANAGER.get().getModel(modelLocation);
	}
	
}
