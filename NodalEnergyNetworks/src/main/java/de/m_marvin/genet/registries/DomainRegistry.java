package de.m_marvin.genet.registries;

import java.util.function.Supplier;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.typchainnet.TypeChainDomain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class DomainRegistry {
	
//	public static final ResourceLocation DOMAINS_REGISTRY_LOCATION = ResourceLocation.tryBuild(NodeNet.MODID, "domains");
//	public static final DeferredRegister<TypeChainDomain> DOMAINS = DeferredRegister.create(DOMAINS_REGISTRY_LOCATION, NodeNet.MODID);
//	public static final Supplier<IForgeRegistry<TypeChainDomain>> DOMAINS_REGISTRY = DOMAINS.makeRegistry(() -> RegistryBuilder.<TypeChainDomain>of(DOMAINS_REGISTRY_LOCATION).disableSync().disableSaving());

//	public static final Format BLOCK_FACE_NODE = TypeChain.registerFormat(MODID, "block_face", Format.of(BlockPos.class, Axis.class));
//	public static final Format BLOCK_ID_NODE = TypeChain.registerFormat(MODID, "block_id", Format.of(BlockPos.class, Integer.class));
//	
//	public static final Format NAME_SUBNODE = TypeChain.registerFormat(MODID, "name", BLOCK_ID_NODE.sub(String.class));
	
	
//	public static void register(FMLJavaModLoadingContext modctx) {
//		DOMAINS.register(modctx.getModEventBus());
//	}
//	
//	public static final RegistryObject<TypeChainDomain> DEFAULT = DOMAINS.register("default", TypeChainDomain::new);
	
}
