package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blockentities.ConduitCoilBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.ElectroMagneticCoilBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.FloodlightBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.IonicThrusterBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.PortableCoalGeneratorBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.PortableFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.blockentities.redstone.TransistorBlockEntity;
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
	
	public static final RegistryObject<BlockEntityType<FloodlightBlockEntity>> 				FLOODLIGHT 					= BLOCK_ENTITY_TYPES.register("floodlight", () -> BlockEntityType.Builder.of(FloodlightBlockEntity::new, ModBlocks.BRASS_FLOODLIGHT.get(), ModBlocks.STEEL_FLOOFLIGHT.get()).build(null));
	public static final RegistryObject<BlockEntityType<PortableFuelGeneratorBlockEntity>> 	PORTABLE_FUEL_GENERATOR 	= BLOCK_ENTITY_TYPES.register("portable_fuel_generator", () -> BlockEntityType.Builder.of(PortableFuelGeneratorBlockEntity::new, ModBlocks.PORTABLE_FUEL_GENERATOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<PortableCoalGeneratorBlockEntity>> 	PORTABLE_COAL_GENERATOR		= BLOCK_ENTITY_TYPES.register("portable_coal_generator", () -> BlockEntityType.Builder.of(PortableCoalGeneratorBlockEntity::new, ModBlocks.PORTABLE_COAL_GENERATOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<ConduitCoilBlockEntity>>				WIRE_COIL					= BLOCK_ENTITY_TYPES.register("wire_coil", () -> BlockEntityType.Builder.of(ConduitCoilBlockEntity::new).build(null));
	public static final RegistryObject<BlockEntityType<IonicThrusterBlockEntity>>			IONIC_THRUSTER				= BLOCK_ENTITY_TYPES.register("ionic_thruster", () -> BlockEntityType.Builder.of(IonicThrusterBlockEntity::new, ModBlocks.IONIC_THRUSTER.get()).build(null));
	public static final RegistryObject<BlockEntityType<TransistorBlockEntity>> 				TRANSISTOR					= BLOCK_ENTITY_TYPES.register("transistor", () -> BlockEntityType.Builder.of(TransistorBlockEntity::new, ModBlocks.REDSTONE_TRANSISTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<ElectroMagneticCoilBlockEntity>> 	ELECTRO_MAGNETIC_COIL		= BLOCK_ENTITY_TYPES.register("electro_magnetic_coil", () -> BlockEntityType.Builder.of(ElectroMagneticCoilBlockEntity::new, ModBlocks.ELECTRO_MAGNETIC_COIL.get()).build(null));
	
}
