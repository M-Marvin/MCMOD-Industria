package de.m_marvin.industria.core.electrics.circuits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.electrics.engine.SPICE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CircuitTemplateManager extends SimplePreparableReloadListener<Map<ResourceLocation, CircuitTemplate>> {
	
	private static final Logger LOGGER = Industria.LOGGER;
	private static final String PATH_NET_SUFIX = ".net";
	private static final String PATH_JSON_SUFIX = ".json";
	
	private final Gson gson;
	private final String directory;
	private Map<ResourceLocation, CircuitTemplate> byLocation = new HashMap<>();
	
	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent event) {
		event.addListener(new CircuitTemplateManager(new Gson(), "circuits"));
	}
	
	public CircuitTemplateManager(Gson gson, String directory) {
		this.gson = gson;
		this.directory = directory;
	}
	
	public CircuitTemplate getTemplate(ResourceLocation location) {
		return this.byLocation.get(location);
	}
	
	public Map<ResourceLocation, CircuitTemplate> getTemplates() {
		return this.byLocation;
	}
	
	protected ResourceLocation getPreparedJsonPath(ResourceLocation rl) {
		return new ResourceLocation(rl.getNamespace(), this.directory + "/" + rl.getPath() + PATH_JSON_SUFIX);
	}

	protected ResourceLocation getPreparedNetPath(ResourceLocation rl) {
		return new ResourceLocation(rl.getNamespace(), this.directory + "/" + rl.getPath() + PATH_NET_SUFIX);
	}
	
	@Override
	protected Map<ResourceLocation, CircuitTemplate> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		
		Map<ResourceLocation, CircuitTemplate> map = Maps.newHashMap();
		
		for (ResourceLocation resourceLocation : resourceManager.listResources(this.directory, (file) -> {
			return file.endsWith(PATH_JSON_SUFIX);
		})) {
			String path = resourceLocation.getPath();
			ResourceLocation namedLocation = new ResourceLocation(resourceLocation.getNamespace(), path.substring(0, path.length() - PATH_JSON_SUFIX.length()));
			
			try {
				Resource resource = resourceManager.getResource(resourceLocation);
				InputStream inputStream = resource.getInputStream();
				Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				JsonObject json = gson.fromJson(reader, JsonObject.class);
				
				if (json != null) {
					
					if (json.has("Properties") && json.has("Networks")) {
						
						String[] properties = gson.fromJson(json.get("Properties"), String[].class);
						String[] networks = gson.fromJson(json.get("Networks"), String[].class);
						
						Resource netResource = resourceManager.getResource(new ResourceLocation(resourceLocation.getNamespace(), namedLocation.getPath() + PATH_NET_SUFIX));
						InputStream netInputStream = netResource.getInputStream();
						BufferedReader netReader = new BufferedReader(new InputStreamReader(netInputStream, StandardCharsets.UTF_8));
						
						StringBuilder netBuilder = new StringBuilder();
						String line;
						while ((line = netReader.readLine()) != null) {
							netBuilder.append(line).append("\n");
						}
						
						netReader.close();
						
						CircuitTemplate circuit = map.put(namedLocation, new CircuitTemplate(networks, properties, netBuilder.toString()));
						if (circuit != null) {
							throw new IllegalStateException("Duplicate circuit template file ignored with ID " + namedLocation);
						}
						
					} else {
						LOGGER.error("Couldn't load circuit template file as its null or empty", namedLocation, resourceLocation);
					}
					
				}
							
			} catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
				LOGGER.error("Couldn't parse circuit template file ", namedLocation, resourceLocation, jsonparseexception);
			}
		}
		
		LOGGER.info("Loaded " + map.size() + " circuit templates");
		
		return map;
	}

	@Override
	protected void apply(Map<ResourceLocation, CircuitTemplate> map, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		
		List<ResourceLocation> notVerifyed = new ArrayList<>();
		for (Entry<ResourceLocation, CircuitTemplate> entry : map.entrySet()) {
			
			CircuitTemplate template = entry.getValue();
			template.setDefault();
			String circuit = template.plot();
			
			if (!SPICE.loadCircuit(circuit)) {
				notVerifyed.add(entry.getKey());
				LOGGER.error("Couldn't verify circuit template ", entry.getKey());
			}
			
		}
		
		notVerifyed.forEach(map::remove);
		this.byLocation = map;
		
		LOGGER.info("Verified " + map.size() + " circuit templates");
		
	}
	
}
