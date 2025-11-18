package de.m_marvin.industria.core.ssdplugins.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.utils.Lists;

import de.m_marvin.industria.core.conduits.engine.ConduitHolderCapability;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.ssdplugins.engine.StructureDataPlugin;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class ConduitDataPlugin extends StructureDataPlugin<ListTag> {
	
	public ConduitDataPlugin() {
		super(ConduitDataPlugin::fillInConduitsArea, ConduitDataPlugin::fillFromWorldPosIterable, ConduitDataPlugin::placeInWorld);
	}
	
	private static Optional<ListTag> fillFromWorldPosIterable(Level level, Iterable<BlockPos> posItr, VanillaTemplateData vanillaData) {

		Optional<BlockPos> minPosOpt = StreamSupport.stream(posItr.spliterator(), false).reduce(MathUtility::getMinCorner);
		if (minPosOpt.isEmpty()) return Optional.empty();
		BlockPos minPos = minPosOpt.get();

		ListTag pluginData = new ListTag();
		serializeConduits(level, minPos, vanillaData, pluginData);
		
		return pluginData.size() > 0 ? Optional.of(pluginData) : Optional.empty();
		
	}
	
	private static Optional<ListTag> fillInConduitsArea(Level level, BlockPos origin, Vec3i size, VanillaTemplateData vanillaData) {
		
		ListTag pluginData = new ListTag();
		serializeConduits(level, origin, vanillaData, pluginData);

		return pluginData.size() > 0 ? Optional.of(pluginData) : Optional.empty();
		
	}
	
	private static boolean placeInWorld(ServerLevel level, BlockPos offset, StructurePlaceSettings settings, RandomSource random, int flags, VanillaTemplateData vanillaData, ListTag pluginData) {
		
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		
		for (Tag conduitTag : pluginData) {
			if (conduitTag instanceof CompoundTag conduitNbt) {
				
				// Calculate new position
				ConduitEntity conduitEntity = ConduitEntity.load(conduitNbt);
				BlockPos np1 = calculateRelativePosition(settings, conduitEntity.getPosition().getNodeApos()).offset(offset);
				BlockPos np2 = calculateRelativePosition(settings, conduitEntity.getPosition().getNodeBpos()).offset(offset);
				ConduitPos position = new ConduitPos(np1, np2, conduitEntity.getPosition().getNodeAid(), conduitEntity.getPosition().getNodeBid());
				
				// If position already occupied, remove current conduit
				Optional<ConduitEntity> replacedConduitEntity = handler.getConduit(position);
				if (replacedConduitEntity.isPresent() && !replacedConduitEntity.get().getConduitState().equals(conduitEntity.getConduitState())) {
					handler.removeConduit(replacedConduitEntity.get());
				}
				
				// Trigger client update to make sure the node blocks are already loaded on the client before placing the conduit
				GameUtility.triggerClientSync(level, np1);
				GameUtility.triggerClientSync(level, np2);
				GameUtility.triggerUpdate(level, np1);
				GameUtility.triggerUpdate(level, np2);
				
				// Place conduit
				if (handler.placeConduit(position, conduitEntity.getConduitState(), conduitEntity.getLength())) {
					Optional<ConduitEntity> placedConduitEntity = handler.getConduit(position);
					if (placedConduitEntity.isPresent()) {
						placedConduitEntity.get().loadAdditional(conduitNbt);
					}
				}
				
			}
		}
		
		return true;
		
	}
	
	private static void serializeConduits(Level level, BlockPos origin, VanillaTemplateData vanillaData, ListTag pluginData) {

		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		
		// Search for conduits connected to blocks of the template and all blocks part of the template
		List<ConduitEntity> conduitsInBounds = Lists.newArrayList();
		List<BlockPos> templateBlocks = Lists.newArrayList();
		for (StructureTemplate.Palette palette : vanillaData.blockPalettes()) {
			palette.blocks().stream()
				.flatMap(s -> {
					if (s.state().getBlock() instanceof IConduitConnector) {
						return handler.getConduitsAtBlock(s.pos().offset(origin)).stream();
					}
					return Stream.of();
				})
				.forEach(conduitsInBounds::add);
			palette.blocks().stream()
				.map(StructureBlockInfo::pos)
				.forEach(templateBlocks::add);
		}

		// Filter and serialize conduits to NBT
		pluginData.clear();
		conduitsInBounds.stream()
			.distinct()
			.filter(c -> {
				return	templateBlocks.contains(c.getPosition().getNodeApos().subtract(origin)) &&
						templateBlocks.contains(c.getPosition().getNodeBpos().subtract(origin));
			})
			.map(c -> c.save(origin))
			.forEach(pluginData::add);
		
	}
	
}
