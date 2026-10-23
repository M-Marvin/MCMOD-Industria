package de.m_marvin.genet;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.m_marvin.genet.domains.TypeChainDomain;
import de.m_marvin.genet.domains.integrators.LevelBlockDomainIntegrator;
import de.m_marvin.genet.domains.integrators.testitems.TestWireBlock;
import de.m_marvin.genet.network.nodal.ElementManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

@Mod("nodenet")
public class NodeNet {
	
	public static final String MODID = "nodenet";

	public static final Logger LOGGER = LogManager.getLogger();
	
	/** Default ElementManager, loads Models from "data/modid/elements" **/
	public static final ElementManager ELEMENTS = new ElementManager(new Gson(), "elements");
	
	/** Resource Key for the Domain Registry **/
	public static final ResourceLocation DOMAINS_REGISTRY_LOCATION = ResourceLocation.tryBuild(NodeNet.MODID, "domains");
	private static final DeferredRegister<TypeChainDomain> DOMAINS = DeferredRegister.create(DOMAINS_REGISTRY_LOCATION, NodeNet.MODID);
	/** Domain Registry for all TypeChainDomains **/
	public static final Supplier<IForgeRegistry<TypeChainDomain>> DOMAINS_REGISTRY = DOMAINS.makeRegistry(() -> RegistryBuilder.<TypeChainDomain>of(DOMAINS_REGISTRY_LOCATION).disableSync().disableSaving());
	
	/** Default Global Domain, used for Level Blocks by default Integrator across all Dimensions **/
	public static final RegistryObject<TypeChainDomain> GLOBAL = DOMAINS.register("global", TypeChainDomain::new);
	
	/** Default Level Block Integrator, implementing connections between neighboring blocks **/
	public static final Capability<LevelBlockDomainIntegrator> LEVEL_BLOCK_INTEGRATOR = CapabilityManager.get(new CapabilityToken<LevelBlockDomainIntegrator>() {});

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NodeNet.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NodeNet.MODID);
	
	public static final RegistryObject<Block> TEST_WIRE = BLOCKS.register("test_wire", () -> new TestWireBlock(Properties.of().mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable()));
	public static final RegistryObject<Item> TEST_WIRE_ITEM = ITEMS.register("test_wire", () -> new BlockItem(TEST_WIRE.get(), new Item.Properties()));
	
	public NodeNet(FMLJavaModLoadingContext modctx) {

		DOMAINS.register(modctx.getModEventBus());
		BLOCKS.register(modctx.getModEventBus());
		ITEMS.register(modctx.getModEventBus());
		
		MinecraftForge.EVENT_BUS.register(this);
		
	}
	
	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel serverLevel)
			event.addCapability(ResourceLocation.tryBuild(MODID, "level_block_integrator"), new LevelBlockDomainIntegrator(serverLevel));
	}
	
}
