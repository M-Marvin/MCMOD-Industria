package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.gui.ContainerRHarvester;
import de.industria.gui.ContainerRHoverControler;
import de.industria.gui.ContainerJigsaw;
import de.industria.gui.ContainerMAirCompressor;
import de.industria.gui.ContainerMAlloyFurnace;
import de.industria.gui.ContainerMBlender;
import de.industria.gui.ContainerMChunkLoader;
import de.industria.gui.ContainerMCoalHeater;
import de.industria.gui.ContainerMElectricFurnace;
import de.industria.gui.ContainerMFluidBath;
import de.industria.gui.ContainerMGenerator;
import de.industria.gui.ContainerMRaffinery;
import de.industria.gui.ContainerMSchredder;
import de.industria.gui.ContainerMThermalZentrifuge;
import de.industria.gui.ContainerNComputer;
import de.industria.gui.ContainerNetworkConfigurator;
import de.industria.gui.ContainerRProcessor;
import de.industria.gui.ContainerReciver;
import de.industria.gui.ContainerMStoredCrafting;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModContainerType {
	
	public static final ContainerType<ContainerMStoredCrafting> STORED_CRAFTING = register(new ResourceLocation(Industria.MODID, "stored_crafting"), ContainerMStoredCrafting::new);
	public static final ContainerType<ContainerRProcessor> PROCESSOR = register(new ResourceLocation(Industria.MODID, "processor"), ContainerRProcessor::new);
	public static final ContainerType<ContainerRHoverControler> HOVER_CONTROLER = register(new ResourceLocation(Industria.MODID, "hover_controler"), ContainerRHoverControler::new);
	public static final ContainerType<ContainerReciver> REDSTONE_RECIVER = register(new ResourceLocation(Industria.MODID, "reciver"), ContainerReciver::new);
	public static final ContainerType<ContainerRHarvester> HARVESTER = register(new ResourceLocation(Industria.MODID, "harvester"), ContainerRHarvester::new);
	public static final ContainerType<ContainerJigsaw> JIGSAW = register(new ResourceLocation(Industria.MODID, "jigsaw"), ContainerJigsaw::new);
	public static final ContainerType<ContainerMGenerator> GENERATOR = register(new ResourceLocation(Industria.MODID, "generator"), ContainerMGenerator::new);
	public static final ContainerType<ContainerMCoalHeater> COAL_HEATER = register(new ResourceLocation(Industria.MODID, "coal_heater"), ContainerMCoalHeater::new);
	public static final ContainerType<ContainerMElectricFurnace> ELECTRIC_FURNACE = register(new ResourceLocation(Industria.MODID, "electric_furnace"), ContainerMElectricFurnace::new);
	public static final ContainerType<ContainerMSchredder> SCHREDDER = register(new ResourceLocation(Industria.MODID, "schredder"), ContainerMSchredder::new);
	public static final ContainerType<ContainerMBlender> BLENDER = register(new ResourceLocation(Industria.MODID, "blender"), ContainerMBlender::new);
	public static final ContainerType<ContainerMRaffinery> RAFFINERY = register(new ResourceLocation(Industria.MODID, "raffinery"), ContainerMRaffinery::new);
	public static final ContainerType<ContainerMThermalZentrifuge> THERMAL_ZENTRIFUGE = register(new ResourceLocation(Industria.MODID, "thermal_zentrifuge"), ContainerMThermalZentrifuge::new);
	public static final ContainerType<ContainerMAlloyFurnace> ALLOY_FURNACE = register(new ResourceLocation(Industria.MODID, "alloy_furnace"), ContainerMAlloyFurnace::new);
	public static final ContainerType<ContainerMFluidBath> FLUID_BATH = register(new ResourceLocation(Industria.MODID, "fluid_bath"), ContainerMFluidBath::new);
	public static final ContainerType<ContainerNComputer> COMPUTER = register(new ResourceLocation(Industria.MODID, "computer"), ContainerNComputer::new);
	public static final ContainerType<ContainerNetworkConfigurator> NETWORK_CONFIGURATOR = register(new ResourceLocation(Industria.MODID, "network_configurator"), ContainerNetworkConfigurator::new);
	public static final ContainerType<ContainerMChunkLoader> CHUNK_LAODER = register(new ResourceLocation(Industria.MODID, "chunk_loader"), ContainerMChunkLoader::new);
	public static final ContainerType<ContainerMAirCompressor> AIR_COMPRESSOR = register(new ResourceLocation(Industria.MODID, "air_compressor"), ContainerMAirCompressor::new);
	
	protected static <T extends Container> ContainerType<T> register(ResourceLocation key, IContainerFactory<T> factory) {
		ContainerType<T> type = IForgeContainerType.create(factory);
		type.setRegistryName(key);
		ForgeRegistries.CONTAINERS.register(type);
		return type;
	}
	
}