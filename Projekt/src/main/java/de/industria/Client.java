package de.industria;

import de.industria.gui.ScreenJigsaw;
import de.industria.gui.ScreenMAirCompressor;
import de.industria.gui.ScreenMAlloyFurnace;
import de.industria.gui.ScreenMBlender;
import de.industria.gui.ScreenMChunkLoader;
import de.industria.gui.ScreenMCoalHeater;
import de.industria.gui.ScreenMElectricFurnace;
import de.industria.gui.ScreenMFluidBath;
import de.industria.gui.ScreenMGenerator;
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
import de.industria.renderer.ItemSchredderToolCrusherModel;
import de.industria.renderer.TileEntityAdvancedMovingBlockRenderer;
import de.industria.renderer.TileEntityControllPanelRenderer;
import de.industria.renderer.TileEntityConveyorBeltRenderer;
import de.industria.renderer.TileEntityEnderCoreRenderer;
import de.industria.renderer.TileEntityFuseBoxRenderer;
import de.industria.renderer.TileEntityGaugeRenderer;
import de.industria.renderer.TileEntityMAirCompressorRenderer;
import de.industria.renderer.TileEntityMBlastFurnaceRenderer;
import de.industria.renderer.TileEntityMBlenderRenderer;
import de.industria.renderer.TileEntityMCoalHeaterRenderer;
import de.industria.renderer.TileEntityMFluidBathRenderer;
import de.industria.renderer.TileEntityMRaffineryRenderer;
import de.industria.renderer.TileEntityMSchredderRenderer;
import de.industria.renderer.TileEntityMSteamGeneratorRenderer;
import de.industria.renderer.TileEntityNComputerRenderer;
import de.industria.renderer.TileEntitySignalProcessorContactRenderer;
import de.industria.renderer.TileEntityStructureScaffoldRenderer;
import de.industria.typeregistys.ModClientBindings;
import de.industria.typeregistys.ModContainerType;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class Client {

	@OnlyIn(Dist.CLIENT)
	public static void setup(final FMLClientSetupEvent event) {
		
		RenderTypeLookup.setRenderLayer(Industria.capacitor, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.pulse_counter, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.stacked_redstone_torch, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.stacked_redstone_wire, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.signal_wire, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.signal_processor_contact, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.salsola_seeds, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.steam, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(Industria.steam_generator, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.DESTILLED_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_DESTILLED_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.STEAM, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(Industria.multimeter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(Industria.fluid_valve, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(Industria.blender, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.rubber_leaves, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(Industria.rubber_sapling, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(Industria.tree_tap, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.burned_cable, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.steel_rail, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(Industria.inductive_rail, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(Industria.structure_scaffold, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(Industria.preassure_pipe, RenderType.getCutoutMipped());
		
		RenderTypeLookup.setRenderLayer(ModFluids.SULFURIC_ACID, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_SULFURIC_ACID, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.IRON_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_IRON_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.COPPER_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_COPPER_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.WOLFRAM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_WOLFRAM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.ALUMINIUM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_ALUMINIUM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.TIN_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_TIN_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.NATRON_LYE, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_NATRON_LYE, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.CHEMICAL_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_CHEMICAL_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.RAW_OIL, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_RAW_OIL, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.COMPRESSED_AIR, RenderType.getTranslucent());
		
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
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.BLAST_FURNACE, TileEntityMBlastFurnaceRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.AIR_COMPRESSOR, TileEntityMAirCompressorRenderer::new);
		
		ScreenManager.registerFactory(ModContainerType.STORED_CRAFTING, ScreenMStoredCrafting::new);
		ScreenManager.registerFactory(ModContainerType.PROCESSOR, ScreenRProcessor::new);
		ScreenManager.registerFactory(ModContainerType.HOVER_CONTROLER, ScreenRHoverControler::new);
		ScreenManager.registerFactory(ModContainerType.REDSTONE_RECIVER, ScreenRReciver::new);
		ScreenManager.registerFactory(ModContainerType.HARVESTER, ScreenRHarvester::new);
		ScreenManager.registerFactory(ModContainerType.JIGSAW, ScreenJigsaw::new);
		ScreenManager.registerFactory(ModContainerType.GENERATOR, ScreenMGenerator::new);
		ScreenManager.registerFactory(ModContainerType.COAL_HEATER, ScreenMCoalHeater::new);
		ScreenManager.registerFactory(ModContainerType.ELECTRIC_FURNACE, ScreenMElectricFurnace::new);
		ScreenManager.registerFactory(ModContainerType.SCHREDDER, ScreenMSchredder::new);
		ScreenManager.registerFactory(ModContainerType.BLENDER, ScreenMBlender::new);
		ScreenManager.registerFactory(ModContainerType.RAFFINERY, ScreenMRaffinery::new);
		ScreenManager.registerFactory(ModContainerType.THERMAL_ZENTRIFUGE, ScreenMThermalZentrifuge::new);
		ScreenManager.registerFactory(ModContainerType.ALLOY_FURNACE, ScreenMAlloyFurnace::new);
		ScreenManager.registerFactory(ModContainerType.FLUID_BATH, ScreenMFluidBath::new);
		ScreenManager.registerFactory(ModContainerType.COMPUTER, ScreenNComputer::new);
		ScreenManager.registerFactory(ModContainerType.NETWORK_CONFIGURATOR, ScreenNetworkConfigurator::new);
		ScreenManager.registerFactory(ModContainerType.CHUNK_LAODER, ScreenMChunkLoader::new);
		ScreenManager.registerFactory(ModContainerType.AIR_COMPRESSOR, ScreenMAirCompressor::new);
		
		ModClientBindings.bindModelToitem(Industria.schredder_crusher, new ResourceLocation(Industria.MODID, "textures/item/schredder_crusher.png"), new ItemSchredderToolCrusherModel());
		
		
	}
	
}
