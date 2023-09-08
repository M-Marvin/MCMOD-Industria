package de.m_marvin.industria.core.conduits.types.items;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IConduitItem {

	public Conduit getConduit();
	
	public int getMaxPlacingLength(ItemStack stack);
	public void onPlaced(UseOnContext context, int length);
	
	public default int tryGetNode(UseOnContext context, BlockPos pos) {
		Level level = context.getLevel();
		if (pos != null) {
			BlockState nodeState = level.getBlockState(pos);
			int nearestNode = -1;
			double distance = 2;
			ConduitNode[] nodes = ((IConduitConnector) nodeState.getBlock()).getConduitNodes(level, pos, nodeState);
			for (int nodeId = 0; nodeId < nodes.length; nodeId++) {
				double nodeDist = nodes[nodeId].getOffset().sub(Vec3d.fromVec(context.getClickLocation()).sub(Vec3d.fromVec(pos)).mul(16.0, 16.0, 16.0)).length() / 16D;
				if (nodeDist < distance) {
					if (ConduitUtility.getConduitsAtNode(level, pos, nodeId).size() < nodes[nodeId].getMaxConnections()) {
						nearestNode = nodeId;
						distance = nodeDist;
					}
				}
			}
			return nearestNode;
		}
		return -1;		
	}
	
	public default BlockPos tryGetNodePos(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos clicked = context.getClickedPos();
		Direction face = context.getClickedFace();
		BlockState clickedState = level.getBlockState(clicked);
		if (clickedState.getBlock() instanceof IConduitConnector) {
			IConduitConnector nodeBlock = (IConduitConnector) clickedState.getBlock();
			if (nodeBlock.hasFreeConduitNode(level, clicked, clickedState)) {
				return clicked;
			}
		} else {
			clickedState = level.getBlockState(clicked.relative(face.getOpposite()));
			if (clickedState.getBlock() instanceof IConduitConnector) {
				IConduitConnector nodeBlock = (IConduitConnector) clickedState.getBlock();
				if (nodeBlock.hasFreeConduitNode(level, clicked.relative(face.getOpposite()), clickedState)) {
					return clicked.relative(face.getOpposite());
				}
			}
		}
		return null;
	}
	
	public default InteractionResult onUsePlacement(UseOnContext context) {

		CompoundTag itemTag = context.getItemInHand().getTag();
		if (itemTag == null) itemTag = new CompoundTag();
		if (context.getPlayer().isShiftKeyDown() && (itemTag.contains("FirstNode") || itemTag.contains("Length"))) {
			itemTag.remove("FirstNode");
			itemTag.remove("Length");
			context.getItemInHand().setTag(itemTag.isEmpty() ? null : itemTag);
			return InteractionResult.SUCCESS;
		} else {
			
			if (itemTag.contains("FirstNode")) {
				
				int firstNodeId = itemTag.getCompound("FirstNode").getInt("Id");
				BlockPos firstNodePos = NbtUtils.readBlockPos(itemTag.getCompound("FirstNode").getCompound("Pos"));
				if (firstNodePos != null) {
					BlockPos secondNodePos = tryGetNodePos(context);
					int secondNodeId = tryGetNode(context, secondNodePos);
					if (secondNodeId >= 0) {
						float placementLengthModifier = Math.max(itemTag.getFloat("Length"), 1);

						ConduitPos conduitPos = new ConduitPos(firstNodePos, secondNodePos, firstNodeId, secondNodeId);
						
						int conduitLengthBlocks = (int) Math.ceil(conduitPos.calculateMinConduitLength(context.getLevel()) * placementLengthModifier);
						int maxLength = Math.min(this.getConduit().getConduitType().getClampingLength(), getMaxPlacingLength(context.getItemInHand()));
						
						if (conduitLengthBlocks <= maxLength) {
							
							itemTag.remove("FirstNode");
							context.getItemInHand().setTag(itemTag);
							if (ConduitUtility.setConduit(context.getLevel(), conduitPos, this.getConduit(), conduitLengthBlocks)) {
								context.getPlayer().displayClientMessage(Component.translatable("industriacore.item.info.conduit.placed"), true);
								onPlaced(context, conduitLengthBlocks);
							} else {
								context.getPlayer().displayClientMessage(Component.translatable("industriacore.item.info.conduit.failed"), true);
							}
							return InteractionResult.SUCCESS;
							
						} else {
							context.getPlayer().displayClientMessage(Component.translatable("industriacore.item.info.conduit.toFarNodes", conduitLengthBlocks, maxLength), true);
							return InteractionResult.FAIL;
						}
					}
				}
				return InteractionResult.FAIL;
				
			} else {
				
				BlockPos nodePos = tryGetNodePos(context);
				int firstNode = tryGetNode(context, nodePos);
				if (firstNode >= 0) {
					CompoundTag nodeTag = new CompoundTag();
					nodeTag.put("Pos", NbtUtils.writeBlockPos(nodePos));
					nodeTag.putInt("Id", firstNode);
					itemTag.put("FirstNode", nodeTag);
					context.getItemInHand().setTag(itemTag);
					context.getPlayer().displayClientMessage(Component.translatable("industriacore.item.info.conduit.nodeSelect"), true);
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.FAIL;
				
			}
			
		}
		
	}
	
}
