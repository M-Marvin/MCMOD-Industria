package de.industria.renderer;

import java.util.HashMap;
import java.util.Map.Entry;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.blocks.BlockRControllPanel;
import de.industria.items.panelitems.ItemPanelElement;
import de.industria.tileentity.TileEntityControllPanel.Pos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;

public class BlockControllPanelItemRenderer extends ItemStackTileEntityRenderer {
	
	@SuppressWarnings("deprecation")
	@Override
	public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Industria.controll_panel.getDefaultState().with(BlockRControllPanel.FACING, Direction.UP), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		
		HashMap<Pos, ItemStack> elements = getElementsFromStack(stack);
		
		matrixStackIn.push();
		matrixStackIn.translate(0.5F, 11 * 0.0625F, 0.5F);
		
		for (Entry<Pos, ItemStack> element : elements.entrySet()) {
			
			Pos position = element.getKey();

			matrixStackIn.push();
			
			((ItemPanelElement) element.getValue().getItem()).draw(position.getX(), position.getY(), element.getValue(), combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);

			matrixStackIn.pop();
			
		}
		
		matrixStackIn.pop();
		
	}

	private HashMap<Pos, ItemStack> getElementsFromStack(ItemStack stack) {
		
		HashMap<Pos, ItemStack> panelElements = new HashMap<Pos, ItemStack>();
		CompoundNBT compound = stack.getTag();
		
		if (compound != null) {
			
			CompoundNBT tileData = compound.getCompound("BlockEntityTag");
			ListNBT list = tileData.getList("PanelElements", 10);
			
			for (int i = 0; i < list.size(); i++) {
				CompoundNBT stackNBT = list.getCompound(i);
				ItemStack stackE = ItemStack.read(stackNBT);
				int[] posArr = stackNBT.getIntArray("Pos");
				Pos pos = new Pos(posArr[0], posArr[1]);
				panelElements.put(pos, stackE);
			}
			
		}
		
		return panelElements;
		
	}
	
}
