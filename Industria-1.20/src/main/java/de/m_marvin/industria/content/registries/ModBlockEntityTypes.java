package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blockentities.machines.FloodlightBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.MobileFuelGeneratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Industria.MODID);
	public static void register() {
		BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<BlockEntityType<FloodlightBlockEntity>> FLOODLIGHT 						= BLOCK_ENTITY_TYPES.register("floodlight", () -> BlockEntityType.Builder.of(FloodlightBlockEntity::new, ModBlocks.BRASS_FLOODLIGHT.get(), ModBlocks.STEEL_FLOOFLIGHT.get()).build(null));
	public static final RegistryObject<BlockEntityType<MobileFuelGeneratorBlockEntity>> MOBILE_FUEL_GENERATOR 	= BLOCK_ENTITY_TYPES.register("mobile_fuel_generator", () -> BlockEntityType.Builder.of(MobileFuelGeneratorBlockEntity::new, ModBlocks.MOBILE_FUEL_GENERATOR.get()).build(null));
	
}
