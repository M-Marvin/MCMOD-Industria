package de.m_marvin.industria.core.electrics.types.blockentities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.m_marvin.industria.content.blocks.JunctionBoxBlock;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.core.client.electrics.JunctionBoxContainer;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface IEditableJunction<T extends BlockEntity & IEditableJunction<T>> extends MenuProvider {
	
	public T getContainerBlockEntity();
	
	@Override
	default AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new JunctionBoxContainer<T>(pContainerId, pPlayerInventory, getContainerBlockEntity());
	}
	
	public default String[] getCableWireLabels(NodePos node) {
		List<String[]> cableLaneLabels = ElectricUtility.getLaneLabels(getContainerBlockEntity().getLevel(), node);
		if (cableLaneLabels.size() >= 1) {
			return cableLaneLabels.get(0);
		} else {
			return new String[] {};
		}
	}
	
	public default void setCableWireLabels(NodePos node, String[] laneLabels) {
		ElectricUtility.setLaneLabels(getContainerBlockEntity().getLevel(), node, laneLabels);
	}
	
	public default NodePos[] getUDLRCableNodes(Direction playerFacing) {
		
		Level level = getContainerBlockEntity().getLevel();
		BlockPos pos = getContainerBlockEntity().getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() == ModBlocks.JUNCTION_BOX.get()) {
			
			Map<Direction, NodePos> cableNodes = getBlockRelativeCableNodes(state, pos);
			Direction blockFacing = ((JunctionBoxBlock) state.getBlock()).getBlockFacing(level, state, pos);
			
			if (blockFacing.getStepY() == 0) {
				Direction left = blockFacing.getCounterClockWise();
				return new NodePos[] {
						cableNodes.get(Direction.UP), 
						cableNodes.get(Direction.DOWN), 
						cableNodes.get(left), 
						cableNodes.get(left.getOpposite())
				};
			} else {
				float angle = playerFacing.toYRot();
				return new NodePos[] {
						cableNodes.get(Direction.fromYRot(0 + angle)), 
						cableNodes.get(Direction.fromYRot(180 + angle)),
						cableNodes.get(Direction.fromYRot(-90 + angle)),
						cableNodes.get(Direction.fromYRot(90 + angle))
				};
			}
			
		}
		
		return new NodePos[] {};
		
	}
	
	public default Map<Direction, NodePos> getBlockRelativeCableNodes(BlockState state, BlockPos position) {
		if (state.getBlock() instanceof IElectricConnector connectorBlock) {
			Level level = getContainerBlockEntity().getLevel();
			NodePos[] nodes = connectorBlock.getConnections(level, position, state);
			ConduitNode[] connections = connectorBlock.getConduitNodes(level, position, state);
			Vec3i center = new Vec3i(8, 8, 8);
			Direction blockFacing = state.getValue(BlockStateProperties.FACING);
			switch (blockFacing.getAxis()) {
				case X: center.setX(blockFacing.getAxisDirection() == AxisDirection.NEGATIVE ? 0 : 16); break;
				case Y: center.setY(blockFacing.getAxisDirection() == AxisDirection.NEGATIVE ? 0 : 16); break;
				case Z: center.setZ(blockFacing.getAxisDirection() == AxisDirection.NEGATIVE ? 0 : 16); break;
			}
			Map<Direction, NodePos> cables = new HashMap<>();
			for (int i = 0; i < connections.length; i++) {
				Vec3d dVf = PhysicUtility.ensureWorldCoordinates(level, position, new Vec3d(connections[i].getOffset())).sub(
							PhysicUtility.ensureWorldCoordinates(level, position, new Vec3d(center))).div(8.0);
				Vec3i dVec = new Vec3i((int) Math.round(dVf.x), (int) Math.round(dVf.y), (int) Math.round(dVf.z));
				Direction d = MathUtility.getVecDirection(dVec);
				cables.put(d, nodes[i]);
			}
			return cables;
		}
		return new HashMap<>();
	}
	
	
}
