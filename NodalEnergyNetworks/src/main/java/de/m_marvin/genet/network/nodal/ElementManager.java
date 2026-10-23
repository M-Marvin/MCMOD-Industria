package de.m_marvin.genet.network.nodal;

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
import de.m_marvin.genet.NodeNet;
import de.m_marvin.tvnlnna.nodal.INodalElement;
import de.m_marvin.tvnlnna.nodal.NodalElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ElementManager extends SimplePreparableReloadListener<Map<ResourceLocation, NodalElement>> {
	
	private static final Logger LOGGER = NodeNet.LOGGER;
	private static final String PATH_XML_SUFIX = ".xml";
	
	private final XMLUnmarshaler unmarshaler;
	private final String directory;
	private Map<ResourceLocation, NodalElement> byLocation = new HashMap<>();
	
	@SubscribeEvent
	public void addReloadListenerEvent(AddReloadListenerEvent event) {
		event.addListener(this);
	}
	
	public ElementManager(Gson gson, String directory) {
		this.unmarshaler = new XMLUnmarshaler(true, NodalElement.class);
		this.directory = directory;
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public NodalElement getElementModel(ResourceLocation location) {
		if (!this.byLocation.containsKey(location)) {
			LOGGER.error("Couldn't find nodel model '" + location + "'!");
		}
		return this.byLocation.get(location); // FIXME fallback invalid model
	}
	
	public <N, E> Element<N, E> makeElement(E name, ResourceLocation modelLocation) {
		INodalElement model = getElementModel(modelLocation);
		return new Element<N, E>(name, model);
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
		
		LOGGER.info("Loaded " + map.size() + " nodal models");
		
		return map;
	}

	@Override
	protected void apply(Map<ResourceLocation, NodalElement> map, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		this.byLocation = map;
	}
	
}
