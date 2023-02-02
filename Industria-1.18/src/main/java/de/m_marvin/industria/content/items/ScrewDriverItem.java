package de.m_marvin.industria.content.items;

import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.Ship;

import de.m_marvin.industria.content.blocks.IScrewDriveable;
import de.m_marvin.industria.content.registries.ModTags;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ScrewDriverItem extends Item implements IScrollOverride {
	
	public ScrewDriverItem(Properties properties) {
		super(properties);
	}
		
	@Override
	public InteractionResult useOn(UseOnContext context) {
		
		if (!context.getLevel().isClientSide()) {
			
			if (context.getPlayer().isShiftKeyDown()) {
				
				for (Ship contraption : PhysicUtility.getLoadedContraptions((ServerLevel) context.getLevel())) {
					System.out.println("SHIP");
					
					AABBic shape = contraption.getShipAABB();
					
					if (shape == null) {
						
						PhysicUtility.removeContraption((ServerLevel) context.getLevel(), contraption);
						
					}
					
					PhysicUtility.removeContraption((ServerLevel) context.getLevel(), contraption);
				}
				
			} else {

				PhysicUtility.createNewContraptionAt((ServerLevel) context.getLevel(), context.getClickedPos(), 1F);
				
			}
			
		}
		
//		BlockPos targetedPos = context.getClickedPos();
//		BlockState targetedBlock = context.getLevel().getBlockState(targetedPos);
//		if (context.getPlayer() == null || !context.getPlayer().mayBuild()) {
//			return super.useOn(context);
//		}
//		if (!(targetedBlock.getBlock() instanceof IScrewDriveable)) {
//			if (canScrewDriverPickup(targetedBlock)) {
//				return IScrewDriveable.pickupBlock(targetedPos, targetedBlock, context);
//			}Client received packet of type
//		} else {
//			IScrewDriveable actor = (IScrewDriveable) targetedBlock.getBlock();
//			if (context.getPlayer().isShiftKeyDown()) {
//				return actor.onSneakScrewDrived(targetedBlock, context);
//			}
//			return actor.onScrewDrived(targetedBlock, context);
//		}
		return super.useOn(context);
	}
	
	public boolean canScrewDriverPickup(BlockState state) {
		return state.is(ModTags.BLOCK_SCREW_DRIVER_PICKUP);
	}
	
	@Override
	public void onScroll(UseOnContext context, double delta) {
//		BlockPos targetedPos = context.getClickedPos();
//		BlockState targetedBlock = context.getLevel().getBlockState(targetedPos);
//		if (targetedBlock.getBlock() instanceof IScrewDriveable) {
//			Industria.NETWORK.sendToServer(new CScrewDriverAdjustmentPackage(context.getHitResult(), delta, context.getHand()));
//			adjustTargetedBlock(targetedBlock, context, delta);
//		}
	}
	
	public void adjustTargetedBlock(BlockState targetedBlock, UseOnContext context, double scrollDelta) {
		IScrewDriveable actor = (IScrewDriveable) targetedBlock.getBlock();
		InteractionResult result = actor.onScrewDriveAdjusting(targetedBlock, context, scrollDelta);
		
		if (result.shouldSwing()) {
			
		}
	}
	
	@Override
	public boolean overridesScroll(UseOnContext context, ItemStack stack) {
		if (context.getHitResult() != null) {
			BlockPos targetedPos = context.getClickedPos();
			Direction targetedFace = context.getClickedFace();
			Vec3 targetedVec = context.getClickLocation();
			BlockState targetedBlock = context.getLevel().getBlockState(targetedPos);
			if (targetedBlock.getBlock() instanceof IScrewDriveable) {
				IScrewDriveable actor = (IScrewDriveable) targetedBlock.getBlock();
				return actor.isAdjustable(targetedBlock, targetedFace, targetedVec);
			}
		}
		return false;		
	}
	
}