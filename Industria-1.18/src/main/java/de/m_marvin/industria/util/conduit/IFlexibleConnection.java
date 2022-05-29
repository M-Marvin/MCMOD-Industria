package de.m_marvin.industria.util.conduit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.registries.ModRegistries;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public interface IFlexibleConnection {
	
	public ConnectionPoint[] getConnectionPoints(BlockGetter level, BlockPos pos, BlockState state);
	
	public default PlacedConduit[] getConnectedConduits(Level level, BlockPos pos, BlockState state) {
		List<PlacedConduit> connections = new ArrayList<>();
		for (ConnectionPoint node : getConnectionPoints(level, pos, state)) {
			LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
			if (conduitHolder.isPresent()) {
				Optional<PlacedConduit> conduit = conduitHolder.resolve().get().getConduit(node);
				if (conduit.isPresent()) {
					connections.add(conduit.get());
				}
			}
		}
		return connections.toArray(new PlacedConduit[] {});
	}
	
	public default boolean connectionAviable(Level level, BlockPos pos, BlockState state) {
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			for (ConnectionPoint con : getConnectionPoints(level, pos, state)) {
				if (!conduitHolder.resolve().get().getConduit(con).isPresent()) return true;
			}
		}
		return false;
	}
	
	public static record ConnectionPoint(
		BlockPos position,
		int connectionId,
		Vec3i offset,
		float angle,
		Direction attachmentFace
	) {}
	
}
