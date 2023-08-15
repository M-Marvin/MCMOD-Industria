package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blockentities.PowerSourceBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityTypes {

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IndustriaCore.MODID);
	public static void register() {
		BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<BlockEntityType<PowerSourceBlockEntity>> POWER_SOURCE = BLOCK_ENTITY_TYPES.register("power_source", () -> BlockEntityType.Builder.of(PowerSourceBlockEntity::new, Blocks.POWER_SOURCE.get()).build(null));
	
}
