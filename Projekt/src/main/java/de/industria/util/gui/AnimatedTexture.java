package de.industria.util.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;

public class AnimatedTexture extends SimpleTexture implements ITickable {
	
	// TextureManager loading (Called from renderer)
	
	public static AnimatedTexture prepareTexture(ResourceLocation texture) {
		if (!(Minecraft.getInstance().getTextureManager().getTexture(texture) instanceof AnimatedTexture)) {
			AnimatedTexture animatedTexture = new AnimatedTexture(texture);
			Minecraft.getInstance().getTextureManager().register(texture, animatedTexture);
			return animatedTexture;
		} else {
			return (AnimatedTexture) Minecraft.getInstance().getTextureManager().getTexture(texture);
		}
	}
	
	// Animated Texture
	
	protected int frameTimer;
	protected int frame;
	protected int frameTime;
	protected int[] frames;
	protected int size;
	protected int textureHeight;
	
	protected AnimatedTexture(ResourceLocation textureResourceLocation) {
		super(textureResourceLocation);
	}
	
	public void load(IResourceManager manager) throws IOException {
		
	      SimpleTexture.TextureData simpletexture$texturedata = this.getTextureImage(manager);
	      simpletexture$texturedata.throwIfError();
	      TextureMetadataSection texturemetadatasection = simpletexture$texturedata.getTextureMetadata();
	      boolean flag;
	      boolean flag1;
	      if (texturemetadatasection != null) {
	         flag = texturemetadatasection.isBlur();
	         flag1 = texturemetadatasection.isClamp();
	      } else {
	         flag = false;
	         flag1 = false;
	      }

	      NativeImage nativeimage = simpletexture$texturedata.getImage();
	      if (!RenderSystem.isOnRenderThreadOrInit()) {
	         RenderSystem.recordRenderCall(() -> {
	            this.loadImage(nativeimage, flag, flag1);
	         });
	      } else {
	         this.loadImage(nativeimage, flag, flag1);
	      }
		
		this.size = simpletexture$texturedata.getImage().getWidth();
		this.textureHeight = simpletexture$texturedata.getImage().getHeight();
		
		ResourceLocation metadataFile = new ResourceLocation(this.location.getNamespace(), this.location.getPath() + ".mcmeta");
		
		
		try {
			
			IResource metadataResource = manager.getResource(metadataFile);
			InputStreamReader inputStreamReader = new InputStreamReader(metadataResource.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer stringBuffer = new StringBuffer();
			String string;
			while ((string = bufferedReader.readLine()) != null) {
				stringBuffer.append(string);
			}

			CompoundNBT metadataNBT = JsonToNBT.parseTag(stringBuffer.toString());
			
			CompoundNBT animation = metadataNBT.getCompound("animation");
			this.frameTime = animation.getInt("frametime");
			
			if (animation.contains("frames")) {
				ListNBT arr = animation.getList("frames", 3);
				frames = new int[arr.size()];
				for (int i = 0; i < arr.size(); i++) {
					this.frames[i] = arr.getInt(i);
				}
			} else {
				int height = simpletexture$texturedata.getImage().getHeight();
				int frameCount = height / size;
				frames = new int[frameCount];
				for (int i = 0; i < frameCount; i++) {
					this.frames[i] = i;
				}
			}
			
		} catch (CommandSyntaxException | FileNotFoundException e) {
			this.frames = new int[] {0};
			this.frameTime = 1;
		}
		
	}
	
	private void loadImage(NativeImage imageIn, boolean blurIn, boolean clampIn) {
		TextureUtil.prepareImage(this.getId(), 0, 2 * 256, 2 * 256);
		imageIn.upload(0, 0, 0, 0, 0, imageIn.getWidth(), imageIn.getHeight(), blurIn, clampIn, true, true);
	}
	
	@Override
	public void tick() {
		
		this.frameTimer++;
		
		if (this.frameTimer >= this.frameTime) {
			
			this.frameTimer = 0;
			this.frame++;
			if (this.frame >= this.frames.length) this.frame = 0;
			
		}
		
	}
	
	/**
	 * Convert the texture to an AnimatedTexture first with prepareTexture()!
	 * Draw method for Screens.
	 * Draws the texture like an normal texture but animated.
	 * 
	 * @param matrixStack
	 * @param screen
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void draw(MatrixStack matrixStack, AbstractGui screen, int x, int y, int width, int height) {
		
		if (this.frames.length > 0) {
			
			int frameOffset = this.frames[this.frame] * 8;
			
			Minecraft.getInstance().getTextureManager().bind(this.location);		
			screen.blit(matrixStack, x + 8, y, 0, frameOffset, width, height);
			
		}
		
	}
	
	/**
	 * Convert the texture to an AnimatedTexture first with prepareTexture()!
	 * Method to get the UV values for rendering the texture with the MatrixStack.
	 * 
	 * @param uvx The X coordinate from the uv to transform to animated.
	 * @param uvy The Y coordinate from the uv to transform to animated.
	 * @return the transformed uv coordinates as a Vector2f.
	 */
	public Vector2f getFrameUV(float uvx, float uvy) {
		
		if (this.frames.length > 0) {
			
			float frameOffset = this.frames[this.frame];
			return new Vector2f(uvx / 32F, (uvy + frameOffset) / 32F);
			
		}
		
		return new Vector2f(uvx / 32F, uvy / 32F);
		
	}
	
}
