package de.m_marvin.industria.items;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.network.CChangeNodesPerBlockPackage;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IConduitConnector;
import de.m_marvin.industria.util.block.IConduitConnector.ConnectionPoint;
import de.m_marvin.industria.util.conduit.ConduitPos;
import de.m_marvin.industria.util.item.IScrollOverride;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FlexibleConduitItem extends Item implements IScrollOverride {
	
	private Conduit conduit;
	
	public FlexibleConduitItem(Properties properties, Conduit conduit) {
		super(properties);
		this.conduit = conduit;
	}
	
	public Conduit getConduit() {
		return conduit;
	}
	
	@Override
	public boolean overridesScroll(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("FirstNode");
	}
	
	@Override
	public void onScroll(UseOnContext context, double delta) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		int nodesPerBlock = (int) UtilityHelper.clamp(itemTag.getInt("NodesPerBlock") + delta, 1, 6);
		Industria.NETWORK.sendToServer(new CChangeNodesPerBlockPackage(nodesPerBlock));
		context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.changeNodes", nodesPerBlock), true);
	}
	
	public void onChangeNodesPerBlock(ItemStack stack, int nodesPerBlock) {
		CompoundTag itemTag = stack.getOrCreateTag();
		itemTag.putInt("NodesPerBlock", nodesPerBlock);
		stack.setTag(itemTag);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		if (context.getPlayer().isShiftKeyDown()) {
			
			itemTag.remove("FirstNode");
			context.getItemInHand().setTag(itemTag);
			context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.abbort"), true);
			return InteractionResult.SUCCESS;
			
		} else {
			
			if (itemTag.contains("FirstNode")) {
				
				int firstNodeId = itemTag.getCompound("FirstNode").getInt("Id");
				BlockPos firstNodePos = NbtUtils.readBlockPos(itemTag.getCompound("FirstNode").getCompound("Pos"));
				if (firstNodePos != null) {
					ConnectionPoint secondNode = tryGetNode(context);
					if (secondNode != null) {
						int nodesPerBlock = Math.max(itemTag.getInt("NodesPerBlock"), 1);
						itemTag.remove("FirstNode");
						context.getItemInHand().setTag(itemTag);
						ConduitPos conduitPos = new ConduitPos(firstNodePos, secondNode.position(), firstNodeId, secondNode.connectionId());
						
						if (UtilityHelper.setConduit(context.getLevel(), conduitPos, this.conduit, nodesPerBlock)) {
							context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.placed"), true);
						} else {
							context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.failed"), true);
						}
						return InteractionResult.SUCCESS;
					}
				}
				return InteractionResult.FAIL;
				
			} else {
				
				ConnectionPoint firstNode = tryGetNode(context);
				if (firstNode != null) {
					CompoundTag nodeTag = new CompoundTag();
					nodeTag.put("Pos", NbtUtils.writeBlockPos(firstNode.position()));
					nodeTag.putInt("Id", firstNode.connectionId());
					itemTag.put("FirstNode", nodeTag);
					context.getItemInHand().setTag(itemTag);
					context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.nodeSelect"), true);
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.FAIL;
				
			}
			
		}
		
	}
	
	protected ConnectionPoint tryGetNode(UseOnContext context) {
		BlockPos nodePos = tryGetNodePos(context.getLevel(), context.getClickedPos(), context.getClickedFace());
		if (nodePos != null) {
			BlockState nodeState = context.getLevel().getBlockState(nodePos);
			ConnectionPoint nearestNode = null;
			float distance = 2;
			for (ConnectionPoint node : ((IConduitConnector) nodeState.getBlock()).getConnectionPoints(context.getLevel(), nodePos, nodeState)) {
				float nodeDist = (float) node.offset().copy().sub(new Vec3i(context.getClickLocation().subtract(nodePos.getX(), nodePos.getY(), nodePos.getZ()).multiply(16, 16, 16))).length() / 16F;
				if (nodeDist < distance) {
					if (((IConduitConnector) nodeState.getBlock()).connectionAviable(context.getLevel(), nodePos, nodeState, node.connectionId())) {
						nearestNode = node;
						distance = nodeDist;
					}
				}
			}
			return nearestNode;
		}
		return null;		
	}
	
	protected BlockPos tryGetNodePos(Level level, BlockPos clicked, Direction face) {
		BlockState clickedState = level.getBlockState(clicked);
		if (clickedState.getBlock() instanceof IConduitConnector) {
			IConduitConnector node = (IConduitConnector) clickedState.getBlock();
			if (node.connectionAviable(level, clicked, clickedState)) {
				return clicked;
			}
		} else {
			clickedState = level.getBlockState(clicked.relative(face.getOpposite()));
			if (clickedState.getBlock() instanceof IConduitConnector) {
				IConduitConnector node = (IConduitConnector) clickedState.getBlock();
				if (node.connectionAviable(level, clicked.relative(face.getOpposite()), clickedState)) {
					return clicked.relative(face.getOpposite());
				}
			}
		}
		return null;
	}
	
}
