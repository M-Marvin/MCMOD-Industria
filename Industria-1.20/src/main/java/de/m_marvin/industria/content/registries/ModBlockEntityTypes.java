package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blockentities.FloodlightBlockEntity;
import de.m_marvin.industria.content.blockentities.GeneratorBlockEntity;
import de.m_marvin.industria.content.blockentities.MotorBlockEntity;
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
	
	public static final RegistryObject<BlockEntityType<FloodlightBlockEntity>> FLOODLIGHT = BLOCK_ENTITY_TYPES.register("floodlight", () -> BlockEntityType.Builder.of(FloodlightBlockEntity::new, ModBlocks.BRASS_FLOODLIGHT.get(), ModBlocks.STEEL_FLOOFLIGHT.get()).build(null));
	public static final RegistryObject<BlockEntityType<MotorBlockEntity>> MOTOR = null; //BLOCK_ENTITY_TYPES.register("motor", () -> BlockEntityType.Builder.of(MotorBlockEntity::new, ModBlocks.MOTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<GeneratorBlockEntity>> GENERATOR = null;//BLOCK_ENTITY_TYPES.register("generator", () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.MOTOR.get()).build(null));
	
}
