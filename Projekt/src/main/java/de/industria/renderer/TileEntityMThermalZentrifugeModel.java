package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMThermalZentrifugeModel extends EntityModel<Entity> {
	private final ModelRenderer bb_main;

	public TileEntityMThermalZentrifugeModel() {
		texWidth = 256;
		texHeight = 256;
		
		bb_main = new ModelRenderer(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		bb_main.texOffs(0, 88).addBox(-24.0F, -16.0F, 8.0F, 32.0F, 16.0F, 16.0F, 0.0F, false);
		bb_main.texOffs(0, 0).addBox(-24.0F, -32.0F, -8.0F, 32.0F, 16.0F, 32.0F, 0.0F, false);
		bb_main.texOffs(113, 33).addBox(-23.0F, -16.0F, -8.0F, 14.0F, 16.0F, 16.0F, 0.0F, false);
		bb_main.texOffs(97, 88).addBox(-7.0F, -16.0F, -8.0F, 14.0F, 16.0F, 16.0F, 0.0F, false);
		bb_main.texOffs(0, 49).addBox(-23.0F, -40.0F, -7.0F, 30.0F, 8.0F, 30.0F, 0.0F, false);
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