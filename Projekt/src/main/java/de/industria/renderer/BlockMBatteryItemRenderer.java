package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.blocks.BlockMBattery;
import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;

public class BlockMBatteryItemRenderer extends ItemStackTileEntityRenderer {
	
	@SuppressWarnings("deprecation")
	@Override
	public void renderByItem(ItemStack batteryStack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		BlockState defaultState = ModItems.battery.getBlock().defaultBlockState();
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(defaultState, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		
		if (batteryStack.getItem() == Item.byBlock(ModItems.battery)) {
			
			BlockMBattery batteryItem = (BlockMBattery) ((BlockItem) batteryStack.getItem()).getBlock();
			float amount = batteryItem.getStorage(batteryStack) / (float) batteryItem.getCapacity();
			Voltage voltage = batteryItem.getVoltage(batteryStack);
			
			if (amount > 0) {
				
				TileEntityMBatteryRenderer.renderEnergyBar(amount, voltage, defaultState.getValue(BlockStateProperties.HORIZONTAL_FACING), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
				
			}
			
		}
		
	}
	
}
