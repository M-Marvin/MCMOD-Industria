package de.industria.renderer;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityAdvancedMovingBlockRenderer extends TileEntityRenderer<TileEntityAdvancedMovingBlock> {
	
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

	public TileEntityAdvancedMovingBlockRenderer(TileEntityRendererDispatcher p_i226012_1_) {
		super(p_i226012_1_);
	}
	
	@SuppressWarnings("deprecation")
	public void render(TileEntityAdvancedMovingBlock tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		World world = tileEntityIn.getLevel();
		if (world != null) {
			BlockPos blockpos = tileEntityIn.getBlockPos().relative(tileEntityIn.getMotionDirection().getOpposite());
			BlockState blockstate = tileEntityIn.getPistonState();
			if (!blockstate.isAir()) {
				BlockModelRenderer.enableCaching();
				matrixStackIn.pushPose();
				matrixStackIn.translate((double)tileEntityIn.getOffsetX(partialTicks), (double)tileEntityIn.getOffsetY(partialTicks), (double)tileEntityIn.getOffsetZ(partialTicks));
				if (blockstate.is(ModItems.advanced_piston_head) && tileEntityIn.getProgress(partialTicks) <= 4.0F) {
					blockstate = blockstate.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(tileEntityIn.getProgress(partialTicks) <= 0.5F));
					this.renderBlock(blockpos, blockstate, tileEntityIn.getPistonTileEntity(), matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				} else if (tileEntityIn.shouldPistonHeadBeRendered() && !tileEntityIn.isExtending()) {
					PistonType pistontype = blockstate.is(ModItems.advanced_sticky_piston) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState blockstate1 = ModItems.advanced_piston_head.defaultBlockState().setValue(PistonHeadBlock.TYPE, pistontype).setValue(PistonHeadBlock.FACING, blockstate.getValue(PistonBlock.FACING));
					blockstate1 = blockstate1.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(tileEntityIn.getProgress(partialTicks) >= 0.5F));
					this.renderBlock(blockpos, blockstate1, tileEntityIn.getTileData(), matrixStackIn, bufferIn, world, false, combinedOverlayIn);
					BlockPos blockpos1 = blockpos.relative(tileEntityIn.getMotionDirection());
					matrixStackIn.popPose();
					matrixStackIn.pushPose();
					blockstate = blockstate.setValue(PistonBlock.EXTENDED, Boolean.valueOf(true));
					this.renderBlock(blockpos1, blockstate, tileEntityIn.getPistonTileEntity(), matrixStackIn, bufferIn, world, true, combinedOverlayIn);
				} else {
					this.renderBlock(blockpos, blockstate, tileEntityIn.getPistonTileEntity(), matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				}
				
				matrixStackIn.popPose();
				BlockModelRenderer.clearCache();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void renderBlock(BlockPos pos, BlockState state, CompoundNBT tileData, MatrixStack matrixStack, IRenderTypeBuffer buffer, World world, boolean p_228876_6_, int p_228876_7_) {
		net.minecraft.client.renderer.RenderType.chunkBufferLayers().stream().filter(t -> RenderTypeLookup.canRenderInLayer(state, t)).forEach(rendertype -> {
		net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
		IVertexBuilder ivertexbuilder = buffer.getBuffer(rendertype);
		if (blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRenderer();
		this.blockRenderer.getModelRenderer().tesselateBlock(world, this.blockRenderer.getBlockModel(state), state, pos, matrixStack, ivertexbuilder, p_228876_6_, new Random(), state.getSeed(pos), p_228876_7_);
		});
		
		// TODO: Bug seit letztem Forge Update, verusacht (warscheinlich) wilk�rliche Abst�rze ohne erkennbaren Grund.
//		if (state.hasTileEntity()) {
//	 		
//	 		TileEntity tileEntity = state.getBlock().createTileEntity(state, world);
//	 		tileEntity.deserializeNBT(tileData);
//	 		tileEntity.setWorldAndPos(world, pos); 
//	 					
// 			try {
// 		 		Field[] fields = FieldUtils.getAllFields(tileEntity.getClass());
// 				for (Field field : fields) {
// 					if (field.getName().equals("blockState") || field.getName().equals("cachedBlockState")) {
// 						FieldUtils.writeField(tileEntity, field.getName(), state, true);
// 					}
// 				}
//			} catch (IllegalAccessException e) {
//				Industria.LOGGER.error("Access Error on render TileEbtity as MovingBlock!");
//				e.printStackTrace();
//			}
// 			
//	 		matrixStack.push();
//	 		matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
//	 		
//			TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileEntity);
//			if (tileentityrenderer != null) {
//				runCrashReportable(tileEntity, () -> {
//					matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
//					tileentityrenderer.render(tileEntity, 0, matrixStack, buffer, WorldRenderer.getCombinedLight(world, pos), OverlayTexture.NO_OVERLAY);
//				});
//			}
//	 		matrixStack.pop();
//			
//		}
		
		net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
	}
	
//	private static void runCrashReportable(TileEntity tileEntityIn, Runnable runnableIn) {
//		try {
//			runnableIn.run();
//		} catch (Throwable throwable) {
//			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
//			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
//			tileEntityIn.addInfoToCrashReport(crashreportcategory);
//			throw new ReportedException(crashreport);
//		}
//	}
	
}