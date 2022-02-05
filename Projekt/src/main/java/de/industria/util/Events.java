package de.industria.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import de.industria.Industria;
import de.industria.fluids.util.BlockGasFluid;
import de.industria.packet.CRequestBlockEntityUpdates;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import de.industria.util.handler.BlockBurnManager;
import de.industria.util.handler.ChunkLoadHandler;
import de.industria.util.handler.JigsawFileManager;
import de.industria.util.handler.MinecartHandler;
import de.industria.util.handler.ModGameRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Events {
	
	@SubscribeEvent
	public static void onResourceManagerReload(net.minecraftforge.event.AddReloadListenerEvent event) {
		JigsawFileManager.onFileManagerReload();
	}
	
	@SubscribeEvent
	public static void onWorldLoad(net.minecraftforge.event.world.WorldEvent.Load event) {
		BlockBurnManager.reloadBurnBehaviors();
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void setupFogDensity(EntityViewRenderEvent.FogDensity event) {
		if (event.getInfo().getFluidInCamera().getType().isSame(ModFluids.RAW_OIL)) {
			event.setDensity(2F);
			event.setCanceled(true);
		} else if (event.getInfo().getFluidInCamera().getType().isSame(ModFluids.TAR) || event.getInfo().getBlockAtCamera().getBlock() == ModItems.tar_crust) {
			event.setDensity(4F);
			event.setCanceled(true);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void setupFogColor(EntityViewRenderEvent.FogColors event) {
		if (event.getInfo().getFluidInCamera().getType().isSame(ModFluids.RAW_OIL)) {
			event.setRed(37 / 255F);
			event.setGreen(37 / 255F);
			event.setBlue(37 / 255F);
		} else if (event.getInfo().getFluidInCamera().getType().isSame(ModFluids.TAR) || event.getInfo().getBlockAtCamera().getBlock() == ModItems.tar_crust) {
			event.setRed(37 / 255F);
			event.setGreen(37 / 255F);
			event.setBlue(37 / 255F);
		}
	}
	
	@SubscribeEvent
	public static void onRightClickBlock(net.minecraftforge.event.entity.player.FillBucketEvent event) {
		World worldIn = event.getWorld();
		PlayerEntity playerIn = event.getPlayer();
		
		RayTraceResult result = event.getTarget();
		Vector3d vec1p = event.getPlayer().getLookAngle();
		int x = (int) (result.getLocation().x % 1 == 0 ? result.getLocation().x + (vec1p.x > 0 ? 0 : -1) : Math.floor(result.getLocation().x));
		int y = (int) (result.getLocation().y % 1 == 0 ? result.getLocation().y + (vec1p.y > 0 ? 0 : -1) : Math.floor(result.getLocation().y));
		int z = (int) (result.getLocation().z % 1 == 0 ? result.getLocation().z + (vec1p.z > 0 ? 0 : -1) : Math.floor(result.getLocation().z));
		BlockPos fluidPos = new BlockPos(x, y, z);
		FluidState fluidState = worldIn.getFluidState(fluidPos);
		
		if (fluidState.createLegacyBlock().getBlock() instanceof BlockGasFluid && event.getEmptyBucket().getItem() != ModItems.fluid_cannister) {
			ItemStack bucketItem = event.getEmptyBucket();
			if (fluidState.createLegacyBlock().getBlock() instanceof IBucketPickupHandler) {
				Fluid fluid = ((IBucketPickupHandler)fluidState.createLegacyBlock().getBlock()).takeLiquid(worldIn, fluidPos, fluidState.createLegacyBlock());
				
				if (fluid != Fluids.EMPTY) {
					
					playerIn.awardStat(Stats.ITEM_USED.get(bucketItem.getItem()));
					
					SoundEvent soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
					playerIn.playSound(soundevent, 1.0F, 1.0F);
					ItemStack itemstack1 = DrinkHelper.createFilledResult(bucketItem.copy(), playerIn, new ItemStack(fluid.getBucket()));
					if (!worldIn.isClientSide) {
						CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)playerIn, new ItemStack(fluid.getBucket()));
						event.setFilledBucket(itemstack1);
						playerIn.setItemSlot(EquipmentSlotType.MAINHAND, itemstack1);
					}
					
				}
			}
		}
	}
	
	protected static long lastServerWorldTick = 0L;
	@SubscribeEvent
	public static final void onWorldTick(net.minecraftforge.event.TickEvent.WorldTickEvent event) {
		if (event.phase == Phase.START) {
			if (!event.world.isClientSide()) {
				if (lastServerWorldTick != event.world.getGameTime()) {
					lastServerWorldTick = event.world.getGameTime();
					MinecartHandler.getHandlerForWorld(event.world).updateMinecarts();
				}
				if (event.world.blockEntityList.size() > 0) {
					ChunkLoadHandler.getHandlerForWorld(event.world).updateChunkForceLoads();
				}
				DataWatcher.updateBlockEntitys(false);
			}
		}
	}
	
	@SubscribeEvent
	public static final void onChunkLoad(net.minecraftforge.event.world.ChunkEvent.Load event) {
		if (event.getWorld().isClientSide()) {
			Industria.NETWORK.sendToServer(new CRequestBlockEntityUpdates(event.getChunk().getPos())); // PERFORMANCE TODO: Only send request if the chunk contains TileEntitys
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onWorldJoined(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer().level.isClientSide()) {
			Industria.NETWORK.sendToServer(new CRequestBlockEntityUpdates());
		}
	}
	
	@SubscribeEvent
	public static final void onWorldTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.START) {
			DataWatcher.updateBlockEntitys(true);
		}
	}
	
	@SubscribeEvent
	public static void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		for (GenerationStage.Decoration decoration : GenerationStage.Decoration.values()) {
			List<ConfiguredFeature<?, ?>> features = ModGameRegistry.getFeaturesToRegister().getOrDefault(event.getName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>()).getOrDefault(decoration, new ArrayList<>());
			for (ConfiguredFeature<?, ?> feature : features) {
				event.getGeneration().getFeatures(decoration).add(() -> feature);
			}
			
			List<ConfiguredFeature<?, ?>> featuresToDeactivate = ModGameRegistry.getFeaturesToDeactivate().getOrDefault(event.getName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>()).getOrDefault(decoration, new ArrayList<>());
			List<Supplier<ConfiguredFeature<?, ?>>> featuresToRemove = new ArrayList<Supplier<ConfiguredFeature<?, ?>>>();
			
			event.getGeneration().getFeatures(decoration).forEach((registredFeature) -> {
				featuresToDeactivate.forEach((feature) -> {
					if (compareBiomes(registredFeature.get(), feature)) featuresToRemove.add(registredFeature);
				});
			});
			
			event.getGeneration().getFeatures(decoration).removeAll(featuresToRemove);
			if (featuresToRemove.size() > 0) Industria.LOGGER.log(Level.INFO, featuresToRemove.size() + " Features deaktivated in " + event.getName());
		}
	}
	
	public static boolean compareBiomes(ConfiguredFeature<?, ?> registredFeature, ConfiguredFeature<?, ?> feature) {
		
		Optional<JsonElement> registredJson = ConfiguredFeature.DIRECT_CODEC.encode(registredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
		if (!registredJson.isPresent()) return false;
		JsonObject json1 = registredJson.get().getAsJsonObject();
		
		String jsonString = json1.toString();
		String jsonString2 = ConfiguredFeature.DIRECT_CODEC.encode(feature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left().get().toString();
//		
//		if (jsonString.contains("oak_leaves")) System.out.println(jsonString + "\n" + jsonString2);
//		if (jsonString.contains(jsonString2)) System.out.println("TSET");//System.out.println(ConfiguredFeature.DIRECT_CODEC.encode(Features.OAK, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left().get().toString() + " " + jsonString);
//		
		return jsonString.contains(jsonString2);
	}
	
	@SubscribeEvent
	public static void onBlockBurn(net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent event) {
		BlockState state = event.getState();
		if (state.getBlock() == Blocks.FIRE || state.getBlock() == Blocks.SOUL_FIRE) {
			BlockPos pos = event.getPos();
			BooleanProperty[] fireStates = new BooleanProperty[] {null, FireBlock.UP, FireBlock.NORTH, FireBlock.SOUTH, FireBlock.WEST, FireBlock.EAST};
			for (Direction d : Direction.values()) {
				BooleanProperty fireProp = fireStates[d.get3DDataValue()];
				if (fireProp != null ? state.getValue(fireProp) : true) {
					BlockPos fireCatchingBlockPos = pos.relative(d);
					BlockState fireCatchingBlockState = event.getWorld().getBlockState(fireCatchingBlockPos);
					if (!fireCatchingBlockState.isFlammable(event.getWorld(), fireCatchingBlockPos, d.getOpposite())) return;
					if (fireCatchingBlockState.getBlock() instanceof DoorBlock) {
						boolean otherLower = fireCatchingBlockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
						BlockPos otherHalf = otherLower ? fireCatchingBlockPos.above() : fireCatchingBlockPos.below();
						BlockState otherState = event.getWorld().getBlockState(otherHalf);
						BlockState[] burnedStates = BlockBurnManager.getBurnedVariants(new BlockState[] {fireCatchingBlockState, otherState}, event.getWorld(), fireCatchingBlockPos);
						if (fireCatchingBlockState != burnedStates[0]) {
							event.getWorld().setBlock(!otherLower ? otherHalf : fireCatchingBlockPos, Blocks.AIR.defaultBlockState(), 0);
							event.getWorld().setBlock(otherLower ? otherHalf : fireCatchingBlockPos, Blocks.AIR.defaultBlockState(), 0);
							event.getWorld().setBlock(fireCatchingBlockPos, burnedStates[0], 2);
							event.getWorld().setBlock(otherHalf, burnedStates[1], 2);
						}
					} else {
						BlockState burnedBlockState = BlockBurnManager.getBurnedVariant(fireCatchingBlockState, event.getWorld(), fireCatchingBlockPos);
						if (fireCatchingBlockState != burnedBlockState) {			
							event.getWorld().setBlock(fireCatchingBlockPos, burnedBlockState, 3);
						}
					}
					
				}
			}
		}
	}
	
//	@SuppressWarnings("resource")
//	@SubscribeEvent
//	@OnlyIn(Dist.CLIENT)
//	public static void onWorldRender(RenderWorldLastEvent event) {
//		
//		MatrixStack matrixStack = event.getMatrixStack();
//		IRenderTypeBuffer buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//		
//		matrixStack.pushPose();
//		{
//			
//			ClientPlayerEntity player = Minecraft.getInstance().player;
//			ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
//			
//			if (heldItem.getItem() == ModItems.empty_blueprint) {
//				
//				BlockPos cornerA = ItemEmptyBlueprint.getPositionA(heldItem);
//				if (cornerA != null) {
//					
//					BlockPos cornerB = ItemEmptyBlueprint.getPositionB(heldItem);
//					if (cornerB == null) cornerB = player.blockPosition();
//					Vector3d camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//					
//					matrixStack.translate(-camera.x(), -camera.y(), -camera.z());
//					
//					IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
//					WorldRenderer.renderLineBox(matrixStack, builder, cornerA.getX(), cornerA.getY(), cornerA.getZ(), cornerB.getX(), cornerB.getY(), cornerB.getZ(), 1F, 1F, 1F, 1F);
//
////					
////					Matrix4f matrix = matrixStack.last().pose();
////					
////					builder.vertex(matrix, 0, 0, 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), 0, 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), size.getY(), 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, 0, size.getY(), 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					
////					builder.vertex(matrix, 0, 0, size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), 0, size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), size.getY(), size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, 0, size.getY(), size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					
////					builder.vertex(matrix, 0, 0, 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, 0, 0, size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, 0, size.getY(), size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, 0, size.getY(), 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////
////					builder.vertex(matrix, size.getX(), 0, 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), 0, size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), size.getY(), size.getZ()).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					builder.vertex(matrix, size.getX(), size.getY(), 0).color(color.x(), color.y(), color.z(), color.w()).uv(0, 0).overlayCoords(0).uv2(0).normal(1, 1, 1).endVertex();
////					
//				}
//				
//			}
//			
//		}
//		matrixStack.popPose();
//	}
	
}
