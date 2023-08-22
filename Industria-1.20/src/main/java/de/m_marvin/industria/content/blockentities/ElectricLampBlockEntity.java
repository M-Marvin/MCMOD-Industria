package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricLampBlockEntity extends BlockEntity implements MenuProvider, IJunctionEdit {
	
	protected String[] nodeLanes = new String[] {"+", "-"};
	
	public ElectricLampBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.ELECTRIC_LAMP.get(), pPos, pBlockState);
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		pTag.putString("PositiveLane", this.nodeLanes[0]);
		pTag.putString("NegativeLane", this.nodeLanes[1]);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.getString("PositiveLane");
		this.nodeLanes[1] = pTag.getString("NegativeLane");
	}
	
	public String[] getNodeLanes() {
		return nodeLanes;
	}
	
	public void setNodeLanes(String[] nodeLanes) {
		this.nodeLanes = nodeLanes;
		this.setChanged();
	}
	
	@Override
	public NodePos[] getEditCableNodes(Direction playerFacing, Direction playerHorizontalFacing) {
		Level level = this.getLevel();
		BlockPos pos = this.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof IElectricConnector connector) {
			return connector.getConnections(level, pos, state);
		}
		return new NodePos[] {};
	}

	@Override
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(
			JunctionBoxContainer<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,
			ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		externalNodeConstructor.construct(new Vec2i(70, 8), 	Direction2d.UP, 	conduitNodes[0]);
		internalNodeConstructor.construct(new Vec2i(70, 112), 	Direction2d.DOWN, 	0);
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new JunctionBoxContainer<ElectricLampBlockEntity>(pContainerId, pPlayerInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}
	
}
