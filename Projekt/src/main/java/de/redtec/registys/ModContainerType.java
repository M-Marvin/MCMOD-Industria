package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.gui.ContainerHarvester;
import de.redtec.gui.ContainerHoverControler;
import de.redtec.gui.ContainerJigsaw;
import de.redtec.gui.ContainerMAlloyFurnace;
import de.redtec.gui.ContainerMBlender;
import de.redtec.gui.ContainerMCoalHeater;
import de.redtec.gui.ContainerMElectricFurnace;
import de.redtec.gui.ContainerMFluidBath;
import de.redtec.gui.ContainerMGenerator;
import de.redtec.gui.ContainerMRaffinery;
import de.redtec.gui.ContainerMSchredder;
import de.redtec.gui.ContainerMThermalZentrifuge;
import de.redtec.gui.ContainerProcessor;
import de.redtec.gui.ContainerReciver;
import de.redtec.gui.ContainerStoredCrafting;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModContainerType {
	
	public static final ContainerType<ContainerStoredCrafting> STORED_CRAFTING = register(new ResourceLocation(RedTec.MODID, "stored_crafting"), ContainerStoredCrafting::new);
	public static final ContainerType<ContainerProcessor> PROCESSOR = register(new ResourceLocation(RedTec.MODID, "processor"), ContainerProcessor::new);
	public static final ContainerType<ContainerHoverControler> HOVER_CONTROLER = register(new ResourceLocation(RedTec.MODID, "hover_controler"), ContainerHoverControler::new);
	public static final ContainerType<ContainerReciver> REDSTONE_RECIVER = register(new ResourceLocation(RedTec.MODID, "reciver"), ContainerReciver::new);
	public static final ContainerType<ContainerHarvester> HARVESTER = register(new ResourceLocation(RedTec.MODID, "harvester"), ContainerHarvester::new);
	public static final ContainerType<ContainerJigsaw> JIGSAW = register(new ResourceLocation(RedTec.MODID, "jigsaw"), ContainerJigsaw::new);
	public static final ContainerType<ContainerMGenerator> GENERATOR = register(new ResourceLocation(RedTec.MODID, "generator"), ContainerMGenerator::new);
	public static final ContainerType<ContainerMCoalHeater> COAL_HEATER = register(new ResourceLocation(RedTec.MODID, "coal_heater"), ContainerMCoalHeater::new);
	public static final ContainerType<ContainerMElectricFurnace> ELECTRIC_FURNACE = register(new ResourceLocation(RedTec.MODID, "electric_furnace"), ContainerMElectricFurnace::new);
	public static final ContainerType<ContainerMSchredder> SCHREDDER = register(new ResourceLocation(RedTec.MODID, "schredder"), ContainerMSchredder::new);
	public static final ContainerType<ContainerMBlender> BLENDER = register(new ResourceLocation(RedTec.MODID, "blender"), ContainerMBlender::new);
	public static final ContainerType<ContainerMRaffinery> RAFFINERY = register(new ResourceLocation(RedTec.MODID, "raffinery"), ContainerMRaffinery::new);
	public static final ContainerType<ContainerMThermalZentrifuge> THERMAL_ZENTRIFUGE = register(new ResourceLocation(RedTec.MODID, "thermal_zentrifuge"), ContainerMThermalZentrifuge::new);
	public static final ContainerType<ContainerMAlloyFurnace> ALLOY_FURNACE = register(new ResourceLocation(RedTec.MODID, "alloy_furnace"), ContainerMAlloyFurnace::new);
	public static final ContainerType<ContainerMFluidBath> FLUID_BATH = register(new ResourceLocation(RedTec.MODID, "fluid_bath"), ContainerMFluidBath::new);
	
	protected static <T extends Container> ContainerType<T> register(ResourceLocation key, IContainerFactory<T> factory) {
		ContainerType<T> type = IForgeContainerType.create(factory);
		type.setRegistryName(key);
		ForgeRegistries.CONTAINERS.register(type);
		return type;
	}
	
}