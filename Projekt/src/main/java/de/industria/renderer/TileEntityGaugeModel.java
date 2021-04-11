package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityGaugeModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;
	private final ModelRenderer gauge;
	
	public TileEntityGaugeModel() {
		textureWidth = 64;
		textureHeight = 64;

		root = new ModelRenderer(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		root.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -6.0F, 16.0F, 16.0F, 14.0F, 0.0F, false);
		root.setTextureOffset(0, 39).addBox(-8.0F, -5.0F, -8.0F, 16.0F, 5.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(0, 48).addBox(-7.0F, -14.0F, -8.0F, 14.0F, 9.0F, 0.0F, 0.0F, false);
		root.setTextureOffset(0, 32).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-8.0F, -14.0F, -8.0F, 1.0F, 9.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(8, 0).addBox(7.0F, -14.0F, -8.0F, 1.0F, 9.0F, 2.0F, 0.0F, false);

		gauge = new ModelRenderer(this);
		gauge.setRotationPoint(0.0F, -4.5F, -6.5F);
		root.addChild(gauge);
		setRotationAngle(gauge, 0.0F, 0.0F, -0.7854F);
		gauge.setTextureOffset(43, 33).addBox(-1.0F, -8.5F, -0.5F, 2.0F, 9.0F, 0.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
	
	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	public void setGaugeState(float value) {
		setRotationAngle(gauge, 0, 0, (float) (((value / 1000) - 0.5F) * (Math.PI / 2)));
	}
	
	public float getGaugeState() {
		return (float) ((gauge.rotateAngleZ / (Math.PI / 2) + 0.5F) * 1000);
	}
	
}