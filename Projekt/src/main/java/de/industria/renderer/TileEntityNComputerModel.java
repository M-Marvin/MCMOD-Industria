package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityNComputerModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;

	public TileEntityNComputerModel() {
		textureWidth = 128;
		textureHeight = 128;

		root = new ModelRenderer(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		root.setTextureOffset(0, 0).addBox(-8.0F, -32.0F, -8.0F, 16.0F, 32.0F, 32.0F, 0.0F, false);
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