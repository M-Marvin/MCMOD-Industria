package de.redtec.registys;

import com.mojang.datafixers.types.Type;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityAdvancedMovingBlock;
import de.redtec.tileentity.TileEntityControllPanel;
import de.redtec.tileentity.TileEntityFluidInput;
import de.redtec.tileentity.TileEntityFluidOutput;
import de.redtec.tileentity.TileEntityFluidPipe;
import de.redtec.tileentity.TileEntityHarvester;
import de.redtec.tileentity.TileEntityHoverControler;
import de.redtec.tileentity.TileEntityJigsaw;
import de.redtec.tileentity.TileEntityLockedCompositeBlock;
import de.redtec.tileentity.TileEntityMCoalHeater;
import de.redtec.tileentity.TileEntityMGenerator;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.tileentity.TileEntityRedstoneReciver;
import de.redtec.tileentity.TileEntitySignalAntenna;
import de.redtec.tileentity.TileEntitySignalProcessorContact;
import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import de.redtec.tileentity.TileEntityStoringCraftingTable;
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
	public static final TileEntityType<TileEntitySignalAntenna> SIGNAL_ANTENNA = register("signal_antenna", TileEntityType.Builder.create(TileEntitySignalAntenna::new, RedTec.signal_antenna_conector));
	public static final TileEntityType<TileEntityStoringCraftingTable> STORING_CRAFTING_TABLE = register("storing_crafting_table", TileEntityType.Builder.create(TileEntityStoringCraftingTable::new, RedTec.storing_crafting_table));
	public static final TileEntityType<TileEntitySignalProcessorContact> SIGNAL_PROCESSOR = register("signal_processor", TileEntityType.Builder.create(TileEntitySignalProcessorContact::new, RedTec.signal_processor_contact));
	public static final TileEntityType<TileEntityLockedCompositeBlock> LOCKED_COMPOSITE_BLOCK = register("locked_radial_conector", TileEntityType.Builder.create(TileEntityLockedCompositeBlock::new, RedTec.radial_conector));
	public static final TileEntityType<TileEntityHoverControler> HOVER_CONTROLER = register("hover_controler", TileEntityType.Builder.create(TileEntityHoverControler::new, RedTec.hover_engine));
	public static final TileEntityType<TileEntityControllPanel> CONTROLL_PANEL = register("controll_panel", TileEntityType.Builder.create(TileEntityControllPanel::new, RedTec.controll_panel));
	public static final TileEntityType<TileEntityHarvester> HARVESTER = register("harvester", TileEntityType.Builder.create(TileEntityHarvester::new, RedTec.harvester));
	public static final TileEntityType<TileEntitySimpleBlockTicking> SIMPLE_BLOCK_TICKING = register("simple_block_ticking", TileEntityType.Builder.create(TileEntitySimpleBlockTicking::new, RedTec.panel_lamp, RedTec.infinity_power_source));
	public static final TileEntityType<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityType.Builder.create(TileEntityJigsaw::new, RedTec.jigsaw));
	public static final TileEntityType<TileEntityMGenerator> GENERATOR = register("generator", TileEntityType.Builder.create(TileEntityMGenerator::new, RedTec.generator));
	public static final TileEntityType<TileEntityFluidPipe> FLUID_PIPE = register("fluid_pipe", TileEntityType.Builder.create(TileEntityFluidPipe::new, RedTec.fluid_pipe));
	public static final TileEntityType<TileEntityFluidInput> FLUID_INPUT = register("fluid_input", TileEntityType.Builder.create(TileEntityFluidInput::new, RedTec.fluid_input));
	public static final TileEntityType<TileEntityFluidOutput> FLUID_OUTPUT = register("fluid_output", TileEntityType.Builder.create(TileEntityFluidOutput::new, RedTec.fluid_output));
	public static final TileEntityType<TileEntityMSteamGenerator> STEAM_GENERATOR = register("steam_generator", TileEntityType.Builder.create(TileEntityMSteamGenerator::new, RedTec.steam_generator));
	public static final TileEntityType<TileEntityMCoalHeater> COAL_HEATER = register("coal_heater", TileEntityType.Builder.create(TileEntityMCoalHeater::new, RedTec.coal_heater));
	
	private static <T extends TileEntity> TileEntityType<T> register(String key, TileEntityType.Builder<T> builder) {
		Type<?> type = Util.func_240976_a_(TypeReferences.BLOCK_ENTITY, key);
		TileEntityType<T> tileEntityType = builder.build(type);
		tileEntityType.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);
		return tileEntityType;
	}
	
}
