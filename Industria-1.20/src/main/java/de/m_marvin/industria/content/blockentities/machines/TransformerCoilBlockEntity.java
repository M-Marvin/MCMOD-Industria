package de.m_marvin.industria.content.blockentities.machines;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TransformerCoilBlockEntity extends ElectroMagneticCoilBlockEntity implements IJunctionEdit, MenuProvider {
	
	protected String[] nodeLanes = {"L", "N"};
	
	public TransformerCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.TRANSFORMER_COIL.get(), pPos, pBlockState);
	}
	
	public String[] getNodeLanes() {
		return nodeLanes;
	}
	
	public void setNodeLanes(String[] nodeLanes) {
		this.nodeLanes = nodeLanes;
		this.setChanged();
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.getString("LiveWireLane");
		this.nodeLanes[1] = pTag.getString("NeutralWireLane");
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putString("LiveWireLane", this.nodeLanes[0]);
		tag.putString("NeutralWireLane", this.nodeLanes[1]);
		return tag;
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return GameUtility.openJunctionScreenOr(this, pContainerId, pPlayer, pPlayerInventory, () -> null);
	}

	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}

	@Override
	public Level getJunctionLevel() {
		return this.level;
	}

	@Override
	public BlockPos getJunctionBlockPos() {
		return this.worldPosition;
	}

	@Override
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(
			JunctionBoxContainer<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,
			ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		externalNodeConstructor.construct(new Vec2i(8, 70), 	Direction2d.LEFT, 	conduitNodes[1]);
		externalNodeConstructor.construct(new Vec2i(112, 70), 	Direction2d.RIGHT, 	conduitNodes[0]);
		internalNodeConstructor.construct(new Vec2i(70, 112), 	Direction2d.DOWN, 	0);
	}
	
}
