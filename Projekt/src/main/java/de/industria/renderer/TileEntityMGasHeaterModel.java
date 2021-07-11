package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports

public class TileEntityMGasHeaterModel extends EntityModel<Entity> {
	private final ModelRenderer root;

	public TileEntityMGasHeaterModel() {
		textureWidth = 256;
		textureHeight = 256;

		root = new ModelRenderer(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		root.setTextureOffset(96, 0).addBox(-7.0F, -14.0F, -8.0F, 13.0F, 13.0F, 14.0F, 0.0F, false);
		root.setTextureOffset(0, 33).addBox(-24.0F, -1.0F, -8.0F, 32.0F, 1.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(0, 16).addBox(-24.0F, -15.0F, -8.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(14, 14).addBox(6.0F, -15.0F, -8.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(8, 0).addBox(6.0F, -15.0F, 22.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-24.0F, -15.0F, 22.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-24.0F, -16.0F, -8.0F, 32.0F, 1.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(0, 66).addBox(-21.0F, -15.0F, -5.0F, 26.0F, 9.0F, 26.0F, 0.0F, false);
		root.setTextureOffset(78, 101).addBox(-12.0F, -6.0F, 1.0F, 3.0F, 5.0F, 20.0F, 0.0F, false);
		root.setTextureOffset(39, 102).addBox(-7.0F, -6.0F, 6.0F, 3.0F, 5.0F, 15.0F, 0.0F, false);
		root.setTextureOffset(0, 102).addBox(-1.0F, -6.0F, 6.0F, 3.0F, 5.0F, 15.0F, 0.0F, false);
		root.setTextureOffset(126, 101).addBox(-18.0F, -6.0F, 1.0F, 3.0F, 5.0F, 20.0F, 0.0F, false);
		root.setTextureOffset(119, 83).addBox(-18.0F, -6.0F, -3.0F, 11.0F, 5.0F, 4.0F, 0.0F, false);
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