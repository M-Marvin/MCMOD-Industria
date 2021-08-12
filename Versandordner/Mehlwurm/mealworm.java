// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class custom_model extends EntityModel<Entity> {
	private final ModelRenderer mealworm;
	private final ModelRenderer head;
	private final ModelRenderer right_front_leg;
	private final ModelRenderer left_front_leg;
	private final ModelRenderer right_tool;
	private final ModelRenderer left_tool;
	private final ModelRenderer body;
	private final ModelRenderer left_leg;
	private final ModelRenderer tail;
	private final ModelRenderer right_leg;

	public custom_model() {
		textureWidth = 32;
		textureHeight = 32;

		mealworm = new ModelRenderer(this);
		mealworm.setRotationPoint(0.0F, 23.0F, -8.0F);
		

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -0.5F, 4.0F);
		mealworm.addChild(head);
		head.setTextureOffset(0, 9).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);

		right_front_leg = new ModelRenderer(this);
		right_front_leg.setRotationPoint(-1.0F, -0.5F, -1.5F);
		head.addChild(right_front_leg);
		setRotationAngle(right_front_leg, 0.0F, 0.0F, 0.3491F);
		right_front_leg.setTextureOffset(0, 2).addBox(0.0F, 0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);

		left_front_leg = new ModelRenderer(this);
		left_front_leg.setRotationPoint(1.0F, -0.5F, -1.5F);
		head.addChild(left_front_leg);
		setRotationAngle(left_front_leg, 0.0F, 0.0F, -0.3491F);
		left_front_leg.setTextureOffset(2, 0).addBox(0.0F, 0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);

		right_tool = new ModelRenderer(this);
		right_tool.setRotationPoint(-0.5F, 0.5F, -3.0F);
		head.addChild(right_tool);
		right_tool.setTextureOffset(2, 4).addBox(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);

		left_tool = new ModelRenderer(this);
		left_tool.setRotationPoint(0.5F, 0.5F, -3.0F);
		head.addChild(left_tool);
		left_tool.setTextureOffset(0, 4).addBox(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, -0.5F, 4.0F);
		mealworm.addChild(body);
		body.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 7.0F, 0.0F, false);

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(1.0F, -0.5F, 0.5F);
		body.addChild(left_leg);
		setRotationAngle(left_leg, 0.0F, 0.0F, -0.3491F);
		left_leg.setTextureOffset(2, 2).addBox(0.0F, 0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 0.0F, 7.0F);
		body.addChild(tail);
		tail.setTextureOffset(10, 10).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-1.0F, -0.5F, 0.5F);
		body.addChild(right_leg);
		setRotationAngle(right_leg, 0.0F, 0.0F, 0.3491F);
		right_leg.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		mealworm.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}