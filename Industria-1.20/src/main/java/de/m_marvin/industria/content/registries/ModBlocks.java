package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blocks.machines.FloodlightBlock;
import de.m_marvin.industria.content.blocks.machines.PortableFuelGeneratorBlock;
import de.m_marvin.industria.core.electrics.types.blocks.JunctionBoxBlock;
import de.m_marvin.industria.core.electrics.types.blocks.WireHolderBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Industria.MODID);
	public static void register() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Block> LIGHT_AIR 				= BLOCKS.register("light_air_block", () -> new LightBlock(Properties.of().replaceable().noCollission().air().strength(-1.0F, 3600000.8F).noLootTable().noOcclusion().lightLevel(LightBlock.LIGHT_EMISSION))); // TODO Fluids do not replace
	public static final RegistryObject<Block> COPPER_WIRE_HOLDER 		= BLOCKS.register("copper_wire_holder", () -> new WireHolderBlock(Properties.of()));
	public static final RegistryObject<Block> GOLD_WIRE_HOLDER 			= BLOCKS.register("gold_wire_holder", () -> new WireHolderBlock(Properties.of()));
	public static final RegistryObject<Block> TIN_WIRE_HOLDER 			= BLOCKS.register("tin_wire_holder", () -> new WireHolderBlock(Properties.of()));
	public static final RegistryObject<Block> ALUMINUM_WIRE_HOLDER 		= BLOCKS.register("aluminum_wire_holder", () -> new WireHolderBlock(Properties.of()));
	public static final RegistryObject<Block> JUNCTION_BOX 				= BLOCKS.register("junction_box", () -> new JunctionBoxBlock(Properties.of()));
	public static final RegistryObject<Block> BRASS_FLOODLIGHT 			= BLOCKS.register("brass_floodlight", () -> new FloodlightBlock(Properties.of()));
	public static final RegistryObject<Block> STEEL_FLOOFLIGHT 			= BLOCKS.register("steel_floodlight", () -> new FloodlightBlock(Properties.of()));
	public static final RegistryObject<Block> PORTABLE_FUEL_GENERATOR 	= BLOCKS.register("portable_fuel_generator", () -> new PortableFuelGeneratorBlock(Properties.of()));
	
}
