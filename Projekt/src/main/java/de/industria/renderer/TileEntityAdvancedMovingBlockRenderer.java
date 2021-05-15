package de.industria.renderer;

import java.lang.reflect.Field;
import java.util.Random;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityAdvancedMovingBlockRenderer extends TileEntityRenderer<TileEntityAdvancedMovingBlock> {
	
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

	public TileEntityAdvancedMovingBlockRenderer(TileEntityRendererDispatcher p_i226012_1_) {
		super(p_i226012_1_);
	}
	
	@SuppressWarnings("deprecation")
	public void render(TileEntityAdvancedMovingBlock tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		World world = tileEntityIn.getWorld();
		if (world != null) {
			BlockPos blockpos = tileEntityIn.getPos().offset(tileEntityIn.getMotionDirection().getOpposite());
			BlockState blockstate = tileEntityIn.getPistonState();
			if (!blockstate.isAir()) {
				BlockModelRenderer.enableCache();
				matrixStackIn.push();
				matrixStackIn.translate((double)tileEntityIn.getOffsetX(partialTicks), (double)tileEntityIn.getOffsetY(partialTicks), (double)tileEntityIn.getOffsetZ(partialTicks));
				if (blockstate.isIn(ModItems.advanced_piston_head) && tileEntityIn.getProgress(partialTicks) <= 4.0F) {
					blockstate = blockstate.with(PistonHeadBlock.SHORT, Boolean.valueOf(tileEntityIn.getProgress(partialTicks) <= 0.5F));
					this.func_228876_a_(blockpos, blockstate, tileEntityIn.getPistonTileEntity(), matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				} else if (tileEntityIn.shouldPistonHeadBeRendered() && !tileEntityIn.isExtending()) {
					PistonType pistontype = blockstate.isIn(ModItems.advanced_sticky_piston) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState blockstate1 = ModItems.advanced_piston_head.getDefaultState().with(PistonHeadBlock.TYPE, pistontype).with(PistonHeadBlock.FACING, blockstate.get(PistonBlock.FACING));
					blockstate1 = blockstate1.with(PistonHeadBlock.SHORT, Boolean.valueOf(tileEntityIn.getProgress(partialTicks) >= 0.5F));
					this.func_228876_a_(blockpos, blockstate1, tileEntityIn.getTileData(), matrixStackIn, bufferIn, world, false, combinedOverlayIn);
					BlockPos blockpos1 = blockpos.offset(tileEntityIn.getMotionDirection());
					matrixStackIn.pop();
					matrixStackIn.push();
					blockstate = blockstate.with(PistonBlock.EXTENDED, Boolean.valueOf(true));
					this.func_228876_a_(blockpos1, blockstate, tileEntityIn.getPistonTileEntity(), matrixStackIn, bufferIn, world, true, combinedOverlayIn);
				} else {
					this.func_228876_a_(blockpos, blockstate, tileEntityIn.getPistonTileEntity(), matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				}
				
				matrixStackIn.pop();
				BlockModelRenderer.disableCache();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void func_228876_a_(BlockPos pos, BlockState state, CompoundNBT tileData, MatrixStack matrixStack, IRenderTypeBuffer buffer, World world, boolean p_228876_6_, int p_228876_7_) {
		net.minecraft.client.renderer.RenderType.getBlockRenderTypes().stream().filter(t -> RenderTypeLookup.canRenderInLayer(state, t)).forEach(rendertype -> {
		net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
		IVertexBuilder ivertexbuilder = buffer.getBuffer(rendertype);
		if (blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		this.blockRenderer.getBlockModelRenderer().renderModel(world, this.blockRenderer.getModelForState(state), state, pos, matrixStack, ivertexbuilder, p_228876_6_, new Random(), state.getPositionRandom(pos), p_228876_7_);
		});
		
		if (state.hasTileEntity()) {
	 		
	 		TileEntity tileEntity = state.getBlock().createTileEntity(state, world);
	 		tileEntity.deserializeNBT(tileData);
	 		tileEntity.setWorldAndPos(world, pos); 
	 					
 			try {
 		 		Field[] fields = FieldUtils.getAllFields(tileEntity.getClass());
 				for (Field field : fields) {
 					if (field.getName().equals("field_195045_e") || field.getName().equals("cachedBlockState")) {
 						FieldUtils.writeField(tileEntity, field.getName(), state, true);
 					}
 				}
			} catch (IllegalAccessException e) {
				Industria.LOGGER.error("Access Error on render TileEbtity as MovingBlock!");
				e.printStackTrace();
			}
 			
	 		matrixStack.push();
	 		matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
	 		
			TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileEntity);
			if (tileentityrenderer != null) {
				runCrashReportable(tileEntity, () -> {
					matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
					tileentityrenderer.render(tileEntity, 0, matrixStack, buffer, WorldRenderer.getCombinedLight(world, pos), OverlayTexture.NO_OVERLAY);
				});
			}
	 		matrixStack.pop();
			
		}
		
		net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
	}
	
	private static void runCrashReportable(TileEntity tileEntityIn, Runnable runnableIn) {
		try {
			runnableIn.run();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
			tileEntityIn.addInfoToCrashReport(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}
	
}