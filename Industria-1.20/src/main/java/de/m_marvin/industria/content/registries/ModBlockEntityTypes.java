package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.content.blockentities.GeneratorBlockEntity;
import de.m_marvin.industria.content.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.content.blockentities.MotorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, IndustriaCore.MODID);
	public static void register() {
		BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<BlockEntityType<MotorBlockEntity>> MOTOR = BLOCK_ENTITY_TYPES.register("motor", () -> BlockEntityType.Builder.of(MotorBlockEntity::new, ModBlocks.MOTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<GeneratorBlockEntity>> GENERATOR = BLOCK_ENTITY_TYPES.register("generator", () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.MOTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<JunctionBoxBlockEntity>> JUNCTION_BOX = BLOCK_ENTITY_TYPES.register("junction_box", () -> BlockEntityType.Builder.of(JunctionBoxBlockEntity::new, ModBlocks.JUNCTION_BOX.get()).build(null));
	
}