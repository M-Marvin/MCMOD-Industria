package de.industria.typeregistys;

import com.mojang.datafixers.types.Type;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.tileentity.TileEntityControllPanel;
import de.industria.tileentity.TileEntityConveyorBelt;
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
import de.industria.tileentity.TileEntityMFluidBath;
import de.industria.tileentity.TileEntityMFluidInput;
import de.industria.tileentity.TileEntityMFluidOutput;
import de.industria.tileentity.TileEntityMFuseBox;
import de.industria.tileentity.TileEntityMGenerator;
import de.industria.tileentity.TileEntityMMetalFormer;
import de.industria.tileentity.TileEntityMMultimeter;
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
	
	public static final TileEntityType<TileEntitySimpleBlockTicking> SIMPLE_BLOCK_TICKING = register("simple_block_ticking", TileEntityType.Builder.create(TileEntitySimpleBlockTicking::new, ModItems.panel_lamp, ModItems.infinity_power_source, ModItems.transformator_contact, ModItems.steam, ModItems.compressed_air, ModItems.rail_adapter));
	public static final TileEntityType<TileEntityAdvancedMovingBlock> ADVANCED_PISTON = register("advanced_piston", TileEntityType.Builder.create(TileEntityAdvancedMovingBlock::new, ModItems.advanced_moving_block));
	public static final TileEntityType<TileEntityRedstoneReciver> REMOTE_CONTROLER = register("remote_controler", TileEntityType.Builder.create(TileEntityRedstoneReciver::new, ModItems.advanced_moving_block));
	public static final TileEntityType<TileEntityRSignalAntenna> SIGNAL_ANTENNA = register("signal_antenna", TileEntityType.Builder.create(TileEntityRSignalAntenna::new, ModItems.antenna_conector));
	public static final TileEntityType<TileEntityMStoringCraftingTable> STORING_CRAFTING_TABLE = register("storing_crafting_table", TileEntityType.Builder.create(TileEntityMStoringCraftingTable::new, ModItems.storing_crafting_table));
	public static final TileEntityType<TileEntityRSignalProcessorContact> SIGNAL_PROCESSOR = register("signal_processor", TileEntityType.Builder.create(TileEntityRSignalProcessorContact::new, ModItems.signal_processor_contact));
	public static final TileEntityType<TileEntityRLockedCompositeBlock> LOCKED_COMPOSITE_BLOCK = register("locked_radial_conector", TileEntityType.Builder.create(TileEntityRLockedCompositeBlock::new, ModItems.radial_conector));
	public static final TileEntityType<TileEntityRHoverControler> HOVER_CONTROLER = register("hover_controler", TileEntityType.Builder.create(TileEntityRHoverControler::new, ModItems.hover_controler));
	public static final TileEntityType<TileEntityControllPanel> CONTROLL_PANEL = register("controll_panel", TileEntityType.Builder.create(TileEntityControllPanel::new, ModItems.controll_panel));
	public static final TileEntityType<TileEntityRHarvester> HARVESTER = register("harvester", TileEntityType.Builder.create(TileEntityRHarvester::new, ModItems.harvester));
	public static final TileEntityType<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityType.Builder.create(TileEntityJigsaw::new, ModItems.jigsaw));
	public static final TileEntityType<TileEntityMGenerator> GENERATOR = register("generator", TileEntityType.Builder.create(TileEntityMGenerator::new, ModItems.generator));
	public static final TileEntityType<TileEntityFluidPipe> FLUID_PIPE = register("fluid_pipe", TileEntityType.Builder.create(TileEntityFluidPipe::new, ModItems.fluid_pipe));
	public static final TileEntityType<TileEntityMFluidInput> FLUID_INPUT = register("fluid_input", TileEntityType.Builder.create(TileEntityMFluidInput::new, ModItems.fluid_input));
	public static final TileEntityType<TileEntityMFluidOutput> FLUID_OUTPUT = register("fluid_output", TileEntityType.Builder.create(TileEntityMFluidOutput::new, ModItems.fluid_output));
	public static final TileEntityType<TileEntityMSteamGenerator> STEAM_GENERATOR = register("steam_generator", TileEntityType.Builder.create(TileEntityMSteamGenerator::new, ModItems.steam_generator));
	public static final TileEntityType<TileEntityMCoalHeater> COAL_HEATER = register("coal_heater", TileEntityType.Builder.create(TileEntityMCoalHeater::new, ModItems.coal_heater));
	public static final TileEntityType<TileEntityMFuseBox> FUSE_BOX = register("fuse_box", TileEntityType.Builder.create(TileEntityMFuseBox::new, ModItems.fuse_box));
	public static final TileEntityType<TileEntityMMultimeter> MULTIMETER = register("block_multimeter", TileEntityType.Builder.create(TileEntityMMultimeter::new, ModItems.multimeter));
	public static final TileEntityType<TileEntityFluidValve> FLUID_VALVE = register("fluid_valve", TileEntityType.Builder.create(TileEntityFluidValve::new, ModItems.fluid_valve));
	public static final TileEntityType<TileEntityMElectricFurnace> ELECTRIC_FURNACE = register("electric_furnace", TileEntityType.Builder.create(TileEntityMElectricFurnace::new, ModItems.electric_furnace));
	public static final TileEntityType<TileEntityMSchredder> SCHREDDER = register("schredder", TileEntityType.Builder.create(TileEntityMSchredder::new, ModItems.schredder));
	public static final TileEntityType<TileEntityMBlender> BLENDER = register("blender", TileEntityType.Builder.create(TileEntityMBlender::new, ModItems.blender));
	public static final TileEntityType<TileEntityMRaffinery> RAFFINERY = register("raffinery", TileEntityType.Builder.create(TileEntityMRaffinery::new, ModItems.raffinery));
	public static final TileEntityType<TileEntityMAlloyFurnace> ALLOY_FURNACE = register("alloy_furnace", TileEntityType.Builder.create(TileEntityMAlloyFurnace::new, ModItems.alloy_furnace));
	public static final TileEntityType<TileEntityConveyorBelt> CONVEYOR_BELT = register("conveyor_belt", TileEntityType.Builder.create(TileEntityConveyorBelt::new, ModItems.conveyor_belt, ModItems.conveyor_spliter, ModItems.conveyor_switch));
	public static final TileEntityType<TileEntityMThermalZentrifuge> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", TileEntityType.Builder.create(TileEntityMThermalZentrifuge::new, ModItems.thermal_zentrifuge));
	public static final TileEntityType<TileEntityMotor> MOTOR = register("motor", TileEntityType.Builder.create(TileEntityMotor::new, ModItems.motor));
	public static final TileEntityType<TileEntityMFluidBath> FLUID_BATH = register("fluid_bath", TileEntityType.Builder.create(TileEntityMFluidBath::new, ModItems.fluid_bath));
	public static final TileEntityType<TileEntityMBurnedCable> BURNED_CABLE = register("burned_cable", TileEntityType.Builder.create(TileEntityMBurnedCable::new, ModItems.burned_cable));
	public static final TileEntityType<TileEntityRItemDetector> ITEM_DETECTOR = register("item_detector", TileEntityType.Builder.create(TileEntityRItemDetector::new, ModItems.item_detector));
	public static final TileEntityType<TileEntityNComputer> COMPUTER = register("computer", TileEntityType.Builder.create(TileEntityNComputer::new, ModItems.computer));
	public static final TileEntityType<TileEntityMChunkLoader> CHUNK_LOADER = register("chunk_loader", TileEntityType.Builder.create(TileEntityMChunkLoader::new, ModItems.chunk_loader));
	public static final TileEntityType<TileEntityEnderCore> ENDER_CORE = register("ender_core", TileEntityType.Builder.create(TileEntityEnderCore::new, ModItems.ender_core));
	public static final TileEntityType<TileEntityStructureScaffold> STRUCTURE_SCAFFOLD = register("structure_scaffold", TileEntityType.Builder.create(TileEntityStructureScaffold::new, ModItems.structure_scaffold));
	public static final TileEntityType<TileEntityMBlastFurnace> BLAST_FURNACE = register("blast_furnace", TileEntityType.Builder.create(TileEntityMBlastFurnace::new, ModItems.blast_furnace));
	public static final TileEntityType<TileEntityMAirCompressor> AIR_COMPRESSOR = register("air_compressor", TileEntityType.Builder.create(TileEntityMAirCompressor::new, ModItems.air_compressor));
	public static final TileEntityType<TileEntityPreassurePipe> PREASSURE_PIPE = register("preassure_pipe", TileEntityType.Builder.create(TileEntityPreassurePipe::new, ModItems.preassure_pipe));
	public static final TileEntityType<TileEntityPipePreassurizer> PIPE_PREASSURIZER = register("pipe_preassurizer", TileEntityType.Builder.create(TileEntityPipePreassurizer::new, ModItems.pipe_preassurizer));
	public static final TileEntityType<TileEntityItemDistributor> ITEM_DISTRIBUTOR = register("item_distributor", TileEntityType.Builder.create(TileEntityItemDistributor::new, ModItems.item_distributor));
	public static final TileEntityType<TileEntityPreassurePipeItemTerminal> PREASSURE_PIPE_ITEM_TERMINAL = register("preassure_pipe_item_terminal", TileEntityType.Builder.create(TileEntityPreassurePipeItemTerminal::new, ModItems.preassure_pipe_item_terminal));
	public static final TileEntityType<TileEntityMMetalFormer> METAL_FORMER = register("metal_former", TileEntityType.Builder.create(TileEntityMMetalFormer::new, ModItems.metal_former));
	
	private static <T extends TileEntity> TileEntityType<T> register(String key, TileEntityType.Builder<T> builder) {
		Type<?> type = Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, key);
		TileEntityType<T> tileEntityType = builder.build(type);
		tileEntityType.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);
		return tileEntityType;
	}
	
}
