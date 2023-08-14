package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.container.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.blockentities.AbstractJunctionBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class JunctionBoxBlockEntity extends AbstractJunctionBoxBlockEntity {

	public JunctionBoxBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(pPos, pBlockState);
	}

	@Override
	public Component getDisplayName() {
		return new TextComponent("junction_box_test");
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new JunctionBoxContainer(pContainerId, pPlayerInventory, this);
	}
	
}
