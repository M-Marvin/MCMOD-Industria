package de.industria.renderer;

import java.awt.Color;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.tileentity.TileEntityGauge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityGaugeRenderer extends TileEntityRenderer<TileEntityGauge> {
	
	private FontRenderer fontRenderer;
	private TileEntityGaugeModel gaugeModel;
	
	public static final ResourceLocation GAUGE_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/gauge.png");
	
	@SuppressWarnings("resource")
	public TileEntityGaugeRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.fontRenderer = Minecraft.getInstance().fontRenderer;
		this.gaugeModel = new TileEntityGaugeModel();
	}
	
	@Override
	public void render(TileEntityGauge tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		Direction facing = tileEntityIn.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(GAUGE_TEXTURES));
		
		matrixStackIn.push();
		
		matrixStackIn.translate(0F, -1F, 0F);
		
		matrixStackIn.translate(0.5F, 1.5F, 0.5F);
		matrixStackIn.rotate(facing.getRotation());
		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
		matrixStackIn.translate(0F, -1F, 0F);
		
		matrixStackIn.scale(0.999F, 0.999F, 0.999F);

		NumberFormat format = new DecimalFormat("0.00");
		
		String unitName = tileEntityIn.getUnit();
		float value = Math.min(Math.max(tileEntityIn.getValue(), 0), 1000);
		
		float currentValue = tileEntityIn.currentGaugeValue;
		
		float motion = (value - currentValue) * 0.02F;
		float newValue = currentValue + motion;
		
		tileEntityIn.currentGaugeValue = newValue;
		gaugeModel.setGaugeState(newValue);
		gaugeModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);

		String unitString = format.format(newValue) + " " + unitName;
		int width = fontRenderer.getStringWidth(unitString);
		float f = 0.0356F * 0.5F;
		
		matrixStackIn.translate(-(width / 2) * f, 1F, -0.45F);
		
		matrixStackIn.scale(f, f, f);
		
		fontRenderer.renderString(unitString, 0, 0, new Color(0, 0, 0).getRGB(), false, matrixStackIn.getLast().getMatrix(), bufferIn, false, combinedOverlayIn, combinedLightIn);
		
		matrixStackIn.pop();
		
	}

}
