package de.m_marvin.industria.items;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.IWireConnector;
import de.m_marvin.industria.util.conduit.IWireConnector.ConnectionPoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class FlexibleConduitItem extends Item {
	
	private Conduit conduit;
	
	public FlexibleConduitItem(Properties properties, Conduit conduit) {
		super(properties);
		this.conduit = conduit;
	}
	
	public Conduit getConduit() {
		return conduit;
	}
	
	public void placeConduit(Level level, BlockPos pos1, BlockPos pos2) {
		BlockState stateA = level.getBlockState(pos1);
		BlockState stateB = level.getBlockState(pos2);
		if (!(stateA.getBlock() instanceof IWireConnector)) {
			return;
		}
		if (!(stateB.getBlock() instanceof IWireConnector)) {
			return;
		}
		ConnectionPoint nodeA = ((IWireConnector) stateA.getBlock()).getConnectionPoints(level, pos1, stateA)[0];
		ConnectionPoint nodeB = ((IWireConnector) stateB.getBlock()).getConnectionPoints(level, pos2, stateB)[0];
		UtilityHelper.setConduit(level, nodeA, nodeB, this.conduit);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		if (itemTag.contains("FirstPos")) {
			BlockPos firstPos = tryGetNodePos(context.getLevel(), context.getClickedPos(), context.getClickedFace());
			if (firstPos != null) {
				BlockPos secondPos = NbtUtils.readBlockPos(itemTag.getCompound("FirstPos"));
				itemTag.remove("FirstPos");
				context.getItemInHand().setTag(itemTag);
				placeConduit(context.getLevel(), secondPos, firstPos);
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.FAIL;
			}
		} else {
			BlockPos firstPos = tryGetNodePos(context.getLevel(), context.getClickedPos(), context.getClickedFace());
			if (firstPos != null) {
				itemTag.put("FirstPos", NbtUtils.writeBlockPos(firstPos));
				context.getItemInHand().setTag(itemTag);
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.FAIL;
			}
		}
	}
	
	protected BlockPos tryGetNodePos(Level level, BlockPos clicked, Direction face) {
		BlockState clickedState = level.getBlockState(clicked);
		if (clickedState.getBlock() instanceof IWireConnector) {
			IWireConnector node = (IWireConnector) clickedState.getBlock();
			if (node.connectionAviable(level, clicked, clickedState)) {
				return clicked;
			}
		} else {
			clickedState = level.getBlockState(clicked.relative(face.getOpposite()));
			if (clickedState.getMaterial().isReplaceable()) {
				return clicked.relative(face.getOpposite());
			} else if (clickedState.getBlock() instanceof IWireConnector) {
				IWireConnector node = (IWireConnector) clickedState.getBlock();
				if (node.connectionAviable(level, clicked.relative(face.getOpposite()), clickedState)) {
					return clicked.relative(face.getOpposite());
				}
			}
		}
		return null;
	}
	
}
