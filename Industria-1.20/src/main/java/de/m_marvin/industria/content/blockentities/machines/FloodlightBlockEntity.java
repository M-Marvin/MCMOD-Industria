package de.m_marvin.industria.content.blockentities.machines;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.content.blocks.machines.FloodlightBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.parametrics.DeviceParametrics;
import de.m_marvin.industria.core.electrics.parametrics.DeviceParametricsManager;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FloodlightBlockEntity extends BlockEntity implements MenuProvider, IJunctionEdit {
	
	protected String[] nodeLanes = new String[] {"L", "N"};
	protected List<BlockPos> lightBlocks = new ArrayList<>();
	
	public FloodlightBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.FLOODLIGHT.get(), pPos, pBlockState);
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		pTag.putString("PositiveLane", this.nodeLanes[0]);
		pTag.putString("NegativeLane", this.nodeLanes[1]);
		ListTag lightBlockTag = new ListTag();
		for (BlockPos lightBlock : this.lightBlocks) {
			lightBlockTag.add(NbtUtils.writeBlockPos(lightBlock));
		}
		pTag.put("LightBlocks", lightBlockTag);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.getString("PositiveLane");
		this.nodeLanes[1] = pTag.getString("NegativeLane");
		ListTag lightBlockTags = pTag.getList("LightBlocks", ListTag.TAG_COMPOUND);
		this.lightBlocks.clear();
		for (int i = 0; i < lightBlockTags.size(); i++) {
			this.lightBlocks.add(NbtUtils.readBlockPos(lightBlockTags.getCompound(i)));
		}
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
		
		DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this.getBlockState().getBlock());
		
		double voltage = ElectricUtility.getVoltageBetween(level, new NodePos(worldPosition, 0), new NodePos(worldPosition, 0), 0, 1, nodeLanes[0], nodeLanes[1]);
		double voltageP = parametrics.getVoltageOvershoot(voltage);
		double powerP = parametrics.getPowerPercentageV(voltage);
		
		boolean shouldLit = powerP >= 1;
		if (getBlockState().getValue(BlockStateProperties.LIT) != shouldLit) setLightState(shouldLit);
		
		if (this.level.random.nextFloat() < parametrics.getExplodeChance(voltageP, powerP)) {
			System.err.println("BOOM!"); // TODO
			
		}
		
	}
	
	public void setLightState(boolean lit) {

		Direction facing = ((FloodlightBlock) getBlockState().getBlock()).getLightDirection(getBlockState());
		this.level.setBlockAndUpdate(this.worldPosition, getBlockState().setValue(BlockStateProperties.LIT, lit));
		
		if (lit) {
			createLightBlocks(worldPosition.relative(facing), facing);
		} else {
			clearLightBlocks();
		}
		
	}
	
	public boolean canShineTrough(BlockPos position) {
		BlockState state = this.level.getBlockState(position);
		return !state.isCollisionShapeFullBlock(level, position) || state.isAir() || state.getBlock() == ModBlocks.LIGHT_AIR.get();
	}
	
	public static final int MAX_RANGE = 32;
	public static final int SPREAD = 16;
	
	public void createLightBlocks(BlockPos origin, Direction direction) {
		
		int length = 0;
		for (int i = 0; i < MAX_RANGE; i++) {
			if (!canShineTrough(origin.relative(direction, i))) break;
			length++;
		}
		
		for (int i = 0; i < length; i++) {
			BlockPos pos = origin.relative(direction, i);
			int spread = (int) ((i / (double) MAX_RANGE) * SPREAD);
			int level = 15;
			
			if (this.level.getBlockState(pos).isAir()) {
				this.lightBlocks.add(pos);
				this.level.setBlockAndUpdate(pos, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
			}
			
			for (int i2 = -spread / 2; i2 < spread / 2; i2++) {
				
				if (direction.getAxis() != Axis.Y && this.level.random.nextFloat() > 0.8F) {
					BlockPos pos1 = pos.offset(0, i2, 0);
					BlockPos pos2 = pos.offset(0, -i2, 0);
					if (this.level.getBlockState(pos1).isAir()) {
						this.lightBlocks.add(pos1);
						this.level.setBlockAndUpdate(pos1, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
					}
					if (this.level.getBlockState(pos2).isAir()) {
						this.lightBlocks.add(pos2);
						this.level.setBlockAndUpdate(pos2, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
					}
				}
				if (direction.getAxis() != Axis.X && this.level.random.nextFloat() > 0.8F) {
					BlockPos pos1 = pos.offset(i2, 0, 0);
					BlockPos pos2 = pos.offset(-i2, 0, 0);
					if (this.level.getBlockState(pos1).isAir()) {
						this.lightBlocks.add(pos1);
						this.level.setBlockAndUpdate(pos1, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
					}
					if (this.level.getBlockState(pos2).isAir()) {
						this.lightBlocks.add(pos2);
						this.level.setBlockAndUpdate(pos2, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
					}
				}
				if (direction.getAxis() != Axis.Z && this.level.random.nextFloat() > 0.8F) {
					BlockPos pos1 = pos.offset(0, 0, i2);
					BlockPos pos2 = pos.offset(0, 0, -i2);
					if (this.level.getBlockState(pos1).isAir()) {
						this.lightBlocks.add(pos1);
						this.level.setBlockAndUpdate(pos1, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
					}
					if (this.level.getBlockState(pos2).isAir()) {
						this.lightBlocks.add(pos2);
						this.level.setBlockAndUpdate(pos2, ModBlocks.LIGHT_AIR.get().defaultBlockState().setValue(BlockStateProperties.LEVEL, level));
					}
				}
				
			}
			
		}
		
	}
	
	public void clearLightBlocks() {
		
		for (BlockPos pos : this.lightBlocks) {
			if (this.level.getBlockState(pos).isAir()) {
				this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
		}
		this.lightBlocks.clear();
		
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
