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
	public static void onWorldLoad(net.minecraftforge.event.world.WorldEvent.Load event) {
		BlockBurnManager.reloadBurnBehaviors();
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
		
		Optional<JsonElement> registredJson = ConfiguredFeature.field_242763_a.encode(registredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
		if (!registredJson.isPresent()) return false;
		JsonObject json1 = registredJson.get().getAsJsonObject();
		
		String jsonString = json1.toString();
		String jsonString2 = ConfiguredFeature.field_242763_a.encode(feature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left().get().toString();
//		
//		if (jsonString.contains("oak_leaves")) System.out.println(jsonString + "\n" + jsonString2);
//		if (jsonString.contains(jsonString2)) System.out.println("TSET");//System.out.println(ConfiguredFeature.field_242763_a.encode(Features.OAK, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left().get().toString() + " " + jsonString);
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
				BooleanProperty fireProp = fireStates[d.getIndex()];
				if (fireProp != null ? state.get(fireProp) : true) {
					BlockPos fireCatchingBlockPos = pos.offset(d);
					BlockState fireCatchingBlockState = event.getWorld().getBlockState(fireCatchingBlockPos);
					if (!fireCatchingBlockState.isFlammable(event.getWorld(), fireCatchingBlockPos, d.getOpposite())) return;
					if (fireCatchingBlockState.getBlock() instanceof DoorBlock) {
						boolean otherLower = fireCatchingBlockState.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
						BlockPos otherHalf = otherLower ? fireCatchingBlockPos.up() : fireCatchingBlockPos.down();
						BlockState otherState = event.getWorld().getBlockState(otherHalf);
						BlockState[] burnedStates = BlockBurnManager.getBurnedVariants(new BlockState[] {fireCatchingBlockState, otherState}, event.getWorld(), fireCatchingBlockPos);
						if (fireCatchingBlockState != burnedStates[0]) {
							event.getWorld().setBlockState(!otherLower ? otherHalf : fireCatchingBlockPos, Blocks.AIR.getDefaultState(), 0);
							event.getWorld().setBlockState(otherLower ? otherHalf : fireCatchingBlockPos, Blocks.AIR.getDefaultState(), 0);
							event.getWorld().setBlockState(fireCatchingBlockPos, burnedStates[0], 2);
							event.getWorld().setBlockState(otherHalf, burnedStates[1], 2);
						}
					} else {
						BlockState burnedBlockState = BlockBurnManager.getBurnedVariant(fireCatchingBlockState, event.getWorld(), fireCatchingBlockPos);
						if (fireCatchingBlockState != burnedBlockState) {			
							event.getWorld().setBlockState(fireCatchingBlockPos, burnedBlockState, 3);
						}
					}
					
				}
			}
			
		}
	}
	
	
	
}
