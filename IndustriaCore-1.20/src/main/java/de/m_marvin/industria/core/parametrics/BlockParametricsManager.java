package de.m_marvin.industria.core.parametrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.parametrics.properties.Parameter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockParametricsManager extends SimplePreparableReloadListener<Map<ResourceLocation, BlockParametrics>> {
	
	private static final Logger LOGGER = IndustriaCore.LOGGER;
	private static final String PATH_JSON_SUFIX = ".json";
	
	private final Gson gson;
	private final String directory;
	private Map<ResourceLocation, BlockParametrics> byLocation = new HashMap<>();
	
	private static BlockParametricsManager instance;
	
	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent event) {
		instance = new BlockParametricsManager(new Gson(), "parametrics");
		event.addListener(instance);
	}
	
	public static BlockParametricsManager getInstance() {
		return instance;
	}
	
	public BlockParametricsManager(Gson gson, String directory) {
		this.gson = gson;
		this.directory = directory;
	}
	
	public BlockParametrics getParametrics(Block block) {
		return getParametrics(ForgeRegistries.BLOCKS.getKey(block));
	}
	
	public BlockParametrics getParametrics(ResourceLocation location) {
		if (!this.byLocation.containsKey(location)) {
			this.byLocation.put(location, new BlockParametrics(location));
			LOGGER.error("Couldn't find electric parametric '" + location + "'!");
		}
		return this.byLocation.get(location);
	}
	
	public Map<ResourceLocation, BlockParametrics> getTemplates() {
		return this.byLocation;
	}
	
	protected ResourceLocation getPreparedJsonPath(ResourceLocation rl) {
		return new ResourceLocation(rl.getNamespace(), this.directory + "/" + rl.getPath() + PATH_JSON_SUFIX);
	}
	
	@Override
	protected Map<ResourceLocation, BlockParametrics> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		
		Map<ResourceLocation, BlockParametrics> map = Maps.newHashMap();
		
		for (Entry<ResourceLocation, Resource> resourceEntry : resourceManager.listResources(this.directory, (file) -> {
			return file.getPath().endsWith(PATH_JSON_SUFIX);
		}).entrySet()) {
			ResourceLocation resourceLocation = resourceEntry.getKey();
			String path = resourceLocation.getPath();
			int i = path.indexOf("/");
			ResourceLocation namedLocation = new ResourceLocation(resourceLocation.getNamespace(), path.substring(i + 1, path.length() - PATH_JSON_SUFIX.length()));
			
			try {
				Resource resource = resourceEntry.getValue();
				InputStream inputStream = resource.open();
				Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				
				JsonObject parametricsJson = gson.fromJson(reader, JsonObject.class);
				BlockParametrics parametrics = new BlockParametrics(namedLocation);
				for (String parameterName : parametricsJson.keySet()) {
					
					Parameter<?> parameter = Parameter.getParameterByName(parameterName);
					if (parameter != null) {
						parametrics.setParameter(parameter, parameter.parseValue(parametricsJson.get(parameterName)));
					}
					
				}
				
				if (map.put(namedLocation, parametrics) != null) {
					throw new IllegalStateException("Duplicate parametrics file ignored with ID " + namedLocation);
				}
						
			} catch (NoSuchElementException | IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
				LOGGER.error("Couldn't parse parametric file {}: {}", namedLocation, jsonparseexception);
			}
		}
		
		LOGGER.info("Loaded " + map.size() + " parametric");
		
		return map;
	}

	@Override
	protected void apply(Map<ResourceLocation, BlockParametrics> map, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		this.byLocation = map;
	}
	
}
