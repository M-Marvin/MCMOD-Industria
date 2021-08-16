package de.industria.renderer;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMBattery;
import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.gui.AnimatedTexture;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityMBatteryRenderer extends TileEntityRenderer<TileEntityMBattery> {
	
	public static final ResourceLocation ENERGY_BAR_TEXTUR = new ResourceLocation(Industria.MODID, "textures/energy_bar.png");
	
	public TileEntityMBatteryRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TileEntityMBattery tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		if (!tileEntityIn.isEmpty()) {
			
			float amount = tileEntityIn.getStorage() / (float) ModItems.battery.getCapacity();
			Voltage voltage = tileEntityIn.getVoltage();
			BlockState state = tileEntityIn.getBlockState();
			Direction rotation = state.getBlock() == ModItems.battery.getBlock() ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
			
			renderEnergyBar(amount, voltage, rotation, matrixStackIn, bufferIn, combinedOverlayIn, combinedLightIn);
			
		}
		
	}
	
	public static void renderEnergyBar(float amount, Voltage voltage, Direction rotation, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		ResourceLocation energyTexture = ENERGY_BAR_TEXTUR;
		AnimatedTexture animatedTexture = AnimatedTexture.prepareTexture(energyTexture);
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(energyTexture));
		
		Color color = voltage.getRenderColor();
		float cr = color.getRed() / 255F;
		float cg = color.getGreen() / 255F;
		float cb = color.getBlue() / 255F;
		float ca = color.getAlpha() / 255F;
		
		Vector3f fc1 = new Vector3f(1F / 16F, 2F / 16F, 13.8F / 16F);
		Vector3f fc2 = new Vector3f(15F / 16F, (11F / 16F) * amount, 13.8F / 16F);
		
		Vector2f frameSideUV0 = animatedTexture.getFrameUV(fc1.y(), fc1.y());
		Vector2f frameSideUV1 = animatedTexture.getFrameUV(fc2.y(), fc2.y());
		
		matrixStackIn.pushPose();
		
		matrixStackIn.translate(0.5F, 0.5F, 0.5F);
		matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(rotation.toYRot()));
		matrixStackIn.translate(-0.5F, -0.5F, -0.5F);
		
		Matrix4f posMatrix = matrixStackIn.last().pose();
		Matrix3f normalMatrix = matrixStackIn.last().normal();
		
		// Fronbar
		vertexBuilder.vertex(posMatrix, fc1.x(), fc1.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV0.x, frameSideUV0.y).overlayCoords(combinedOverlayIn).uv2(15728880).normal(normalMatrix, 1, 1, 1).endVertex();;
		vertexBuilder.vertex(posMatrix, fc2.x(), fc1.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV1.x, frameSideUV0.y).overlayCoords(combinedOverlayIn).uv2(15728880).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc2.x(), fc2.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV1.x, frameSideUV1.y).overlayCoords(combinedOverlayIn).uv2(15728880).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc1.x(), fc2.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV0.x, frameSideUV1.y).overlayCoords(combinedOverlayIn).uv2(15728880).normal(normalMatrix, 1, 1, 1).endVertex();
		
		matrixStackIn.popPose();
		
	}
	
}
