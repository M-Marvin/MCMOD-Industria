package de.industria;

import java.util.Optional;

import de.industria.packet.CConfigureNetworkDevice;
import de.industria.packet.CEditComputerCode;
import de.industria.packet.CEditJigsawTileEntityPacket;
import de.industria.packet.CEditProcessorCodePacket;
import de.industria.packet.CGenerateJigsaw;
import de.industria.packet.CUpdateChunkLoader;
import de.industria.packet.SSendENHandeler;
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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class ServerSetup {
	
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
		
	}
	
}
