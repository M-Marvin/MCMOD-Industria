package de.industria.items;

import de.industria.Industria;
import de.industria.gui.ContainerNetworkConfigurator;
import de.industria.util.blockfeatures.INetworkDevice;
import de.industria.util.blockfeatures.INetworkDevice.NetworkDeviceIP;
import de.industria.util.blockfeatures.INetworkDevice.NetworkDeviceType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemNetworkConfigurator extends ItemBase implements INamedContainerProvider {
	
	public ItemNetworkConfigurator() {
		super("network_configurator", Industria.TOOLS, 1);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		
		World worldIn = context.getWorld();
		PlayerEntity playerIn = context.getPlayer();
		BlockPos pos = context.getPos();
		BlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		
		if (block instanceof INetworkDevice) {
			
			NetworkDeviceIP ip = ((INetworkDevice) state.getBlock()).getIP(pos, state, worldIn);
			
			if (!worldIn.isRemote() && ((INetworkDevice) block).getNetworkType() != NetworkDeviceType.WIRING) NetworkHooks.openGui((ServerPlayerEntity) playerIn, this, (buffer) -> {
				buffer.writeBlockPos(pos);
				buffer.writeString(ip.getString());
			});
			return ActionResultType.SUCCESS;
			
		}
		
		return super.onItemUse(context);
		
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		RayTraceResult result = rayTrace(player.world, player, FluidMode.NONE);
		BlockPos pos = result.getType() != Type.MISS ? new BlockPos(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z) : BlockPos.ZERO;
		BlockState state = player.world.getBlockState(pos);
		NetworkDeviceIP ip = state.getBlock() instanceof INetworkDevice ? ((INetworkDevice) state.getBlock()).getIP(pos, state, player.world) : NetworkDeviceIP.DEFAULT;
		return new ContainerNetworkConfigurator(id, playerInv, pos, ip);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("item.industria.network_configurator");
	}
	
}
