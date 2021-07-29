package de.industria.util.blockfeatures;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public interface ITEPostMoveHandled {
	
	/**
	 * 
	 * @param pos New Position of the Block
	 * @param moveDirection Move Direction
	 * @param moveDistance Move Distance
	 * @param multipleCall
	 */
	public default void handlePostMove2(BlockPos pos, Direction moveDirection, int moveDistance, boolean multipleCall) {
		this.handlePostMove(pos.relative(moveDirection.getOpposite(), moveDistance), pos, multipleCall);
	}
	
	/**
	 * 
	 * @param pos Old Position of the Block
	 * @param moveDirection Move Direction
	 * @param moveDistance Move Distance
	 * @param multipleCall
	 */
	public default void handlePostMove(BlockPos pos, Direction moveDirection, int moveDistance, boolean multipleCall) {
		this.handlePostMove(pos, pos.relative(moveDirection, moveDistance), multipleCall);
	}
	
	public void handlePostMove(BlockPos pos, BlockPos newPos, boolean multipleCall);
	
}
