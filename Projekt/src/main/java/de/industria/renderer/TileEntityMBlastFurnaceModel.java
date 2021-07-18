package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMBlastFurnaceModel extends EntityModel<Entity> {
	
	private final ModelRenderer bb_main;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;

	public TileEntityMBlastFurnaceModel() {
		texWidth = 256;
		texHeight = 256;

		bb_main = new ModelRenderer(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		bb_main.texOffs(0, 0).addBox(-24.0F, -8.0F, -24.0F, 48.0F, 8.0F, 48.0F, 0.0F, false);
		bb_main.texOffs(0, 56).addBox(-16.0F, -40.0F, -16.0F, 32.0F, 32.0F, 32.0F, 0.0F, false);
		bb_main.texOffs(108, 100).addBox(-10.0F, -80.0F, -10.0F, 20.0F, 40.0F, 20.0F, 0.0F, false);
		bb_main.texOffs(40, 161).addBox(-16.0F, -79.0F, 14.0F, 2.0F, 39.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(32, 161).addBox(14.0F, -79.0F, 14.0F, 2.0F, 39.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(24, 161).addBox(-16.0F, -79.0F, -16.0F, 2.0F, 39.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(16, 161).addBox(14.0F, -79.0F, -16.0F, 2.0F, 39.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(128, 90).addBox(-14.0F, -79.0F, -16.0F, 28.0F, 2.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(128, 86).addBox(-14.0F, -79.0F, 14.0F, 28.0F, 2.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(144, 0).addBox(-16.0F, -79.0F, -14.0F, 2.0F, 2.0F, 28.0F, 0.0F, false);
		bb_main.texOffs(45, 120).addBox(14.0F, -79.0F, -14.0F, 2.0F, 2.0F, 28.0F, 0.0F, false);
		bb_main.texOffs(130, 160).addBox(-8.0F, -16.0F, -24.0F, 16.0F, 8.0F, 16.0F, 0.0F, false);
		bb_main.texOffs(66, 160).addBox(-24.0F, -16.0F, -24.0F, 16.0F, 8.0F, 16.0F, 0.0F, false);
		bb_main.texOffs(0, 0).addBox(-19.0F, -38.0F, -2.0F, 3.0F, 16.0F, 16.0F, 0.0F, false);
		bb_main.texOffs(38, 11).addBox(-18.0F, -26.0F, -4.0F, 1.0F, 18.0F, 1.0F, 0.0F, false);
		bb_main.texOffs(0, 0).addBox(-18.0F, -27.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(0, 56).addBox(-15.0F, -42.0F, -21.0F, 4.0F, 26.0F, 4.0F, 0.0F, false);
		bb_main.texOffs(0, 40).addBox(-15.0F, -46.0F, -21.0F, 19.0F, 4.0F, 4.0F, 0.0F, false);
		bb_main.texOffs(22, 0).addBox(0.0F, -46.0F, -17.0F, 4.0F, 4.0F, 7.0F, 0.0F, false);
		bb_main.texOffs(0, 161).addBox(-20.0F, -56.0F, -21.0F, 4.0F, 40.0F, 4.0F, 0.0F, false);
		bb_main.texOffs(0, 120).addBox(-20.0F, -60.0F, -21.0F, 4.0F, 4.0F, 37.0F, 0.0F, false);
		bb_main.texOffs(0, 32).addBox(-20.0F, -60.0F, 16.0F, 20.0F, 4.0F, 4.0F, 0.0F, false);
		bb_main.texOffs(0, 120).addBox(-4.0F, -60.0F, 10.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(16.9623F, -40.0F, -17.553F);
		bb_main.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, -0.7854F, 0.0F);
		cube_r1.texOffs(96, 71).addBox(-20.5F, -21.0F, 23.0F, 42.0F, 2.0F, 3.0F, 0.0F, false);
		cube_r1.texOffs(96, 76).addBox(-20.5F, -38.0F, 23.0F, 42.0F, 2.0F, 3.0F, 0.0F, false);
		cube_r1.texOffs(96, 81).addBox(-20.5F, -5.0F, 23.0F, 42.0F, 2.0F, 3.0F, 0.0F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(16.9623F, -40.0F, -17.553F);
		bb_main.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.7854F, 0.0F);
		cube_r2.texOffs(96, 56).addBox(-45.5F, -21.0F, -1.0F, 43.0F, 2.0F, 3.0F, 0.0F, false);
		cube_r2.texOffs(96, 61).addBox(-45.5F, -38.0F, -1.0F, 43.0F, 2.0F, 3.0F, 0.0F, false);
		cube_r2.texOffs(96, 66).addBox(-45.5F, -5.0F, -1.0F, 43.0F, 2.0F, 3.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
}