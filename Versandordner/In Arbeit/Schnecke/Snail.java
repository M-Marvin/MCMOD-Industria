// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class Snail extends EntityModel<Entity> {
	private final ModelRenderer head;
	private final ModelRenderer eye_left;
	private final ModelRenderer eye_right;
	private final ModelRenderer sensor_left;
	private final ModelRenderer sensor_right;

	public Snail() {
		textureWidth = 32;
		textureHeight = 32;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 22.0F, -3.0F);
		head.setTextureOffset(15, 20).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		eye_left = new ModelRenderer(this);
		eye_left.setRotationPoint(0.0F, -2.0F, -4.0F);
		head.addChild(eye_left);
		setRotationAngle(eye_left, 0.2618F, 0.2618F, 0.0F);
		eye_left.setTextureOffset(0, 26).addBox(-2.0F, -3.0341F, -0.2588F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		eye_right = new ModelRenderer(this);
		eye_right.setRotationPoint(0.0F, -2.0F, -4.0F);
		head.addChild(eye_right);
		setRotationAngle(eye_right, 0.2618F, -0.2618F, 0.0F);
		eye_right.setTextureOffset(0, 21).addBox(1.0F, -3.0341F, -0.2588F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		sensor_left = new ModelRenderer(this);
		sensor_left.setRotationPoint(0.0F, 2.0F, -5.0F);
		head.addChild(sensor_left);
		setRotationAngle(sensor_left, 0.0F, 0.2618F, 0.0F);
		sensor_left.setTextureOffset(5, 29).addBox(0.5F, -1.0F, 0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		sensor_right = new ModelRenderer(this);
		sensor_right.setRotationPoint(0.0F, 2.0F, -5.0F);
		head.addChild(sensor_right);
		setRotationAngle(sensor_right, 0.0F, -0.2618F, 0.0F);
		sensor_right.setTextureOffset(5, 27).addBox(-1.5F, -1.0F, 0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}