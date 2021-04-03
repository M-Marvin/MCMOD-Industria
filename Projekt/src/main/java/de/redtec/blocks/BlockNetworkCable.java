package de.redtec.blocks;

import de.redtec.typeregistys.ModToolType;
import de.redtec.util.blockfeatures.INetworkDevice;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockNetworkCable extends BlockWiring implements INetworkDevice {
	
	public BlockNetworkCable() {
		super("network_cable", Material.WOOL, 0.2F, SoundType.CLOTH, 4);
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return ModToolType.CUTTER;
	}
	
	@Override
	public NetworkDeviceType getNetworkType() {
		return NetworkDeviceType.WIRING;
	}
	
	@Override
	public NetworkDeviceIP getIP(BlockPos pos, BlockState state, World world) {
		return NetworkDeviceIP.DEFAULT;
	}

	@Override
	public void setIP(NetworkDeviceIP ip, BlockPos pos, BlockState state, World world) {
		new IllegalAccessException("Cant set DeviceIP from an NetworkCable!").printStackTrace();
	}
	
	@Override
	public boolean canConnectTo(BlockState wireState, World worldIn, BlockPos wirePos, BlockPos connectPos, Direction direction) {
		BlockState state = worldIn.getBlockState(connectPos);
		if (state.getBlock() instanceof INetworkDevice) {
			return ((INetworkDevice) state.getBlock()).getNetworkType().canConnectWith(this.getNetworkType());
		}
		return false;
	}

	@Override
	public boolean canConectNetworkWire(IWorldReader world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public void onMessageRecived(NetworkMessage message, World world, BlockPos pos, BlockState state) {}
	
}
