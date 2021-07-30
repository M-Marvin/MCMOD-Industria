package de.industria.typeregistys;

import com.mojang.datafixers.types.Type;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.tileentity.TileEntityControllPanel;
import de.industria.tileentity.TileEntityConveyorBelt;
import de.industria.tileentity.TileEntityEncasedFluidPipe;
import de.industria.tileentity.TileEntityEnderCore;
import de.industria.tileentity.TileEntityFluidPipe;
import de.industria.tileentity.TileEntityFluidValve;
import de.industria.tileentity.TileEntityItemDistributor;
import de.industria.tileentity.TileEntityJigsaw;
import de.industria.tileentity.TileEntityMAirCompressor;
import de.industria.tileentity.TileEntityMAlloyFurnace;
import de.industria.tileentity.TileEntityMBlastFurnace;
import de.industria.tileentity.TileEntityMBlender;
import de.industria.tileentity.TileEntityMBurnedCable;
import de.industria.tileentity.TileEntityMChunkLoader;
import de.industria.tileentity.TileEntityMCoalHeater;
import de.industria.tileentity.TileEntityMElectricFurnace;
import de.industria.tileentity.TileEntityMElectricHeater;
import de.industria.tileentity.TileEntityMFluidBath;
import de.industria.tileentity.TileEntityMFluidInput;
import de.industria.tileentity.TileEntityMFluidOutput;
import de.industria.tileentity.TileEntityMFuseBox;
import de.industria.tileentity.TileEntityMGasHeater;
import de.industria.tileentity.TileEntityMGenerator;
import de.industria.tileentity.TileEntityMMetalFormer;
import de.industria.tileentity.TileEntityMMultimeter;
import de.industria.tileentity.TileEntityMOreWashingPlant;
import de.industria.tileentity.TileEntityMRaffinery;
import de.industria.tileentity.TileEntityMSchredder;
import de.industria.tileentity.TileEntityMSteamGenerator;
import de.industria.tileentity.TileEntityMStoringCraftingTable;
import de.industria.tileentity.TileEntityMThermalZentrifuge;
import de.industria.tileentity.TileEntityMotor;
import de.industria.tileentity.TileEntityNComputer;
import de.industria.tileentity.TileEntityPipePreassurizer;
import de.industria.tileentity.TileEntityPreassurePipe;
import de.industria.tileentity.TileEntityPreassurePipeItemTerminal;
import de.industria.tileentity.TileEntityRHarvester;
import de.industria.tileentity.TileEntityRHoverControler;
import de.industria.tileentity.TileEntityRItemDetector;
import de.industria.tileentity.TileEntityRLockedCompositeBlock;
import de.industria.tileentity.TileEntityRSignalAntenna;
import de.industria.tileentity.TileEntityRSignalProcessorContact;
import de.industria.tileentity.TileEntityRedstoneReciver;
import de.industria.tileentity.TileEntitySimpleBlockTicking;
import de.industria.tileentity.TileEntityStructureScaffold;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntityType {
	
	public static final TileEntityType<TileEntitySimpleBlockTicking> SIMPLE_BLOCK_TICKING = register("simple_block_ticking", TileEntityType.Builder.of(TileEntitySimpleBlockTicking::new, ModItems.panel_lamp, ModItems.infinity_power_source, ModItems.transformator_contact, ModItems.steam, ModItems.compressed_air, ModItems.rail_adapter, ModItems.biogas, ModItems.fuel_gas));
	public static final TileEntityType<TileEntityAdvancedMovingBlock> ADVANCED_PISTON = register("advanced_piston", TileEntityType.Builder.of(TileEntityAdvancedMovingBlock::new, ModItems.advanced_moving_block));
	public static final TileEntityType<TileEntityRedstoneReciver> REMOTE_CONTROLER = register("remote_controler", TileEntityType.Builder.of(TileEntityRedstoneReciver::new, ModItems.advanced_moving_block));
	public static final TileEntityType<TileEntityRSignalAntenna> SIGNAL_ANTENNA = register("signal_antenna", TileEntityType.Builder.of(TileEntityRSignalAntenna::new, ModItems.antenna_conector));
	public static final TileEntityType<TileEntityMStoringCraftingTable> STORING_CRAFTING_TABLE = register("storing_crafting_table", TileEntityType.Builder.of(TileEntityMStoringCraftingTable::new, ModItems.storing_crafting_table));
	public static final TileEntityType<TileEntityRSignalProcessorContact> SIGNAL_PROCESSOR = register("signal_processor", TileEntityType.Builder.of(TileEntityRSignalProcessorContact::new, ModItems.signal_processor_contact));
	public static final TileEntityType<TileEntityRLockedCompositeBlock> LOCKED_COMPOSITE_BLOCK = register("locked_radial_conector", TileEntityType.Builder.of(TileEntityRLockedCompositeBlock::new, ModItems.radial_conector));
	public static final TileEntityType<TileEntityRHoverControler> HOVER_CONTROLER = register("hover_controler", TileEntityType.Builder.of(TileEntityRHoverControler::new, ModItems.hover_controler));
	public static final TileEntityType<TileEntityControllPanel> CONTROLL_PANEL = register("controll_panel", TileEntityType.Builder.of(TileEntityControllPanel::new, ModItems.controll_panel));
	public static final TileEntityType<TileEntityRHarvester> HARVESTER = register("harvester", TileEntityType.Builder.of(TileEntityRHarvester::new, ModItems.harvester));
	public static final TileEntityType<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityType.Builder.of(TileEntityJigsaw::new, ModItems.jigsaw));
	public static final TileEntityType<TileEntityMGenerator> GENERATOR = register("generator", TileEntityType.Builder.of(TileEntityMGenerator::new, ModItems.generator));
	public static final TileEntityType<TileEntityFluidPipe> FLUID_PIPE = register("fluid_pipe", TileEntityType.Builder.of(TileEntityFluidPipe::new, ModItems.steel_pipe, ModItems.copper_pipe));
	public static final TileEntityType<TileEntityMFluidInput> FLUID_INPUT = register("fluid_input", TileEntityType.Builder.of(TileEntityMFluidInput::new, ModItems.fluid_input));
	public static final TileEntityType<TileEntityMFluidOutput> FLUID_OUTPUT = register("fluid_output", TileEntityType.Builder.of(TileEntityMFluidOutput::new, ModItems.fluid_output));
	public static final TileEntityType<TileEntityMSteamGenerator> STEAM_GENERATOR = register("steam_generator", TileEntityType.Builder.of(TileEntityMSteamGenerator::new, ModItems.steam_generator));
	public static final TileEntityType<TileEntityMCoalHeater> COAL_HEATER = register("coal_heater", TileEntityType.Builder.of(TileEntityMCoalHeater::new, ModItems.coal_heater));
	public static final TileEntityType<TileEntityMElectricHeater> ELECTRIC_HEATER = register("electric_heater", TileEntityType.Builder.of(TileEntityMElectricHeater::new, ModItems.electric_heater));
	public static final TileEntityType<TileEntityMGasHeater> GAS_HEATER = register("gas_heater", TileEntityType.Builder.of(TileEntityMGasHeater::new, ModItems.gas_heater));
	public static final TileEntityType<TileEntityMFuseBox> FUSE_BOX = register("fuse_box", TileEntityType.Builder.of(TileEntityMFuseBox::new, ModItems.fuse_box));
	public static final TileEntityType<TileEntityMMultimeter> MULTIMETER = register("block_multimeter", TileEntityType.Builder.of(TileEntityMMultimeter::new, ModItems.multimeter));
	public static final TileEntityType<TileEntityFluidValve> FLUID_VALVE = register("fluid_valve", TileEntityType.Builder.of(TileEntityFluidValve::new, ModItems.fluid_valve));
	public static final TileEntityType<TileEntityMElectricFurnace> ELECTRIC_FURNACE = register("electric_furnace", TileEntityType.Builder.of(TileEntityMElectricFurnace::new, ModItems.electric_furnace));
	public static final TileEntityType<TileEntityMSchredder> SCHREDDER = register("schredder", TileEntityType.Builder.of(TileEntityMSchredder::new, ModItems.schredder));
	public static final TileEntityType<TileEntityMBlender> BLENDER = register("blender", TileEntityType.Builder.of(TileEntityMBlender::new, ModItems.blender));
	public static final TileEntityType<TileEntityMRaffinery> RAFFINERY = register("raffinery", TileEntityType.Builder.of(TileEntityMRaffinery::new, ModItems.raffinery));
	public static final TileEntityType<TileEntityMAlloyFurnace> ALLOY_FURNACE = register("alloy_furnace", TileEntityType.Builder.of(TileEntityMAlloyFurnace::new, ModItems.alloy_furnace));
	public static final TileEntityType<TileEntityConveyorBelt> CONVEYOR_BELT = register("conveyor_belt", TileEntityType.Builder.of(TileEntityConveyorBelt::new, ModItems.conveyor_belt, ModItems.conveyor_spliter, ModItems.conveyor_switch));
	public static final TileEntityType<TileEntityMThermalZentrifuge> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", TileEntityType.Builder.of(TileEntityMThermalZentrifuge::new, ModItems.thermal_zentrifuge));
	public static final TileEntityType<TileEntityMotor> MOTOR = register("motor", TileEntityType.Builder.of(TileEntityMotor::new, ModItems.motor));
	public static final TileEntityType<TileEntityMFluidBath> FLUID_BATH = register("fluid_bath", TileEntityType.Builder.of(TileEntityMFluidBath::new, ModItems.fluid_bath));
	public static final TileEntityType<TileEntityMBurnedCable> BURNED_CABLE = register("burned_cable", TileEntityType.Builder.of(TileEntityMBurnedCable::new, ModItems.burned_cable));
	public static final TileEntityType<TileEntityRItemDetector> ITEM_DETECTOR = register("item_detector", TileEntityType.Builder.of(TileEntityRItemDetector::new, ModItems.item_detector));
	public static final TileEntityType<TileEntityNComputer> COMPUTER = register("computer", TileEntityType.Builder.of(TileEntityNComputer::new, ModItems.computer));
	public static final TileEntityType<TileEntityMChunkLoader> CHUNK_LOADER = register("chunk_loader", TileEntityType.Builder.of(TileEntityMChunkLoader::new, ModItems.chunk_loader));
	public static final TileEntityType<TileEntityEnderCore> ENDER_CORE = register("ender_core", TileEntityType.Builder.of(TileEntityEnderCore::new, ModItems.ender_core));
	public static final TileEntityType<TileEntityStructureScaffold> STRUCTURE_SCAFFOLD = register("structure_scaffold", TileEntityType.Builder.of(TileEntityStructureScaffold::new, ModItems.structure_scaffold, ModItems.encased_electric_copper_cable, ModItems.encased_electric_electrolyt_copper_cable, ModItems.encased_electric_aluminium_cable, ModItems.encased_electric_burned_cable, ModItems.encased_network_cable));
	public static final TileEntityType<TileEntityStructureScaffold> ENCASED_FLUID_PIPE = register("encased_steel_pipe", TileEntityType.Builder.of(TileEntityEncasedFluidPipe::new, ModItems.encased_steel_pipe));
	public static final TileEntityType<TileEntityMBlastFurnace> BLAST_FURNACE = register("blast_furnace", TileEntityType.Builder.of(TileEntityMBlastFurnace::new, ModItems.blast_furnace));
	public static final TileEntityType<TileEntityMAirCompressor> AIR_COMPRESSOR = register("air_compressor", TileEntityType.Builder.of(TileEntityMAirCompressor::new, ModItems.air_compressor));
	public static final TileEntityType<TileEntityPreassurePipe> PREASSURE_PIPE = register("preassure_pipe", TileEntityType.Builder.of(TileEntityPreassurePipe::new, ModItems.preassure_pipe));
	public static final TileEntityType<TileEntityPipePreassurizer> PIPE_PREASSURIZER = register("pipe_preassurizer", TileEntityType.Builder.of(TileEntityPipePreassurizer::new, ModItems.pipe_preassurizer));
	public static final TileEntityType<TileEntityItemDistributor> ITEM_DISTRIBUTOR = register("item_distributor", TileEntityType.Builder.of(TileEntityItemDistributor::new, ModItems.item_distributor));
	public static final TileEntityType<TileEntityPreassurePipeItemTerminal> PREASSURE_PIPE_ITEM_TERMINAL = register("preassure_pipe_item_terminal", TileEntityType.Builder.of(TileEntityPreassurePipeItemTerminal::new, ModItems.preassure_pipe_item_terminal));
	public static final TileEntityType<TileEntityMMetalFormer> METAL_FORMER = register("metal_former", TileEntityType.Builder.of(TileEntityMMetalFormer::new, ModItems.metal_former));
	public static final TileEntityType<TileEntityMOreWashingPlant> ORE_WASHING_PLANT = register("ore_washing_plant", TileEntityType.Builder.of(TileEntityMOreWashingPlant::new, ModItems.ore_washing_plant));
	
	private static <T extends TileEntity> TileEntityType<T> register(String key, TileEntityType.Builder<T> builder) {
		Type<?> type = Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, key);
		TileEntityType<T> tileEntityType = builder.build(type);
		tileEntityType.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);
		return tileEntityType;
	}
	
}
