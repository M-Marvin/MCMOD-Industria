package de.m_marvin.industria.core.ssdplugins.engine.mixin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.registries.StructureDataPlugins;
import de.m_marvin.industria.core.ssdplugins.engine.IStructureTemplateExtended;
import de.m_marvin.industria.core.ssdplugins.engine.StructureDataPlugin;
import de.m_marvin.industria.core.ssdplugins.engine.StructureDataPlugin.VanillaTemplateData;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.Palette;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateExtender implements IStructureTemplateExtended {
	
	@Shadow
	private final List<StructureTemplate.Palette> palettes = Lists.newArrayList();
	@Shadow
	private final List<StructureTemplate.StructureEntityInfo> entityInfoList = Lists.newArrayList();
	@Shadow
	private Vec3i size = Vec3i.ZERO;
	private final Map<ResourceLocation, Tag> pluginData = Maps.newHashMap();
	
	@Shadow
	public static BlockPos calculateRelativePosition(StructurePlaceSettings pDecorator, BlockPos pPos) { return null; }

	@Shadow
	private static void addToLists(StructureTemplate.StructureBlockInfo pBlockInfo, List<StructureTemplate.StructureBlockInfo> pNormalBlocks, List<StructureTemplate.StructureBlockInfo> pBlocksWithNbt, List<StructureTemplate.StructureBlockInfo> pBlocksWithSpecialShape) {}
	
	@Shadow
	private static List<StructureTemplate.StructureBlockInfo> buildInfoList(List<StructureTemplate.StructureBlockInfo> pNormalBlocks, List<StructureTemplate.StructureBlockInfo> pBlocksWithNbt, List<StructureTemplate.StructureBlockInfo> pBlocksWithSpecialShape) { return null; };
	
	@Override
	public BlockPos fillFromLevelPosIterable(Level pLevel, Iterable<BlockPos> pIterator, Block pToIgnore) {
		
		Optional<BlockPos> blockpos1o = StreamSupport.stream(pIterator.spliterator(), false).reduce(MathUtility::getMinCorner);
		Optional<BlockPos> blockpos2o = StreamSupport.stream(pIterator.spliterator(), false).reduce(MathUtility::getMaxCorner);
		
		if (blockpos1o.isPresent() && blockpos2o.isPresent()) {

			List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
			List<StructureTemplate.StructureBlockInfo> list1 = Lists.newArrayList();
			List<StructureTemplate.StructureBlockInfo> list2 = Lists.newArrayList();
			BlockPos blockpos1 = blockpos1o.get();
			BlockPos blockpos2 = blockpos2o.get();
			
			this.size = blockpos2.subtract(blockpos1).offset(1, 1, 1);
			for(BlockPos blockpos3 : pIterator) {
				BlockPos blockpos4 = blockpos3.subtract(blockpos1);
				
				BlockState blockstate = pLevel.getBlockState(blockpos3);
				if (pToIgnore == null || !blockstate.is(pToIgnore)) {
				   BlockEntity blockentity = pLevel.getBlockEntity(blockpos3);
				   StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo;
				   if (blockentity != null) {
					  structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockpos4, blockstate, blockentity.saveWithId());
				   } else {
					  structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockpos4, blockstate, (CompoundTag)null);
				   }
				   addToLists(structuretemplate$structureblockinfo, list, list1, list2);
				}
			 }

			List<StructureTemplate.StructureBlockInfo> list3 = buildInfoList(list, list1, list2);
			this.palettes.clear();
			
			try {
				Constructor<Palette> cnstr = StructureTemplate.Palette.class.getDeclaredConstructor(List.class);
				cnstr.setAccessible(true);
				this.palettes.add(cnstr.newInstance(list3));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			this.entityInfoList.clear();
			
			// Apply structure data plugin's
			VanillaTemplateData vanillaData = new VanillaTemplateData(this.palettes, this.entityInfoList, this.size);
			this.pluginData.clear();
			for (ResourceLocation pluginLoc : StructureDataPlugins.DATA_PLUGINS_REGISTRY.get().getKeys()) {
				StructureDataPlugin<? extends Tag> plugin = StructureDataPlugins.DATA_PLUGINS_REGISTRY.get().getValue(pluginLoc);
				Optional<? extends Tag> data = plugin.getFillIterableFunc().fillFromWorldPosIterable(pLevel, pIterator, vanillaData);
				if (data.isPresent()) {
					try {
						this.pluginData.put(pluginLoc, data.get());
					} catch (Throwable e) {
						IndustriaCore.LOGGER.error("Exception was thrown while applying structure template plugin: %s", pluginLoc.toString(), e);
					}
				}
			}
			
		}
		
		return blockpos1o.get();
		
	}
	
	@Inject( 
			method = "fillFromWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Vec3i;ZLnet/minecraft/world/level/block/Block;)V",
			at = @At("RETURN")
	)
	private void fillFromWorld( Level pLevel, BlockPos pPos, Vec3i pSize, boolean pWithEntities, @Nullable Block pToIgnore, CallbackInfo callback) {
		
		if (pSize.getX() >= 1 && pSize.getY() >= 1 && pSize.getZ() >= 1) {
			
			BlockPos opositePos = pPos.offset(pSize).offset(-1, -1, -1);
			BlockPos minPos = new BlockPos(Math.min(pPos.getX(), opositePos.getX()), Math.min(pPos.getY(), opositePos.getY()), Math.min(pPos.getZ(), opositePos.getZ()));
			
			// Apply structure data plugin's
			VanillaTemplateData vanillaData = new VanillaTemplateData(this.palettes, this.entityInfoList, this.size);
			this.pluginData.clear();
			for (ResourceLocation pluginLoc : StructureDataPlugins.DATA_PLUGINS_REGISTRY.get().getKeys()) {
				StructureDataPlugin<? extends Tag> plugin = StructureDataPlugins.DATA_PLUGINS_REGISTRY.get().getValue(pluginLoc);
				Optional<? extends Tag> data = plugin.getFillAreaFunc().fillFromWorldArea(pLevel, minPos, pSize, vanillaData);
				if (data.isPresent()) {
					try {
						this.pluginData.put(pluginLoc, data.get());
					} catch (Throwable e) {
						IndustriaCore.LOGGER.error("Exception was thrown while applying structure template plugin: %s", pluginLoc.toString(), e);
					}
				}
			}
			
		}
		
	}
	
	@Inject(at = @At("RETURN"), method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z")
	private void placeInWorld(ServerLevelAccessor pServerLevel, BlockPos pOffset, BlockPos pPos, StructurePlaceSettings pSettings, RandomSource pRandom, int pFlags, CallbackInfoReturnable<Boolean> callback) {
		
		if (callback.getReturnValue() && !this.pluginData.isEmpty()) {
			
			ServerLevel level = pServerLevel.getLevel();

			// Apply structure data plugin's
			VanillaTemplateData vanillaData = new VanillaTemplateData(this.palettes, this.entityInfoList, this.size);
			for (ResourceLocation pluginLoc : this.pluginData.keySet()) {
				@SuppressWarnings("unchecked")
				StructureDataPlugin<Tag> plugin = (StructureDataPlugin<Tag>) StructureDataPlugins.DATA_PLUGINS_REGISTRY.get().getValue(pluginLoc);
				if (plugin == null) {
					IndustriaCore.LOGGER.warn("Not registered structure data plugin in structure: %s, ignoring", pluginLoc.toString());
					continue;
				}
				Tag data = this.pluginData.get(pluginLoc);
				try {
					plugin.getPlaceFunc().placeInWorld(level, pOffset, pSettings, pRandom, pFlags, vanillaData, data);
				} catch (Throwable e) {
					IndustriaCore.LOGGER.error("Exception was thrown while applying structure template plugin: %s", pluginLoc.toString(), e);
				}
			}
			
		}
		
	}
	
	@Inject(at = @At("RETURN"), method = "save(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;")
	private void save(CompoundTag pTag, CallbackInfoReturnable<CompoundTag> callback) {
		CompoundTag pluginTag = new CompoundTag();
		this.pluginData.forEach((k, v) -> pluginTag.put(k.toString(), v));
		pTag.put("plugins", pluginTag);
	}
	
	@Inject(at = @At("RETURN"), method = "load(Lnet/minecraft/core/HolderGetter;Lnet/minecraft/nbt/CompoundTag;)V")
	private void load(HolderGetter<Block> pBlockGetter, CompoundTag pTag, CallbackInfo callback) {
		this.pluginData.clear();
		CompoundTag pluginTag = pTag.getCompound("plugins");
		pluginTag.getAllKeys().forEach(k -> {
			this.pluginData.put(ResourceLocation.tryParse(k), pluginTag.get(k));
		});
	}
	
}
