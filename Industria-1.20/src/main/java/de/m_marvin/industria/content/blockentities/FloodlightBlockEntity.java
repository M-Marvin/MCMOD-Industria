package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.blocks.FloodlightBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FloodlightBlockEntity extends BlockEntity implements MenuProvider, IJunctionEdit {
	
	protected String[] nodeLanes = new String[] {"L", "N"};
	
	public FloodlightBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.FLOODLIGHT.get(), pPos, pBlockState);
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
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(
			JunctionBoxContainer<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,
			ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		externalNodeConstructor.construct(new Vec2i(70, 8), 	Direction2d.UP, 	conduitNodes[1]);
		externalNodeConstructor.construct(new Vec2i(8, 70), 	Direction2d.LEFT, 	conduitNodes[2]);
		externalNodeConstructor.construct(new Vec2i(112, 70), 	Direction2d.RIGHT, 	conduitNodes[0]);
		internalNodeConstructor.construct(new Vec2i(70, 112), 	Direction2d.DOWN, 	0);
	}
	
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return GameUtility.openJunctionScreenOr(this, pContainerId, pPlayer, pPlayerInventory, () -> null);
	}
	
	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}
	
	public void updateLight() {
		
		double voltage = ElectricUtility.getVoltageBetween(level, new NodePos(worldPosition, 0), new NodePos(worldPosition, 0), nodeLanes[0], nodeLanes[1]);
		double overshoot = ElectricUtility.getPowerOvershoot(voltage, FloodlightBlock.TARGET_VOLTAGE);
		double power = ElectricUtility.getPowerPercentage(voltage * FloodlightBlock.TARGET_POWER / FloodlightBlock.TARGET_VOLTAGE, FloodlightBlock.TARGET_POWER);
		
		boolean shouldLit = power > 0.8;
		if (getBlockState().getValue(BlockStateProperties.LIT) != shouldLit) this.level.setBlockAndUpdate(this.worldPosition, getBlockState().setValue(BlockStateProperties.LIT, shouldLit));
		
		if (this.level.random.nextFloat() < overshoot) {
			System.err.println("BOOM!"); // TODO
		}
		
	}
	
}
