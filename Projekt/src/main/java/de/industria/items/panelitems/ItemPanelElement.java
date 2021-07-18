package de.industria.items.panelitems;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.items.ItemBase;
import de.industria.tileentity.TileEntityControllPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class ItemPanelElement extends ItemBase {
	
	public ItemPanelElement(String name) {
		super(name, ItemGroup.TAB_REDSTONE);
	}
	
	public abstract AxisAlignedBB getCollisionBounds();
	
	public abstract void onActivated(TileEntityControllPanel panel, ItemStack elementStack);
	public abstract void onPowerStateChange(TileEntityControllPanel panel, ItemStack elementStack, boolean powered);
	public void onSheduleTick(TileEntityControllPanel panel, ItemStack elementStack) {}
	
	public void draw(int x, int y, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
		
		matrixStackIn.translate(x * 0.0625F, 0, y * 0.0625F);
		
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.HEAD, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
		
	}
	
}
