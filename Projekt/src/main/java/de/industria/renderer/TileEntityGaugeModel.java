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
		texWidth = 64;
		texHeight = 64;

		root = new ModelRenderer(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		root.texOffs(0, 0).addBox(-8.0F, -16.0F, -6.0F, 16.0F, 16.0F, 14.0F, 0.0F, false);
		root.texOffs(0, 39).addBox(-8.0F, -5.0F, -8.0F, 16.0F, 5.0F, 2.0F, 0.0F, false);
		root.texOffs(0, 48).addBox(-7.0F, -14.0F, -8.0F, 14.0F, 9.0F, 0.0F, 0.0F, false);
		root.texOffs(0, 32).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(-8.0F, -14.0F, -8.0F, 1.0F, 9.0F, 2.0F, 0.0F, false);
		root.texOffs(8, 0).addBox(7.0F, -14.0F, -8.0F, 1.0F, 9.0F, 2.0F, 0.0F, false);

		gauge = new ModelRenderer(this);
		gauge.setPos(0.0F, -4.5F, -6.5F);
		root.addChild(gauge);
		setRotationAngle(gauge, 0.0F, 0.0F, -0.7854F);
		gauge.texOffs(43, 33).addBox(-1.0F, -8.5F, -0.5F, 2.0F, 9.0F, 0.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
	
	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	public void setGaugeState(float value) {
		setRotationAngle(gauge, 0, 0, (float) (((value / 1000) - 0.5F) * (Math.PI / 2)));
	}
	
	public float getGaugeState() {
		return (float) ((gauge.zRot / (Math.PI / 2) + 0.5F) * 1000);
	}
	
}