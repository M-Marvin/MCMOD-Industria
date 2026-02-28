package de.m_marvin.industria.core.electrics.types.blockentities;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitElement;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitNode;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxMenu;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxMenu.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxMenu.InternalNodeConstructor;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.util.container.FriendlyContainerData;
import de.m_marvin.industria.core.util.container.IDataSlotContainer;
import de.m_marvin.industria.core.util.types.PlanarDirection;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSourceBlockEntity extends BlockEntity implements MenuProvider, IDataSlotContainer, IJunctionEdit {

	protected String[] nodeLanes = new String[] {"L", "N"};
	protected int power;
	
	public AbstractSourceBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(BlockEntityTypes.VOLTAGE_SOURCE.get(), pPos, pBlockState);
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(Blocks.VOLTAGE_SOURCE.get());
		this.power = parametrics.getNominalPower();
	}
	
	public String[] getNodeLanes() {
		return this.nodeLanes;
	}
	
	public void getNodeLanes(String[] laneLabels) {
		if (laneLabels.length == 2) {
			this.nodeLanes = laneLabels;
			this.setChanged();
		}
	}
	
	public void setPower(int power) {
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(Blocks.VOLTAGE_SOURCE.get());
		this.power = Math.max(parametrics.getPowerMin(), Math.min(parametrics.getPowerMax(), power));
		this.setChanged();
	}
	
	public int getPower() {
		return power;
	}

	public abstract double getDeviceCurrent();
	// since the element names of the sources circuit are defined in the block, leave it to the source block implementation
	// to call the function below with the correct element name
	
	protected double getDeviceCurrent(String sourceElement) {
		if (getBlockState().getBlock() instanceof IElectricBlock electric) {
			BlockPos masterPos = electric.getConnectorMasterPos(level, worldPosition, getBlockState());
			ElectricReference reference = ElectricReference.block(masterPos);
			NodePos[] nodes = electric.getElectricConnections(level, reference, getBlockState());
			if (nodes.length >= 1) {
				return ElectricUtility.getElementCurrent(level, CircuitElement.element(reference, sourceElement));
			}
		}
		return 0.0;
	}
	
	public double getDeviceVoltage() {
		// we assume here that all conduit nodes on the block serve the same function and have exactly two connectors for power
		// more complex sources might need to override this
		if (getBlockState().getBlock() instanceof IElectricBlock electric) {
			BlockPos masterPos = electric.getConnectorMasterPos(level, worldPosition, getBlockState());
			ElectricReference reference = ElectricReference.block(masterPos);
			NodePos[] nodes = electric.getElectricConnections(level, reference, getBlockState());
			if (nodes.length >= 1) {
				String[] lanes = electric.getWireLanes(level, reference, getBlockState(), nodes[0]);
				if (lanes.length >= 2) {
					CircuitNode node1 = CircuitNode.node(nodes[0], lanes[0]);
					CircuitNode node2 = CircuitNode.node(nodes[0], lanes[1]);
					return ElectricUtility.getVoltageBetween(level, node1, node2);
				}
			}
		}
		return 0.0;
	}
	
//	public double getCurrentPowerProduction() {
//		if (getBlockState().getBlock() instanceof IElectricBlock block) {
//			return block.getCurrentPower(level, ElectricReference.block(worldPosition), getBlockState());
//		}
//		return 0.0;
//	}
	
	@Override
	public Component getDisplayName() {
 		return this.getBlockState().getBlock().getName();
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
		pTag.putInt("Power", this.power);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.contains("LiveWireLane") ? pTag.getString("LiveWireLane") : "L";
		this.nodeLanes[1] = pTag.contains("NeutralWireLane") ? pTag.getString("NeutralWireLane") : "N";
		this.power = pTag.getInt("Power");
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString("LiveWireLane", this.nodeLanes[0]);
		tag.putString("NeutralWireLane", this.nodeLanes[1]);
		tag.putInt("Power", this.power);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	@Override
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(JunctionBoxMenu<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		externalNodeConstructor.construct(new Vec2i(69, 8), 	PlanarDirection.Y_POS, 	conduitNodes[0]);
		internalNodeConstructor.construct(new Vec2i(69, 112), 	PlanarDirection.Y_NEG, 	0);
	}

	@Override
	public boolean connectsOnlyToInternal() {
		return true;
	}
	
	@Override
	public Level getJunctionLevel() {
		return this.level;
	}
	
	@Override
	public BlockPos getJunctionBlockPos() {
		return this.worldPosition;
	}
	
}
