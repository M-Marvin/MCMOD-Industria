package de.industria.renderer;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.util.gui.AnimatedTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class TileEntityMFluidBathModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;
	private final ModelRenderer axisX;
	private final ModelRenderer axisY;
	
	public TileEntityMFluidBathModel() {
		
		texWidth = 256;
		texHeight = 256;
		
		root = new ModelRenderer(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		root.texOffs(0, 41).addBox(5.0F, -32.0F, -7.0F, 2.0F, 2.0F, 46.0F, 0.0F, false);
		root.texOffs(81, 93).addBox(-24.0F, -16.0F, -8.0F, 32.0F, 16.0F, 8.0F, 0.0F, false);
		root.texOffs(9, 118).addBox(-23.0F, -30.0F, -7.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.texOffs(0, 118).addBox(-23.0F, -30.0F, 37.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.texOffs(0, 93).addBox(-24.0F, -16.0F, 32.0F, 32.0F, 16.0F, 8.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(-24.0F, -8.0F, 0.0F, 32.0F, 8.0F, 32.0F, 0.0F, false);
		root.texOffs(102, 41).addBox(-24.0F, -24.0F, 0.0F, 32.0F, 16.0F, 2.0F, 0.0F, false);
		root.texOffs(51, 41).addBox(-24.0F, -24.0F, 2.0F, 2.0F, 16.0F, 16.0F, 0.0F, false);
		root.texOffs(97, 0).addBox(-24.0F, -24.0F, 18.0F, 32.0F, 16.0F, 2.0F, 0.0F, false);
		root.texOffs(21, 41).addBox(-1.0F, -22.0F, 20.0F, 4.0F, 4.0F, 8.0F, 0.0F, false);
		root.texOffs(0, 9).addBox(-19.0F, -22.0F, 20.0F, 4.0F, 4.0F, 8.0F, 0.0F, false);
		root.texOffs(102, 60).addBox(-1.0F, -18.0F, 24.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);
		root.texOffs(72, 41).addBox(-9.0F, -18.0F, 24.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(-19.0F, -18.0F, 24.0F, 10.0F, 4.0F, 4.0F, 0.0F, false);
		root.texOffs(0, 41).addBox(6.0F, -24.0F, 2.0F, 2.0F, 16.0F, 16.0F, 0.0F, false);
		root.texOffs(51, 74).addBox(-16.0F, -17.0F, 33.0F, 15.0F, 1.0F, 6.0F, 0.0F, false);
		root.texOffs(0, 74).addBox(-16.0F, -17.0F, -7.0F, 15.0F, 1.0F, 6.0F, 0.0F, false);
		root.texOffs(51, 44).addBox(-23.0F, -32.0F, -7.0F, 2.0F, 2.0F, 46.0F, 0.0F, false);
		root.texOffs(88, 54).addBox(5.0F, -30.0F, 37.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.texOffs(117, 73).addBox(5.0F, -30.0F, -7.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);

		axisX = new ModelRenderer(this);
		axisX.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(axisX);
		axisX.texOffs(97, 19).addBox(-24.0F, -30.0F, -5.0F, 32.0F, 1.0F, 3.0F, 0.0F, false);
		axisX.texOffs(0, 22).addBox(-12.0F, -29.0F, -5.0F, 7.0F, 3.0F, 3.0F, 0.0F, false);

		axisY = new ModelRenderer(this);
		axisY.setPos(0.0F, 0.0F, 0.0F);
		axisX.addChild(axisY);
		axisY.texOffs(37, 54).addBox(-10.0F, -31.0F, -4.0F, 3.0F, 16.0F, 1.0F, 0.0F, false);
		
//		texWidth = 256;
//		texHeight = 256;
//
//		root = new ModelRenderer(this);
//		root.setPos(-11.0F, -6.0F, 16.0F);
//		root.texOffs(0, 0).addBox(-13.0F, 14.0F, -24.0F, 32.0F, 16.0F, 48.0F, 0.0F, false);
//		root.texOffs(0, 104).addBox(-13.0F, -2.0F, -24.0F, 32.0F, 16.0F, 12.0F, 0.0F, false);
//		root.texOffs(72, 76).addBox(-13.0F, -2.0F, 12.0F, 32.0F, 16.0F, 12.0F, 0.0F, false);
//		root.texOffs(112, 0).addBox(18.0F, -2.0F, -12.0F, 1.0F, 16.0F, 24.0F, 0.0F, false);
//		root.texOffs(88, 104).addBox(-13.0F, -2.0F, -12.0F, 1.0F, 16.0F, 24.0F, 0.0F, false);
//		root.texOffs(0, 132).addBox(14.0F, -1.0F, -12.0F, 1.0F, 2.0F, 24.0F, 0.0F, false);
//		root.texOffs(114, 120).addBox(-9.0F, -1.0F, -12.0F, 1.0F, 2.0F, 24.0F, 0.0F, false);
//
//		fluidPlate = new ModelRenderer(this);
//		fluidPlate.setPos(19.0F, 13.0F, -24.0F);
//		fluidPlate.texOffs(0, 64).addBox(-31.0F, 0.0F, 12.0F, 30.0F, 0.0F, 24.0F, 0.0F, false);
//
//		itemHolder = new ModelRenderer(this);
//		itemHolder.setPos(0.0F, 0.0F, 0.0F);
//		itemHolder.texOffs(84, 64).addBox(-10.0F, 0.0F, -16.0F, 26.0F, 1.0F, 3.0F, 0.0F, false);
//		itemHolder.texOffs(0, 0).addBox(1.0F, 1.0F, -16.0F, 4.0F, 7.0F, 1.0F, 0.0F, false);
		
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void renderItemHolder(ItemStack itemIn, ItemStack itemOut, float progress, ItemRenderer itemRenderer, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		//itemHolder.render(matrixStack, buffer, packedLight, packedOverlay);
		
		//matrixStackIn.translate(4 * 0.0625F, 8 * 0.0625F, -15 * 0.0625F);
		//itemRenderDispatcher.renderStatic(tileEntityIn.getItem(0), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
		// TODO
	}

	public void renderFluidPlate(Fluid fluidIn, Fluid fluidOut, float durabillity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		
		Matrix4f posMatix = matrixStack.last().pose();
		Matrix3f normMatix = matrixStack.last().normal();
		
		Vector3f fp1 = new Vector3f(-22 / 16F, 1 / 16F, 2 / 16F);
		Vector3f fp2 = new Vector3f(6 / 16F, 0 / 16F, 18 / 16F);
		
		if (fluidIn != Fluids.EMPTY) {
			
			ResourceLocation fluidInLoc = fluidIn.getAttributes().getStillTexture();
			ResourceLocation fluidInTexture = new ResourceLocation(fluidInLoc.getNamespace(), "textures/" + fluidInLoc.getPath() + ".png");
			AnimatedTexture animTexture = AnimatedTexture.prepareTexture(fluidInTexture);
			
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.entityTranslucent(fluidInTexture));
			
			Vector2f ft11 = animTexture.getFrameUV(0, 0);
			Vector2f ft12 = animTexture.getFrameUV(1, 1);
			
			Color fluidInColor = new Color(fluidIn.getAttributes().getColor());
			Vector4f fc1 = new Vector4f(fluidInColor.getRed() / 255F, fluidInColor.getGreen() / 255F, fluidInColor.getBlue() / 255F, fluidInColor.getAlpha() / 255F);
			
			matrixStack.pushPose();
			
			vertexBuffer.vertex(posMatix, fp1.x(), fp1.y(), fp1.z()).color(fc1.x(), fc1.y(), fc1.z(), fc1.w()).uv(ft11.x, ft11.y).overlayCoords(packedOverlay).uv2(packedLight).normal(normMatix, 1, 1, 1).endVertex();
			vertexBuffer.vertex(posMatix, fp2.x(), fp1.y(), fp1.z()).color(fc1.x(), fc1.y(), fc1.z(), fc1.w()).uv(ft12.x, ft11.y).overlayCoords(packedOverlay).uv2(packedLight).normal(normMatix, 1, 1, 1).endVertex();
			vertexBuffer.vertex(posMatix, fp2.x(), fp1.y(), fp2.z()).color(fc1.x(), fc1.y(), fc1.z(), fc1.w()).uv(ft12.x, ft12.y).overlayCoords(packedOverlay).uv2(packedLight).normal(normMatix, 1, 1, 1).endVertex();
			vertexBuffer.vertex(posMatix, fp1.x(), fp1.y(), fp2.z()).color(fc1.x(), fc1.y(), fc1.z(), fc1.w()).uv(ft11.x, ft12.y).overlayCoords(packedOverlay).uv2(packedLight).normal(normMatix, 1, 1, 1).endVertex();
			
			matrixStack.popPose();
			
		}
		
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
}
