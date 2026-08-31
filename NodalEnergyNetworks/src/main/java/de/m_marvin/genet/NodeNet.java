package de.m_marvin.genet;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.genet.typchainnet.TypeChainDomain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

@Mod("nodenet")
public class NodeNet {
	
	public static final String MODID = "nodenet";

	public static final Logger LOGGER = LogManager.getLogger();
	
	public static final ResourceLocation DOMAINS_REGISTRY_LOCATION = ResourceLocation.tryBuild(NodeNet.MODID, "domains");
	public static final DeferredRegister<TypeChainDomain> DOMAINS = DeferredRegister.create(DOMAINS_REGISTRY_LOCATION, NodeNet.MODID);
	public static final Supplier<IForgeRegistry<TypeChainDomain>> DOMAINS_REGISTRY = DOMAINS.makeRegistry(() -> RegistryBuilder.<TypeChainDomain>of(DOMAINS_REGISTRY_LOCATION).disableSync().disableSaving());
	
	public static final RegistryObject<TypeChainDomain> GLOBAL_DOMAIN = DOMAINS.register("global", TypeChainDomain::new);
	
	public NodeNet(FMLJavaModLoadingContext modctx) {

		DOMAINS.register(modctx.getModEventBus());
		
	}
	
}
