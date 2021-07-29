package de.industria.items;

import de.industria.Industria;
import de.industria.gui.ContainerNetworkConfigurator;
import de.industria.util.blockfeatures.ITENetworkDevice;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkDeviceIP;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkDeviceType;
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
	public ActionResultType useOn(ItemUseContext context) {
		
		World worldIn = context.getLevel();
		PlayerEntity playerIn = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		BlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		
		if (block instanceof ITENetworkDevice) {
			
			NetworkDeviceIP ip = ((ITENetworkDevice) state.getBlock()).getIP(pos, state, worldIn);
			
			if (!worldIn.isClientSide() && ((ITENetworkDevice) block).getNetworkType() != NetworkDeviceType.WIRING) NetworkHooks.openGui((ServerPlayerEntity) playerIn, this, (buffer) -> {
				buffer.writeBlockPos(pos);
				buffer.writeUtf(ip.getString());
			});
			return ActionResultType.SUCCESS;
			
		}
		
		return super.useOn(context);
		
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		RayTraceResult result = getPlayerPOVHitResult(player.level, player, FluidMode.NONE);
		BlockPos pos = result.getType() != Type.MISS ? new BlockPos(result.getLocation().x, result.getLocation().y, result.getLocation().z) : BlockPos.ZERO;
		BlockState state = player.level.getBlockState(pos);
		NetworkDeviceIP ip = state.getBlock() instanceof ITENetworkDevice ? ((ITENetworkDevice) state.getBlock()).getIP(pos, state, player.level) : NetworkDeviceIP.DEFAULT;
		return new ContainerNetworkConfigurator(id, playerInv, pos, ip);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("item.industria.network_configurator");
	}
	
}
