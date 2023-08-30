package de.m_marvin.industria.content.blockentities.machines;

import de.m_marvin.industria.content.container.MobileFuelGeneratorContainer;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.container.IFluidSlotContainer.FluidContainer;
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
import net.minecraftforge.fluids.FluidStack;

public class MobileFuelGeneratorBlockEntity extends BlockEntity implements IJunctionEdit, MenuProvider {
	
	protected FluidContainer fluidContainer = new FluidContainer(1);
	protected String[] nodeLanes = new String[] {"L", "N"};
	protected boolean canRun = false;
	
	public MobileFuelGeneratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.MOBILE_FUEL_GENERATOR.get(), pPos, pBlockState);
		this.fluidContainer.addListener(container -> this.setChanged());
	}
	
	public FluidContainer getFluidContainer() {
		return fluidContainer;
	}
	
	public void setNodeLanes(String[] nodeLanes) {
		this.nodeLanes = nodeLanes;
		this.setChanged();
	}
	
	public String[] getNodeLanes() {
		return nodeLanes;
	}
	
	public void setFuelStorage(FluidStack fuelStorage) {
		this.fluidContainer.setFluid(0, fuelStorage);
		this.setChanged();
	}
	
	public FluidStack getFuelStorage() {
		return this.fluidContainer.getFluid(0);
	}
	
	public boolean canRun() {
		return getFuelStorage().getAmount() > 0; // TODO
	}
	
	public int getPowerUsed() {
		return 0;
	}
	
	public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MobileFuelGeneratorBlockEntity pBlockEntity) {
		
		if (pBlockEntity.canRun != pBlockEntity.canRun()) {
			pBlockEntity.canRun = pBlockEntity.canRun();
			pBlockEntity.setChanged();
			ElectricUtility.updateNetwork(pLevel, pPos);
		}
		
		if (pBlockEntity.canRun) {
			
			// TODO drain fuel
			FluidStack fuel = pBlockEntity.getFuelStorage();
			if (!fuel.isEmpty()) {
				//fuel.shrink(10);
			}
			
		}
		
	}
	
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return GameUtility.openJunctionScreenOr(this, pContainerId, pPlayer, pPlayerInventory, () -> new MobileFuelGeneratorContainer(pContainerId, pPlayerInventory, this)); // TODO
	}
	
	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
		pTag.put("Fuel", this.getFuelStorage().writeToNBT(new CompoundTag()));
		pTag.putBoolean("canRun", this.canRun);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.contains("LiveWireLane") ? pTag.getString("LiveWireLane") : "L";
		this.nodeLanes[1] = pTag.contains("NeutralWireLane") ? pTag.getString("NeutralWireLane") : "N";
		this.setFuelStorage(FluidStack.loadFluidStackFromNBT(pTag.getCompound("Fuel")));
		this.canRun = pTag.getBoolean("canRun");
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString("LiveWireLane", this.nodeLanes[0]);
		tag.putString("NeutralWireLane", this.nodeLanes[1]);
		tag.put("Fuel", this.getFuelStorage().writeToNBT(new CompoundTag()));
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	@Override
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(
			JunctionBoxContainer<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,
			ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		externalNodeConstructor.construct(new Vec2i(70, 8), 	Direction2d.UP, 	conduitNodes[0]);
		internalNodeConstructor.construct(new Vec2i(70, 112), 	Direction2d.DOWN, 	0);
	}

}
