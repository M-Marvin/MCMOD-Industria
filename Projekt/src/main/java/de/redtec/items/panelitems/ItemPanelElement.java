package de.redtec.items.panelitems;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.redtec.items.ItemBase;
import de.redtec.tileentity.TileEntityControllPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class ItemPanelElement extends ItemBase {
	
	public ItemPanelElement(String name) {
		super(name, ItemGroup.REDSTONE);
	}
	
	public abstract AxisAlignedBB getCollisionBounds();
	
	public abstract void onActivated(TileEntityControllPanel panel, ItemStack elementStack);
	public abstract void onPowerStateChange(TileEntityControllPanel panel, ItemStack elementStack, boolean powered);
	public void onSheduleTick(TileEntityControllPanel panel, ItemStack elementStack) {}
	
	public void draw(int x, int y, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
		
		matrixStackIn.translate(x * 0.0625F, 0, y * 0.0625F);
		
		Minecraft.getInstance().getItemRenderer().renderItem(stack, TransformType.HEAD, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
		
	}
	
}
