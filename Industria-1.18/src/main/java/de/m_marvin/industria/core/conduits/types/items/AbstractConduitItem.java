package de.m_marvin.industria.core.conduits.types.items;

import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.items.IScrollOverride;
import de.m_marvin.industria.core.MathUtility;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.ConduitPos;
import de.m_marvin.industria.core.conduits.engine.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.core.conduits.engine.network.CChangeNodesPerBlockPackage;
import de.m_marvin.industria.core.conduits.types.Conduit;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
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

public abstract class AbstractConduitItem extends Item implements IScrollOverride {
	
	private Supplier<Conduit> conduit;
	
	public AbstractConduitItem(Properties properties, Supplier<Conduit> conduit) {
		super(properties);
		this.conduit = conduit;
	}
	
	public Conduit getConduit() {
		return conduit.get();
	}
	
	public abstract int getMaxPlacingLength(ItemStack stack);
	public abstract void onPlaced(ItemStack stack, int length);
	
	@Override
	public boolean overridesScroll(UseOnContext context, ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("FirstNode");
	}
	
	@Override
	public void onScroll(UseOnContext context, double delta) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		int nodesPerBlock = (int) MathUtility.clamp(itemTag.getInt("NodesPerBlock") + delta, 1, 6);
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
		if (context.getPlayer().isShiftKeyDown() && (itemTag.contains("FirstNode") || itemTag.contains("NodesPerBlock"))) {
			itemTag.remove("FirstNode");
			itemTag.remove("NodesPerBlock");
			context.getItemInHand().setTag(itemTag.isEmpty() ? null : itemTag);
			return InteractionResult.SUCCESS;
		} else {
			
			if (itemTag.contains("FirstNode")) {
				
				int firstNodeId = itemTag.getCompound("FirstNode").getInt("Id");
				BlockPos firstNodePos = NbtUtils.readBlockPos(itemTag.getCompound("FirstNode").getCompound("Pos"));
				if (firstNodePos != null) {
					ConnectionPoint secondNode = tryGetNode(context);
					if (secondNode != null) {
						int nodesPerBlock = Math.max(itemTag.getInt("NodesPerBlock"), 1);
						ConduitPos conduitPos = new ConduitPos(firstNodePos, secondNode.position, firstNodeId, secondNode.connectionId);
						
						int nodeDist = (int) Math.round(Math.sqrt(firstNodePos.distSqr(secondNode.position)));
						int maxLength = Math.min(this.getConduit().getConduitType().getClampingLength(), getMaxPlacingLength(context.getItemInHand()));
						if (nodeDist <= maxLength) {
							
							itemTag.remove("FirstNode");
							context.getItemInHand().setTag(itemTag);
							if (ConduitUtility.setConduit(context.getLevel(), conduitPos, this.getConduit(), nodesPerBlock)) {
								context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.placed"), true);
								onPlaced(context.getItemInHand(), nodeDist);
							} else {
								context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.failed"), true);
							}
							return InteractionResult.SUCCESS;
							
						} else {
							context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.toFarNodes", nodeDist, maxLength), true);
							return InteractionResult.FAIL;
						}
					}
				}
				return InteractionResult.FAIL;
				
			} else {
				
				ConnectionPoint firstNode = tryGetNode(context);
				if (firstNode != null) {
					CompoundTag nodeTag = new CompoundTag();
					nodeTag.put("Pos", NbtUtils.writeBlockPos(firstNode.position));
					nodeTag.putInt("Id", firstNode.connectionId);
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
			for (ConnectionPoint node : ((IConduitConnector) nodeState.getBlock()).getConnectionPoints(nodePos, nodeState)) {
				float nodeDist = (float) node.offset.copy().sub(new Vec3i(Vec3f.fromVec(context.getClickLocation()).sub(Vec3f.fromVec(nodePos)).mul(16F, 16F, 16F))).length() / 16F;
				if (nodeDist < distance) {
					if (((IConduitConnector) nodeState.getBlock()).connectionAviable(context.getLevel(), nodePos, nodeState, node.connectionId)) {
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
