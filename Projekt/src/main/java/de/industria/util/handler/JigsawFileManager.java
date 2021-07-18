package de.industria.util.handler;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import com.mojang.brigadier.StringReader;

import de.industria.Industria;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.server.ServerWorld;

public class JigsawFileManager {
	
	private static HashMap<ResourceLocation, Template> templates = new HashMap<ResourceLocation, Template>();
	private static HashMap<ResourceLocation, ListNBT> poolLists = new HashMap<ResourceLocation, ListNBT>();
	
	public static ListNBT getPoolList(ServerWorld world, ResourceLocation location) {
		
		if (poolLists.containsKey(location) ? poolLists.get(location) != null : false) {
			
			return poolLists.get(location);
			
		} else {
			
			ListNBT list = loadListNBT(world, location);
			poolLists.put(location, list);
			return list;
			
		}
		
	}
	
	public static Template getTemplate(ServerWorld world, ResourceLocation location) {
		
		if (templates.containsKey(location) ? templates.get(location) != null : false) {
			
			return templates.get(location);
			
		} else {
			
			Template template = loadTemplate(world, location);
			templates.put(location, template);
			return template;
			
		}
		
	}
	
	public static void onFileManagerReload() {
		Industria.LOGGER.info("Reload static JigsawFileManager");
		templates.clear();
		poolLists.clear();
	}
	
	private static IResourceManager getResourceManager(ServerWorld world) {
		return world.getServer().getDataPackRegistries().getResourceManager();
	}
	
	@SuppressWarnings("deprecation")
	private static ListNBT loadListNBT(ServerWorld world, ResourceLocation location) {
		
		IResourceManager resourceManager = getResourceManager(world);
		ResourceLocation resourcePath = new ResourceLocation(location.getNamespace(), "structures/" + location.getPath() + ".mcmeta");
		
		try (IResource resource = resourceManager.getResource(resourcePath)) {
			
			InputStream inputStream = resource.getInputStream();
			String jsonString = IOUtils.toString(inputStream);
			CompoundNBT fileNBT = new JsonToNBT(new StringReader(jsonString)).readStruct();
			ListNBT list = fileNBT.getList("structures", 10);
			
			return list;
			
		} catch (FileNotFoundException e) {
			return null;
		} catch (Throwable throwable) {
			Industria.LOGGER.error("Couldn't load Jigsaw-Structure-List {}: {}", location, throwable.toString());
			throwable.printStackTrace();
			return null;
		}
				
	}
	
	private static Template loadTemplate(ServerWorld world, ResourceLocation location) {
		
		IResourceManager resourceManager = getResourceManager(world);
		ResourceLocation resourcePath = new ResourceLocation(location.getNamespace(), "structures/" + location.getPath() + ".nbt");
		
		try (IResource resource = resourceManager.getResource(resourcePath)) {
			
			InputStream inputStream = resource.getInputStream();
			CompoundNBT fileNBT = CompressedStreamTools.readCompressed(inputStream);
			Template template = new Template();
			template.load(fileNBT);
			
			return template;
			
		} catch (FileNotFoundException e) {
			return null;
		} catch (Throwable throwable) {
			Industria.LOGGER.error("Couldn't load Structure {}: {}", location, throwable);
			return null;
		}
		
	}
	
}
