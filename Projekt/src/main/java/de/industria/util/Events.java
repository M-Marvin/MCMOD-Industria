package de.industria.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import de.industria.Industria;
import de.industria.fluids.util.BlockGasFluid;
import de.industria.util.handler.ChunkLoadHandler;
import de.industria.util.handler.JigsawFileManager;
import de.industria.util.handler.MinecartHandler;
import de.industria.util.handler.ModGameRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Events {
	
	@SubscribeEvent
	public static void onResourceManagerReload(net.minecraftforge.event.AddReloadListenerEvent event) {
		JigsawFileManager.onFileManagerReload();
	}
	
	@SubscribeEvent
	public static void onRightClickBlock(net.minecraftforge.event.entity.player.FillBucketEvent event) {

		World worldIn = event.getWorld();
		PlayerEntity playerIn = event.getPlayer();
		
		RayTraceResult result = event.getTarget();
		Vector3d vec1p = event.getPlayer().getLookVec();
		int x = (int) (result.getHitVec().x % 1 == 0 ? result.getHitVec().x + (vec1p.x > 0 ? 0 : -1) : Math.floor(result.getHitVec().x));
		int y = (int) (result.getHitVec().y % 1 == 0 ? result.getHitVec().y + (vec1p.y > 0 ? 0 : -1) : Math.floor(result.getHitVec().y));
		int z = (int) (result.getHitVec().z % 1 == 0 ? result.getHitVec().z + (vec1p.z > 0 ? 0 : -1) : Math.floor(result.getHitVec().z));
		BlockPos fluidPos = new BlockPos(x, y, z);
		FluidState fluidState = worldIn.getFluidState(fluidPos);
		
		if (fluidState.getBlockState().getBlock() instanceof BlockGasFluid) {
			
			ItemStack bucketItem = playerIn.getHeldItemMainhand();
						
			if (fluidState.getBlockState().getBlock() instanceof IBucketPickupHandler) {
				Fluid fluid = ((IBucketPickupHandler)fluidState.getBlockState().getBlock()).pickupFluid(worldIn, fluidPos, fluidState.getBlockState());
				
				if (fluid != Fluids.EMPTY) {
					
					playerIn.addStat(Stats.ITEM_USED.get(bucketItem.getItem()));
					
					SoundEvent soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
					playerIn.playSound(soundevent, 1.0F, 1.0F);
					ItemStack itemstack1 = DrinkHelper.fill(bucketItem.copy(), playerIn, new ItemStack(fluid.getFilledBucket()));
					if (!worldIn.isRemote) {
						CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)playerIn, new ItemStack(fluid.getFilledBucket()));
						playerIn.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack1);
					}
					
				}
			}
			
		}
		
	}
	
	protected static long lastServerWorldTick = 0L;
	@SubscribeEvent
	public static final void onWorldTick(net.minecraftforge.event.TickEvent.WorldTickEvent event) {
		if (!event.world.isRemote() && event.phase == Phase.START && event.side == LogicalSide.SERVER) {
			if (lastServerWorldTick != event.world.getGameTime()) {
				lastServerWorldTick = event.world.getGameTime();
				MinecartHandler.getHandlerForWorld(event.world).updateMinecarts();
			}
			if (event.world.loadedTileEntityList.size() > 0) {
				ChunkLoadHandler.getHandlerForWorld(event.world).updateChunkForceLoads();
			}
		}
	}
	
	@SubscribeEvent
	public void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		for (GenerationStage.Decoration decoration : GenerationStage.Decoration.values()) {
			List<ConfiguredFeature<?, ?>> features = ModGameRegistry.getFeaturesToRegister().getOrDefault(event.getName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>()).getOrDefault(decoration, new ArrayList<>());
			for (ConfiguredFeature<?, ?> feature : features) {
				event.getGeneration().getFeatures(decoration).add(() -> feature);
			}
			
			List<Supplier<ConfiguredFeature<?, ?>>> featuresToRemove = new ArrayList<Supplier<ConfiguredFeature<?, ?>>>();
			event.getGeneration().getFeatures(decoration).forEach((registredFeature) -> {
				if (compareBiomes(registredFeature.get(), Features.OAK)) featuresToRemove.add(registredFeature);
				// This does not work, registredFeature always is an "minecraft:decorated" feature ...
			});
			System.out.println(featuresToRemove.size() + " Features deaktivated");
			event.getGeneration().getFeatures(decoration).removeAll(featuresToRemove);
			
		}
		
	}

	protected boolean compareBiomes(ConfiguredFeature<?, ?> registredFeature, ConfiguredFeature<?, ?> feature) {
		// TODO Not Working
		Optional<JsonElement> registredJson = ConfiguredFeature.field_242763_a.encode(registredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
		Optional<JsonElement> searchedJson = ConfiguredFeature.field_242763_a.encode(feature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
		if (!registredJson.isPresent() || !searchedJson.isPresent()) return false;
		JsonObject json1 = registredJson.get().getAsJsonObject();
		JsonObject json2 = searchedJson.get().getAsJsonObject();
		System.out.println(json1);
		return json1.equals(json2);
	}
	
}
