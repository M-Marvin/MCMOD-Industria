package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMFluidBathModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;
	private final ModelRenderer fluidPlate;
	private final ModelRenderer itemHolder;

	public TileEntityMFluidBathModel() {
		
		texWidth = 256;
		texHeight = 256;

		root = new ModelRenderer(this);
		root.setPos(-11.0F, -6.0F, 16.0F);
		root.texOffs(0, 0).addBox(-13.0F, 14.0F, -24.0F, 32.0F, 16.0F, 48.0F, 0.0F, false);
		root.texOffs(0, 104).addBox(-13.0F, -2.0F, -24.0F, 32.0F, 16.0F, 12.0F, 0.0F, false);
		root.texOffs(72, 76).addBox(-13.0F, -2.0F, 12.0F, 32.0F, 16.0F, 12.0F, 0.0F, false);
		root.texOffs(112, 0).addBox(18.0F, -2.0F, -12.0F, 1.0F, 16.0F, 24.0F, 0.0F, false);
		root.texOffs(88, 104).addBox(-13.0F, -2.0F, -12.0F, 1.0F, 16.0F, 24.0F, 0.0F, false);
		root.texOffs(0, 132).addBox(14.0F, -1.0F, -12.0F, 1.0F, 2.0F, 24.0F, 0.0F, false);
		root.texOffs(114, 120).addBox(-9.0F, -1.0F, -12.0F, 1.0F, 2.0F, 24.0F, 0.0F, false);

		fluidPlate = new ModelRenderer(this);
		fluidPlate.setPos(19.0F, 13.0F, -24.0F);
		fluidPlate.texOffs(0, 64).addBox(-31.0F, 0.0F, 12.0F, 30.0F, 0.0F, 24.0F, 0.0F, false);

		itemHolder = new ModelRenderer(this);
		itemHolder.setPos(0.0F, 0.0F, 0.0F);
		itemHolder.texOffs(84, 64).addBox(-10.0F, 0.0F, -16.0F, 26.0F, 1.0F, 3.0F, 0.0F, false);
		itemHolder.texOffs(0, 0).addBox(1.0F, 1.0F, -16.0F, 4.0F, 7.0F, 1.0F, 0.0F, false);
		
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void renderItemHolder(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		itemHolder.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void renderFluidPlate(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		fluidPlate.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
}
