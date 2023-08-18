package de.m_marvin.industria.core.electrics.types.blockentities;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.electrics.types.containers.PowerSourceJunctionContainer;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
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

public class PowerSourceBlockEntity extends BlockEntity implements MenuProvider, IJunctionEdit {
	
	protected String[] internalNodeLanes = new String[] {"L", "N"};
	
	public PowerSourceBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(BlockEntityTypes.POWER_SOURCE.get(), pPos, pBlockState);
	}
	
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new PowerSourceJunctionContainer(pContainerId, pPlayerInventory, this); 
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
	
	public String[] getWireLabels() {
		return this.internalNodeLanes;
	}
	
	public void setWireLabels(String[] laneLabels) {
		if (laneLabels.length == 2) {
			this.internalNodeLanes = laneLabels;
			this.setChanged();
		}
	}
	
	@Override
	public Component getDisplayName() {
 		return Component.translatable("industria.block.power_source");
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putString("LiveWireLane", this.internalNodeLanes[0]);
		pTag.putString("NeutralWireLane", this.internalNodeLanes[1]);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.internalNodeLanes[0] = pTag.contains("LiveWireLane") ? pTag.getString("LiveWireLane") : "L";
		this.internalNodeLanes[1] = pTag.contains("NeutralWireLane") ? pTag.getString("NeutralWireLane") : "N";
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString("LiveWireLane", this.internalNodeLanes[0]);
		tag.putString("NeutralWireLane", this.internalNodeLanes[1]);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
}
