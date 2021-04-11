package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class TileEntityMSteamGeneratorModel extends EntityModel<Entity> {
	private final ModelRenderer root;
	private final ModelRenderer shaft;
	private final ModelRenderer turbin1;
	private final ModelRenderer blade;
	private final ModelRenderer part;
	private final ModelRenderer blade2;
	private final ModelRenderer part2;
	private final ModelRenderer blade3;
	private final ModelRenderer part3;
	private final ModelRenderer blade4;
	private final ModelRenderer part4;
	private final ModelRenderer blade5;
	private final ModelRenderer part5;
	private final ModelRenderer blade6;
	private final ModelRenderer part6;
	private final ModelRenderer blade7;
	private final ModelRenderer part7;
	private final ModelRenderer blade8;
	private final ModelRenderer part8;
	private final ModelRenderer blade9;
	private final ModelRenderer part9;
	private final ModelRenderer blade10;
	private final ModelRenderer part10;
	private final ModelRenderer blade11;
	private final ModelRenderer part11;
	private final ModelRenderer blade12;
	private final ModelRenderer part12;
	private final ModelRenderer blade13;
	private final ModelRenderer part13;
	private final ModelRenderer blade14;
	private final ModelRenderer part14;
	private final ModelRenderer blade15;
	private final ModelRenderer part15;
	private final ModelRenderer blade16;
	private final ModelRenderer part16;
	private final ModelRenderer blade17;
	private final ModelRenderer part17;
	private final ModelRenderer blade18;
	private final ModelRenderer part18;
	private final ModelRenderer turbin2;
	private final ModelRenderer blade19;
	private final ModelRenderer part19;
	private final ModelRenderer blade20;
	private final ModelRenderer part20;
	private final ModelRenderer blade21;
	private final ModelRenderer part21;
	private final ModelRenderer blade22;
	private final ModelRenderer part22;
	private final ModelRenderer blade23;
	private final ModelRenderer part23;
	private final ModelRenderer blade24;
	private final ModelRenderer part24;
	private final ModelRenderer blade25;
	private final ModelRenderer part25;
	private final ModelRenderer blade26;
	private final ModelRenderer part26;
	private final ModelRenderer blade27;
	private final ModelRenderer part27;
	private final ModelRenderer blade28;
	private final ModelRenderer part28;
	private final ModelRenderer blade29;
	private final ModelRenderer part29;
	private final ModelRenderer blade30;
	private final ModelRenderer part30;
	private final ModelRenderer blade31;
	private final ModelRenderer part31;
	private final ModelRenderer blade32;
	private final ModelRenderer part32;
	private final ModelRenderer blade33;
	private final ModelRenderer part33;
	private final ModelRenderer blade34;
	private final ModelRenderer part34;
	private final ModelRenderer blade35;
	private final ModelRenderer part35;
	private final ModelRenderer blade36;
	private final ModelRenderer part36;

	public TileEntityMSteamGeneratorModel() {

		textureWidth = 256;
		textureHeight = 256;

		root = new ModelRenderer(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		root.setTextureOffset(167, 33).addBox(-19.0F, -27.0F, -7.0F, 27.0F, 6.0F, 4.0F, 0.0F, false);
		root.setTextureOffset(0, 7).addBox(-40.0F, -48.0F, -8.0F, 48.0F, 48.0F, 32.0F, 0.0F, false);

		shaft = new ModelRenderer(this);
		shaft.setRotationPoint(-16.0F, -24.0F, -8.0F);
		root.addChild(shaft);
		

		turbin1 = new ModelRenderer(this);
		turbin1.setRotationPoint(0.0F, 0.0F, 8.0F);
		shaft.addChild(turbin1);
		turbin1.setTextureOffset(135, 0).addBox(-2.0F, -2.0F, -9.0F, 4.0F, 4.0F, 17.0F, 0.0F, false);

		blade = new ModelRenderer(this);
		blade.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade);
		

		part = new ModelRenderer(this);
		part.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade.addChild(part);
		setRotationAngle(part, -0.5236F, 0.0F, 0.0F);
		part.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade2 = new ModelRenderer(this);
		blade2.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade2);
		setRotationAngle(blade2, 0.0F, 0.0F, 0.3491F);
		

		part2 = new ModelRenderer(this);
		part2.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade2.addChild(part2);
		setRotationAngle(part2, -0.5236F, 0.0F, 0.0F);
		part2.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade3 = new ModelRenderer(this);
		blade3.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade3);
		setRotationAngle(blade3, 0.0F, 0.0F, 0.6981F);
		

		part3 = new ModelRenderer(this);
		part3.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade3.addChild(part3);
		setRotationAngle(part3, -0.5236F, 0.0F, 0.0F);
		part3.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade4 = new ModelRenderer(this);
		blade4.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade4);
		setRotationAngle(blade4, 0.0F, 0.0F, 1.0472F);
		

		part4 = new ModelRenderer(this);
		part4.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade4.addChild(part4);
		setRotationAngle(part4, -0.5236F, 0.0F, 0.0F);
		part4.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade5 = new ModelRenderer(this);
		blade5.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade5);
		setRotationAngle(blade5, 0.0F, 0.0F, 1.3963F);
		

		part5 = new ModelRenderer(this);
		part5.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade5.addChild(part5);
		setRotationAngle(part5, -0.5236F, 0.0F, 0.0F);
		part5.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade6 = new ModelRenderer(this);
		blade6.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade6);
		setRotationAngle(blade6, 0.0F, 0.0F, 1.7453F);
		

		part6 = new ModelRenderer(this);
		part6.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade6.addChild(part6);
		setRotationAngle(part6, -0.5236F, 0.0F, 0.0F);
		part6.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade7 = new ModelRenderer(this);
		blade7.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade7);
		setRotationAngle(blade7, 0.0F, 0.0F, 2.0944F);
		

		part7 = new ModelRenderer(this);
		part7.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade7.addChild(part7);
		setRotationAngle(part7, -0.5236F, 0.0F, 0.0F);
		part7.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade8 = new ModelRenderer(this);
		blade8.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade8);
		setRotationAngle(blade8, 0.0F, 0.0F, 2.4435F);
		

		part8 = new ModelRenderer(this);
		part8.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade8.addChild(part8);
		setRotationAngle(part8, -0.5236F, 0.0F, 0.0F);
		part8.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade9 = new ModelRenderer(this);
		blade9.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade9);
		setRotationAngle(blade9, 0.0F, 0.0F, 2.7925F);
		

		part9 = new ModelRenderer(this);
		part9.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade9.addChild(part9);
		setRotationAngle(part9, -0.5236F, 0.0F, 0.0F);
		part9.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade10 = new ModelRenderer(this);
		blade10.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade10);
		setRotationAngle(blade10, 0.0F, 0.0F, -3.1416F);
		

		part10 = new ModelRenderer(this);
		part10.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade10.addChild(part10);
		setRotationAngle(part10, -0.5236F, 0.0F, 0.0F);
		part10.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade11 = new ModelRenderer(this);
		blade11.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade11);
		setRotationAngle(blade11, 0.0F, 0.0F, -2.7925F);
		

		part11 = new ModelRenderer(this);
		part11.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade11.addChild(part11);
		setRotationAngle(part11, -0.5236F, 0.0F, 0.0F);
		part11.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade12 = new ModelRenderer(this);
		blade12.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade12);
		setRotationAngle(blade12, 0.0F, 0.0F, -2.4435F);
		

		part12 = new ModelRenderer(this);
		part12.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade12.addChild(part12);
		setRotationAngle(part12, -0.5236F, 0.0F, 0.0F);
		part12.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade13 = new ModelRenderer(this);
		blade13.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade13);
		setRotationAngle(blade13, 0.0F, 0.0F, -2.0944F);
		

		part13 = new ModelRenderer(this);
		part13.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade13.addChild(part13);
		setRotationAngle(part13, -0.5236F, 0.0F, 0.0F);
		part13.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade14 = new ModelRenderer(this);
		blade14.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade14);
		setRotationAngle(blade14, 0.0F, 0.0F, -1.7453F);
		

		part14 = new ModelRenderer(this);
		part14.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade14.addChild(part14);
		setRotationAngle(part14, -0.5236F, 0.0F, 0.0F);
		part14.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade15 = new ModelRenderer(this);
		blade15.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade15);
		setRotationAngle(blade15, 0.0F, 0.0F, -1.3963F);
		

		part15 = new ModelRenderer(this);
		part15.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade15.addChild(part15);
		setRotationAngle(part15, -0.5236F, 0.0F, 0.0F);
		part15.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade16 = new ModelRenderer(this);
		blade16.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade16);
		setRotationAngle(blade16, 0.0F, 0.0F, -1.0472F);
		

		part16 = new ModelRenderer(this);
		part16.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade16.addChild(part16);
		setRotationAngle(part16, -0.5236F, 0.0F, 0.0F);
		part16.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade17 = new ModelRenderer(this);
		blade17.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade17);
		setRotationAngle(blade17, 0.0F, 0.0F, -0.6981F);
		

		part17 = new ModelRenderer(this);
		part17.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade17.addChild(part17);
		setRotationAngle(part17, -0.5236F, 0.0F, 0.0F);
		part17.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade18 = new ModelRenderer(this);
		blade18.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin1.addChild(blade18);
		setRotationAngle(blade18, 0.0F, 0.0F, -0.3491F);
		

		part18 = new ModelRenderer(this);
		part18.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade18.addChild(part18);
		setRotationAngle(part18, -0.5236F, 0.0F, 0.0F);
		part18.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		turbin2 = new ModelRenderer(this);
		turbin2.setRotationPoint(0.0F, 0.0F, 24.0F);
		shaft.addChild(turbin2);
		setRotationAngle(turbin2, 0.0F, 0.0F, 0.1745F);
		turbin2.setTextureOffset(135, 0).addBox(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 17.0F, 0.0F, false);

		blade19 = new ModelRenderer(this);
		blade19.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade19);
		

		part19 = new ModelRenderer(this);
		part19.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade19.addChild(part19);
		setRotationAngle(part19, -0.5236F, 0.0F, 0.0F);
		part19.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade20 = new ModelRenderer(this);
		blade20.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade20);
		setRotationAngle(blade20, 0.0F, 0.0F, 0.3491F);
		

		part20 = new ModelRenderer(this);
		part20.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade20.addChild(part20);
		setRotationAngle(part20, -0.5236F, 0.0F, 0.0F);
		part20.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade21 = new ModelRenderer(this);
		blade21.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade21);
		setRotationAngle(blade21, 0.0F, 0.0F, 0.6981F);
		

		part21 = new ModelRenderer(this);
		part21.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade21.addChild(part21);
		setRotationAngle(part21, -0.5236F, 0.0F, 0.0F);
		part21.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade22 = new ModelRenderer(this);
		blade22.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade22);
		setRotationAngle(blade22, 0.0F, 0.0F, 1.0472F);
		

		part22 = new ModelRenderer(this);
		part22.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade22.addChild(part22);
		setRotationAngle(part22, -0.5236F, 0.0F, 0.0F);
		part22.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade23 = new ModelRenderer(this);
		blade23.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade23);
		setRotationAngle(blade23, 0.0F, 0.0F, 1.3963F);
		

		part23 = new ModelRenderer(this);
		part23.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade23.addChild(part23);
		setRotationAngle(part23, -0.5236F, 0.0F, 0.0F);
		part23.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade24 = new ModelRenderer(this);
		blade24.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade24);
		setRotationAngle(blade24, 0.0F, 0.0F, 1.7453F);
		

		part24 = new ModelRenderer(this);
		part24.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade24.addChild(part24);
		setRotationAngle(part24, -0.5236F, 0.0F, 0.0F);
		part24.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade25 = new ModelRenderer(this);
		blade25.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade25);
		setRotationAngle(blade25, 0.0F, 0.0F, 2.0944F);
		

		part25 = new ModelRenderer(this);
		part25.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade25.addChild(part25);
		setRotationAngle(part25, -0.5236F, 0.0F, 0.0F);
		part25.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade26 = new ModelRenderer(this);
		blade26.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade26);
		setRotationAngle(blade26, 0.0F, 0.0F, 2.4435F);
		

		part26 = new ModelRenderer(this);
		part26.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade26.addChild(part26);
		setRotationAngle(part26, -0.5236F, 0.0F, 0.0F);
		part26.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade27 = new ModelRenderer(this);
		blade27.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade27);
		setRotationAngle(blade27, 0.0F, 0.0F, 2.7925F);
		

		part27 = new ModelRenderer(this);
		part27.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade27.addChild(part27);
		setRotationAngle(part27, -0.5236F, 0.0F, 0.0F);
		part27.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade28 = new ModelRenderer(this);
		blade28.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade28);
		setRotationAngle(blade28, 0.0F, 0.0F, -3.1416F);
		

		part28 = new ModelRenderer(this);
		part28.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade28.addChild(part28);
		setRotationAngle(part28, -0.5236F, 0.0F, 0.0F);
		part28.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade29 = new ModelRenderer(this);
		blade29.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade29);
		setRotationAngle(blade29, 0.0F, 0.0F, -2.7925F);
		

		part29 = new ModelRenderer(this);
		part29.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade29.addChild(part29);
		setRotationAngle(part29, -0.5236F, 0.0F, 0.0F);
		part29.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade30 = new ModelRenderer(this);
		blade30.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade30);
		setRotationAngle(blade30, 0.0F, 0.0F, -2.4435F);
		

		part30 = new ModelRenderer(this);
		part30.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade30.addChild(part30);
		setRotationAngle(part30, -0.5236F, 0.0F, 0.0F);
		part30.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade31 = new ModelRenderer(this);
		blade31.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade31);
		setRotationAngle(blade31, 0.0F, 0.0F, -2.0944F);
		

		part31 = new ModelRenderer(this);
		part31.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade31.addChild(part31);
		setRotationAngle(part31, -0.5236F, 0.0F, 0.0F);
		part31.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade32 = new ModelRenderer(this);
		blade32.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade32);
		setRotationAngle(blade32, 0.0F, 0.0F, -1.7453F);
		

		part32 = new ModelRenderer(this);
		part32.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade32.addChild(part32);
		setRotationAngle(part32, -0.5236F, 0.0F, 0.0F);
		part32.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade33 = new ModelRenderer(this);
		blade33.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade33);
		setRotationAngle(blade33, 0.0F, 0.0F, -1.3963F);
		

		part33 = new ModelRenderer(this);
		part33.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade33.addChild(part33);
		setRotationAngle(part33, -0.5236F, 0.0F, 0.0F);
		part33.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade34 = new ModelRenderer(this);
		blade34.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade34);
		setRotationAngle(blade34, 0.0F, 0.0F, -1.0472F);
		

		part34 = new ModelRenderer(this);
		part34.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade34.addChild(part34);
		setRotationAngle(part34, -0.5236F, 0.0F, 0.0F);
		part34.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade35 = new ModelRenderer(this);
		blade35.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade35);
		setRotationAngle(blade35, 0.0F, 0.0F, -0.6981F);
		

		part35 = new ModelRenderer(this);
		part35.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade35.addChild(part35);
		setRotationAngle(part35, -0.5236F, 0.0F, 0.0F);
		part35.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);

		blade36 = new ModelRenderer(this);
		blade36.setRotationPoint(0.0F, 0.0F, 0.0F);
		turbin2.addChild(blade36);
		setRotationAngle(blade36, 0.0F, 0.0F, -0.3491F);
		

		part36 = new ModelRenderer(this);
		part36.setRotationPoint(-12.0F, -0.5F, 0.5F);
		blade36.addChild(part36);
		setRotationAngle(part36, -0.5236F, 0.0F, 0.0F);
		part36.setTextureOffset(0, 0).addBox(-8.0F, -2.5F, -0.5F, 20.0F, 5.0F, 1.0F, 0.0F, false);
		
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
	
	public void setTurbinRotation(float rotation) {
		
		this.shaft.rotateAngleZ = rotation;
		
	}
	
}