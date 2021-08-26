// Made with Blockbench 3.9.3
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


public class TileEntityMMetalFormerModel extends EntityModel<Entity> {
	private final ModelRenderer root;
	private final ModelRenderer cube_r1;
	private final ModelRenderer saw;
	private final ModelRenderer roll1;
	private final ModelRenderer roll2;

	public TileEntityMMetalFormerModel() {
		textureWidth = 128;
		textureHeight = 128;

		root = new ModelRenderer(this);
		root.setRotationPoint(0.0F, 24.0F, 16.0F);
		root.setTextureOffset(0, 0).addBox(7.5F, -15.0F, -16.0F, 0.0F, 9.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(68, 47).addBox(-7.0F, -6.0F, 0.0F, 14.0F, 6.0F, 8.0F, 0.0F, false);
		root.setTextureOffset(34, 39).addBox(-7.0F, -15.0F, 7.0F, 14.0F, 9.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-7.0F, -15.0F, -24.0F, 14.0F, 9.0F, 1.0F, 0.0F, false);
		root.setTextureOffset(68, 33).addBox(-7.0F, -6.0F, -24.0F, 14.0F, 6.0F, 8.0F, 0.0F, false);
		root.setTextureOffset(34, 39).addBox(-8.0F, -6.0F, -24.0F, 1.0F, 6.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(36, 77).addBox(-8.0F, -15.0F, 0.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.setTextureOffset(18, 71).addBox(-8.0F, -15.0F, -24.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.setTextureOffset(0, 42).addBox(7.0F, -15.0F, 0.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.setTextureOffset(0, 71).addBox(7.0F, -15.0F, -24.0F, 1.0F, 9.0F, 8.0F, 0.0F, false);
		root.setTextureOffset(0, 33).addBox(7.0F, -6.0F, -24.0F, 1.0F, 6.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(64, 0).addBox(-7.0F, -1.0F, -16.0F, 14.0F, 1.0F, 16.0F, 0.0F, false);
		root.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -24.0F, 16.0F, 1.0F, 32.0F, 0.0F, false);
		root.setTextureOffset(0, 17).addBox(-7.5F, -15.0F, -16.0F, 0.0F, 9.0F, 16.0F, 0.0F, false);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-7.0F, -6.0F, -16.0F);
		root.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.3927F, 0.0F, 0.0F);
		cube_r1.setTextureOffset(64, 17).addBox(0.0F, 0.0F, 0.0F, 14.0F, 1.0F, 13.0F, 0.0F, false);

		saw = new ModelRenderer(this);
		saw.setRotationPoint(0.0F, -7.0F, -20.0F);
		root.addChild(saw);
		saw.setTextureOffset(0, 4).addBox(0.0F, -3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F, false);

		roll1 = new ModelRenderer(this);
		roll1.setRotationPoint(0.0F, -4.5F, -2.5F);
		root.addChild(roll1);
		roll1.setTextureOffset(68, 61).addBox(-7.0F, -1.5F, -1.5F, 14.0F, 3.0F, 3.0F, 0.0F, false);

		roll2 = new ModelRenderer(this);
		roll2.setRotationPoint(0.0F, -9.5F, -2.5F);
		root.addChild(roll2);
		roll2.setTextureOffset(34, 33).addBox(-7.0F, -1.5F, -1.5F, 14.0F, 3.0F, 3.0F, 0.0F, false);
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