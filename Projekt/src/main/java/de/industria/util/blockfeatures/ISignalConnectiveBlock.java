package de.industria.util.blockfeatures;

import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public interface ISignalConnectiveBlock {
	
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side);
	
	public default void sendSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal) {
		
		BlockState state = worldIn.getBlockState(pos);
		
		for (Direction direction : Direction.values()) {
			
			BlockState state2 = worldIn.getBlockState(pos.offset(direction));
			
			if (state2.getBlock() instanceof ISignalConnectiveBlock && state.getBlock() instanceof ISignalConnectiveBlock) {
				
				boolean flag1 = ((ISignalConnectiveBlock) state.getBlock()).canConectSignalWire(worldIn, pos, direction);
				boolean flag2 = ((ISignalConnectiveBlock) state2.getBlock()).canConectSignalWire(worldIn, pos.offset(direction), direction.getOpposite());
				
				if (flag1 && flag2) {
					
					((ISignalConnectiveBlock) state2.getBlock()).onReciveSignal(worldIn, pos.offset(direction), signal, direction.getOpposite());
					
				}
				
			}
			
		}
		
	}
	
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side);
	
}
