package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMBlenderModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;
	private final ModelRenderer mixer;
	private final ModelRenderer fluid;
	
	public TileEntityMBlenderModel() {
		texWidth = 256;
		texHeight = 256;

		root = new ModelRenderer(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		root.texOffs(142, 45).addBox(-24.0F, -24.0F, -8.0F, 24.0F, 8.0F, 16.0F, 0.0F, false);
		root.texOffs(142, 75).addBox(-24.0F, -48.0F, 6.0F, 28.0F, 16.0F, 2.0F, 0.0F, false);
		root.texOffs(144, 0).addBox(-40.0F, -40.0F, -8.0F, 16.0F, 24.0F, 16.0F, 0.0F, false);
		root.texOffs(144, 147).addBox(3.0F, -21.0F, 33.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);
		root.texOffs(188, 164).addBox(3.0F, -25.0F, 25.0F, 4.0F, 4.0F, 12.0F, 0.0F, false);
		root.texOffs(165, 146).addBox(3.0F, -32.0F, 25.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);
		root.texOffs(189, 105).addBox(-10.0F, -36.0F, 25.0F, 17.0F, 4.0F, 4.0F, 0.0F, false);
		root.texOffs(143, 104).addBox(-10.0F, -30.0F, 30.0F, 7.0F, 5.0F, 5.0F, 0.0F, false);
		root.texOffs(175, 122).addBox(-10.0F, -30.0F, 15.0F, 10.0F, 5.0F, 5.0F, 0.0F, false);
		root.texOffs(143, 123).addBox(-10.0F, -44.0F, 23.0F, 8.0F, 5.0F, 5.0F, 0.0F, false);
		root.texOffs(215, 126).addBox(-3.0F, -30.0F, 20.0F, 5.0F, 5.0F, 15.0F, 0.0F, false);
		root.texOffs(142, 160).addBox(-2.0F, -44.0F, 13.0F, 5.0F, 5.0F, 15.0F, 0.0F, false);
		root.texOffs(229, 150).addBox(-2.0F, -44.0F, 8.0F, 5.0F, 28.0F, 5.0F, 0.0F, false);
		root.texOffs(15, 192).addBox(-40.0F, -16.0F, -8.0F, 48.0F, 16.0F, 48.0F, 0.0F, false);
		root.texOffs(186, 139).addBox(0.0F, -30.0F, 15.0F, 5.0F, 14.0F, 5.0F, 0.0F, false);
		root.texOffs(65, 45).addBox(-9.0F, -48.0F, 9.0F, 1.0F, 32.0F, 30.0F, 0.0F, false);
		root.texOffs(0, 45).addBox(-40.0F, -48.0F, 9.0F, 1.0F, 32.0F, 30.0F, 0.0F, false);
		root.texOffs(71, 0).addBox(-40.0F, -48.0F, 8.0F, 32.0F, 32.0F, 1.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(-40.0F, -48.0F, 39.0F, 32.0F, 32.0F, 1.0F, 0.0F, false);

		mixer = new ModelRenderer(this);
		mixer.setPos(-24.0F, -22.0F, 24.0F);
		root.addChild(mixer);
		setRotationAngle(mixer, 0.0F, -0.3491F, 0.0F);
		mixer.texOffs(36, 156).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
		mixer.texOffs(0, 186).addBox(-13.0F, 1.0F, -2.0F, 26.0F, 1.0F, 4.0F, 0.0F, false);
		mixer.texOffs(0, 151).addBox(-2.0F, 0.0F, -13.0F, 4.0F, 1.0F, 26.0F, 0.0F, false);

		fluid = new ModelRenderer(this);
		fluid.setPos(0.0F, 0.0F, 0.0F);
		fluid.texOffs(0, 113).addBox(-39.0F, -17.0F, 9.0F, 30.0F, 0.0F, 30.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
	public void renderFluidPlate(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		fluid.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	public void setRotation(float rotation) {
		this.setRotationAngle(mixer, 0, (float) (rotation / 180 * Math.PI), 0);
	}
		
}