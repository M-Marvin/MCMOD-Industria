// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class custom_model extends EntityModel<Entity> {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer right_leg;
	private final ModelRenderer left_leg;
	private final ModelRenderer left_front_leg;
	private final ModelRenderer right_front_leg;

	public custom_model() {
		textureWidth = 32;
		textureHeight = 32;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 21.5F, -3.0F);
		head.setTextureOffset(0, 9).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
		head.setTextureOffset(12, 12).addBox(-0.5F, 0.0F, -2.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(13, 3).addBox(-2.0F, -2.0F, -1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(0, 3).addBox(1.0F, -2.0F, -1.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.setTextureOffset(9, 9).addBox(-1.5F, -1.0F, -2.5F, 3.0F, 3.0F, 0.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 21.5F, -3.0F);
		body.setTextureOffset(8, 15).addBox(-0.5F, 0.5F, 5.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		body.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 5.0F, 0.0F, false);

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-1.0F, 0.5F, 4.0F);
		body.addChild(right_leg);
		right_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(1.0F, 0.5F, 4.0F);
		body.addChild(left_leg);
		left_leg.setTextureOffset(0, 15).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		left_front_leg = new ModelRenderer(this);
		left_front_leg.setRotationPoint(1.0F, 0.5F, 1.0F);
		body.addChild(left_front_leg);
		left_front_leg.setTextureOffset(4, 15).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		right_front_leg = new ModelRenderer(this);
		right_front_leg.setRotationPoint(-1.0F, 0.5F, 1.0F);
		body.addChild(right_front_leg);
		right_front_leg.setTextureOffset(13, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		body.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}