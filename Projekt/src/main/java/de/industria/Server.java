package de.industria;

import java.util.Optional;

import de.industria.packet.CConfigureNetworkDevice;
import de.industria.packet.CEditComputerCode;
import de.industria.packet.CEditJigsawTileEntityPacket;
import de.industria.packet.CEditProcessorCodePacket;
import de.industria.packet.CGenerateJigsaw;
import de.industria.packet.CUpdateChunkLoader;
import de.industria.packet.SSendENHandeler;
import de.industria.typeregistys.ModConfiguredFeatures;
import de.industria.util.handler.ModGameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class Server {
	
	@SuppressWarnings("deprecation")
	public static void setup(final FMLCommonSetupEvent event) {
		
		// Register Packets
		Industria.NETWORK.registerMessage(0, CEditProcessorCodePacket.class, CEditProcessorCodePacket::encode, CEditProcessorCodePacket::new, CEditProcessorCodePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		Industria.NETWORK.registerMessage(1, CEditJigsawTileEntityPacket.class, CEditJigsawTileEntityPacket::encode, CEditJigsawTileEntityPacket::new, CEditJigsawTileEntityPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		Industria.NETWORK.registerMessage(2, CGenerateJigsaw.class, CGenerateJigsaw::encode, CGenerateJigsaw::new, CGenerateJigsaw::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		Industria.NETWORK.registerMessage(3, SSendENHandeler.class, SSendENHandeler::encode, SSendENHandeler::new, SSendENHandeler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));		
		Industria.NETWORK.registerMessage(4, CEditComputerCode.class, CEditComputerCode::encode, CEditComputerCode::new, CEditComputerCode::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		Industria.NETWORK.registerMessage(5, CConfigureNetworkDevice.class, CConfigureNetworkDevice::encode, CConfigureNetworkDevice::new, CConfigureNetworkDevice::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		Industria.NETWORK.registerMessage(6, CUpdateChunkLoader.class, CUpdateChunkLoader::encode, CUpdateChunkLoader::new, CUpdateChunkLoader::handle);
		
		// Dispenser Behaviors
		IDispenseItemBehavior placeBlockBehavior = new IDispenseItemBehavior() {
			@Override
			public ItemStack dispense(IBlockSource source, ItemStack stack) {
				BlockPos pos = source.getBlockPos().offset(source.getBlockState().get(BlockStateProperties.FACING));
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != null && (source.getWorld().getBlockState(pos).isAir() || source.getWorld().getBlockState(pos).getBlock() instanceof FlowingFluidBlock) && block.isValidPosition(block.getDefaultState(), source.getWorld(), pos)) {
					source.getWorld().setBlockState(pos, block.getDefaultState());
					stack.shrink(1);
					return stack;
				}
				return stack;
			}
		};
		Registry.BLOCK.stream().forEach((block) -> {
			Item item = Item.getItemFromBlock(block);
			if (item != null) {
				if (item != Items.TNT) DispenserBlock.registerDispenseBehavior(item, placeBlockBehavior);
			}
		});
		
		// World Generation Settings (Adding Features to the specific Biomes)
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.COPPER_ORE);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.TIN_ORE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.TIN_ORE_EXTRA, Category.JUNGLE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.TIN_ORE_EXTRA, Category.SAVANNA);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.NICKEL_ORE);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SILVER_ORE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SILVER_ORE_EXTRA, Category.JUNGLE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SILVER_ORE_EXTRA, Category.EXTREME_HILLS);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.PALLADIUM_ORE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.PALLADIUM_ORE_EXTRA, Category.SWAMP);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.PALLADIUM_ORE_EXTRA, Category.TAIGA);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SULFUR_ORE, Category.NETHER);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.BAUXIT_STONE_ORE, Category.JUNGLE);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.WOLFRAM_STONE_ORE);
		
		// TODO
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.OIL_DEPOT, Category.DESERT);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.OIL_DEPOT, Category.OCEAN);
		
		ModGameRegistry.addFeatureToBiomes(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.RUBBER_TREE, Biomes.SWAMP_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE);
		
	}
	
}
