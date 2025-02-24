package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import de.m_marvin.industria.core.electrics.types.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.electrics.types.blockentities.VoltageSourceBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.MotorBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.SimpleKineticBlockEntity;
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
	
	public static final RegistryObject<BlockEntityType<JunctionBoxBlockEntity>> JUNCTION_BOX = BLOCK_ENTITY_TYPES.register("junction_box", () -> BlockEntityType.Builder.of(JunctionBoxBlockEntity::new).build(null));
	public static final RegistryObject<BlockEntityType<VoltageSourceBlockEntity>> VOLTAGE_SOURCE = BLOCK_ENTITY_TYPES.register("voltage_source", () -> BlockEntityType.Builder.of(VoltageSourceBlockEntity::new, Blocks.VOLTAGE_SOURCE.get()).build(null));
	public static final RegistryObject<BlockEntityType<SimpleKineticBlockEntity>> SIMPLE_KINETIC = BLOCK_ENTITY_TYPES.register("simple_kinetic", () -> BlockEntityType.Builder.of(SimpleKineticBlockEntity::new, 
			Blocks.GEAR.get(),
			Blocks.LARGE_GEAR.get(),
			Blocks.SHAFT.get(),
			Blocks.SHORT_SHAFT_1.get(), 
			Blocks.SHORT_SHAFT_2.get(),
			Blocks.BELT_SHAFT.get()
		).build(null));
	public static final RegistryObject<BlockEntityType<MotorBlockEntity>> MOTOR = BLOCK_ENTITY_TYPES.register("motor", () -> BlockEntityType.Builder.of(MotorBlockEntity::new, Blocks.MOTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<CompoundBlockEntity>> COMPOUND_BLOCK = BLOCK_ENTITY_TYPES.register("compound_block", () -> BlockEntityType.Builder.of(CompoundBlockEntity::new, Blocks.COMPOUND_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<BeltBlockEntity>> BELT = BLOCK_ENTITY_TYPES.register("belt", () -> BlockEntityType.Builder.of(BeltBlockEntity::new, Blocks.BELT.get()).build(null));
	
}
