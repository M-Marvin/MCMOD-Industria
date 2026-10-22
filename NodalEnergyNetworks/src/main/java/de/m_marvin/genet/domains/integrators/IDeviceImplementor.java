package de.m_marvin.genet.domains.integrators;

import de.m_marvin.genet.domains.integrators.LevelBlockDomainIntegrator.BlockDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDeviceImplementor {
	
	public static enum FaceConnection {
		NONE,OFFERS,OPEN;
	}
	
	public FaceConnection hasDeviceConnection(Level level, BlockPos pos, BlockState state, Direction side);

	default public boolean doesDeviceKeepElements(Level level, BlockPos pos, BlockState state, BlockState stateNew) {
		
		// if the block type did not change, keep elements
		if (state.getBlock().equals(stateNew.getBlock()))
			return true;
		
		return false;
		
	}
	
	/**
	 * Invoked when all elements had to be removed and need to be re-installed into the network.
	 * This happens if the devices public nodes have changed and the grid had to be rebuild.
	 * @param device
	 */
	public void installDeviceElements(BlockDevice device);
	
	/**
	 * Invoked when the state of the device has changed, to allow the block to apply changes to thbe elements if it wants.
	 * @param device
	 */
	default public void updateDeviceElements(BlockDevice device) {}
	
}
