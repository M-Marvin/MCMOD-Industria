package de.m_marvin.industria.content.blockentities.machines;

import de.m_marvin.industria.content.blocks.machines.PortableCoalGeneratorBlock;
import de.m_marvin.industria.content.container.PortableCoalGeneratorContainer;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.parametrics.DeviceParametrics;
import de.m_marvin.industria.core.electrics.parametrics.DeviceParametricsManager;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.ExternalNodeConstructor;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer.InternalNodeConstructor;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.blocks.BaseEntityMultiBlock;
import de.m_marvin.industria.core.util.container.IFluidSlotContainer.FluidContainer;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.fluids.FluidStack;

public class PortableCoalGeneratorBlockEntity extends BlockEntity implements IJunctionEdit, MenuProvider {
	
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
		return this.container;
	}
	
	public FluidStack getWaterStorage() {
		return this.container.getFluid(0);
	}
	
	public void setWaterStorage(FluidStack waterStorage) {
		this.container.setFluid(0, waterStorage);
		this.setChanged();
	}
	
	public ItemStack getFuelStorage() {
		return this.container.getItem(this.container.getFirstAdditional());
	}
	
	public void setFuelStorage(ItemStack fuelStorage) {
		this.container.setItem(this.container.getFirstAdditional(), fuelStorage);
		this.setChanged();
	}
	
	public int getMaxBurnTime() {
		return maxBurnTime;
	}
	
	public int getBurnTime() {
		return burnTime;
	}
	
	public String[] getNodeLanes() {
		return nodeLanes;
	}
	
	public void setNodeLanes(String[] nodeLanes) {
		this.nodeLanes = nodeLanes;
		this.setChanged();
	}

	@SuppressWarnings("deprecation")
	public boolean canRun() {
		return
				this.getWaterStorage().getFluid().is(FluidTags.WATER) && this.getWaterStorage().getAmount() > 0 &&
				(ForgeHooks.getBurnTime(this.getFuelStorage(), RecipeType.SMELTING) > 0 || this.burnTime > 0);
	}

	public static void tick(Level pLevel, BlockPos pPos, BlockState pState, PortableCoalGeneratorBlockEntity pBlockEntity) {

		if (pBlockEntity.canRun != pBlockEntity.canRun()) {
			pBlockEntity.canRun = pBlockEntity.canRun();
			if (pBlockEntity.getBlockState().getBlock() instanceof BaseEntityMultiBlock multiBlock) {
				BlockPos center = multiBlock.getCenterBlock(pBlockEntity.worldPosition, pBlockEntity.getBlockState());
				BlockPos second = multiBlock.getBlockAt(center, pBlockEntity.getBlockState(), new Vec3i(1, 0, 0));
				pBlockEntity.level.setBlockAndUpdate(center, pBlockEntity.level.getBlockState(center).setValue(BlockStateProperties.LIT, pBlockEntity.canRun));
				pBlockEntity.level.setBlockAndUpdate(second, pBlockEntity.level.getBlockState(second).setValue(BlockStateProperties.LIT, pBlockEntity.canRun));
			}
			pBlockEntity.setChanged();
			ElectricUtility.updateNetwork(pLevel, pPos);
			GameUtility.triggerClientSync(pBlockEntity.level, pBlockEntity.worldPosition);
		}

		if (pBlockEntity.canRun && pState.getBlock() instanceof PortableCoalGeneratorBlock generatorBlock) {
			
			double powerProduction = generatorBlock.getPower(pState, pLevel, pPos);
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(generatorBlock);
			double waterConsumtionTick = powerProduction / parametrics.getConfig("wattsPerWaterMB", 100000);
			double fuelConsumtionTick = powerProduction / parametrics.getConfig("wattsPerFuelTick", 1000);
			
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
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
		pTag.put("Fuel", this.getFuelStorage().save(new CompoundTag()));
		pTag.put("Water", this.getWaterStorage().writeToNBT(new CompoundTag()));
		pTag.putInt("BurnTime", this.burnTime);
		pTag.putInt("MaxBurnTime", this.maxBurnTime);
		pTag.putFloat("fuelTimer", this.fuelTimer);
		pTag.putFloat("waterTimer", this.waterTimer);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.nodeLanes[0] = pTag.contains("LiveWireLane") ? pTag.getString("LiveWireLane") : "L";
		this.nodeLanes[1] = pTag.contains("NeutralWireLane") ? pTag.getString("NeutralWireLane") : "N";
		this.setFuelStorage(ItemStack.of(pTag.getCompound("Fuel")));
		this.setWaterStorage(FluidStack.loadFluidStackFromNBT(pTag.getCompound("Water")));
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
		tag.put("Fuel", this.getFuelStorage().save(new CompoundTag()));
		tag.put("Water", this.getWaterStorage().writeToNBT(new CompoundTag()));
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
		return this.worldPosition;
	}
	
}
