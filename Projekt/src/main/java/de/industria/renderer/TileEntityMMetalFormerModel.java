package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMMetalFormerModel extends EntityModel<Entity> {
	private final ModelRenderer root;
	private final ModelRenderer saw;
	private final ModelRenderer roll1;
	private final ModelRenderer roll2;
	private final ModelRenderer cube_r1;

	public TileEntityMMetalFormerModel() {
		texWidth = 128;
		texHeight = 128;
		
		root = new ModelRenderer(this);
		root.setPos(0.0F, 24.0F, 16.0F);
		root.texOffs(0, 0).addBox(7.5F, -15.0F, -16.0F, 0.0F, 9.0F, 16.0F, 0.0F, false);
		root.texOffs(68, 47).addBox(-7.0F, -6.0F, 0.0F, 14.0F, 6.0F, 8.0F, 0.0F, false);
		root.texOffs(34, 39).addBox(-7.0F, -15.0F, 7.0F, 14.0F, 9.0F, 1.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(-7.0F, -15.0F, -24.0F, 14.0F, 9.0F, 1.0F, 0.0F, false);
		root.texOffs(68, 33).addBox(-7.0F, -6.0F, -24.0F, 14.0F, 6.0F, 8.0F, 0.0F, false);
		root.texOffs(34, 39).addBox(-8.0F, -6.0F, -24.0F, 1.0F, 6.0F, 32.0F, 0.0F, false);
		root.texOffs(36, 77).addBox(-8.0F, -15.0F, 0.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.texOffs(18, 71).addBox(-8.0F, -15.0F, -24.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.texOffs(0, 42).addBox(7.0F, -15.0F, 0.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.texOffs(0, 71).addBox(7.0F, -15.0F, -24.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.texOffs(0, 33).addBox(7.0F, -6.0F, -24.0F, 1.0F, 6.0F, 32.0F, 0.0F, false);
		root.texOffs(64, 0).addBox(-7.0F, -1.0F, -16.0F, 14.0F, 1.0F, 16.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(-8.0F, -16.0F, -24.0F, 16.0F, 1.0F, 32.0F, 0.0F, false);
		root.texOffs(0, 17).addBox(-7.5F, -15.0F, -16.0F, 0.0F, 9.0F, 16.0F, 0.0F, false);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(-7.0F, -6.0F, -16.0F);
		root.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.3927F, 0.0F, 0.0F);
		cube_r1.texOffs(64, 17).addBox(0.0F, 0.0F, 0.0F, 14.0F, 1.0F, 13.0F, 0.0F, false);

		saw = new ModelRenderer(this);
		saw.setPos(0.0F, -7.0F, -20.0F);
		root.addChild(saw);
		saw.texOffs(0, 4).addBox(0.0F, -3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F, false);

		roll1 = new ModelRenderer(this);
		roll1.setPos(0.0F, -4.5F, -2.5F);
		root.addChild(roll1);
		roll1.texOffs(68, 61).addBox(-7.0F, -1.5F, -1.5F, 14.0F, 3.0F, 3.0F, 0.0F, false);

		roll2 = new ModelRenderer(this);
		roll2.setPos(0.0F, -9.5F, -2.5F);
		root.addChild(roll2);
		roll2.texOffs(34, 33).addBox(-7.0F, -1.5F, -1.5F, 14.0F, 3.0F, 3.0F, 0.0F, false);
		
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	public void setRotation(float rotation) {
		float rot1 = (float) ((rotation / 360) * Math.PI * -2);
		float rot2 = (float) ((rotation / 360) * Math.PI * -2);
		setRotationAngle(this.roll1, -rot1, 0, 0);
		setRotationAngle(this.roll2, rot2, 0, 0);
		setRotationAngle(this.saw, rot2, 0, 0);
	}
	
}