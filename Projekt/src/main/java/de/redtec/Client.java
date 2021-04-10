package de.redtec;

import de.redtec.gui.ScreenJigsaw;
import de.redtec.gui.ScreenMAlloyFurnace;
import de.redtec.gui.ScreenMBlender;
import de.redtec.gui.ScreenMChunkLoader;
import de.redtec.gui.ScreenMCoalHeater;
import de.redtec.gui.ScreenMElectricFurnace;
import de.redtec.gui.ScreenMFluidBath;
import de.redtec.gui.ScreenMGenerator;
import de.redtec.gui.ScreenMRaffinery;
import de.redtec.gui.ScreenMSchredder;
import de.redtec.gui.ScreenMStoredCrafting;
import de.redtec.gui.ScreenMThermalZentrifuge;
import de.redtec.gui.ScreenNComputer;
import de.redtec.gui.ScreenNetworkConfigurator;
import de.redtec.gui.ScreenRHarvester;
import de.redtec.gui.ScreenRHoverControler;
import de.redtec.gui.ScreenRProcessor;
import de.redtec.gui.ScreenRReciver;
import de.redtec.renderer.ItemSchredderToolCrusherModel;
import de.redtec.renderer.TileEntityAdvancedMovingBlockRenderer;
import de.redtec.renderer.TileEntityControllPanelRenderer;
import de.redtec.renderer.TileEntityConveyorBeltRenderer;
import de.redtec.renderer.TileEntityEnderCoreRenderer;
import de.redtec.renderer.TileEntityFuseBoxRenderer;
import de.redtec.renderer.TileEntityGaugeRenderer;
import de.redtec.renderer.TileEntityMBlenderRenderer;
import de.redtec.renderer.TileEntityMFluidBathRenderer;
import de.redtec.renderer.TileEntityMRaffineryRenderer;
import de.redtec.renderer.TileEntityMSchredderRenderer;
import de.redtec.renderer.TileEntityMSteamGeneratorRenderer;
import de.redtec.renderer.TileEntityNComputerRenderer;
import de.redtec.renderer.TileEntitySignalProcessorContactRenderer;
import de.redtec.typeregistys.ModClientBindings;
import de.redtec.typeregistys.ModContainerType;
import de.redtec.typeregistys.ModFluids;
import de.redtec.typeregistys.ModTileEntityType;
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
		
		RenderTypeLookup.setRenderLayer(RedTec.capacitor, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.pulse_counter, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.stacked_redstone_torch, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.stacked_redstone_wire, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.signal_wire, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.signal_processor_contact, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.salsola_seeds, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.steam, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(RedTec.steam_generator, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.DESTILLED_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_DESTILLED_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.STEAM, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(RedTec.multimeter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(RedTec.fluid_valve, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(RedTec.blender, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.rubber_leaves, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(RedTec.rubber_sapling, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(RedTec.tree_tap, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.burned_cable, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(RedTec.steel_rail, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(RedTec.inductive_rail, RenderType.getCutout());
		
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
		
		ModClientBindings.bindModelToitem(RedTec.schredder_crusher, new ResourceLocation(RedTec.MODID, "textures/item/schredder_crusher.png"), new ItemSchredderToolCrusherModel());
		
		
	}
	
}
