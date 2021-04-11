package de.industria.renderer;
// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityEnderCoreModel extends EntityModel<Entity> {
	private final ModelRenderer core;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;

	public TileEntityEnderCoreModel() {
		textureWidth = 64;
		textureHeight = 64;

		core = new ModelRenderer(this);
		core.setRotationPoint(0.0F, 16.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		core.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.7854F, -2.3562F, -0.7854F);
		cube_r1.setTextureOffset(0, 0).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, 0.0F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		core.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.7854F, -0.7854F, -0.7854F);
		cube_r2.setTextureOffset(0, 0).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, 0.0F, false);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		core.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.7854F, 0.0F, -0.7854F);
		cube_r3.setTextureOffset(0, 0).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		core.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	public void setRotation(float rotation) {
		cube_r1.rotateAngleY = rotation + 0;
		cube_r2.rotateAngleX = rotation + 45;
		cube_r2.rotateAngleY = rotation + 45;
		cube_r3.rotateAngleZ = rotation + 90;
	}
	
}