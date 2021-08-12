// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class custom_model extends EntityModel<Entity> {
	private final ModelRenderer stick_insect;
	private final ModelRenderer left_legs;
	private final ModelRenderer left_front_leg;
	private final ModelRenderer left_center_leg;
	private final ModelRenderer left_back_leg;
	private final ModelRenderer right_legs;
	private final ModelRenderer right_front_leg;
	private final ModelRenderer right_back_leg;
	private final ModelRenderer right_center_leg;
	private final ModelRenderer left_sensor;
	private final ModelRenderer right_sensor;

	public custom_model() {
		textureWidth = 32;
		textureHeight = 32;

		stick_insect = new ModelRenderer(this);
		stick_insect.setRotationPoint(0.0F, 23.0F, -4.0F);
		stick_insect.setTextureOffset(0, 0).addBox(-0.5F, -0.5F, -4.0F, 1.0F, 1.0F, 8.0F, 0.0F, false);

		left_legs = new ModelRenderer(this);
		left_legs.setRotationPoint(0.5F, 0.0F, -0.5F);
		stick_insect.addChild(left_legs);
		

		left_front_leg = new ModelRenderer(this);
		left_front_leg.setRotationPoint(0.0F, 0.0F, -2.0F);
		left_legs.addChild(left_front_leg);
		setRotationAngle(left_front_leg, 0.0F, 0.0F, 0.3491F);
		left_front_leg.setTextureOffset(0, 4).addBox(0.0F, 0.0F, -1.5F, 3.0F, 0.0F, 2.0F, 0.0F, false);

		left_center_leg = new ModelRenderer(this);
		left_center_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
		left_legs.addChild(left_center_leg);
		setRotationAngle(left_center_leg, 0.0F, 0.0F, 0.3491F);
		left_center_leg.setTextureOffset(6, 9).addBox(0.0F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, 0.0F, false);

		left_back_leg = new ModelRenderer(this);
		left_back_leg.setRotationPoint(0.0F, 0.0F, 2.0F);
		left_legs.addChild(left_back_leg);
		setRotationAngle(left_back_leg, 0.0F, 0.0F, 0.3491F);
		left_back_leg.setTextureOffset(0, 6).addBox(0.0F, 0.0F, -0.5F, 3.0F, 0.0F, 2.0F, 0.0F, false);

		right_legs = new ModelRenderer(this);
		right_legs.setRotationPoint(-0.5F, 0.0F, -0.5F);
		stick_insect.addChild(right_legs);
		

		right_front_leg = new ModelRenderer(this);
		right_front_leg.setRotationPoint(0.0F, 0.0F, -2.0F);
		right_legs.addChild(right_front_leg);
		setRotationAngle(right_front_leg, 0.0F, 0.0F, -0.3491F);
		right_front_leg.setTextureOffset(0, 2).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 0.0F, 2.0F, 0.0F, false);

		right_back_leg = new ModelRenderer(this);
		right_back_leg.setRotationPoint(0.0F, 0.0F, 2.0F);
		right_legs.addChild(right_back_leg);
		setRotationAngle(right_back_leg, 0.0F, 0.0F, -0.3491F);
		right_back_leg.setTextureOffset(0, 0).addBox(-3.0F, 0.0F, -0.5F, 3.0F, 0.0F, 2.0F, 0.0F, false);

		right_center_leg = new ModelRenderer(this);
		right_center_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
		right_legs.addChild(right_center_leg);
		setRotationAngle(right_center_leg, 0.0F, 0.0F, -0.3491F);
		right_center_leg.setTextureOffset(0, 9).addBox(-3.0F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, 0.0F, false);

		left_sensor = new ModelRenderer(this);
		left_sensor.setRotationPoint(0.5F, -0.5F, -3.5F);
		stick_insect.addChild(left_sensor);
		setRotationAngle(left_sensor, 0.0F, 0.0F, 0.7854F);
		left_sensor.setTextureOffset(0, 1).addBox(0.0F, -1.0F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);

		right_sensor = new ModelRenderer(this);
		right_sensor.setRotationPoint(-0.5F, -0.5F, -3.5F);
		stick_insect.addChild(right_sensor);
		setRotationAngle(right_sensor, 0.0F, 0.0F, -0.7854F);
		right_sensor.setTextureOffset(0, 0).addBox(0.0F, -1.0F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		stick_insect.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}