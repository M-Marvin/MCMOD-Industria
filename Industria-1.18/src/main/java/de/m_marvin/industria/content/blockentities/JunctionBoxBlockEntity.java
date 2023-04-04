package de.m_marvin.industria.content.blockentities;

import java.util.Map;

import de.m_marvin.industria.content.blocks.JunctionBoxBlock;
import de.m_marvin.industria.content.container.JunctionBoxContainer;
import de.m_marvin.industria.content.registries.ModBlockEntities;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class JunctionBoxBlockEntity extends BlockEntity implements MenuProvider {
	
	public JunctionBoxBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntities.JUNCTION_BOX.get(), pPos, pBlockState);
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		// TODO Auto-generated method stub
		super.saveAdditional(pTag);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		// TODO Auto-generated method stub
		super.load(pTag);
	}
	
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new JunctionBoxContainer(pContainerId, pPlayerInventory, this);
	}
	
	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("test");
	}
	
	public NodePos[] getUDLRCableNodes(Direction playerFacing) {
		
		BlockState state = this.level.getBlockState(this.worldPosition);
		if (state.getBlock() == ModBlocks.JUNCTION_BOX.get()) {
			
			Map<Direction, NodePos> cableNodes = ((JunctionBoxBlock) state.getBlock()).getBlockRelativeCableNodes(level, state, worldPosition);
			Direction blockFacing = state.getValue(BlockStateProperties.FACING);
			
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
						cableNodes.get(Direction.fromYRot(angle)), 
						cableNodes.get(Direction.fromYRot(180 + angle)),
						cableNodes.get(Direction.fromYRot(-90 + angle)),
						cableNodes.get(Direction.fromYRot(90 + angle))
				};
			}
						
		}
		
		return new NodePos[] {};
		
	}
	
}
