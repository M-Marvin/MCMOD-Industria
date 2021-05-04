package de.industria.typeregistys;

import com.mojang.datafixers.types.Type;

import de.industria.Industria;
import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.tileentity.TileEntityControllPanel;
import de.industria.tileentity.TileEntityConveyorBelt;
import de.industria.tileentity.TileEntityEnderCore;
import de.industria.tileentity.TileEntityFluidPipe;
import de.industria.tileentity.TileEntityFluidValve;
import de.industria.tileentity.TileEntityItemPipe;
import de.industria.tileentity.TileEntityItemPipePreassurizer;
import de.industria.tileentity.TileEntityJigsaw;
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
import de.industria.tileentity.TileEntityMMultimeter;
import de.industria.tileentity.TileEntityMRaffinery;
import de.industria.tileentity.TileEntityMSchredder;
import de.industria.tileentity.TileEntityMSteamGenerator;
import de.industria.tileentity.TileEntityMStoringCraftingTable;
import de.industria.tileentity.TileEntityMThermalZentrifuge;
import de.industria.tileentity.TileEntityMotor;
import de.industria.tileentity.TileEntityNComputer;
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
	
	public static final TileEntityType<TileEntitySimpleBlockTicking> SIMPLE_BLOCK_TICKING = register("simple_block_ticking", TileEntityType.Builder.create(TileEntitySimpleBlockTicking::new, Industria.panel_lamp, Industria.infinity_power_source, Industria.transformator_contact, Industria.steam, Industria.rail_adapter));
	public static final TileEntityType<TileEntityAdvancedMovingBlock> ADVANCED_PISTON = register("advanced_piston", TileEntityType.Builder.create(TileEntityAdvancedMovingBlock::new, Industria.advanced_moving_block));
	public static final TileEntityType<TileEntityRedstoneReciver> REMOTE_CONTROLER = register("remote_controler", TileEntityType.Builder.create(TileEntityRedstoneReciver::new, Industria.advanced_moving_block));
	public static final TileEntityType<TileEntityRSignalAntenna> SIGNAL_ANTENNA = register("signal_antenna", TileEntityType.Builder.create(TileEntityRSignalAntenna::new, Industria.antenna_conector));
	public static final TileEntityType<TileEntityMStoringCraftingTable> STORING_CRAFTING_TABLE = register("storing_crafting_table", TileEntityType.Builder.create(TileEntityMStoringCraftingTable::new, Industria.storing_crafting_table));
	public static final TileEntityType<TileEntityRSignalProcessorContact> SIGNAL_PROCESSOR = register("signal_processor", TileEntityType.Builder.create(TileEntityRSignalProcessorContact::new, Industria.signal_processor_contact));
	public static final TileEntityType<TileEntityRLockedCompositeBlock> LOCKED_COMPOSITE_BLOCK = register("locked_radial_conector", TileEntityType.Builder.create(TileEntityRLockedCompositeBlock::new, Industria.radial_conector));
	public static final TileEntityType<TileEntityRHoverControler> HOVER_CONTROLER = register("hover_controler", TileEntityType.Builder.create(TileEntityRHoverControler::new, Industria.hover_controler));
	public static final TileEntityType<TileEntityControllPanel> CONTROLL_PANEL = register("controll_panel", TileEntityType.Builder.create(TileEntityControllPanel::new, Industria.controll_panel));
	public static final TileEntityType<TileEntityRHarvester> HARVESTER = register("harvester", TileEntityType.Builder.create(TileEntityRHarvester::new, Industria.harvester));
	public static final TileEntityType<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityType.Builder.create(TileEntityJigsaw::new, Industria.jigsaw));
	public static final TileEntityType<TileEntityMGenerator> GENERATOR = register("generator", TileEntityType.Builder.create(TileEntityMGenerator::new, Industria.generator));
	public static final TileEntityType<TileEntityFluidPipe> FLUID_PIPE = register("fluid_pipe", TileEntityType.Builder.create(TileEntityFluidPipe::new, Industria.fluid_pipe));
	public static final TileEntityType<TileEntityMFluidInput> FLUID_INPUT = register("fluid_input", TileEntityType.Builder.create(TileEntityMFluidInput::new, Industria.fluid_input));
	public static final TileEntityType<TileEntityMFluidOutput> FLUID_OUTPUT = register("fluid_output", TileEntityType.Builder.create(TileEntityMFluidOutput::new, Industria.fluid_output));
	public static final TileEntityType<TileEntityMSteamGenerator> STEAM_GENERATOR = register("steam_generator", TileEntityType.Builder.create(TileEntityMSteamGenerator::new, Industria.steam_generator));
	public static final TileEntityType<TileEntityMCoalHeater> COAL_HEATER = register("coal_heater", TileEntityType.Builder.create(TileEntityMCoalHeater::new, Industria.coal_heater));
	public static final TileEntityType<TileEntityMFuseBox> FUSE_BOX = register("fuse_box", TileEntityType.Builder.create(TileEntityMFuseBox::new, Industria.fuse_box));
	public static final TileEntityType<TileEntityMMultimeter> MULTIMETER = register("block_multimeter", TileEntityType.Builder.create(TileEntityMMultimeter::new, Industria.multimeter));
	public static final TileEntityType<TileEntityFluidValve> FLUID_VALVE = register("fluid_valve", TileEntityType.Builder.create(TileEntityFluidValve::new, Industria.fluid_valve));
	public static final TileEntityType<TileEntityMElectricFurnace> ELECTRIC_FURNACE = register("electric_furnace", TileEntityType.Builder.create(TileEntityMElectricFurnace::new, Industria.electric_furnace));
	public static final TileEntityType<TileEntityMSchredder> SCHREDDER = register("schredder", TileEntityType.Builder.create(TileEntityMSchredder::new, Industria.schredder));
	public static final TileEntityType<TileEntityMBlender> BLENDER = register("blender", TileEntityType.Builder.create(TileEntityMBlender::new, Industria.blender));
	public static final TileEntityType<TileEntityMRaffinery> RAFFINERY = register("raffinery", TileEntityType.Builder.create(TileEntityMRaffinery::new, Industria.raffinery));
	public static final TileEntityType<TileEntityMAlloyFurnace> ALLOY_FURNACE = register("alloy_furnace", TileEntityType.Builder.create(TileEntityMAlloyFurnace::new, Industria.alloy_furnace));
	public static final TileEntityType<TileEntityConveyorBelt> CONVEYOR_BELT = register("conveyor_belt", TileEntityType.Builder.create(TileEntityConveyorBelt::new, Industria.conveyor_belt, Industria.conveyor_spliter, Industria.conveyor_switch));
	public static final TileEntityType<TileEntityMThermalZentrifuge> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", TileEntityType.Builder.create(TileEntityMThermalZentrifuge::new, Industria.thermal_zentrifuge));
	public static final TileEntityType<TileEntityMotor> MOTOR = register("motor", TileEntityType.Builder.create(TileEntityMotor::new, Industria.motor));
	public static final TileEntityType<TileEntityMFluidBath> FLUID_BATH = register("fluid_bath", TileEntityType.Builder.create(TileEntityMFluidBath::new, Industria.fluid_bath));
	public static final TileEntityType<TileEntityMBurnedCable> BURNED_CABLE = register("burned_cable", TileEntityType.Builder.create(TileEntityMBurnedCable::new, Industria.burned_cable));
	public static final TileEntityType<TileEntityRItemDetector> ITEM_DETECTOR = register("item_detector", TileEntityType.Builder.create(TileEntityRItemDetector::new, Industria.item_detector));
	public static final TileEntityType<TileEntityNComputer> COMPUTER = register("computer", TileEntityType.Builder.create(TileEntityNComputer::new, Industria.computer));
	public static final TileEntityType<TileEntityMChunkLoader> CHUNK_LOADER = register("chunk_loader", TileEntityType.Builder.create(TileEntityMChunkLoader::new, Industria.chunk_loader));
	public static final TileEntityType<TileEntityEnderCore> ENDER_CORE = register("ender_core", TileEntityType.Builder.create(TileEntityEnderCore::new, Industria.ender_core));
	public static final TileEntityType<TileEntityStructureScaffold> STRUCTURE_SCAFFOLD = register("structure_scaffold", TileEntityType.Builder.create(TileEntityStructureScaffold::new, Industria.structure_scaffold));
	public static final TileEntityType<TileEntityMBlastFurnace> BLAST_FURNACE = register("blast_furnace", TileEntityType.Builder.create(TileEntityMBlastFurnace::new, Industria.blast_furnace));
	public static final TileEntityType<TileEntityItemPipe> ITEM_PIPE = register("item_pipe", TileEntityType.Builder.create(TileEntityItemPipe::new, Industria.item_pipe));
	public static final TileEntityType<TileEntityItemPipePreassurizer> ITEM_PIPE_PREASSURIZER = register("item_pipe_preassurizer", TileEntityType.Builder.create(TileEntityItemPipePreassurizer::new, Industria.item_pipe_preassurizer));
	
	private static <T extends TileEntity> TileEntityType<T> register(String key, TileEntityType.Builder<T> builder) {
		Type<?> type = Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, key);
		TileEntityType<T> tileEntityType = builder.build(type);
		tileEntityType.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);
		return tileEntityType;
	}
	
}
