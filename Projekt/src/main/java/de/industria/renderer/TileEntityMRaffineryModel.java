package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMRaffineryModel extends EntityModel<Entity> {
	private final ModelRenderer root;
	
	public TileEntityMRaffineryModel() {
		textureWidth = 256;
		textureHeight = 256;

		root = new ModelRenderer(this);
		root.setRotationPoint(8.0F, 24.0F, -8.0F);
		root.setTextureOffset(16, 24).addBox(-32.0F, -20.0F, 0.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
		root.setTextureOffset(20, 48).addBox(-3.0F, -20.0F, 0.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
		root.setTextureOffset(114, 114).addBox(-47.0F, -60.0F, 2.0F, 14.0F, 44.0F, 14.0F, 0.0F, false);
		root.setTextureOffset(43, 129).addBox(-24.0F, -20.0F, 8.0F, 16.0F, 4.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-48.0F, -16.0F, 0.0F, 48.0F, 16.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(20, 62).addBox(-3.0F, -20.0F, 29.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
		root.setTextureOffset(17, 55).addBox(-32.0F, -20.0F, 29.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
		root.setTextureOffset(0, 48).addBox(-32.0F, -64.0F, 0.0F, 32.0F, 44.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-42.0F, -42.0F, 18.0F, 4.0F, 26.0F, 4.0F, 0.0F, false);
		root.setTextureOffset(16, 0).addBox(-42.0F, -49.0F, 27.0F, 4.0F, 20.0F, 4.0F, 0.0F, false);
		root.setTextureOffset(0, 58).addBox(-38.0F, -33.0F, 27.0F, 6.0F, 4.0F, 4.0F, 0.0F, false);
		root.setTextureOffset(0, 48).addBox(-42.0F, -46.0F, 16.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);
		root.setTextureOffset(0, 124).addBox(-42.0F, -53.0F, 16.0F, 4.0F, 4.0F, 15.0F, 0.0F, false);
		root.setTextureOffset(96, 48).addBox(-46.0F, -65.0F, 3.0F, 12.0F, 5.0F, 12.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}