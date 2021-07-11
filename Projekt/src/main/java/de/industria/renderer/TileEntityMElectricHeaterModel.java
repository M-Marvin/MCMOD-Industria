package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.9.1
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports

public class TileEntityMElectricHeaterModel extends EntityModel<Entity> {
	private final ModelRenderer bb_main;

	public TileEntityMElectricHeaterModel() {
		textureWidth = 256;
		textureHeight = 256;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.setTextureOffset(0, 33).addBox(-24.0F, -1.0F, -8.0F, 32.0F, 1.0F, 32.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 16).addBox(-24.0F, -15.0F, -8.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 67).addBox(-18.0F, -6.0F, -5.0F, 20.0F, 5.0F, 26.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 0).addBox(-24.0F, -16.0F, -8.0F, 32.0F, 1.0F, 32.0F, 0.0F, false);
		bb_main.setTextureOffset(14, 14).addBox(6.0F, -15.0F, -8.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.setTextureOffset(8, 0).addBox(6.0F, -15.0F, 22.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 0).addBox(-24.0F, -15.0F, 22.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 105).addBox(-21.0F, -15.0F, -5.0F, 26.0F, 9.0F, 26.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}