package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class ItemBluePrintRenderer extends ItemStackTileEntityRenderer {
	
	protected BlockRendererDispatcher blockRenderer;
	
	@SuppressWarnings("deprecation")
	@Override
	public void renderByItem(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		if (stack.hasTag()) {
			
			CompoundNBT tag = stack.getTag();
			
			if (tag.contains("Blueprint")) {
				
				ListNBT blueprintData = tag.getList("Blueprint", 10);
				
				for (int i = 0; i < blueprintData.size(); i++) {
					
					CompoundNBT blockData = blueprintData.getCompound(i);
					BlockPos position = NBTUtil.readBlockPos(blockData.getCompound("Pos"));
					BlockState state = NBTUtil.readBlockState(blockData.getCompound("BlockState"));
					//CompoundNBT tileData = blockData.getCompound("TileData");
					
					matrixStackIn.pushPose();
					
					matrixStackIn.translate(position.getX(), position.getY(), position.getZ());
					
					blockRenderer.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
					
					matrixStackIn.popPose();
					
				}
				
			}
			
		}
		
	}
	
}
