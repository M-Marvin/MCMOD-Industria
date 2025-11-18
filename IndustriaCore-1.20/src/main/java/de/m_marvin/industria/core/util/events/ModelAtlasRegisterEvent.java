package de.m_marvin.industria.core.util.events;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class ModelAtlasRegisterEvent extends Event implements IModBusEvent {
	
	private final Map<ResourceLocation, ResourceLocation> atlases;
	
	public ModelAtlasRegisterEvent(Map<ResourceLocation, ResourceLocation> atlases) {
		this.atlases = atlases;
	}
	
	public void register(ResourceLocation atlasTextureLocation, ResourceLocation atlasName) {
		this.atlases.put(atlasTextureLocation, atlasName);
	}
	
	public Map<ResourceLocation, ResourceLocation> getAtlases() {
		return atlases;
	}
	
}