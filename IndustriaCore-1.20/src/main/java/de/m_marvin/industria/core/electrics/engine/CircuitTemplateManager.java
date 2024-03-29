package de.m_marvin.industria.core.electrics.engine;

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
import de.m_marvin.industria.core.electrics.engine.network.SSyncCircuitTemplatesPackage;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CircuitTemplateManager extends SimplePreparableReloadListener<Map<ResourceLocation, CircuitTemplate>> {
	
	public static final CircuitTemplate DEFAULT_TEMPLATE = new CircuitTemplate(new String[] {}, new String[] {}, "", "");
	
	private static final Logger LOGGER = IndustriaCore.LOGGER;
	private static final String PATH_NET_SUFIX = ".net";
	private static final String PATH_JSON_SUFIX = ".json";
	
	private final Gson gson;
	private final String directory;
	private Map<ResourceLocation, CircuitTemplate> byLocation = new HashMap<>();
	
	private static CircuitTemplateManager instance;
	
	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent event) {
		instance = new CircuitTemplateManager(new Gson(), "circuits");
		event.addListener(instance);
	}
	
	public static CircuitTemplateManager getInstance() {
		return instance;
	}
	
	public CircuitTemplateManager(Gson gson, String directory) {
		this.gson = gson;
		this.directory = directory;
	}
	
	public CircuitTemplate getTemplate(ResourceLocation location) {
		if (!this.byLocation.containsKey(location)) {
			this.byLocation.put(location, DEFAULT_TEMPLATE);
			LOGGER.error("Couldn't find circuit template '" + location + "'!");
		}
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
				JsonObject json = gson.fromJson(reader, JsonObject.class);
				
				if (json != null) {
					
					if (json.has("Properties") && json.has("Networks")) {
						
						String[] properties = gson.fromJson(json.get("Properties"), String[].class);
						String[] networks = gson.fromJson(json.get("Networks"), String[].class);
						String idProperty = json.get("IdProperty").getAsString();
						
						Resource netResource = resourceManager.getResourceOrThrow(new ResourceLocation(resourceLocation.getNamespace(), path.substring(0, path.length() - PATH_JSON_SUFIX.length()) + PATH_NET_SUFIX));
						InputStream netInputStream = netResource.open();
						BufferedReader netReader = new BufferedReader(new InputStreamReader(netInputStream, StandardCharsets.UTF_8));
						
						StringBuilder netBuilder = new StringBuilder();
						String line;
						while ((line = netReader.readLine()) != null) {
							netBuilder.append(line).append("\n");
						}
						
						netReader.close();
						
						CircuitTemplate circuit = map.put(namedLocation, new CircuitTemplate(networks, properties, netBuilder.toString(), idProperty));
						if (circuit != null) {
							throw new IllegalStateException("Duplicate circuit template file ignored with ID " + namedLocation);
						}
						
					} else {
						LOGGER.error("Couldn't load circuit template file {} as its null or empty", namedLocation);
					}
					
				}
							
			} catch (NoSuchElementException | IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
				LOGGER.error("Couldn't parse circuit template file {}: {}", namedLocation, jsonparseexception);
			}
		}
		
		LOGGER.info("Loaded " + map.size() + " circuit templates");
		
		return map;
	}

	@Override
	protected void apply(Map<ResourceLocation, CircuitTemplate> map, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		this.byLocation = map;
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		ServerPlayer player = event.getPlayer();
		if (player == null) {
			IndustriaCore.NETWORK.send(PacketDistributor.ALL.noArg(), new SSyncCircuitTemplatesPackage(getInstance().byLocation));
		} else {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SSyncCircuitTemplatesPackage(getInstance().byLocation));
		}
	}
	
	public static void updateClientTemplates(Map<ResourceLocation, CircuitTemplate> templates) {
		if (instance == null) instance = new CircuitTemplateManager(null, null);
		instance.byLocation = templates;
	}
	
}
