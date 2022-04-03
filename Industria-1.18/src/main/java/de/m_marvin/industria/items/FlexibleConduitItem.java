package de.m_marvin.industria.items;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModBlocks;
import de.m_marvin.industria.registries.ModRegistries;
import de.m_marvin.industria.util.IFlexibleConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
		BlockEntity nodeBE1 = level.getBlockEntity(pos1);
		BlockEntity nodeBE2 = level.getBlockEntity(pos2);
		IFlexibleConnection node1;
		IFlexibleConnection node2;
		if (nodeBE1 instanceof IFlexibleConnection) {
			node1 = (IFlexibleConnection) nodeBE1;
		} else {
			level.setBlockAndUpdate(pos1, ModBlocks.CONDUIT_NODE.defaultBlockState());
			node1 = (IFlexibleConnection) level.getBlockEntity(pos1);
		}
		if (nodeBE2 instanceof IFlexibleConnection) {
			node2 = (IFlexibleConnection) nodeBE2;
		} else {
			level.setBlockAndUpdate(pos2, ModBlocks.CONDUIT_NODE.defaultBlockState());
			node2 = (IFlexibleConnection) level.getBlockEntity(pos2);
		}
		node1.connectWith(node2, this.conduit);
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
		BlockEntity clickedBE = level.getBlockEntity(clicked);
		if (clickedBE instanceof IFlexibleConnection) {
			IFlexibleConnection node = (IFlexibleConnection) clickedBE;
			if (node.angleAviable()) {
				return clicked;
			}
		} else {
			BlockState clickedState = level.getBlockState(clicked);
			if (clickedState.getMaterial().isReplaceable()) {
				return clicked;
			} else {
				clickedBE = level.getBlockEntity(clicked.relative(face));
				if (clickedBE instanceof IFlexibleConnection) {
					IFlexibleConnection node = (IFlexibleConnection) clickedBE;
					if (node.angleAviable()) {
						return clicked.relative(face);
					}
				} else {
					clickedState = level.getBlockState(clicked.relative(face));
					if (clickedState.getMaterial().isReplaceable()) {
						return clicked.relative(face);
					}
				}
			}
		}
		return null;
	}
	
}
