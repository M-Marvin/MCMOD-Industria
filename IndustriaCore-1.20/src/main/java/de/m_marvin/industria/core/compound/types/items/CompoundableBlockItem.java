package de.m_marvin.industria.core.compound.types.items;

import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.Tags;
import de.m_marvin.industria.core.util.virtualblock.VirtualBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CompoundableBlockItem extends BlockItem {

	public CompoundableBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext pContext) {
		BlockPlaceContext context1 = new BlockPlaceContext(pContext.getLevel(), pContext.getPlayer(), pContext.getHand(), pContext.getItemInHand(), pContext.getHitResult());
		context1.replaceClicked = true;
		InteractionResult result = tryPlaceCompound(context1);
		if (result == InteractionResult.FAIL)
			result = tryPlaceCompound(pContext);
		return result;
	}
	
	public InteractionResult tryPlaceCompound(BlockPlaceContext pContext) {
		Level level = pContext.getLevel();
		BlockPos pos = pContext.getClickedPos();
		BlockState replaceState = level.getBlockState(pos);
		
		BlockState placeState = getPlacementState(pContext);
		if (placeState == null)
			return InteractionResult.FAIL;
		
		if (replaceState.is(Tags.Blocks.COMPOUNDABLE)) {
			
			if (canCompound(level, pos, replaceState, null, null, placeState)) {
				BlockEntity replaceEntity = level.getBlockEntity(pos);
				level.removeBlockEntity(pos); // Prevent the replaced block droping inventory items or interacting otherwise with the BE when removing
				level.setBlockAndUpdate(pos, Blocks.COMPOUND_BLOCK.get().defaultBlockState());
				if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
					compound.addPart(replaceState, replaceEntity);
					replaceState = level.getBlockState(pos);
				}
			}
			
		}
		
		if (replaceState.is(Blocks.COMPOUND_BLOCK.get())) {
			
			if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
				
				for (var part : compound.getParts().values()) {
					if (!canCompound(part.getLevel(), part.getPos(), part.getState(), null, null, placeState))
						return InteractionResult.FAIL;
				}
				
				int partId = compound.addPart(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
				BlockPlaceContext context = new BlockPlaceContext(compound.getParts().get(partId).getLevel(), pContext.getPlayer(), pContext.getHand(), pContext.getItemInHand(), pContext.getHitResult());
				InteractionResult result = super.place(context);
				if (result == InteractionResult.FAIL)
					compound.getParts().get(partId).setBlock(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
				return result;
			}
			
		}
		
		if (!replaceState.canBeReplaced(pContext)) return InteractionResult.FAIL;
		return super.place(pContext);
	}
	
	public boolean tryPlaceCompound(Level level, BlockPos pos, BlockState state, boolean simulate) {
		BlockState replaceState = level.getBlockState(pos);
		
		if (replaceState.is(Tags.Blocks.COMPOUNDABLE)) {
			
			if (canCompound(level, pos, replaceState, null, null, state)) {
				if (simulate) return true;
				
				BlockEntity replaceEntity = level.getBlockEntity(pos);
				level.setBlockAndUpdate(pos, Blocks.COMPOUND_BLOCK.get().defaultBlockState());
				if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
					level.setBlockAndUpdate(pos, Blocks.COMPOUND_BLOCK.get().defaultBlockState());
					compound.addPart(replaceState, replaceEntity);
					replaceState = level.getBlockState(pos);
				}
			}
			
		}
		
		if (replaceState.is(Blocks.COMPOUND_BLOCK.get())) {
			
			if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
				
				for (var part : compound.getParts().values()) {
					if (!canCompound(part.getLevel(), part.getPos(), part.getState(), null, null, state))
						return false;
				}
				
				if (simulate) return true;
				
				int partId = compound.addPart(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
				VirtualBlock part = compound.getParts().get(partId);
				part.getLevel().setBlockAndUpdate(part.getPos(), state);
				return true;
			}
			
		}

		if (!replaceState.canBeReplaced()) return false;
		if (simulate) return true;
		
		level.setBlockAndUpdate(pos, state);
		return true;
	}
	
	public static boolean canCompound(Level level1, BlockPos pos1, BlockState state1, Level level2, BlockPos pos2,  BlockState state2) {
		VoxelShape shape1 = state1.getShape(level1, pos1);
		VoxelShape shape2 = state2.getShape(level2, pos2);
		VoxelShape intersect = Shapes.join(shape1, shape2, BooleanOp.AND);
		return intersect.isEmpty();
	}
	
}
