package de.m_marvin.industria.core.conduits.types.conduits;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.nbt.CompoundTag;

public interface IElectricConduit extends IElectric<PlacedConduit, ConduitPos, Conduit> {
	
//	@Override
//	default ConnectionPoint[] getConnections(Level level, ConduitPos pos, PlacedConduit instance) {
////		BlockState state1 = level.getBlockState(instance.getNodeA());
////		BlockState state2 = level.getBlockState(instance.getNodeB());
////		ConduitNode[] points1 = state1.getBlock() instanceof IConduitConnector ? ((IConduitConnector)state1.getBlock()).getConduitNodes(level, instance.getNodeA(), state1) : null;
////		ConduitNode[] points2 = state2.getBlock() instanceof IConduitConnector ? ((IConduitConnector)state2.getBlock()).getConduitNodes(level, instance.getNodeB(), state2) : null;
////		List<ConduitNode> points = new ArrayList<ConduitNode>();
////		if (points1 != null && points1.length > instance.getConnectionPointA()) points.add(points1[instance.getConnectionPointA()]);
////		if (points2 != null && points2.length > instance.getConnectionPointB()) points.add(points2[instance.getConnectionPointB()]);
////		return points.toArray((length) -> new ConduitNode[length]);
//		return null;
//	}

	@Override
	default void serializeNBT(PlacedConduit instance, ConduitPos position, CompoundTag nbt) {
		nbt.put("State", instance.save());
		nbt.put("Position", position.writeNBT(new CompoundTag()));
	}

	@Override
	default PlacedConduit deserializeNBTInstance(CompoundTag nbt) {
		return PlacedConduit.load(nbt.getCompound("State"));
	}
	
	@Override
	default ConduitPos deserializeNBTPosition(CompoundTag nbt) {
		return ConduitPos.readNBT(nbt.getCompound("Position"));
	}
	
}
