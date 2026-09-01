package de.m_marvin.genet;

import java.util.function.Supplier;

import de.m_marvin.genet.references.TypeChain;
import de.m_marvin.genet.references.TypeChain.Format;
import dev.lukebemish.codecextras.structured.RecordStructure;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod("nodenet")
public class NodeNet {
	
	public static final String MODID = "nodenet";
	
	public static final ResourceKey<Registry<TypeChain.Format>> TYPE_CHAINS_KEY = ResourceKey.createRegistryKey(ResourceLocation.tryBuild(MODID, "typechains"));
	public static final DeferredRegister<TypeChain.Format> TYPE_CHAINS = DeferredRegister.create(TYPE_CHAINS_KEY, MODID);
	public static final Supplier<IForgeRegistry<Format>> TYPE_CHAIN_REGISTRY = TYPE_CHAINS.makeRegistry(() -> new RegistryBuilder<TypeChain.Format>());
	
	
	
	public NodeNet(FMLJavaModLoadingContext modctx) {
		
	}
	
}
