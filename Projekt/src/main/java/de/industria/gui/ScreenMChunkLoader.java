package de.industria.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.packet.CUpdateChunkLoader;
import de.industria.tileentity.TileEntityMChunkLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap.Type;

public class ScreenMChunkLoader extends ContainerScreen<ContainerMChunkLoader> {
	
	public static final ResourceLocation CHUNK_LOADER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/chunk_loader.png");
	public static final String SCREEN_MAP_NAME = "chunk_loader_map";
	
	protected DynamicTexture mapScreen;
		

	public ScreenMChunkLoader(ContainerMChunkLoader screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.mapScreen = new DynamicTexture((2 * TileEntityMChunkLoader.CHUNK_RANGE + 1) * 16, (2 * TileEntityMChunkLoader.CHUNK_RANGE + 1) * 16, true);
	}
	
	@Override
	protected void init() {
		super.init();
		
		this.imageHeight = 180;
		this.titleLabelY -= 8;
		
		TileEntityMChunkLoader tileEntity = this.menu.getTileEntity();
		BlockPos loaderPosition = tileEntity.getBlockPos();
		ChunkPos centerChunk = new ChunkPos(loaderPosition);
		
		for (int x = -TileEntityMChunkLoader.CHUNK_RANGE; x <= TileEntityMChunkLoader.CHUNK_RANGE; x++) {
			for (int z = -TileEntityMChunkLoader.CHUNK_RANGE; z <= TileEntityMChunkLoader.CHUNK_RANGE; z++) {
				ChunkPos chunkToMap = new ChunkPos(centerChunk.x + x, centerChunk.z + z);
				this.mapChunk(chunkToMap, tileEntity.getLevel(), TileEntityMChunkLoader.CHUNK_RANGE + x, TileEntityMChunkLoader.CHUNK_RANGE + z);
			}
		}
		this.mapScreen.upload();
		
	}
	
	private void mapChunk(ChunkPos pos, World world, int chunkX, int chunkZ) {
		
		IChunk chunk = world.getChunk(pos.x, pos.z);
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int y = chunk.getHeight(Type.WORLD_SURFACE, (pos.x * 16) + x, (pos.z * 16) + z);
				BlockPos blockToMap = new BlockPos((pos.x * 16) + x, y, (pos.z * 16) + z);
				BlockState stateToMap = world.getBlockState(blockToMap);
				MaterialColor color = stateToMap.getMapColor(world, blockToMap);
				this.putColorOnMap(color, chunkX * 16 + x, chunkZ * 16 + z);
			}
		}
		
	}
	
	private void putColorOnMap(MaterialColor color, int x, int y) {
		int pixelRGB = color.calculateRGBColor(4);
		this.mapScreen.getPixels().setPixelRGBA(x, y, pixelRGB);
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(CHUNK_LOADER_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMChunkLoader te = this.menu.getTileEntity();
		
		matrixStack.pushPose();
		matrixStack.translate(i + 8, j + 15, 0);
		
		// Render Map
		matrixStack.pushPose();
		float scale = 160F / (this.mapScreen.getPixels().getWidth() - 16);
		matrixStack.scale(scale, scale, scale);
		this.mapScreen.bind();
		this.blit(matrixStack, 0, 0, 0, 0, this.mapScreen.getPixels().getWidth() - 16, this.mapScreen.getPixels().getHeight() - 16);
		matrixStack.popPose();
		
		// Render Raster
		scale = 160F / (this.mapScreen.getPixels().getWidth());
		matrixStack.scale(scale, scale, scale);
		int rasterSize = TileEntityMChunkLoader.CHUNK_RANGE * 2 + 1;
		for (int rx = 1; rx < rasterSize; rx++) {
			this.vLine(matrixStack, rx * 16, 0, this.mapScreen.getPixels().getHeight(), new Color(0, 0, 0255, 100).getRGB());
		}
		for (int ry = 1; ry < rasterSize; ry++) {
			this.hLine(matrixStack, 0, this.mapScreen.getPixels().getWidth(), ry * 16, new Color(0, 0, 0, 100).getRGB());
		}
		
		// Render Selected Chunks
		for (ChunkPos chunk : te.activeRelativeChunks) {
			int sx = (chunk.x + TileEntityMChunkLoader.CHUNK_RANGE) * 16;
			int sy = (chunk.z + TileEntityMChunkLoader.CHUNK_RANGE) * 16;
			fill(matrixStack, sx + 1, sy + 1, sx + 16, sy + 16, new Color(0, 255, 0, 100).getRGB());
		}
		
		matrixStack.popPose();
		
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
	      this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	}
	
	protected boolean mousePressed1 = false;
	protected boolean mousePressed2 = false;
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isInRaster(mouseX, mouseY)) {
			if (button == 0) {
				mousePressed1 = true;
				this.setSelected(mouseX, mouseY, true);
			} else if (button == 1) {
				mousePressed2 = true;
				this.setSelected(mouseX, mouseY, false);
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.mousePressed1 = false;
		} else if (button == 1) {
			this.mousePressed2 = false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (isInRaster(mouseX, mouseY)) {
			if (mousePressed1) {
				this.setSelected(mouseX, mouseY, true);
			} else if (mousePressed2) {
				this.setSelected(mouseX, mouseY, false);
			}
		}
		super.mouseMoved(mouseX, mouseY);
	}
	
	public boolean isInRaster(double mouseX, double mouseY) {
		double screenX = (this.leftPos - mouseX) + 8;
		double screenY = ((this.height - this.imageHeight) / 2 - mouseY) + 15;
		int chunkX = (int) Math.floor(-screenX / (160F / ((TileEntityMChunkLoader.CHUNK_RANGE * 2) + 1))) - TileEntityMChunkLoader.CHUNK_RANGE;
		int chunkY = (int) Math.floor(-screenY / (160F / ((TileEntityMChunkLoader.CHUNK_RANGE * 2) + 1))) - TileEntityMChunkLoader.CHUNK_RANGE;
		return chunkX >= -TileEntityMChunkLoader.CHUNK_RANGE && chunkX <= TileEntityMChunkLoader.CHUNK_RANGE && chunkY >= -TileEntityMChunkLoader.CHUNK_RANGE && chunkY <= TileEntityMChunkLoader.CHUNK_RANGE;
	}
	
	public void setSelected(double mouseX, double mouseY, boolean selected) {
		double screenX = (this.leftPos - mouseX) + 8;
		double screenY = ((this.height - this.imageHeight) / 2 - mouseY) + 15;
		int chunkX = (int) Math.floor(-screenX / (160F / ((TileEntityMChunkLoader.CHUNK_RANGE * 2) + 1))) - TileEntityMChunkLoader.CHUNK_RANGE;
		int chunkY = (int) Math.floor(-screenY / (160F / ((TileEntityMChunkLoader.CHUNK_RANGE * 2) + 1))) - TileEntityMChunkLoader.CHUNK_RANGE;
		boolean flag = this.menu.getTileEntity().setChunkCactive(new ChunkPos(chunkX, chunkY), selected);
		if (flag) this.onSelctionChanged();
	}
	
	public void onSelctionChanged() {
		Industria.NETWORK.sendToServer(new CUpdateChunkLoader(this.menu.getTileEntity().getBlockPos(), this.menu.getTileEntity().activeRelativeChunks));
	}
	
}