package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blocks.ConduitCoilBlock;
import de.m_marvin.industria.content.blocks.WeatheringBrassFullBlock;
import de.m_marvin.industria.content.blocks.WeatheringCopperPlatesFullBlock;
import de.m_marvin.industria.content.blocks.WeatheringIronFullBlock;
import de.m_marvin.industria.content.blocks.WeatheringSteelFullBlock;
import de.m_marvin.industria.content.blocks.WeatheringTinFullBlock;
import de.m_marvin.industria.content.blocks.machines.FloodlightBlock;
import de.m_marvin.industria.content.blocks.machines.IonicThrusterBlock;
import de.m_marvin.industria.content.blocks.machines.PortableCoalGeneratorBlock;
import de.m_marvin.industria.content.blocks.machines.PortableFuelGeneratorBlock;
import de.m_marvin.industria.content.blocks.machines.TransformerCoilBlock;
import de.m_marvin.industria.content.blocks.redstone.TransistorBlock;
import de.m_marvin.industria.core.electrics.types.blocks.JunctionBoxBlock;
import de.m_marvin.industria.core.electrics.types.blocks.WireHolderBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
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
	
	public static final RegistryObject<Block> EMPTY_WIRE_COIL					= BLOCKS.register("empty_wire_coil", () -> new ConduitCoilBlock(Properties.of().noCollission().strength(3, 3), false));
	public static final RegistryObject<Block> INSULATED_COPPER_WIRE_COIL		= BLOCKS.register("insulated_copper_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> INSULATED_TIN_WIRE_COIL			= BLOCKS.register("insulated_tin_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> INSULATED_GOLD_WIRE_COIL			= BLOCKS.register("insulated_gold_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> INSULATED_ALUMINUM_WIRE_COIL		= BLOCKS.register("insulated_aluminum_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> COPPER_WIRE_COIL					= BLOCKS.register("copper_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> TIN_WIRE_COIL						= BLOCKS.register("tin_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> GOLD_WIRE_COIL					= BLOCKS.register("gold_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	public static final RegistryObject<Block> ALUMINUM_WIRE_COIL				= BLOCKS.register("aluminum_wire_coil", () -> new ConduitCoilBlock(Properties.of().strength(4, 4), true));
	
	public static final RegistryObject<Block> LIGHT_AIR 						= BLOCKS.register("light_air_block", () -> new LightBlock(Properties.of().replaceable().noCollission().air().strength(-1.0F, 3600000.8F).noLootTable().noOcclusion().lightLevel(LightBlock.LIGHT_EMISSION))); // TODO Fluids do not replace
	public static final RegistryObject<Block> COPPER_WIRE_HOLDER 				= BLOCKS.register("copper_wire_holder", () -> new WireHolderBlock(Properties.of().strength(3, 3)));
	public static final RegistryObject<Block> GOLD_WIRE_HOLDER 					= BLOCKS.register("gold_wire_holder", () -> new WireHolderBlock(Properties.of().strength(3, 3)));
	public static final RegistryObject<Block> TIN_WIRE_HOLDER 					= BLOCKS.register("tin_wire_holder", () -> new WireHolderBlock(Properties.of().strength(3, 3)));
	public static final RegistryObject<Block> ALUMINUM_WIRE_HOLDER 				= BLOCKS.register("aluminum_wire_holder", () -> new WireHolderBlock(Properties.of().strength(3, 3)));
	public static final RegistryObject<Block> IRON_JUNCTION_BOX 				= BLOCKS.register("iron_junction_box", () -> new JunctionBoxBlock(Properties.of().strength(3F, 3F)));
	public static final RegistryObject<Block> ZINC_JUNCTION_BOX 				= BLOCKS.register("zinc_junction_box", () -> new JunctionBoxBlock(Properties.of().strength(3F, 3F)));
	public static final RegistryObject<Block> BRASS_JUNCTION_BOX 				= BLOCKS.register("brass_junction_box", () -> new JunctionBoxBlock(Properties.of().strength(3F, 3F)));
	public static final RegistryObject<Block> STEEL_JUNCTION_BOX 				= BLOCKS.register("steel_junction_box", () -> new JunctionBoxBlock(Properties.of().strength(3F, 3F)));
	public static final RegistryObject<Block> TRANSFORMER_COIL					= BLOCKS.register("transformer_coil", () -> new TransformerCoilBlock(Properties.of().strength(3, 2)));
	
	public static final RegistryObject<Block> BRASS_FLOODLIGHT 					= BLOCKS.register("brass_floodlight", () -> new FloodlightBlock(Properties.of().strength(2, 1.5F)));
	public static final RegistryObject<Block> STEEL_FLOOFLIGHT 					= BLOCKS.register("steel_floodlight", () -> new FloodlightBlock(Properties.of().strength(2, 1.5F)));
	public static final RegistryObject<Block> PORTABLE_FUEL_GENERATOR 			= BLOCKS.register("portable_fuel_generator", () -> new PortableFuelGeneratorBlock(Properties.of().strength(3.5F, 3.5F)));
	public static final RegistryObject<Block> PORTABLE_COAL_GENERATOR			= BLOCKS.register("portable_coal_generator", () -> new PortableCoalGeneratorBlock(Properties.of().strength(3.5F, 3.5F)));
	public static final RegistryObject<Block> IONIC_THRUSTER					= BLOCKS.register("ionic_thruster", () -> new IonicThrusterBlock(Properties.of().strength(2, 1.5F)));
	
	public static final RegistryObject<Block> REDSTONE_TRANSISTOR				= BLOCKS.register("redstone_transistor", () -> new TransistorBlock(Properties.of().strength(0, 12))); // TODO
	
	public static final RegistryObject<Block> RAW_ZINC_BLOCK					= BLOCKS.register("raw_zinc_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> RAW_LEAD_BLOCK					= BLOCKS.register("raw_lead_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> RAW_NICKEL_BLOCK					= BLOCKS.register("raw_nickel_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> RAW_PALLADIUM_BLOCK				= BLOCKS.register("raw_palladium_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> RAW_TIN_BLOCK						= BLOCKS.register("raw_tin_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> RAW_SILVER_BLOCK					= BLOCKS.register("raw_silver_block", () -> new Block(Properties.of().strength(5, 6)));
	
	public static final RegistryObject<Block> LEAD_BLOCK						= BLOCKS.register("lead_block", () -> new Block(Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> STEEL_BLOCK						= BLOCKS.register("steel_block", () -> new WeatheringSteelFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> WEATHERED_STEEL_BLOCK				= BLOCKS.register("weathered_steel_block", () -> new WeatheringSteelFullBlock(WeatherState.WEATHERED, Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> EXPOSED_STEEL_BLOCK				= BLOCKS.register("exposed_steel_block", () -> new WeatheringSteelFullBlock(WeatherState.EXPOSED, Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> OXIDIZED_STEEL_BLOCK				= BLOCKS.register("oxidized_steel_block", () -> new WeatheringSteelFullBlock(WeatherState.OXIDIZED, Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> WAXED_STEEL_BLOCK					= BLOCKS.register("waxed_steel_block", () -> new Block(Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> WAXED_WEATHERED_STEEL_BLOCK		= BLOCKS.register("waxed_weathered_steel_block", () -> new Block(Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> WAXED_EXPOSED_STEEL_BLOCK			= BLOCKS.register("waxed_exposed_steel_block", () -> new Block(Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_STEEL_BLOCK		= BLOCKS.register("waxed_oxidized_steel_block", () -> new Block(Properties.of().strength(6, 10)));
	public static final RegistryObject<Block> NICKEL_BLOCK						= BLOCKS.register("nickel_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> MONEL_BLOCK						= BLOCKS.register("monel_block", () -> new Block(Properties.of().strength(4, 6)));
	public static final RegistryObject<Block> PALLADIUM_BLOCK					= BLOCKS.register("palladium_block", () -> new Block(Properties.of().strength(8, 6)));
	public static final RegistryObject<Block> ALUMINUM_BLOCK					= BLOCKS.register("aluminum_block", () -> new Block(Properties.of().strength(2, 4)));
	public static final RegistryObject<Block> TUNGSTEN_BLOCK					= BLOCKS.register("tungsten_block", () -> new Block(Properties.of().strength(10, 6)));
	public static final RegistryObject<Block> TIN_BLOCK							= BLOCKS.register("tin_block", () -> new WeatheringTinFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> WEATHERED_TIN_BLOCK				= BLOCKS.register("weathered_tin_block", () -> new WeatheringTinFullBlock(WeatherState.WEATHERED, Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> EXPOSED_TIN_BLOCK					= BLOCKS.register("exposed_tin_block", () -> new WeatheringTinFullBlock(WeatherState.EXPOSED, Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> OXIDIZED_TIN_BLOCK				= BLOCKS.register("oxidized_tin_block", () -> new WeatheringTinFullBlock(WeatherState.OXIDIZED, Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> WAXED_TIN_BLOCK					= BLOCKS.register("waxed_tin_block", () -> new Block(Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> WAXED_WEATHERED_TIN_BLOCK			= BLOCKS.register("waxed_weathered_tin_block", () -> new Block(Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> WAXED_EXPOSED_TIN_BLOCK			= BLOCKS.register("waxed_exposed_tin_block", () -> new Block(Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_TIN_BLOCK			= BLOCKS.register("waxed_oxidized_tin_block", () -> new Block(Properties.of().strength(1, 6)));
	public static final RegistryObject<Block> WEATHERED_IRON_BLOCK				= BLOCKS.register("weathered_iron_block", () -> new WeatheringIronFullBlock(WeatherState.WEATHERED, Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> EXPOSED_IRON_BLOCK				= BLOCKS.register("exposed_iron_block", () -> new WeatheringIronFullBlock(WeatherState.EXPOSED, Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> OXIDIZED_IRON_BLOCK				= BLOCKS.register("oxidized_iron_block", () -> new WeatheringIronFullBlock(WeatherState.OXIDIZED, Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> WAXED_IRON_BLOCK					= BLOCKS.register("waxed_iron_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> WAXED_WEATHERED_IRON_BLOCK		= BLOCKS.register("waxed_weathered_iron_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> WAXED_EXPOSED_IRON_BLOCK			= BLOCKS.register("waxed_exposed_iron_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_IRON_BLOCK			= BLOCKS.register("waxed_oxidized_iron_block", () -> new Block(Properties.of().strength(5, 6)));
	public static final RegistryObject<Block> SILVER_BLOCK						= BLOCKS.register("silver_block", () -> new Block(Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> BRASS_BLOCK						= BLOCKS.register("brass_block", () -> new WeatheringBrassFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> WEATHERED_BRASS_BLOCK				= BLOCKS.register("weathered_brass_block", () -> new WeatheringBrassFullBlock(WeatherState.WEATHERED, Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> EXPOSED_BRASS_BLOCK				= BLOCKS.register("exposed_brass_block", () -> new WeatheringBrassFullBlock(WeatherState.EXPOSED, Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> OXIDIZED_BRASS_BLOCK				= BLOCKS.register("oxidized_brass_block", () -> new WeatheringBrassFullBlock(WeatherState.OXIDIZED, Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> WAXED_BRASS_BLOCK					= BLOCKS.register("waxed_brass_block", () -> new Block(Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> WAXED_WEATHERED_BRASS_BLOCK		= BLOCKS.register("waxed_weathered_brass_block", () -> new Block(Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> WAXED_EXPOSED_BRASS_BLOCK			= BLOCKS.register("waxed_exposed_brass_block", () -> new Block(Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_BRASS_BLOCK		= BLOCKS.register("waxed_oxidized_brass_block", () -> new Block(Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> REDSTONE_ALLOY_BLOCK				= BLOCKS.register("redstone_alloy_block", () -> new Block(Properties.of().strength(3, 6)));
	public static final RegistryObject<Block> ZINC_BLOCK						= BLOCKS.register("zinc_block", () -> new Block(Properties.of().strength(3, 6)));
	
	public static final RegistryObject<Block> ZINC_PLATES						= BLOCKS.register("zinc_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> LEAD_PLATES						= BLOCKS.register("lead_plates", () -> new Block(Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> GOLD_PLATES						= BLOCKS.register("gold_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> NETHERITE_PLATES					= BLOCKS.register("netherite_plates", () -> new Block(Properties.of().strength(38, 600)));
	public static final RegistryObject<Block> STEEL_PLATES						= BLOCKS.register("steel_plates", () -> new WeatheringSteelFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> WEATHERED_STEEL_PLATES			= BLOCKS.register("weathered_steel_plates", () -> new WeatheringSteelFullBlock(WeatherState.WEATHERED, Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> OXIDIZED_STEEL_PLATES				= BLOCKS.register("oxidized_steel_plates", () -> new WeatheringSteelFullBlock(WeatherState.OXIDIZED, Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> EXPOSED_STEEL_PLATES				= BLOCKS.register("exposed_steel_plates", () -> new WeatheringSteelFullBlock(WeatherState.EXPOSED, Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> WAXED_STEEL_PLATES				= BLOCKS.register("waxed_steel_plates", () -> new Block(Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> WAXED_WEATHERED_STEEL_PLATES		= BLOCKS.register("waxed_weathered_steel_plates", () -> new Block(Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_STEEL_PLATES		= BLOCKS.register("waxed_oxidized_steel_plates", () -> new Block(Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> WAXED_EXPOSED_STEEL_PLATES		= BLOCKS.register("waxed_exposed_steel_plates", () -> new Block(Properties.of().strength(5, 5)));
	public static final RegistryObject<Block> NICKEL_PLATES						= BLOCKS.register("nickel_plates", () -> new Block(Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> MONEL_PLATES						= BLOCKS.register("monel_plates", () -> new Block(Properties.of().strength(3, 3)));
	public static final RegistryObject<Block> PALLADIUM_PLATES					= BLOCKS.register("palladium_plates", () -> new Block(Properties.of().strength(6, 3)));
	public static final RegistryObject<Block> ALUMINUM_PLATES					= BLOCKS.register("aluminum_plates", () -> new Block(Properties.of().strength(1, 2)));
	public static final RegistryObject<Block> TUNGSTEN_PLATES					= BLOCKS.register("tungsten_plates", () -> new Block(Properties.of().strength(8, 3)));
	public static final RegistryObject<Block> TIN_PLATES						= BLOCKS.register("tin_plates", () -> new WeatheringTinFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> WEATHERED_TIN_PLATES				= BLOCKS.register("weathered_tin_plates", () -> new WeatheringTinFullBlock(WeatherState.WEATHERED, Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> EXPOSED_TIN_PLATES				= BLOCKS.register("exposed_tin_plates", () -> new WeatheringTinFullBlock(WeatherState.EXPOSED, Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> OXIDIZED_TIN_PLATES				= BLOCKS.register("oxidized_tin_plates", () -> new WeatheringTinFullBlock(WeatherState.OXIDIZED, Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> WAXED_TIN_PLATES					= BLOCKS.register("waxed_tin_plates", () -> new Block(Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> WAXED_WEATHERED_TIN_PLATES		= BLOCKS.register("waxed_weathered_tin_plates", () -> new Block(Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> WAXED_EXPOSED_TIN_PLATES			= BLOCKS.register("waxed_exposed_tin_plates", () -> new Block(Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_TIN_PLATES			= BLOCKS.register("waxed_oxidized_tin_plates", () -> new Block(Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> IRON_PLATES						= BLOCKS.register("iron_plates", () -> new WeatheringIronFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(1, 3)));
	public static final RegistryObject<Block> WEATHERED_IRON_PLATES				= BLOCKS.register("weathered_iron_plates", () -> new WeatheringIronFullBlock(WeatherState.WEATHERED, Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> EXPOSED_IRON_PLATES				= BLOCKS.register("exposed_iron_plates", () -> new WeatheringIronFullBlock(WeatherState.EXPOSED, Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> OXIDIZED_IRON_PLATES				= BLOCKS.register("oxidized_iron_plates", () -> new WeatheringIronFullBlock(WeatherState.OXIDIZED, Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> WAXED_IRON_PLATES					= BLOCKS.register("waxed_iron_plates", () -> new Block(Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> WAXED_WEATHERED_IRON_PLATES		= BLOCKS.register("waxed_weathered_iron_plates", () -> new Block(Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> WAXED_EXPOSED_IRON_PLATES			= BLOCKS.register("waxed_exposed_iron_plates", () -> new Block(Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_IRON_PLATES		= BLOCKS.register("waxed_oxidized_iron_plates", () -> new Block(Properties.of().strength(4, 3)));
	public static final RegistryObject<Block> COPPER_PLATES						= BLOCKS.register("copper_plates", () -> new WeatheringCopperPlatesFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WEATHERED_COPPER_PLATES			= BLOCKS.register("weathered_copper_plates", () -> new WeatheringCopperPlatesFullBlock(WeatherState.WEATHERED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> EXPOSED_COPPER_PLATES				= BLOCKS.register("exposed_copper_plates", () -> new WeatheringCopperPlatesFullBlock(WeatherState.EXPOSED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> OXIDIZED_COPPER_PLATES			= BLOCKS.register("oxidized_copper_plates", () -> new WeatheringCopperPlatesFullBlock(WeatherState.OXIDIZED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_COPPER_PLATES				= BLOCKS.register("waxed_copper_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_PLATES		= BLOCKS.register("waxed_weathered_copper_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_PLATES		= BLOCKS.register("waxed_exposed_copper_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_PLATES		= BLOCKS.register("waxed_oxidized_copper_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> SILVER_PLATES						= BLOCKS.register("silver_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> BRASS_PLATES						= BLOCKS.register("brass_plates", () -> new WeatheringBrassFullBlock(WeatherState.UNAFFECTED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WEATHERED_BRASS_PLATES			= BLOCKS.register("weathered_brass_plates", () -> new WeatheringBrassFullBlock(WeatherState.WEATHERED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> EXPOSED_BRASS_PLATES				= BLOCKS.register("exposed_brass_plates", () -> new WeatheringBrassFullBlock(WeatherState.EXPOSED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> OXIDIZED_BRASS_PLATES				= BLOCKS.register("oxidized_brass_plates", () -> new WeatheringBrassFullBlock(WeatherState.OXIDIZED, Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_BRASS_PLATES				= BLOCKS.register("waxed_brass_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_WEATHERED_BRASS_PLATES		= BLOCKS.register("waxed_weathered_brass_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_EXPOSED_BRASS_PLATES		= BLOCKS.register("waxed_exposed_brass_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> WAXED_OXIDIZED_BRASS_PLATES		= BLOCKS.register("waxed_oxidized_brass_plates", () -> new Block(Properties.of().strength(2, 3)));
	public static final RegistryObject<Block> REDSTONE_ALLOY_PLATES				= BLOCKS.register("redstone_alloy_plates", () -> new Block(Properties.of().strength(2, 3)));
	
}
