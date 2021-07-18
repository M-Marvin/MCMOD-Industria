package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports

public class ItemSchredderToolMaceratorModel extends ItemSchredderToolModel {
	
	private final ModelRenderer root;
	private final ModelRenderer axe1;
	private final ModelRenderer axe2;

	public ItemSchredderToolMaceratorModel() {
		texWidth = 128;
		texHeight = 128;

		root = new ModelRenderer(this);
		root.setPos(-2.0F, 24.0F, 1.0F);
		

		axe1 = new ModelRenderer(this);
		axe1.setPos(-20.0F, -23.0F, 1.0F);
		root.addChild(axe1);
		axe1.texOffs(0, 24).addBox(0.0F, -2.0F, -2.0F, 28.0F, 4.0F, 4.0F, 0.0F, false);
		axe1.texOffs(0, 0).addBox(2.0F, -6.0F, -6.0F, 24.0F, 12.0F, 12.0F, 0.0F, false);

		axe2 = new ModelRenderer(this);
		axe2.setPos(8.0F, -23.0F, 13.0F);
		root.addChild(axe2);
		axe2.texOffs(0, 24).addBox(-28.0F, -2.0F, -2.0F, 28.0F, 4.0F, 4.0F, 0.0F, false);
		axe2.texOffs(0, 0).addBox(-26.0F, -6.0F, -6.0F, 24.0F, 12.0F, 12.0F, 0.0F, false);
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
	
	@Override
	public ModelRenderer getAxe1() {
		return axe1;
	}
	
	@Override
	public ModelRenderer getAxe2() {
		return axe2;
	}
	
	@Override
	public void setRotationState(float rotation) {
		float rot1 = (float) ((rotation / 360) * Math.PI * 2);
		float rot2 = (float) ((rotation / 360) * Math.PI * 2);
		setRotationAngle(this.axe1, -rot1, 0, 0);
		setRotationAngle(this.axe2, rot2, 0, 0);
	}
	
}