package de.m_marvin.industria.core.client.debugview;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.types.DiagonalDirection;
import de.m_marvin.univec.impl.Vec2f;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Bus.FORGE, value=Dist.CLIENT)
public class ModifiedDebugScreenOverlay {
	
	private static final Supplier<Minecraft> MC = Minecraft::getInstance;
	private static final List<Component[]> DEBUG_LINES = new ArrayList<Component[]>();
	private static final ResourceLocation DEBUG_SCREEN_ID = ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, "debug_text");
	
	private static int debugScreenScroll = 0;
	
	@SubscribeEvent
	public static void onOverlayRender(RenderGuiOverlayEvent.Pre event) {
		
		if (event.getOverlay().id().equals(DEBUG_SCREEN_ID) && MC.get().options.renderDebug) {

			event.setCanceled(true);
			
			DEBUG_LINES.clear();
			fillDebugInformation(DEBUG_LINES);
			drawDebugLines(event.getGuiGraphics(), DEBUG_LINES, debugScreenScroll);
			
		}
		
	}

	@SubscribeEvent
	public static void onMouseScrollInput(InputEvent.MouseScrollingEvent event) {
		Minecraft mc = MC.get();
		if (mc.player.isShiftKeyDown() && MC.get().options.renderDebug) {
			int limit = DEBUG_LINES.size() * mc.font.lineHeight;
			debugScreenScroll += -event.getScrollDelta() * 10;
			if (debugScreenScroll < 0) debugScreenScroll = 0;
			if (debugScreenScroll > limit) debugScreenScroll = limit;
			event.setCanceled(true);
		}
	}
	
	public static void fillDebugInformation(List<Component[]> debugLines) {
		
		// Currently unused, maybe switching on and off individual sections ?
		boolean showSystem = true;
		boolean showClient = true;
		boolean showEnvironment = true;
		boolean showPosition = true;
		boolean showBlock = true;
		
		Minecraft mc = MC.get();
		
		if (showSystem) {
			
			String fpsString = mc.fpsString;
			String fps = fpsString.substring(0, fpsString.indexOf(' '));
			String gpu = fpsString.substring(fpsString.lastIndexOf(' '));
			IntegratedServer integratedserver = mc.getSingleplayerServer();
			
			debugLines.add(new Component[] { mdl("[§b§lSystem§r]") });
			debugLines.add(new Component[] { mdl(" §bMinecraft: §r%s/%s", SharedConstants.getCurrentVersion().getName(), mc.getLaunchedVersion()) });
			debugLines.add(new Component[] { mdl(" §bClient Brand: §r%s ", ClientBrandRetriever.getClientModName()) });
			debugLines.add(new Component[] { mdl(" §bFPS: §r%s §bGPU:§r%s", fps, gpu) });
			
			if (integratedserver != null)
				debugLines.add(new Component[] { mdl(" §bTPSmax: §r%d/20 §bTms: §r%.02f ms/50 ms", Math.max(0, Math.round(1000F /integratedserver.getAverageTickTime())), integratedserver.getAverageTickTime()) });
			
			debugLines.add(new Component[] { mdl("") });
			
		}
		
		if (showClient) {

			debugLines.add(new Component[] { mdl("[§b§lClient§r]") });
			debugLines.add(new Component[] { mdl(" §bChunks: §r%s", mc.levelRenderer.getChunkStatistics()) });
			debugLines.add(new Component[] { mdl(" §bEntities: §r%s", mc.levelRenderer.getEntityStatistics()) });
			debugLines.add(new Component[] { mdl(" §bParticles: §rP: %s", mc.particleEngine.countParticles()) });
			
			debugLines.add(new Component[] { mdl("") });
			
		}
		
		Vec3 playerPosition = mc.cameraEntity.position();
		BlockPos playerBlockPos = new BlockPos((int) Math.floor(playerPosition.x), (int) Math.floor(playerPosition.y), (int) Math.floor(playerPosition.z));
		ChunkPos playerChunk = new ChunkPos(playerBlockPos);
		int playerChunkSection = playerBlockPos.getY() >> 4;
		BlockPos playerChunkOrigin = playerChunk.getWorldPosition();
		playerChunkOrigin = playerChunkOrigin.offset(0, playerChunkSection << 4, 0);
		Vec2f playerOrientation = Vec2f.fromVec(mc.cameraEntity.getRotationVector()).module(360F);
		if (playerOrientation.y > 180) playerOrientation.y += -360;
		if (playerOrientation.y < -180) playerOrientation.y += +360;
		if (playerOrientation.x > 180) playerOrientation.x += -360;
		if (playerOrientation.x < -180) playerOrientation.x += +360;
		Vec3 playerNormal = mc.cameraEntity.getLookAngle();
		DiagonalDirection playerDirection = DiagonalDirection.getNearest(playerNormal.x, playerNormal.y, playerNormal.z);
		
		BlockHitResult targetedBlockHit = MathUtility.getPlayerPOVHitResult(mc.level, mc.player, Fluid.NONE, 32);
		Vec3 blockPosition = Vec3.ZERO;
		BlockPos blockBlockPos = BlockPos.ZERO;
		Direction blockDirection = null;
		if (targetedBlockHit.getType() == Type.BLOCK) {
			blockPosition = targetedBlockHit.getLocation();
			blockBlockPos = targetedBlockHit.getBlockPos();
			blockDirection = targetedBlockHit.getDirection();
		}
		ChunkPos blockChunk = new ChunkPos(blockBlockPos);
		int blockChunkSection = blockBlockPos.getY() >> 4;
		BlockPos blockChunkOrigin = blockChunk.getWorldPosition();
		blockChunkOrigin = blockChunkOrigin.offset(0, blockChunkSection << 4, 0);
		
		if (showPosition) {

			debugLines.add(new Component[] { mdl("[§b§lPosition§r]") });
			debugLines.add(new Component[] { mdl(" §cX §aY §9Z"), 		mdl("§bPlayer "), 						mdl(""),									mdl(""),								mdl("§bTargeted Block"), mdl(""), mdl("")  });
			debugLines.add(new Component[] { mdl(" §bAbsolute: "), 		mdl("§c%.02f ", playerPosition.x), 		mdl("§a%.02f ", playerPosition.y), 			mdl("§9%.02f ", playerPosition.z),	 	mdl(blockDirection != null ? "§c%.02f " : "§8N/A", blockPosition.x), 		mdl(blockDirection != null ? "§a%.02f " : "", blockPosition.y), 			mdl(blockDirection != null ? "§9%.02f" : "", blockPosition.z) });
			debugLines.add(new Component[] { mdl(" §bBlock: "), 		mdl("§c%d ", playerBlockPos.getX()), 	mdl("§a%d ", playerBlockPos.getY()), 		mdl("§9%d", playerBlockPos.getZ()),	 	mdl(blockDirection != null ? "§c%d " : "§8N/A", blockBlockPos.getX()), 	mdl(blockDirection != null ? "§a%d " : "", blockBlockPos.getY()), 		mdl(blockDirection != null ? "§9%d" : "", blockBlockPos.getZ()) });
			debugLines.add(new Component[] { mdl(" §bChunk: "), 		mdl("§c%d ", playerChunk.x), 			mdl("§a[%d] ", playerChunkSection), 		mdl("§9%d", playerChunk.z),				mdl(blockDirection != null ? "§c%d " : "§8N/A", blockChunk.x), 			mdl(blockDirection != null ? "§a[%d] " : "", blockChunkSection), 		mdl(blockDirection != null ? "§9%d" : "", blockChunk.z) });
			debugLines.add(new Component[] { mdl(" §bOrigin: "), 		mdl("§c%d ", playerChunkOrigin.getX()), mdl("§a[%d] ", playerChunkOrigin.getY()), 	mdl("§9%d", playerChunkOrigin.getZ()),	mdl(blockDirection != null ? "§c%d " : "§8N/A", blockChunkOrigin.getX()), mdl(blockDirection != null ? "§a[%d] " : "", blockChunkOrigin.getY()), 	mdl(blockDirection != null ? "§9%d" : "", blockChunkOrigin.getZ()) });
			debugLines.add(new Component[] { mdl(" §bOrientation: "),	mdl("§c%.02f ", playerOrientation.x), 	mdl("§a%.02f ", playerOrientation.y), 		mdl(""), 								mdl("§8N/A"), mdl(""), mdl("") });
			debugLines.add(new Component[] { mdl(" §bNormal: "),		mdl("§c%.02f ", playerNormal.x), 		mdl("§a%.02f ", playerNormal.y), 			mdl("§9%.02f ", playerNormal.z),		mdl("§8N/A"), mdl(""), mdl("") });
			debugLines.add(new Component[] { mdl(" §bDirection: "),		mdl("%s", playerDirection.getName()),	mdl(""), 									mdl(""),								mdl("%s", blockDirection != null ? blockDirection.getName() : "§8N/A"), mdl(""), mdl("") });
			
			debugLines.add(new Component[] { mdl("") });
			
		}
		
		Holder<Biome> biome = mc.level.getBiome(playerBlockPos);
		ResourceKey<Biome> biomeKey = biome.unwrap().left().orElse(null);
        int lightLevel = mc.level.getChunkSource().getLightEngine().getRawBrightness(playerBlockPos, 0);
        int lightLevelSky = mc.level.getBrightness(LightLayer.SKY, playerBlockPos);
        int lightLevelBlock = mc.level.getBrightness(LightLayer.BLOCK, playerBlockPos);
        
        if (showEnvironment) {

    		debugLines.add(new Component[] { mdl("[§b§lEnvironment§r]") });
    		debugLines.add(new Component[] { mdl(" §bLight: "), 	mdl("%d [S: %d B: %d]", lightLevel, lightLevelSky, lightLevelBlock)});
    		debugLines.add(new Component[] { mdl(" §bBiome: "), 	mdl("§5%s", biomeKey == null ? "§8N/A" : biomeKey.location().toString()) });
    		
    		for (var biomeTag : biome.getTagKeys().toList()) {
    			debugLines.add(new Component[] { mdl(" §d#%s", biomeTag.location().toString()) });
    		}
    		
			debugLines.add(new Component[] { mdl("") });
			
        }
        
		if (blockDirection != null && showBlock) {
			
			BlockState state = mc.level.getBlockState(blockBlockPos);
			BlockEntity blockEntity = null;
			boolean isCompound = false;
			BlockState blockState = null;
			if (state.getBlock() instanceof CompoundBlock) {
				isCompound = true;
				
				Level level = CompoundBlock.performOnTargetedAndReturn(mc.level, blockBlockPos, mc.player, 
						() -> mc.level,
						(compound, part) -> part.getLevel());
				blockState = level.getBlockState(blockBlockPos);
				blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockBlockPos) : null;
			} else {
				blockState = state;
				blockEntity = blockState.hasBlockEntity() ? mc.level.getBlockEntity(blockBlockPos) : null;
			}
			
			ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
			int blockLight = mc.level.getLightEmission(blockBlockPos);
			ResourceLocation blockEntityId = blockEntity != null ? BlockEntityType.getKey(blockEntity.getType()) : null;
			
			debugLines.add(new Component[] { mdl("[§b§lBlock§r] %s", isCompound ? "[§b§lIn Compound§r]" : "") });
			debugLines.add(new Component[] { mdl(" §bLight: "), 		mdl("%d", blockLight)});
			debugLines.add(new Component[] { mdl(" §bBlock ID: "),		mdl("§5%s", blockId.toString()) });
			debugLines.add(new Component[] { mdl(" §bBlock Entity: "), 	mdl("§5%s", blockEntityId != null ? blockEntityId.toString() : "§8N/A") });
			
			for (var prop : blockState.getProperties()) {
				Object value = blockState.getValue(prop);
				String valueName = value.toString();
				if (value instanceof StringRepresentable s) valueName = s.getSerializedName();
				debugLines.add(new Component[] { mdl(" §3%s: ", prop.getName()), mdl("%s", valueName) });
			}
			
			for (var tag : blockState.getTags().toList()) {
				debugLines.add(new Component[] { mdl(" §d#%s", tag.location().toString()) });
			}

			debugLines.add(new Component[] { mdl("") });
			
		}
		
	}
	
	public static Component mdl(String format, Object... args) {
		return Component.literal(String.format(format, args));
	}
	
	public static void drawDebugLines(GuiGraphics graphics, List<Component[]> debugLines, int offset) {

		@SuppressWarnings("resource")
		Font font = MC.get().font;
		int lastColumns = 0;
		int lineOffset = 0;
		int[] lastOffsets = new int[0];
		for (int line = 0; line < debugLines.size(); line++) {
			Component[] entries = debugLines.get(line);
			if (entries.length != lastColumns) {
				lastColumns = entries.length;
				lastOffsets = new int[lastColumns];
				
				for (int i = line; i < debugLines.size();  i++) {
					Component[] e = debugLines.get(i);
					if (e.length != lastColumns) break;
					for (int c = 0; c < lastColumns; c++) {
						int len = font.width(e[c]);
						if (len > lastOffsets[c]) lastOffsets[c] = len;
					}
				}
			}
			
			if (entries.length == 0) continue;
			
			int columnOffset = 0;
			for (int c = 0; c < lastColumns; c++) {
				graphics.drawString(font, entries[c], 2 + columnOffset, 2 + lineOffset - offset, 0xFFFFFF);
				columnOffset += lastOffsets[c];
			}
			
			lineOffset += font.lineHeight;
			
		}
		
	}
	
}
