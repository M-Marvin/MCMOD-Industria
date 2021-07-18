package de.industria.renderer;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.items.ItemStructureCladdingPane;
import de.industria.tileentity.TileEntityStructureScaffold;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class TileEntityStructureScaffoldRenderer extends TileEntityRenderer<TileEntityStructureScaffold> {
	
	protected BlockRendererDispatcher blockRenderDispatcher;
	
	public TileEntityStructureScaffoldRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(TileEntityStructureScaffold tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		Direction[] claddingSides = tileEntityIn.getCladdingSides();
		
		for (Direction side : claddingSides) {
			
			ItemStack claddingStack = tileEntityIn.getCladding(side);
			BlockState claddingState = ItemStructureCladdingPane.getBlockState(claddingStack);
			
			matrixStackIn.pushPose();
			
			switch(side) {
			case UP: 
				matrixStackIn.translate(0, 1.002F, 0);
				matrixStackIn.scale(1, 0.004F, 1); 
				break;
			case DOWN: 
				matrixStackIn.translate(0, -0.002F, 0);
				matrixStackIn.scale(1, 0.004F, 1); 
				break;
			case NORTH: 
				matrixStackIn.translate(0, 0, -0.002F);
				matrixStackIn.scale(1, 1, 0.004F); 
				break;
			case SOUTH: 
				matrixStackIn.translate(0, 0, 1.002F);
				matrixStackIn.scale(1, 1, 0.004F); 
				break;
			case EAST: 
				matrixStackIn.translate(1.002F, 0, 0);
				matrixStackIn.scale(0.004F, 1, 1); 
				break;
			case WEST: 
				matrixStackIn.translate(-0.002F, 0, 0);
				matrixStackIn.scale(0.004F, 1, 1); 
				break;
			}
			
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
            for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
               if (RenderTypeLookup.canRenderInLayer(claddingState, type)) {
                  net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
                  blockrendererdispatcher.getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), blockrendererdispatcher.getBlockModel(claddingState), claddingState, tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(type), false, new Random(), claddingState.getSeed(tileEntityIn.getBlockPos()), OverlayTexture.NO_OVERLAY);
               }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
            
			matrixStackIn.popPose();
			
		}
		
	}
	
}
