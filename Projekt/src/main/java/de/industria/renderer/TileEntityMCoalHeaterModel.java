package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMCoalHeaterModel extends EntityModel<Entity> {
	private final ModelRenderer bb_main;

	public TileEntityMCoalHeaterModel() {
		texWidth = 256;
		texHeight = 256;

		bb_main = new ModelRenderer(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		bb_main.texOffs(0, 33).addBox(-24.0F, -1.0F, -8.0F, 32.0F, 1.0F, 32.0F, 0.0F, false);
		bb_main.texOffs(0, 16).addBox(-24.0F, -15.0F, -8.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(0, 66).addBox(-21.0F, -15.0F, -5.0F, 26.0F, 9.0F, 26.0F, 0.0F, false);
		bb_main.texOffs(78, 75).addBox(-18.0F, -6.0F, -5.0F, 20.0F, 5.0F, 26.0F, 0.0F, false);
		bb_main.texOffs(0, 0).addBox(-24.0F, -16.0F, -8.0F, 32.0F, 1.0F, 32.0F, 0.0F, false);
		bb_main.texOffs(14, 14).addBox(6.0F, -15.0F, -8.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(8, 0).addBox(6.0F, -15.0F, 22.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(0, 0).addBox(-24.0F, -15.0F, 22.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
		bb_main.texOffs(96, 33).addBox(-7.0F, -14.0F, -8.0F, 13.0F, 13.0F, 14.0F, 0.0F, false);
		bb_main.texOffs(96, 0).addBox(-22.0F, -14.0F, -8.0F, 13.0F, 13.0F, 14.0F, 0.0F, false);
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