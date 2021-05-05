package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


public class TileEntityMAirCompressorModel extends EntityModel<Entity> {
	private final ModelRenderer root;

	public TileEntityMAirCompressorModel() {
		textureWidth = 128;
		textureHeight = 128;

		root = new ModelRenderer(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		root.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(0, 32).addBox(-8.0F, -5.0F, 8.0F, 16.0F, 5.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(51, 19).addBox(-4.0F, -15.0F, 10.0F, 8.0F, 8.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-2.0F, -13.0F, 8.0F, 4.0F, 4.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(48, 0).addBox(-3.0F, -7.0F, 10.0F, 6.0F, 2.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(73, 56).addBox(-5.0F, -13.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(73, 2).addBox(-5.0F, -15.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(58, 69).addBox(-5.0F, -9.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(43, 68).addBox(0.0F, -16.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(58, 55).addBox(2.0F, -16.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(43, 54).addBox(4.0F, -15.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(28, 53).addBox(4.0F, -13.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(0, 53).addBox(4.0F, -11.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(51, 40).addBox(4.0F, -9.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(28, 67).addBox(-2.0F, -16.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(0, 67).addBox(-5.0F, -11.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(66, 41).addBox(-4.0F, -16.0F, 10.0F, 1.0F, 1.0F, 13.0F, 0.0F, false);
		root.setTextureOffset(0, 86).addBox(7.0F, -15.0F, 23.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(0, 86).addBox(-8.0F, -15.0F, 23.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(0, 86).addBox(-8.0F, -15.0F, 8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(0, 86).addBox(7.0F, -15.0F, 8.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(39, 86).addBox(7.0F, -16.0F, 8.0F, 1.0F, 1.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(39, 86).addBox(-8.0F, -16.0F, 8.0F, 1.0F, 1.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(0, 82).addBox(-7.0F, -16.0F, 8.0F, 14.0F, 1.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(0, 82).addBox(-7.0F, -16.0F, 23.0F, 14.0F, 1.0F, 1.0F, 0.0F, false);
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