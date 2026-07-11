package de.m_marvin.industria.core.client.util;

import java.awt.Color;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.registries.RenderTypes;
import de.m_marvin.industria.core.util.types.Tiler;
import de.m_marvin.univec.impl.Vec3f;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GraphicsUtility {

	private GraphicsUtility() {}

	public static final ResourceLocation UTILITY_WIDGETS_TEXTURE = ResourceLocation.tryBuild(IndustriaCore.MODID, "textures/gui/utility_widgets.png");
	public static final ResourceLocation UTILITY_WIDGETS_TEXTURE_CREATIVE = ResourceLocation.tryBuild(IndustriaCore.MODID, "textures/gui/utility_widgets_creative.png");
	
	public static class Spacial {

		private Spacial() {}
		
		/**
		 * Draws an string horizontally centered on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param r The red color component for the string
		 * @param g The green color component for the string
		 * @param b The blue color component for the string
		 * @param a The alpha color component for the string
		 */
		public static void drawStringCentered(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int x, int y, float r, float g, float b, float a) {
			drawStringCentered(matrixStack, bufferSource, string, x, y, new Color(r, g, b, a).getRGB());
		}

		/**
		 * Draws an string horizontally centered on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param color The combined color code for the string
		 */
		public static void drawStringCentered(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int x, int y, int color) {
			float width = Minecraft.getInstance().font.width(string);
			Minecraft.getInstance().font.drawInBatch(string, x + -width / 2, y, color, false, matrixStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, false);
		}
		
		/**
		 * Draws an string on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param r The red color component for the string
		 * @param g The green color component for the string
		 * @param b The blue color component for the string
		 * @param a The alpha color component for the string
		 */
		public static void drawString(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int x, int y, float r, float g, float b, float a) {
			drawString(matrixStack, bufferSource, string, x, y, new Color(r, g, b, a).getRGB());
		}

		/**
		 * Renders an string on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param color The combined color code for the string
		 */
		public static void drawString(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int x, int y, int color) {
			Minecraft.getInstance().font.drawInBatch(string, x, y, color, false, matrixStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, Minecraft.getInstance().font.isBidirectional());
		}
		
		/**
		 * Draws an portion of an texture on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param uOffset The U offset of the texture portion
		 * @param vOffset The V offset of the texture portion
		 * @param uWidth The width of the texture portion to draw
		 * @param vHeight The height of the texture portion to draw
		 * @param color The combined color code for the string
		 */
		public static void drawTexture(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, float uOffset, float vOffset, float uWidth, float vHeight, int color) {
			
			float fu = uOffset;
			float fv = vOffset;
			float fuw = uWidth;
			float fvh = vHeight;
			
			Matrix4f pose = matrixStack.last().pose();
			Matrix3f normal = matrixStack.last().normal();
			VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.cutoutTexture(texture));
			defaultVertex(vertexConsumer, pose, normal, x + w, y + 0, 0,	0, 0, 1, 	fu + fuw, 	fv, 		15728880, color);
			defaultVertex(vertexConsumer, pose, normal, x + w, y - h, 0,	0, 0, 1, 	fu + fuw, 	fv + fvh, 	15728880, color);
			defaultVertex(vertexConsumer, pose, normal, x + 0, y - h, 0,	0, 0, 1, 	fu, 		fv + fvh, 	15728880, color);
			defaultVertex(vertexConsumer, pose, normal, x + 0, y + 0, 0,	0, 0, 1, 	fu, 		fv, 		15728880, color);
			
		}
		
		/**
		 * Draws an portion of an texture on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param uOffset The U offset of the texture portion
		 * @param vOffset The V offset of the texture portion
		 * @param uWidth The width of the texture portion to draw
		 * @param vHeight The height of the texture portion to draw
		 * @param r The red color component for the texture
		 * @param g The green color component for the texture
		 * @param b The blue color component for the texture
		 * @param a The alpha color component for the texture
		 */
		public static void drawTexture(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, float uOffset, float vOffset, float uWidth, float vHeight, float r, float g, float b, float a) {
			drawTexture(matrixStack, bufferSource, texture, x, y, w, h, uOffset, vOffset, uWidth, vHeight, new Color(r, g, b, a).getRGB());
		}
		
		/**
		 * Draws an portion of an texture horizontally centered on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param uOffset The U offset of the texture portion
		 * @param vOffset The V offset of the texture portion
		 * @param uWidth The width of the texture portion to draw
		 * @param vHeight The height of the texture portion to draw
		 * @param color The combined color code for the string
		 */
		public static void drawTextureCentered(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, float uOffset, float vOffset, float uWidth, float vHeight, int color) {
			drawTexture(matrixStack, bufferSource, texture, x - w / 2, y, w, h, uOffset, vOffset, uWidth, vHeight, color);
		}
		
		/**
		 * Draws an portion of an texture horizontally centered on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param uOffset The U offset of the texture portion
		 * @param vOffset The V offset of the texture portion
		 * @param uWidth The width of the texture portion to draw
		 * @param vHeight The height of the texture portion to draw
		 * @param r The red color component for the texture
		 * @param g The green color component for the texture
		 * @param b The blue color component for the texture
		 * @param a The alpha color component for the texture
		 */
		public static void drawTextureCentered(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, float uOffset, float vOffset, float uWidth, float vHeight, float r, float g, float b, float a) {
			drawTexture(matrixStack, bufferSource, texture, x - w / 2, y, w, h, uOffset, vOffset, uWidth, vHeight, new Color(r, g, b, a).getRGB());
		}
		
		/**
		 * Draws an entire texture on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param color The combined color code for the string
		 */
		public static void drawTexture(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, int color) {
			drawTexture(matrixStack, bufferSource, texture, x, y, w, h, 0F, 0F, 1F, 1F, color);
		}
		
		/**
		 * Draws an entire texture on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param r The red color component for the texture
		 * @param g The green color component for the texture
		 * @param b The blue color component for the texture
		 * @param a The alpha color component for the texture
		 */
		public static void drawTexture(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, float r, float g, float b, float a) {
			drawTexture(matrixStack, bufferSource, texture, x, y, w, h, new Color(r, g, b, a).getRGB());
		}
		
		/**
		 * Draws an entire texture horizontally centered on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param color The combined color code for the string
		 */
		public static void drawTextureCentered(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, int color) {
			drawTexture(matrixStack, bufferSource, texture, x - w / 2, y, w, h, color);
		}

		/**
		 * Draws an entire texture horizontally centered on an 2D plane, applying the supplied color and pose stack.
		 * @param matrixStack The pose stack to apply to the rendered string
		 * @param bufferSource The buffer source to get the text buffer from
		 * @param string The string to draw
		 * @param x The X offset on the strings 2D plane
		 * @param y The Y offset on the strings 2D plane
		 * @param w The width of the texture
		 * @param h The height of the texture
		 * @param r The red color component for the texture
		 * @param g The green color component for the texture
		 * @param b The blue color component for the texture
		 * @param a The alpha color component for the texture
		 */
		public static void drawTextureCentered(PoseStack matrixStack, MultiBufferSource bufferSource, ResourceLocation texture, float x, float y, float w, float h, float r, float g, float b, float a) {
			drawTexture(matrixStack, bufferSource, texture, x - w / 2, y, w, h, new Color(r, g, b, a).getRGB());
		}
		
		/**
		 * Puts data for one vertex into the vertex consumer, using the default format used by most shaders.
		 * @param vertexBuilder The vertex consumer/buffer
		 * @param pose The pose transformation to apply
		 * @param normal The normal transformation to apply
		 * @param x The vertex x coordinate
		 * @param y The vertex y coordinate
		 * @param z The vertex z coordinate
		 * @param nx The normal vector x component
		 * @param ny The normal vector y component
		 * @param nz The normal vector z component
		 * @param u The texture u coordinate
		 * @param v The texture v coordinate
		 * @param light The combined light code
		 * @param color The combined color code
		 */
		public static void defaultVertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float nx, float ny, float nz, float u, float v, int light, int color) {
			vertexBuilder
				.vertex(pose, x, y, z)
				.color(color)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(light)
				.normal(normal, nx, ny, nz)
				.endVertex();
		}
		
		/**
		 * Render an 3D vector.
		 * @param vertexConsumer Buffer to fill with the vertex data
		 * @param matrixStack The pose stack stack to apply to the vector
		 * @param origin The origin point of the vector
		 * @param vector The vector
		 * @param r The red color component for the vector
		 * @param g The green color component for the vector
		 * @param b The blue color component for the vector
		 * @param a The alpha color component for the vector
		 */
		public static void renderVector(VertexConsumer vertexConsumer, PoseStack matrixStack, Vec3f origin, Vec3f vector, float r, float g, float b, float a) {
			
			Matrix4f pose = matrixStack.last().pose();
			Matrix3f normal = matrixStack.last().normal();
			
			Vec3f normalv = vector.tryNormalize();
			Vec3f vector2 = origin.add(vector);
			
			vertexConsumer.vertex(pose, origin.x, origin.y, origin.z).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			vertexConsumer.vertex(pose, vector2.x, vector2.y, vector2.z).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			
			vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y - 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y + 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();

			vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y - 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y + 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();

			vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y - 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y + 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();

			vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y - 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y + 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
			
		}
		
	}
	
	public static class UI {
		
		private UI() {}

		/**
		 * Blits a tillable texture to fill the specified area, starting at the position specified by pUOffset and pVOffset within the tillable texture, pUSource and pVSource specify where the starting point of the tillable texture would be.
		 * @param pGuiGraphics GUI graphics instance
		 * @param texture Texture to use for the blit
		 * @param pX The X offset of the blit area
		 * @param pY The Y offset of the blit area
		 * @param pUOffset The U offset to start the blit from
		 * @param pVOffset The V offset to start the blit from
		 * @param pUWidth The width of the blit area
		 * @param pVHeight The height of the blit area
		 * @param pUSource The U offset of the texture source
		 * @param pVSource The V offset of the texture source
		 * @param pSourceWidth The width of the texture source
		 * @param pSourceHeight The height of the texture source
		 */
		public static void blitTilable(GuiGraphics pGuiGraphics, ResourceLocation texture, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight, int pUSource, int pVSource, int pSourceWidth, int pSourceHeight) {
		    
			int sx = pUSource + (pUOffset - pUSource) % pSourceWidth;
			int sy = pVSource + (pVOffset - pVSource) % pSourceHeight;
			
			int x = 0;
			int w = 0;
			for (IntIterator widths = tileSlices(pUOffset, pUSource, pSourceWidth, pUWidth); widths.hasNext(); x += w) {
				w = widths.nextInt();
				
				int y = 0;
				int h = 0;
				for (IntIterator heights = tileSlices(pVOffset, pVSource, pSourceHeight, pVHeight); heights.hasNext(); y += h) {
					h = heights.nextInt();
					
					pGuiGraphics.blit(texture, pX + x, pY + y, x == 0 ? sx : pUSource, y == 0 ? sy : pVSource, w, h);
				}
			}
			
		}
		
		/**
		 * Blits a tillable texture to fill the specified area, always starting from the beginning of the tillable texture.
		 * @param pGuiGraphics GUI graphics instance
		 * @param texture Texture to use for the blit
		 * @param pX The X offset of the blit area
		 * @param pY The Y offset of the blit area
		 * @param pUOffest The U offset of the texture source
		 * @param pVOffset The V offset of the texture source
		 * @param pUWidth The width of the blit area
		 * @param pVHeight The height of the blit area
		 * @param pSourceWidth The width of the texture source
		 * @param pSourceHeight The height of the texture source
		 */
		public static void blitTilable(GuiGraphics pGuiGraphics, ResourceLocation texture, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight, int pSourceWidth, int pSourceHeight) {
			blitTilable(pGuiGraphics, texture, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight, pUOffset, pVOffset, pSourceWidth, pSourceHeight);
		}
		
		public static IntIterator tileSlices(int offset, int source, int sourceLength, int length) {
			int start = (offset - source) % sourceLength;
			return new Tiler(start, length, sourceLength);
		}
		
	}
	
}
