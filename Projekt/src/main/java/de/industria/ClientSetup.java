package de.industria;

import de.industria.gui.ScreenCardboardBox;
import de.industria.gui.ScreenItemDistributor;
import de.industria.gui.ScreenJigsaw;
import de.industria.gui.ScreenMAirCompressor;
import de.industria.gui.ScreenMAlloyFurnace;
import de.industria.gui.ScreenMBlastFurnace;
import de.industria.gui.ScreenMBlender;
import de.industria.gui.ScreenMChunkLoader;
import de.industria.gui.ScreenMCoalHeater;
import de.industria.gui.ScreenMElectricFurnace;
import de.industria.gui.ScreenMFluidBath;
import de.industria.gui.ScreenMGenerator;
import de.industria.gui.ScreenMMetalFormer;
import de.industria.gui.ScreenMOreWashingPlant;
import de.industria.gui.ScreenMRaffinery;
import de.industria.gui.ScreenMSchredder;
import de.industria.gui.ScreenMStoredCrafting;
import de.industria.gui.ScreenMThermalZentrifuge;
import de.industria.gui.ScreenNComputer;
import de.industria.gui.ScreenNetworkConfigurator;
import de.industria.gui.ScreenRHarvester;
import de.industria.gui.ScreenRHoverControler;
import de.industria.gui.ScreenRProcessor;
import de.industria.gui.ScreenRReciver;
import de.industria.gui.ScreenRecipeCreator;
import de.industria.renderer.EntityDummyRenderer;
import de.industria.renderer.ItemSchredderToolCrusherModel;
import de.industria.renderer.ItemSchredderToolMaceratorModel;
import de.industria.renderer.TileEntityAdvancedMovingBlockRenderer;
import de.industria.renderer.TileEntityControllPanelRenderer;
import de.industria.renderer.TileEntityConveyorBeltRenderer;
import de.industria.renderer.TileEntityEnderCoreRenderer;
import de.industria.renderer.TileEntityFluidCannisterRenderer;
import de.industria.renderer.TileEntityFuseBoxRenderer;
import de.industria.renderer.TileEntityGaugeRenderer;
import de.industria.renderer.TileEntityMAirCompressorRenderer;
import de.industria.renderer.TileEntityMBatteryRenderer;
import de.industria.renderer.TileEntityMBlastFurnaceRenderer;
import de.industria.renderer.TileEntityMBlenderRenderer;
import de.industria.renderer.TileEntityMCoalHeaterRenderer;
import de.industria.renderer.TileEntityMElectricHeaterRenderer;
import de.industria.renderer.TileEntityMFluidBathRenderer;
import de.industria.renderer.TileEntityMGasHeaterRenderer;
import de.industria.renderer.TileEntityMMetalFormerRenderer;
import de.industria.renderer.TileEntityMOreWashingPlantRenderer;
import de.industria.renderer.TileEntityMRaffineryRenderer;
import de.industria.renderer.TileEntityMSchredderRenderer;
import de.industria.renderer.TileEntityMSteamGeneratorRenderer;
import de.industria.renderer.TileEntityNComputerRenderer;
import de.industria.renderer.TileEntitySignalProcessorContactRenderer;
import de.industria.renderer.TileEntityStructureScaffoldRenderer;
import de.industria.typeregistys.ModClientBindings;
import de.industria.typeregistys.ModContainerType;
import de.industria.typeregistys.ModEntityType;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ClientSetup {
	
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event) {
		
		RenderTypeLookup.setRenderLayer(ModItems.capacitor, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.pulse_counter, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.stacked_redstone_torch, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.stacked_redstone_wire, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.signal_wire, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.signal_processor_contact, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.salsola_seeds, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.steam, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.steam_generator, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.DESTILLED_WATER, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_DESTILLED_WATER, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.STEAM, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.multimeter, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModItems.fluid_valve, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModItems.blender, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.rubber_leaves, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModItems.rubber_sapling, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModItems.tree_tap, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.burned_cable, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.steel_rail, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModItems.inductive_rail, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ModItems.structure_scaffold, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.preassure_pipe, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.swamp_algae, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.hanging_vine, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.burned_trapdoor, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.burned_door, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.white_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.orange_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.magenta_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.light_blue_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.yellow_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.green_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.pink_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.gray_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.light_gray_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.cyan_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.purple_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.black_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.blue_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.red_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.lime_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.brown_stained_glass_slab, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.glass_slab, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_electric_copper_cable, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_electric_electrolyt_copper_cable, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_electric_aluminium_cable, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_electric_burned_cable, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_network_cable, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_steel_pipe, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_copper_pipe, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.encased_metallic_glass_pipe, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.fluorite_crystal, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModItems.zircon_crystal, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.iron_ladder, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.fluid_cannister.getBlock(), RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.flax_crop, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(ModItems.battery, RenderType.cutoutMipped());
		
		RenderTypeLookup.setRenderLayer(ModFluids.HYDROFLUORIC_ACID, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_HYDROFLUORIC_ACID, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.SULFURIC_ACID, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_SULFURIC_ACID, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.IRON_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_IRON_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.COPPER_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_COPPER_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.WOLFRAM_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_WOLFRAM_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.ALUMINIUM_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_ALUMINIUM_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.TIN_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_TIN_SOLUTION, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.NATRON_LYE, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_NATRON_LYE, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.CHEMICAL_WATER, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_CHEMICAL_WATER, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.RAW_OIL, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_RAW_OIL, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.COMPRESSED_AIR, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.BIOGAS, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FUEL_GAS, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(ModFluids.VANADIUM_SOLUTION, RenderType.translucent());
		
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.ADVANCED_PISTON, TileEntityAdvancedMovingBlockRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.SIGNAL_PROCESSOR, TileEntitySignalProcessorContactRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.CONTROLL_PANEL, TileEntityControllPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.STEAM_GENERATOR, TileEntityMSteamGeneratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.FUSE_BOX, TileEntityFuseBoxRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.MULTIMETER, TileEntityGaugeRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.SCHREDDER, TileEntityMSchredderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.BLENDER, TileEntityMBlenderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.CONVEYOR_BELT, TileEntityConveyorBeltRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.RAFFINERY, TileEntityMRaffineryRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.FLUID_BATH, TileEntityMFluidBathRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.COMPUTER, TileEntityNComputerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.ENDER_CORE, TileEntityEnderCoreRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.COAL_HEATER, TileEntityMCoalHeaterRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.STRUCTURE_SCAFFOLD, TileEntityStructureScaffoldRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.ENCASED_FLUID_PIPE, TileEntityStructureScaffoldRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.BLAST_FURNACE, TileEntityMBlastFurnaceRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.AIR_COMPRESSOR, TileEntityMAirCompressorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.METAL_FORMER, TileEntityMMetalFormerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.ORE_WASHING_PLANT, TileEntityMOreWashingPlantRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.ELECTRIC_HEATER, TileEntityMElectricHeaterRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.GAS_HEATER, TileEntityMGasHeaterRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.FLUID_CANISTER, TileEntityFluidCannisterRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.BATTERY, TileEntityMBatteryRenderer::new);
		
		RenderingRegistry.registerEntityRenderingHandler(ModEntityType.FALLING_FLUID, EntityDummyRenderer::new);
		
		ScreenManager.register(ModContainerType.STORED_CRAFTING, ScreenMStoredCrafting::new);
		ScreenManager.register(ModContainerType.PROCESSOR, ScreenRProcessor::new);
		ScreenManager.register(ModContainerType.HOVER_CONTROLER, ScreenRHoverControler::new);
		ScreenManager.register(ModContainerType.REDSTONE_RECIVER, ScreenRReciver::new);
		ScreenManager.register(ModContainerType.HARVESTER, ScreenRHarvester::new);
		ScreenManager.register(ModContainerType.JIGSAW, ScreenJigsaw::new);
		ScreenManager.register(ModContainerType.GENERATOR, ScreenMGenerator::new);
		ScreenManager.register(ModContainerType.COAL_HEATER, ScreenMCoalHeater::new);
		ScreenManager.register(ModContainerType.ELECTRIC_FURNACE, ScreenMElectricFurnace::new);
		ScreenManager.register(ModContainerType.SCHREDDER, ScreenMSchredder::new);
		ScreenManager.register(ModContainerType.BLENDER, ScreenMBlender::new);
		ScreenManager.register(ModContainerType.RAFFINERY, ScreenMRaffinery::new);
		ScreenManager.register(ModContainerType.THERMAL_ZENTRIFUGE, ScreenMThermalZentrifuge::new);
		ScreenManager.register(ModContainerType.ALLOY_FURNACE, ScreenMAlloyFurnace::new);
		ScreenManager.register(ModContainerType.FLUID_BATH, ScreenMFluidBath::new);
		ScreenManager.register(ModContainerType.COMPUTER, ScreenNComputer::new);
		ScreenManager.register(ModContainerType.NETWORK_CONFIGURATOR, ScreenNetworkConfigurator::new);
		ScreenManager.register(ModContainerType.CHUNK_LAODER, ScreenMChunkLoader::new);
		ScreenManager.register(ModContainerType.AIR_COMPRESSOR, ScreenMAirCompressor::new);
		ScreenManager.register(ModContainerType.ITEM_DISTRIBUTOR, ScreenItemDistributor::new);
		ScreenManager.register(ModContainerType.BLAST_FURNACE, ScreenMBlastFurnace::new);
		ScreenManager.register(ModContainerType.METAL_FORMER, ScreenMMetalFormer::new);
		ScreenManager.register(ModContainerType.ORE_WASHING_PLANT, ScreenMOreWashingPlant::new);
		ScreenManager.register(ModContainerType.CARDBOARD_BOX, ScreenCardboardBox::new);
		ScreenManager.register(ModContainerType.RECIPE_CREATOR, ScreenRecipeCreator::new);
		
		ModClientBindings.bindModelToitem(ModItems.schredder_crusher, new ResourceLocation(Industria.MODID, "textures/item/schredder_crusher.png"), new ItemSchredderToolCrusherModel());
		ModClientBindings.bindModelToitem(ModItems.schredder_macerator, new ResourceLocation(Industria.MODID, "textures/item/schredder_macerator.png"), new ItemSchredderToolMaceratorModel());
		
	}
	
	@SubscribeEvent
	public static void setupBlockColors(ColorHandlerEvent.Block event) {
		BlockColors colors = event.getBlockColors();
		colors.register((state, world, pos, tint) -> world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColors.getDefaultColor(), ModItems.beech_leaves);
		colors.register((state, world, pos, tint) -> world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColors.getDefaultColor(), ModItems.marple_leaves);
		colors.register((state, world, pos, tint) -> world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColors.getDefaultColor(), ModItems.mangrove_leaves);
		colors.register((state, world, pos, tint) -> world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColors.getDefaultColor(), ModItems.rubber_leaves);
		colors.register((state, world, pos, tint) -> world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColors.getDefaultColor(), ModItems.swamp_algae);
		colors.register((state, world, pos, tint) -> world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColors.getDefaultColor(), ModItems.hanging_vine);
	}
    
	@SubscribeEvent
	public static void setupItemColors(ColorHandlerEvent.Item event) {
		ItemColors colors = event.getItemColors();
		colors.register((stack, tint) -> FoliageColors.getDefaultColor(), ModItems.beech_leaves);
		colors.register((stack, tint) -> FoliageColors.getDefaultColor(), ModItems.marple_leaves);
		colors.register((stack, tint) -> FoliageColors.getDefaultColor(), ModItems.mangrove_leaves);
		colors.register((stack, tint) -> FoliageColors.getDefaultColor(), ModItems.rubber_leaves);
		colors.register((stack, tint) -> FoliageColors.getDefaultColor(), ModItems.swamp_algae);
		colors.register((stack, tint) -> FoliageColors.getDefaultColor(), ModItems.hanging_vine);
	}
	
}
