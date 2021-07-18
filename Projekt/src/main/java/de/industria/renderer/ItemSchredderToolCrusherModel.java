package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class ItemSchredderToolCrusherModel extends ItemSchredderToolModel {
	private final ModelRenderer root;
	private final ModelRenderer axe1;
	private final ModelRenderer blades;
	private final ModelRenderer crusher1;
	private final ModelRenderer crusher2;
	private final ModelRenderer crusher3;
	private final ModelRenderer crusher4;
	private final ModelRenderer crusher5;
	private final ModelRenderer crusher6;
	private final ModelRenderer axe2;
	private final ModelRenderer blades2;
	private final ModelRenderer crusher7;
	private final ModelRenderer crusher8;
	private final ModelRenderer crusher9;
	private final ModelRenderer crusher10;
	private final ModelRenderer crusher11;
	private final ModelRenderer crusher12;

	public ItemSchredderToolCrusherModel() {
		texWidth = 64;
		texHeight = 64;

		root = new ModelRenderer(this);
		root.setPos(-2.0F, 24.0F, 1.0F);
		

		axe1 = new ModelRenderer(this);
		axe1.setPos(-20.0F, -23.0F, 1.0F);
		root.addChild(axe1);
		axe1.texOffs(0, 23).addBox(0.0F, -2.0F, -2.0F, 28.0F, 4.0F, 4.0F, 0.0F, false);

		blades = new ModelRenderer(this);
		blades.setPos(22.0F, 10.0F, -24.0F);
		axe1.addChild(blades);
		

		crusher1 = new ModelRenderer(this);
		crusher1.setPos(-22.0F, -10.0F, 24.0F);
		blades.addChild(crusher1);
		crusher1.texOffs(14, 0).addBox(2.0F, -7.0F, -5.0F, 2.0F, 12.0F, 9.0F, 0.0F, false);
		crusher1.texOffs(0, 0).addBox(2.0F, -7.0F, 4.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		crusher2 = new ModelRenderer(this);
		crusher2.setPos(-16.0F, -10.0F, 24.0F);
		blades.addChild(crusher2);
		setRotationAngle(crusher2, -0.7854F, 0.0F, 0.0F);
		crusher2.texOffs(14, 0).addBox(0.0F, -7.0F, -5.0F, 2.0F, 12.0F, 9.0F, 0.0F, false);
		crusher2.texOffs(0, 0).addBox(0.0F, -7.0F, 4.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		crusher3 = new ModelRenderer(this);
		crusher3.setPos(-12.0F, -10.0F, 24.0F);
		blades.addChild(crusher3);
		setRotationAngle(crusher3, -1.5708F, 0.0F, 0.0F);
		crusher3.texOffs(14, 0).addBox(0.0F, -7.0F, -5.0F, 2.0F, 12.0F, 9.0F, 0.0F, false);
		crusher3.texOffs(0, 0).addBox(0.0F, -7.0F, 4.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		crusher4 = new ModelRenderer(this);
		crusher4.setPos(-8.0F, -10.0F, 24.0F);
		blades.addChild(crusher4);
		setRotationAngle(crusher4, -2.3562F, 0.0F, 0.0F);
		crusher4.texOffs(14, 0).addBox(0.0F, -7.0F, -5.0F, 2.0F, 12.0F, 9.0F, 0.0F, false);
		crusher4.texOffs(0, 0).addBox(0.0F, -7.0F, 4.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		crusher5 = new ModelRenderer(this);
		crusher5.setPos(-4.0F, -10.0F, 24.0F);
		blades.addChild(crusher5);
		setRotationAngle(crusher5, 3.1416F, 0.0F, 0.0F);
		crusher5.texOffs(14, 0).addBox(0.0F, -7.0F, -5.0F, 2.0F, 12.0F, 9.0F, 0.0F, false);
		crusher5.texOffs(0, 0).addBox(0.0F, -7.0F, 4.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		crusher6 = new ModelRenderer(this);
		crusher6.setPos(0.0F, -10.0F, 24.0F);
		blades.addChild(crusher6);
		setRotationAngle(crusher6, 2.3562F, 0.0F, 0.0F);
		crusher6.texOffs(14, 0).addBox(0.0F, -7.0F, -5.0F, 2.0F, 12.0F, 9.0F, 0.0F, false);
		crusher6.texOffs(0, 0).addBox(0.0F, -7.0F, 4.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		axe2 = new ModelRenderer(this);
		axe2.setPos(8.0F, -23.0F, 13.0F);
		root.addChild(axe2);
		axe2.texOffs(0, 23).addBox(-28.0F, -2.0F, -2.0F, 28.0F, 4.0F, 4.0F, 0.0F, true);

		blades2 = new ModelRenderer(this);
		blades2.setPos(-22.0F, 10.0F, 24.0F);
		axe2.addChild(blades2);
		

		crusher7 = new ModelRenderer(this);
		crusher7.setPos(22.0F, -10.0F, -24.0F);
		blades2.addChild(crusher7);
		crusher7.texOffs(14, 0).addBox(-4.0F, -7.0F, -4.0F, 2.0F, 12.0F, 9.0F, 0.0F, true);
		crusher7.texOffs(0, 0).addBox(-4.0F, -7.0F, -7.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);

		crusher8 = new ModelRenderer(this);
		crusher8.setPos(16.0F, -10.0F, -24.0F);
		blades2.addChild(crusher8);
		setRotationAngle(crusher8, 0.7854F, 0.0F, 0.0F);
		crusher8.texOffs(14, 0).addBox(-2.0F, -7.0F, -4.0F, 2.0F, 12.0F, 9.0F, 0.0F, true);
		crusher8.texOffs(0, 0).addBox(-2.0F, -7.0F, -7.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);

		crusher9 = new ModelRenderer(this);
		crusher9.setPos(12.0F, -10.0F, -24.0F);
		blades2.addChild(crusher9);
		setRotationAngle(crusher9, 1.5708F, 0.0F, 0.0F);
		crusher9.texOffs(14, 0).addBox(-2.0F, -7.0F, -4.0F, 2.0F, 12.0F, 9.0F, 0.0F, true);
		crusher9.texOffs(0, 0).addBox(-2.0F, -7.0F, -7.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);

		crusher10 = new ModelRenderer(this);
		crusher10.setPos(8.0F, -10.0F, -24.0F);
		blades2.addChild(crusher10);
		setRotationAngle(crusher10, 2.3562F, 0.0F, 0.0F);
		crusher10.texOffs(14, 0).addBox(-2.0F, -7.0F, -4.0F, 2.0F, 12.0F, 9.0F, 0.0F, true);
		crusher10.texOffs(0, 0).addBox(-2.0F, -7.0F, -7.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);

		crusher11 = new ModelRenderer(this);
		crusher11.setPos(4.0F, -10.0F, -24.0F);
		blades2.addChild(crusher11);
		setRotationAngle(crusher11, -3.1416F, 0.0F, 0.0F);
		crusher11.texOffs(14, 0).addBox(-2.0F, -7.0F, -4.0F, 2.0F, 12.0F, 9.0F, 0.0F, true);
		crusher11.texOffs(0, 0).addBox(-2.0F, -7.0F, -7.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);

		crusher12 = new ModelRenderer(this);
		crusher12.setPos(0.0F, -10.0F, -24.0F);
		blades2.addChild(crusher12);
		setRotationAngle(crusher12, -2.3562F, 0.0F, 0.0F);
		crusher12.texOffs(14, 0).addBox(-2.0F, -7.0F, -4.0F, 2.0F, 12.0F, 9.0F, 0.0F, true);
		crusher12.texOffs(0, 0).addBox(-2.0F, -7.0F, -7.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);
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
	
	public ModelRenderer getAxe1() {
		return axe1;
	}
	
	public ModelRenderer getAxe2() {
		return axe2;
	}

	public void setRotationState(float rotation) {
		float rot1 = (float) ((rotation / 360) * Math.PI * 2);
		float rot2 = (float) ((rotation / 360) * Math.PI * 2);
		setRotationAngle(this.axe1, -rot1, 0, 0);
		setRotationAngle(this.axe2, rot2, 0, 0);
	}
	
}