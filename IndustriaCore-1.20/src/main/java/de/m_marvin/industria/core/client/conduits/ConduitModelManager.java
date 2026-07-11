package de.m_marvin.industria.core.client.conduits;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.jline.utils.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import de.m_marvin.industria.core.util.events.ModelAtlasRegisterEvent;
import dev.engine_room.flywheel.api.material.Material;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.model.ModelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConduitModelManager {

	private ConduitModelManager() {}
	
	public static final int TEXTURE_MAP_WIDTH = 64;
	public static final int TEXTURE_MAP_HEIGHT = 32;
	
	private static final Supplier<TextureManager> TEXTURE_MANAGER = Minecraft.getInstance()::getTextureManager; 
	private static final Supplier<ModelManager> MODEL_MANAGER = Minecraft.getInstance()::getModelManager;
	private static final Supplier<ResourceManager> RESOURCE_MANAGER = Minecraft.getInstance()::getResourceManager;
	private static final Logger LOGGER = IndustriaCore.LOGGER;
	private static final Gson GSON = new Gson();
	private static final String CONDUIT_STATE_FOLDER = "conduitstates";
	private static final String PATH_JSON_SUFIX = ".json";
	
	public static final ResourceLocation LOCATION_CONDUITS = ResourceLocation.tryBuild(IndustriaCore.MODID, "textures/atlas/conduits.png");
	public static final ResourceLocation MISSING_MODEL = ResourceLocation.tryBuild(IndustriaCore.MODID, "conduit/missingno");
	
	private static Map<ConduitState, BakedModel[]> state2modelMap = new HashMap<>();
	private static Map<ConduitState, ResourceLocation[]> state2modelLocationMap;
	
	@SubscribeEvent
	public static void onRegisterAtlases(ModelAtlasRegisterEvent event) {
		// the primary conduit texture atlas used for all conduit models
		event.register(LOCATION_CONDUITS, ResourceLocation.tryBuild(IndustriaCore.MODID, "conduits"));
	}
	
	@SubscribeEvent
	public static void test(ModelEvent.RegisterAdditional event) {
		state2modelLocationMap = loadConduitModelMap();
		state2modelLocationMap.values().stream().flatMap(Stream::of).forEach(event::register);
		event.register(MISSING_MODEL);
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAfterDelay(state2modelMap::clear, 10);
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
	
	public static BakedModel[] getModels(ConduitState conduit) {
		BakedModel[] models = state2modelMap.get(conduit);
		if (models == null) {
			ResourceLocation[] modelLocation = state2modelLocationMap.get(conduit);
			if (modelLocation == null)
				modelLocation = new ResourceLocation[] {MISSING_MODEL};
			models = Stream.of(modelLocation).map(MODEL_MANAGER.get()::getModel).toArray(BakedModel[]::new);
			state2modelMap.put(conduit, models);
		}
		return models;
	}
	
	private static boolean compareModelKeys(String first, String second) {
		String[] firstKeys = first.split(",");
		String[] secondKeys = second.split(",");
		if (firstKeys.length != secondKeys.length) return false;
		for (String s1 : firstKeys) {
			boolean match = false;
			for (String s2 : secondKeys) {
				if (s1.equals(s2)) {
					match = true;
					break;
				}
			}
			if (!match) return false;
		}
		return true;
	}
	
	private static Map<ConduitState, ResourceLocation[]> loadConduitModelMap() {
		ResourceManager resourceManager = RESOURCE_MANAGER.get();
		return Conduits.CONDUITS_REGISTRY.get().getEntries().stream().map(conduitEntry -> {
			ResourceLocation conduitId = conduitEntry.getKey().location();
			ResourceLocation stateDefinitionLocation = ResourceLocation.tryBuild(conduitId.getNamespace(), CONDUIT_STATE_FOLDER + "/" + conduitId.getPath() + PATH_JSON_SUFIX);
			Optional<Resource> stateDefinitionResource = resourceManager.getResource(stateDefinitionLocation);
			if (stateDefinitionResource.isEmpty()) {
				LOGGER.warn("Missing conduit state definition json for conduit {}:\n   {}", conduitId, stateDefinitionLocation);
				return new Pair<>(conduitEntry.getValue(), new JsonObject());
			}
			try {
				InputStream resourceStream = stateDefinitionResource.get().open();
				JsonObject stateDefinition = GSON.fromJson(new InputStreamReader(resourceStream), JsonObject.class);
				if (!stateDefinition.has("variants")) {
					LOGGER.warn("Missing conduit state definition variants entry for conduit {}:\n   {}", conduitId, stateDefinitionLocation);
					return new Pair<>(conduitEntry.getValue(), new JsonObject());
				}
				JsonObject variantsMap = stateDefinition.get("variants").getAsJsonObject();
				return new Pair<>(conduitEntry.getValue(), variantsMap);
			} catch (IOException e) {
				LOGGER.warn("IO exception while attempting to read from conduit state definition json {}:\n   {}", conduitId, stateDefinitionLocation, e);
				return new Pair<>(conduitEntry.getValue(), new JsonObject());
			} catch (JsonSyntaxException e) {
				LOGGER.error("JSON syntax while attempting to read from conduit state definition json {}:\n   {}", conduitId, stateDefinitionLocation, e);
				return new Pair<>(conduitEntry.getValue(), new JsonObject());
			}
		})
		.flatMap(pair -> pair.getFirst().getStateDefinition().getPossibleStates().stream().map(state -> {
			ResourceLocation conduitId = Conduits.CONDUITS_REGISTRY.get().getKey(state.getConduit());
			String variantKey = BlockModelShaper.statePropertiesToString(state.getValues());
			Optional<String> matchingModelKey = pair.getSecond().keySet().stream().filter(key -> compareModelKeys(key, variantKey)).findAny();
			if (matchingModelKey.isEmpty()) {
				LOGGER.warn("Missing conduit model for conduit: {}#{}", conduitId, variantKey);
				return new Pair<>(state, new ResourceLocation[] {MISSING_MODEL});
			}
			JsonObject modelEntry = pair.getSecond().get(matchingModelKey.get()).getAsJsonObject();
			if (!modelEntry.has("segments")) {
				LOGGER.warn("Missing segments entry for conduit state definition: {}#{}", conduitId, variantKey);
				return new Pair<>(state, new ResourceLocation[] {MISSING_MODEL});
			}
			JsonElement segmentsJson = modelEntry.get("segments");
			ResourceLocation[] modelLocations = null;
			if (segmentsJson.isJsonArray() && segmentsJson.getAsJsonArray().size() > 0) {
				modelLocations = segmentsJson.getAsJsonArray().asList().stream()
						.map(JsonElement::getAsString)
						.map(ResourceLocation::parse)
						.toArray(ResourceLocation[]::new);
			} else if (segmentsJson.isJsonPrimitive()) {
				modelLocations = new ResourceLocation[] { ResourceLocation.tryParse(segmentsJson.getAsString()) };
			} else {
				LOGGER.warn("Invalid segments entry for conduit state definition: {}#{}", conduitId, variantKey);
				return new Pair<>(state, new ResourceLocation[] {MISSING_MODEL});
			}
			return new Pair<>(state, modelLocations);
		}))
		.collect(Collectors.toMap(pair -> pair.getFirst(), pair -> pair.getSecond()));
	}
	
}
