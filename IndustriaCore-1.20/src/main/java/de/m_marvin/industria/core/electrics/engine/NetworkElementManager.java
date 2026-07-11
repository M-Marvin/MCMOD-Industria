package de.m_marvin.industria.core.electrics.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import de.m_marvin.basicxml.XMLException;
import de.m_marvin.basicxml.XMLInputStream;
import de.m_marvin.basicxml.marshaling.XMLMarshalingException;
import de.m_marvin.basicxml.marshaling.XMLUnmarshaler;
import de.m_marvin.industria.IndustriaCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tvnlnna.nodal.NodalElement;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NetworkElementManager extends SimplePreparableReloadListener<Map<ResourceLocation, NodalElement>> {
	
	private static final Logger LOGGER = IndustriaCore.LOGGER;
	private static final String PATH_XML_SUFIX = ".xml";
	
	private final XMLUnmarshaler unmarshaler;
	private final String directory;
	private Map<ResourceLocation, NodalElement> byLocation = new HashMap<>();
	
	private static NetworkElementManager instance = new NetworkElementManager(new Gson(), "netmodels");
	
	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent event) {
		event.addListener(instance);
	}
	
	public static NetworkElementManager getInstance() {
		return instance;
	}
	
	public NetworkElementManager(Gson gson, String directory) {
		this.unmarshaler = new XMLUnmarshaler(true, NodalElement.class);
		this.directory = directory;
	}
	
	public NodalElement getElement(ResourceLocation location) {
		if (!this.byLocation.containsKey(location)) {
			LOGGER.error("Couldn't find network element '" + location + "'!");
		}
		return this.byLocation.get(location);
	}
	
	public Map<ResourceLocation, NodalElement> getElementDefinitions() {
		return this.byLocation;
	}
	
	protected ResourceLocation getPreparedXMLPath(ResourceLocation rl) {
		return ResourceLocation.tryBuild(rl.getNamespace(), this.directory + "/" + rl.getPath() + PATH_XML_SUFIX);
	}
	
	@Override
	protected Map<ResourceLocation, NodalElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		
		Map<ResourceLocation, NodalElement> map = Maps.newHashMap();
		
		for (Entry<ResourceLocation, Resource> resourceEntry : resourceManager.listResources(this.directory, (file) -> {
			return file.getPath().endsWith(PATH_XML_SUFIX);
		}).entrySet()) {
			ResourceLocation resourceLocation = resourceEntry.getKey();
			String path = resourceLocation.getPath();
			int i = path.indexOf("/");
			ResourceLocation namedLocation = ResourceLocation.tryBuild(resourceLocation.getNamespace(), path.substring(i + 1, path.length() - PATH_XML_SUFIX.length()));
			
			try {
				Resource resource = resourceEntry.getValue();
				InputStream inputStream = resource.open();
				
				NodalElement element = this.unmarshaler.unmarshall(new XMLInputStream(inputStream), NodalElement.class);
				
				if (map.put(namedLocation, element) != null) {
					throw new IllegalStateException("Duplicate element file ignored with ID " + namedLocation);
				}	
			} catch (IOException | XMLException | XMLMarshalingException xmlparseexception) {
				LOGGER.error("Couldn't parse element xml file {}: {}", namedLocation, xmlparseexception);
			}
		}
		
		LOGGER.info("Loaded " + map.size() + " parametrics");
		
		return map;
	}

	@Override
	protected void apply(Map<ResourceLocation, NodalElement> map, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		this.byLocation = map;
	}
	
}
