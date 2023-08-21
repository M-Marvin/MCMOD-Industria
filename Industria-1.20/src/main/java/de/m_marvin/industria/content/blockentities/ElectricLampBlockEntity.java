package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricLampBlockEntity extends BlockEntity implements MenuSupplier<JunctionBoxContainer<ElectricLampBlockEntity>>, IJunctionEdit {

	public ElectricLampBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.ELECTRIC_LAMP.get(), pPos, pBlockState);
	}

	@Override
	public NodePos[] getEditCableNodes(Direction playerFacing, Direction playerHorizontalFacing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(
			JunctionBoxContainer<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,
			ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		
		
		
	}

	@Override
	public JunctionBoxContainer<ElectricLampBlockEntity> create(int pContainerId, Inventory pPlayerInventory) {
		return new JunctionBoxContainer<ElectricLampBlockEntity>(pContainerId, pPlayerInventory, this);
	}
	
}
