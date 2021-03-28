package de.redtec.typeregistys;

import com.mojang.datafixers.types.Type;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityAdvancedMovingBlock;
import de.redtec.tileentity.TileEntityControllPanel;
import de.redtec.tileentity.TileEntityConveyorBelt;
import de.redtec.tileentity.TileEntityFluidPipe;
import de.redtec.tileentity.TileEntityFluidValve;
import de.redtec.tileentity.TileEntityJigsaw;
import de.redtec.tileentity.TileEntityMAlloyFurnace;
import de.redtec.tileentity.TileEntityMBlender;
import de.redtec.tileentity.TileEntityMBurnedCable;
import de.redtec.tileentity.TileEntityMCoalHeater;
import de.redtec.tileentity.TileEntityMElectricFurnace;
import de.redtec.tileentity.TileEntityMFluidBath;
import de.redtec.tileentity.TileEntityMFluidInput;
import de.redtec.tileentity.TileEntityMFluidOutput;
import de.redtec.tileentity.TileEntityMFuseBox;
import de.redtec.tileentity.TileEntityMGenerator;
import de.redtec.tileentity.TileEntityMMultimeter;
import de.redtec.tileentity.TileEntityMRaffinery;
import de.redtec.tileentity.TileEntityMSchredder;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.tileentity.TileEntityMStoringCraftingTable;
import de.redtec.tileentity.TileEntityMThermalZentrifuge;
import de.redtec.tileentity.TileEntityMotor;
import de.redtec.tileentity.TileEntityNComputer;
import de.redtec.tileentity.TileEntityRHarvester;
import de.redtec.tileentity.TileEntityRHoverControler;
import de.redtec.tileentity.TileEntityRItemDetector;
import de.redtec.tileentity.TileEntityRLockedCompositeBlock;
import de.redtec.tileentity.TileEntityRSignalAntenna;
import de.redtec.tileentity.TileEntityRSignalProcessorContact;
import de.redtec.tileentity.TileEntityRedstoneReciver;
import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntityType {
	
	public static final TileEntityType<TileEntityAdvancedMovingBlock> ADVANCED_PISTON = register("advanced_piston", TileEntityType.Builder.create(TileEntityAdvancedMovingBlock::new, RedTec.advanced_moving_block));
	public static final TileEntityType<TileEntityRedstoneReciver> REMOTE_CONTROLER = register("remote_controler", TileEntityType.Builder.create(TileEntityRedstoneReciver::new, RedTec.advanced_moving_block));
	public static final TileEntityType<TileEntityRSignalAntenna> SIGNAL_ANTENNA = register("signal_antenna", TileEntityType.Builder.create(TileEntityRSignalAntenna::new, RedTec.antenna_conector));
	public static final TileEntityType<TileEntityMStoringCraftingTable> STORING_CRAFTING_TABLE = register("storing_crafting_table", TileEntityType.Builder.create(TileEntityMStoringCraftingTable::new, RedTec.storing_crafting_table));
	public static final TileEntityType<TileEntityRSignalProcessorContact> SIGNAL_PROCESSOR = register("signal_processor", TileEntityType.Builder.create(TileEntityRSignalProcessorContact::new, RedTec.signal_processor_contact));
	public static final TileEntityType<TileEntityRLockedCompositeBlock> LOCKED_COMPOSITE_BLOCK = register("locked_radial_conector", TileEntityType.Builder.create(TileEntityRLockedCompositeBlock::new, RedTec.radial_conector));
	public static final TileEntityType<TileEntityRHoverControler> HOVER_CONTROLER = register("hover_controler", TileEntityType.Builder.create(TileEntityRHoverControler::new, RedTec.hover_controler));
	public static final TileEntityType<TileEntityControllPanel> CONTROLL_PANEL = register("controll_panel", TileEntityType.Builder.create(TileEntityControllPanel::new, RedTec.controll_panel));
	public static final TileEntityType<TileEntityRHarvester> HARVESTER = register("harvester", TileEntityType.Builder.create(TileEntityRHarvester::new, RedTec.harvester));
	public static final TileEntityType<TileEntitySimpleBlockTicking> SIMPLE_BLOCK_TICKING = register("simple_block_ticking", TileEntityType.Builder.create(TileEntitySimpleBlockTicking::new, RedTec.panel_lamp, RedTec.infinity_power_source, RedTec.transformator_contact, RedTec.steam, RedTec.rail_adapter));
	public static final TileEntityType<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityType.Builder.create(TileEntityJigsaw::new, RedTec.jigsaw));
	public static final TileEntityType<TileEntityMGenerator> GENERATOR = register("generator", TileEntityType.Builder.create(TileEntityMGenerator::new, RedTec.generator));
	public static final TileEntityType<TileEntityFluidPipe> FLUID_PIPE = register("fluid_pipe", TileEntityType.Builder.create(TileEntityFluidPipe::new, RedTec.fluid_pipe));
	public static final TileEntityType<TileEntityMFluidInput> FLUID_INPUT = register("fluid_input", TileEntityType.Builder.create(TileEntityMFluidInput::new, RedTec.fluid_input));
	public static final TileEntityType<TileEntityMFluidOutput> FLUID_OUTPUT = register("fluid_output", TileEntityType.Builder.create(TileEntityMFluidOutput::new, RedTec.fluid_output));
	public static final TileEntityType<TileEntityMSteamGenerator> STEAM_GENERATOR = register("steam_generator", TileEntityType.Builder.create(TileEntityMSteamGenerator::new, RedTec.steam_generator));
	public static final TileEntityType<TileEntityMCoalHeater> COAL_HEATER = register("coal_heater", TileEntityType.Builder.create(TileEntityMCoalHeater::new, RedTec.coal_heater));
	public static final TileEntityType<TileEntityMFuseBox> FUSE_BOX = register("fuse_box", TileEntityType.Builder.create(TileEntityMFuseBox::new, RedTec.fuse_box));
	public static final TileEntityType<TileEntityMMultimeter> MULTIMETER = register("block_multimeter", TileEntityType.Builder.create(TileEntityMMultimeter::new, RedTec.multimeter));
	public static final TileEntityType<TileEntityFluidValve> FLUID_VALVE = register("fluid_valve", TileEntityType.Builder.create(TileEntityFluidValve::new, RedTec.fluid_valve));
	public static final TileEntityType<TileEntityMElectricFurnace> ELECTRIC_FURNACE = register("electric_furnace", TileEntityType.Builder.create(TileEntityMElectricFurnace::new, RedTec.electric_furnace));
	public static final TileEntityType<TileEntityMSchredder> SCHREDDER = register("schredder", TileEntityType.Builder.create(TileEntityMSchredder::new, RedTec.schredder));
	public static final TileEntityType<TileEntityMBlender> BLENDER = register("blender", TileEntityType.Builder.create(TileEntityMBlender::new, RedTec.blender));
	public static final TileEntityType<TileEntityMRaffinery> RAFFINERY = register("raffinery", TileEntityType.Builder.create(TileEntityMRaffinery::new, RedTec.raffinery));
	public static final TileEntityType<TileEntityMAlloyFurnace> ALLOY_FURNACE = register("alloy_furnace", TileEntityType.Builder.create(TileEntityMAlloyFurnace::new, RedTec.alloy_furnace));
	public static final TileEntityType<TileEntityConveyorBelt> CONVEYOR_BELT = register("conveyor_belt", TileEntityType.Builder.create(TileEntityConveyorBelt::new, RedTec.conveyor_belt, RedTec.conveyor_spliter));
	public static final TileEntityType<TileEntityMThermalZentrifuge> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", TileEntityType.Builder.create(TileEntityMThermalZentrifuge::new, RedTec.thermal_zentrifuge));
	public static final TileEntityType<TileEntityMotor> MOTOR = register("motor", TileEntityType.Builder.create(TileEntityMotor::new, RedTec.motor));
	public static final TileEntityType<TileEntityMFluidBath> FLUID_BATH = register("fluid_bath", TileEntityType.Builder.create(TileEntityMFluidBath::new, RedTec.fluid_bath));
	public static final TileEntityType<TileEntityMBurnedCable> BURNED_CABLE = register("burned_cable", TileEntityType.Builder.create(TileEntityMBurnedCable::new, RedTec.burned_cable));
	public static final TileEntityType<TileEntityRItemDetector> ITEM_DETECTOR = register("item_detector", TileEntityType.Builder.create(TileEntityRItemDetector::new, RedTec.item_detector));
	public static final TileEntityType<TileEntityNComputer> COMPUTER = register("computer", TileEntityType.Builder.create(TileEntityNComputer::new, RedTec.computer));
	
	private static <T extends TileEntity> TileEntityType<T> register(String key, TileEntityType.Builder<T> builder) {
		Type<?> type = Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, key);
		TileEntityType<T> tileEntityType = builder.build(type);
		tileEntityType.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);
		return tileEntityType;
	}
	
}
