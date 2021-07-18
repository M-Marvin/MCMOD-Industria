package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TileEntityMSchredderModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;
	
	public TileEntityMSchredderModel() {
		
		texWidth = 128;
		texHeight = 128;

		root = new ModelRenderer(this);
		root.setPos(-2.0F, 24.0F, 1.0F);
		root.texOffs(0, 82).addBox(-22.0F, -14.0F, -9.0F, 32.0F, 14.0F, 32.0F, 0.0F, false);
		root.texOffs(0, 0).addBox(8.0F, -32.0F, -9.0F, 2.0F, 18.0F, 32.0F, 0.0F, false);
		root.texOffs(70, 0).addBox(-20.0F, -30.0F, 22.0F, 28.0F, 16.0F, 1.0F, 0.0F, false);
		root.texOffs(0, 62).addBox(-20.0F, -30.0F, -9.0F, 28.0F, 16.0F, 1.0F, 0.0F, false);
		root.texOffs(60, 29).addBox(-22.0F, -32.0F, -9.0F, 2.0F, 18.0F, 32.0F, 0.0F, false);
		
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
		
}