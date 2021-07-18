package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.9.1
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports

public class TileEntityMOreWashingPlantModel extends EntityModel<Entity> {
	
	private final ModelRenderer root;
	private final ModelRenderer cube_r1;
	private final ModelRenderer conveyor;
	private final ModelRenderer piston2;
	private final ModelRenderer piston1;
	private final ModelRenderer filter;
	private final ModelRenderer cube_r2;
	private final ModelRenderer pipes;
	
	public TileEntityMOreWashingPlantModel() {
		texWidth = 256;
		texHeight = 256;

		root = new ModelRenderer(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		root.texOffs(0, 0).addBox(-56.0F, -16.0F, -8.0F, 64.0F, 16.0F, 48.0F, 0.0F, false);
		root.texOffs(120, 135).addBox(1.0F, -26.0F, 24.0F, 7.0F, 10.0F, 16.0F, 0.0F, false);
		root.texOffs(120, 135).addBox(1.0F, -26.0F, 24.0F, 7.0F, 10.0F, 16.0F, 0.0F, false);
		root.texOffs(36, 127).addBox(-15.0F, -18.0F, 13.0F, 14.0F, 2.0F, 11.0F, 0.0F, false);
		root.texOffs(0, 127).addBox(-40.0F, -35.0F, 24.0F, 10.0F, 19.0F, 16.0F, 0.0F, false);
		root.texOffs(128, 64).addBox(-48.0F, -27.0F, 24.0F, 8.0F, 11.0F, 15.0F, 0.0F, false);
		root.texOffs(96, 64).addBox(-44.0F, -30.0F, 27.0F, 3.0F, 3.0F, 5.0F, 0.0F, false);
		root.texOffs(30, 36).addBox(-44.0F, -33.0F, 27.0F, 4.0F, 3.0F, 5.0F, 0.0F, false);
		root.texOffs(0, 64).addBox(-40.0F, -40.0F, 16.0F, 8.0F, 18.0F, 8.0F, 0.0F, false);
		root.texOffs(80, 70).addBox(-32.0F, -40.0F, -8.0F, 8.0F, 24.0F, 32.0F, 0.0F, false);
		root.texOffs(0, 64).addBox(-56.0F, -22.0F, -8.0F, 24.0F, 6.0F, 32.0F, 0.0F, false);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.0873F, 0.0F, 0.0F);
		cube_r1.texOffs(0, 44).addBox(-40.0F, -39.0F, -11.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
		cube_r1.texOffs(72, 126).addBox(-40.0F, -36.0F, -11.0F, 8.0F, 1.0F, 24.0F, 0.0F, false);

		conveyor = new ModelRenderer(this);
		conveyor.setPos(-36.5F, 5.0F, 2.0F);
		root.addChild(conveyor);
		setRotationAngle(conveyor, 0.0F, 0.0F, 0.2618F);
		conveyor.texOffs(0, 162).addBox(-23.5F, -39.0F, -8.0F, 10.0F, 18.0F, 1.0F, 0.0F, false);
		conveyor.texOffs(112, 126).addBox(-23.5F, -39.0F, 11.0F, 10.0F, 18.0F, 1.0F, 0.0F, false);

		piston2 = new ModelRenderer(this);
		piston2.setPos(18.5F, 6.0F, -2.0F);
		conveyor.addChild(piston2);
		piston2.texOffs(142, 108).addBox(-38.0F, -36.0F, -5.0F, 3.0F, 9.0F, 18.0F, 0.0F, false);

		piston1 = new ModelRenderer(this);
		piston1.setPos(18.5F, 6.0F, -2.0F);
		conveyor.addChild(piston1);
		piston1.texOffs(0, 0).addBox(-35.0F, -45.0F, -5.0F, 3.0F, 18.0F, 18.0F, 0.0F, false);

		filter = new ModelRenderer(this);
		filter.setPos(-30.0F, -28.1667F, 32.0F);
		root.addChild(filter);
		setRotationAngle(filter, 0.0F, 0.0F, 0.3054F);
		filter.texOffs(0, 110).addBox(0.0F, 0.1667F, -8.0F, 32.0F, 1.0F, 16.0F, 0.0F, false);
		filter.texOffs(0, 36).addBox(0.0F, -3.8333F, -8.0F, 16.0F, 4.0F, 1.0F, 0.0F, false);
		filter.texOffs(0, 64).addBox(29.0F, -3.8333F, -7.9F, 3.0F, 4.0F, 1.0F, 0.0F, false);
		filter.texOffs(104, 152).addBox(23.0F, -0.8333F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		filter.texOffs(88, 151).addBox(20.0F, -0.8333F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		filter.texOffs(150, 135).addBox(17.0F, -0.8333F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		filter.texOffs(80, 79).addBox(29.0F, -0.8333F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		filter.texOffs(80, 64).addBox(26.0F, -0.8333F, -7.0F, 1.0F, 1.0F, 14.0F, 0.0F, false);
		filter.texOffs(0, 102).addBox(0.0F, -3.8333F, 6.9F, 32.0F, 4.0F, 1.0F, 0.0F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(22.5F, 0.1667F, -8.0F);
		filter.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.48F, 0.0F, 0.0F);
		cube_r2.texOffs(0, 41).addBox(-6.5F, 0.0F, -2.0F, 13.0F, 1.0F, 2.0F, 0.0F, false);

		pipes = new ModelRenderer(this);
		pipes.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(pipes);
		pipes.texOffs(160, 98).addBox(-24.0F, -38.0F, -4.0F, 9.0F, 4.0F, 4.0F, 0.0F, false);
		pipes.texOffs(134, 161).addBox(-15.0F, -38.0F, 3.0F, 4.0F, 22.0F, 4.0F, 0.0F, false);
		pipes.texOffs(52, 140).addBox(-24.0F, -38.0F, 11.0F, 9.0F, 4.0F, 4.0F, 0.0F, false);
		pipes.texOffs(0, 107).addBox(-15.0F, -38.0F, 11.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);
		pipes.texOffs(128, 90).addBox(-15.0F, -31.0F, 11.0F, 21.0F, 4.0F, 4.0F, 0.0F, false);
		pipes.texOffs(0, 0).addBox(2.0F, -27.0F, 11.0F, 4.0F, 11.0F, 4.0F, 0.0F, false);
		pipes.texOffs(159, 64).addBox(-24.0F, -38.0F, 3.0F, 9.0F, 4.0F, 4.0F, 0.0F, false);
		pipes.texOffs(150, 161).addBox(-15.0F, -38.0F, -4.0F, 4.0F, 22.0F, 4.0F, 0.0F, false);
		pipes.texOffs(52, 151).addBox(-3.0F, -32.0F, -5.0F, 9.0F, 16.0F, 9.0F, 0.0F, false);
		pipes.texOffs(24, 0).addBox(-3.0F, -27.0F, 4.0F, 6.0F, 11.0F, 6.0F, 0.0F, false);
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