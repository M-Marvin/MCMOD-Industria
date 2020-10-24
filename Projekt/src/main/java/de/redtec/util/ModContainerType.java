package de.redtec.util;

import de.redtec.RedTec;
import de.redtec.gui.ContainerHarvester;
import de.redtec.gui.ContainerHoverControler;
import de.redtec.gui.ContainerProcessor;
import de.redtec.gui.ContainerReciver;
import de.redtec.gui.ContainerStoredCrafting;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("deprecation")
public class ModContainerType {
	
	public static final ContainerType<ContainerStoredCrafting> STORED_CRAFTING = Registry.register(Registry.MENU, new ResourceLocation(RedTec.MODID, "stored_crafting"), IForgeContainerType.create(ContainerStoredCrafting::new));
	public static final ContainerType<ContainerProcessor> PROCESSOR = Registry.register(Registry.MENU, new ResourceLocation(RedTec.MODID, "processor"), IForgeContainerType.create(ContainerProcessor::new));
	public static final ContainerType<ContainerHoverControler> HOVER_CONTROLER = Registry.register(Registry.MENU, new ResourceLocation(RedTec.MODID, "hover_controler"), IForgeContainerType.create(ContainerHoverControler::new));
	public static final ContainerType<ContainerReciver> REDSTONE_RECIVER = Registry.register(Registry.MENU, new ResourceLocation(RedTec.MODID, "reciver"), IForgeContainerType.create(ContainerReciver::new));
	public static final ContainerType<ContainerHarvester> HARVESTER = Registry.register(Registry.MENU, new ResourceLocation(RedTec.MODID, "harvester"), IForgeContainerType.create(ContainerHarvester::new));
	
}