package de.m_marvin.industria.items;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IConduitConnector;
import de.m_marvin.industria.util.block.IConduitConnector.ConnectionPoint;
import de.m_marvin.industria.util.item.IScrollOverride;
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
	
	public boolean placeConduit(Level level, BlockPos pos1, BlockPos pos2, int nodesPerBlock) {
		
		BlockState stateA = level.getBlockState(pos1);
		BlockState stateB = level.getBlockState(pos2);
		
		if (!(stateA.getBlock() instanceof IConduitConnector)) {
			return false;
		}
		if (!(stateB.getBlock() instanceof IConduitConnector)) {
			return false;
		}
		
		ConnectionPoint nodeA = ((IConduitConnector) stateA.getBlock()).getConnectionPoints(level, pos1, stateA)[0];
		ConnectionPoint nodeB = ((IConduitConnector) stateB.getBlock()).getConnectionPoints(level, pos2, stateB)[0];
		UtilityHelper.setConduit(level, nodeA, nodeB, this.conduit, nodesPerBlock);
		
		return true;
		
	}

	@Override
	public boolean overridesScroll(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("FirstPos");
	}
	
	@Override
	public void onScroll(UseOnContext context, double delta) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		int nodesPerBlock = (int) UtilityHelper.clamp(itemTag.getInt("NodesPerBlock") + delta, 1, 6);
		itemTag.putInt("NodesPerBlock", nodesPerBlock);
		context.getItemInHand().setTag(itemTag);
		context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.changeNodes", nodesPerBlock), true);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		if (context.getPlayer().isShiftKeyDown()) {
			itemTag.remove("FirstPos");
			context.getItemInHand().setTag(itemTag);
			context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.abbort"), true);
			return InteractionResult.SUCCESS;
		} else {
			if (itemTag.contains("FirstPos")) {
				BlockPos firstPos = tryGetNodePos(context.getLevel(), context.getClickedPos(), context.getClickedFace());
				if (firstPos != null) {
					BlockPos secondPos = NbtUtils.readBlockPos(itemTag.getCompound("FirstPos"));
					itemTag.remove("FirstPos");
					int nodesPerBlock = itemTag.getInt("NodesPerBlock");
					context.getItemInHand().setTag(itemTag);
					if (placeConduit(context.getLevel(), secondPos, firstPos, nodesPerBlock)) {
						context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.placed"), true);
					} else {
						context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.failed"), true);
					}
					return InteractionResult.SUCCESS;
				} else {
					return InteractionResult.FAIL;
				}
			} else {
				BlockPos firstPos = tryGetNodePos(context.getLevel(), context.getClickedPos(), context.getClickedFace());
				if (firstPos != null) {
					itemTag.put("FirstPos", NbtUtils.writeBlockPos(firstPos));
					context.getItemInHand().setTag(itemTag);
					context.getPlayer().displayClientMessage(new TranslatableComponent("industria.item.info.conduit.nodeSelect"), true);
					return InteractionResult.SUCCESS;
				} else {
					return InteractionResult.FAIL;
				}
			}
		}
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
			if (clickedState.getMaterial().isReplaceable()) {
				return clicked.relative(face.getOpposite());
			} else if (clickedState.getBlock() instanceof IConduitConnector) {
				IConduitConnector node = (IConduitConnector) clickedState.getBlock();
				if (node.connectionAviable(level, clicked.relative(face.getOpposite()), clickedState)) {
					return clicked.relative(face.getOpposite());
				}
			}
		}
		return null;
	}
	
}
