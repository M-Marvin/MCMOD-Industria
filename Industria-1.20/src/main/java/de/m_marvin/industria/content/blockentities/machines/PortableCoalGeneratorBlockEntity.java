package de.m_marvin.industria.content.blockentities.machines;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.content.blocks.machines.PortableCoalGeneratorBlock;
import de.m_marvin.industria.content.container.PortableCoalGeneratorContainer;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.blocks.BaseEntityFixedMultiBlock;
import de.m_marvin.industria.core.util.blocks.FixedMultiBlockEntity;
import de.m_marvin.industria.core.util.container.IFluidSlotContainer.FluidContainer;
import de.m_marvin.industria.core.util.types.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class PortableCoalGeneratorBlockEntity extends FixedMultiBlockEntity<PortableCoalGeneratorBlockEntity> implements IJunctionEdit, MenuProvider, IItemHandler, IFluidHandler {
	
	protected FluidContainer container = new FluidContainer(1, 1);
	protected String[] nodeLanes = new String[] {"L", "N"};
	protected boolean canRun;
	protected int burnTime;
	protected int maxBurnTime;
	protected float fuelTimer;
	protected float waterTimer;
	
	public PortableCoalGeneratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.PORTABLE_COAL_GENERATOR.get(), pPos, pBlockState);
	}

	public FluidContainer getContainer() {
		return getMaster() == null ? container : getMaster().container;
	}
	
	public FluidStack getWaterStorage() {
		return getContainer().getFluid(0);
	}
	
	public void setWaterStorage(FluidStack waterStorage) {
		getContainer().setFluid(0, waterStorage);
		getMaster().setChanged();
	}
	
	public ItemStack getFuelStorage() {
		return getContainer().getItem(this.container.getFirstAdditional());
	}
	
	public void setFuelStorage(ItemStack fuelStorage) {
		getContainer().setItem(this.container.getFirstAdditional(), fuelStorage);
		getMaster().setChanged();
	}
	
	public int getMaxBurnTime() {
		return getMaster().maxBurnTime;
	}
	
	public int getBurnTime() {
		return getMaster().burnTime;
	}
	
	public String[] getNodeLanes() {
		return getMaster().nodeLanes;
	}
	
	public void setNodeLanes(String[] nodeLanes) {
		getMaster().nodeLanes = nodeLanes;
		getMaster().setChanged();
	}

	@SuppressWarnings("deprecation")
	public boolean canRun() {
		return
				getWaterStorage().getFluid().is(FluidTags.WATER) && getWaterStorage().getAmount() > 0 &&
				(ForgeHooks.getBurnTime(getFuelStorage(), RecipeType.SMELTING) > 0 || getBurnTime() > 0);
	}

	public static void tick(Level pLevel, BlockPos pPos, BlockState pState, PortableCoalGeneratorBlockEntity pBlockEntity) {
		
		if (pBlockEntity.getMaster() == null) return;
		
		if (pBlockEntity.canRun != pBlockEntity.canRun()) {
			pBlockEntity.canRun = pBlockEntity.canRun();
			if (pBlockEntity.getBlockState().getBlock() instanceof BaseEntityFixedMultiBlock multiBlock) {
				BlockPos center = multiBlock.getOriginBlock(pBlockEntity.worldPosition, pBlockEntity.getBlockState());
				BlockPos second = multiBlock.getBlockAtMBPos(center, pBlockEntity.getBlockState(), new Vec3i(1, 0, 0));
				pBlockEntity.level.setBlockAndUpdate(center, pBlockEntity.level.getBlockState(center).setValue(BlockStateProperties.LIT, pBlockEntity.canRun));
				pBlockEntity.level.setBlockAndUpdate(second, pBlockEntity.level.getBlockState(second).setValue(BlockStateProperties.LIT, pBlockEntity.canRun));
				ElectricUtility.updateNetwork(pLevel, second);
			}
			GameUtility.triggerClientSync(pBlockEntity.level, pBlockEntity.worldPosition);
			pBlockEntity.setChanged();
		}

		if (pBlockEntity.canRun && pState.getBlock() instanceof PortableCoalGeneratorBlock generatorBlock) {
			
			double powerProduction = generatorBlock.getPower(pState, pLevel, pPos);
			BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(generatorBlock);
			double waterConsumtionTick = powerProduction / parametrics.getParameter(PortableCoalGeneratorBlock.PARAMETER_WATTS_PER_WATER_MB);
			double fuelConsumtionTick = powerProduction / parametrics.getParameter(PortableCoalGeneratorBlock.PARAMETER_WATTS_PER_FUEL_TICK);
			
			if (fuelConsumtionTick < 1.0) {
				pBlockEntity.fuelTimer += fuelConsumtionTick;
				if (pBlockEntity.fuelTimer >= 1) {
					pBlockEntity.fuelTimer--;
					pBlockEntity.burnTime--;
				}
			} else {
				pBlockEntity.burnTime -= fuelConsumtionTick;
			}
			
			if (pBlockEntity.burnTime <= 0) {
				ItemStack fuelStack = pBlockEntity.getFuelStorage();
				pBlockEntity.burnTime = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING);
				pBlockEntity.maxBurnTime = pBlockEntity.burnTime;
				ItemStack remainingItem = fuelStack.getCraftingRemainingItem();
				fuelStack.shrink(1);
				if (fuelStack.isEmpty() && !remainingItem.isEmpty()) {
					pBlockEntity.setFuelStorage(remainingItem);
				}
			}

			FluidStack water = pBlockEntity.getWaterStorage();
			if (waterConsumtionTick < 1.0) {
				pBlockEntity.waterTimer += waterConsumtionTick;
				if (pBlockEntity.waterTimer >= 1) {
					pBlockEntity.waterTimer--;
					water.shrink(1);
				}
			} else {
				water.shrink((int) waterConsumtionTick);
			}
			
			pBlockEntity.setChanged();
			
		}
		
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		if (!this.isMaster()) return;
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
		ItemStack fuelStorage = this.container.getItem(this.container.getFirstAdditional() + 0);
		if (!fuelStorage.isEmpty()) pTag.put("Fuel", fuelStorage.save(new CompoundTag()));
		FluidStack waterStorage = this.container.getFluid(0);
		if (!waterStorage.isEmpty()) pTag.put("Water", waterStorage.writeToNBT(new CompoundTag()));
		if (this.burnTime > 0) pTag.putInt("BurnTime", this.burnTime);
		if (this.maxBurnTime > 0) pTag.putInt("MaxBurnTime", this.maxBurnTime);
		pTag.putFloat("fuelTimer", this.fuelTimer);
		pTag.putFloat("waterTimer", this.waterTimer);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.contains("LiveWireLane") ? pTag.getString("LiveWireLane") : "L";
		this.nodeLanes[1] = pTag.contains("NeutralWireLane") ? pTag.getString("NeutralWireLane") : "N";
		this.container.setItem(this.container.getFirstAdditional() + 0, ItemStack.of(pTag.getCompound("Fuel")));
		this.container.setFluid(0, FluidStack.loadFluidStackFromNBT(pTag.getCompound("Water")));
		this.burnTime = pTag.getInt("BurnTime");
		this.maxBurnTime = pTag.getInt("MaxBurnTime");
		this.fuelTimer = pTag.getFloat("fuelTimer");
		this.waterTimer = pTag.getFloat("waterTimer");
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
		tag.put("Fuel", this.container.getItem(this.container.getFirstAdditional() + 0).save(new CompoundTag()));
		tag.put("Water", this.container.getFluid(0).writeToNBT(new CompoundTag()));
		tag.putInt("BurnTime", this.burnTime);
		tag.putInt("MaxBurnTime", this.maxBurnTime);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return GameUtility.openJunctionScreenOr(this, pContainerId, pPlayer, pPlayerInventory, () -> new PortableCoalGeneratorContainer(pContainerId, pPlayerInventory, this));
	}

	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}

	@Override
	public <B extends BlockEntity & IJunctionEdit> void setupScreenConduitNodes(
			JunctionBoxContainer<B> abstractJunctionBoxScreen, NodePos[] conduitNodes,
			ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		externalNodeConstructor.construct(new Vec2i(70, 8), 	Direction2d.UP, 	conduitNodes[0]);
		internalNodeConstructor.construct(new Vec2i(70, 112), 	Direction2d.DOWN, 	0);
	}

	@Override
	public Level getJunctionLevel() {
		return this.level;
	}

	@Override
	public BlockPos getJunctionBlockPos() {
		if (this.getBlockState().getBlock() instanceof BaseEntityFixedMultiBlock multiBlock) {
			return multiBlock.getBlockAtMBPos(multiBlock.getOriginBlock(worldPosition, getBlockState()), getBlockState(), new Vec3i(1, 0, 0));
		}
		return this.worldPosition;
	}
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) return LazyOptional.of(() -> this).cast();
		if (cap == ForgeCapabilities.FLUID_HANDLER) return LazyOptional.of(() -> this).cast();
		return super.getCapability(cap, side);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return getMaster().container.getItem(this.container.getFirstAdditional() + slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack2, boolean simulate) {
		if (!isItemValid(slot, stack2)) return stack2;
		ItemStack stack = getMaster().container.getItem(getMaster().container.getFirstAdditional() + slot);
		ItemStack remainder = ItemStack.EMPTY;
		if (stack.isEmpty()) {
			if (!simulate) stack = stack2;
		} else if (stack.getItem() == stack2.getItem()) {
			int insertable = Math.min(stack.getMaxStackSize() - stack.getCount(), stack2.getCount());
			if (insertable < stack2.getCount()) {
				remainder = stack2.copy();
				remainder.shrink(insertable);
			}
			if (!simulate) stack.grow(insertable);
		}
		if (!simulate) getMaster().container.setItem(getMaster().container.getFirstAdditional() + slot, stack);
		return remainder;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
//		ItemStack stack = this.container.getItem(this.container.getFirstAdditional() + slot);
//		ItemStack extracted = stack.copy();
//		extracted.setCount(Math.min(stack.getCount(), amount));
//		if (!simulate) stack.shrink(extracted.getCount());
//		return extracted;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (slot == 0) return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
		return false;
	}

	// TODO fluid behavior gets implemented with pipes
	
	@Override
	public int getTanks() {
		return 2;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return this.container.getFluid(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.container.getMaxFluidAmount();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		if (tank == 0) return stack.getFluid().is(FluidTags.WATER);
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
	
}
