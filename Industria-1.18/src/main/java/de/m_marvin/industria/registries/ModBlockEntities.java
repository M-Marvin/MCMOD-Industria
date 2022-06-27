package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntities {

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Industria.MODID);
	public static void register() {
		BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
