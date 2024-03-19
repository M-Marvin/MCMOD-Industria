package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.machines.PortableCoalGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.core.util.ItemFluidExchangeHelper;
import de.m_marvin.industria.core.util.container.AbstractBlockEntityFluidContainerBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;

public class PortableCoalGeneratorContainer extends AbstractBlockEntityFluidContainerBase<PortableCoalGeneratorBlockEntity> {

	public PortableCoalGeneratorContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(ModMenuTypes.PORTABLE_COAL_GENERATOR.get(), id, playerInv, data);
	}

	public PortableCoalGeneratorContainer(int id, Inventory playerInv, PortableCoalGeneratorBlockEntity tileEntity) {
		super(ModMenuTypes.PORTABLE_COAL_GENERATOR.get(), id, playerInv, tileEntity);
	}
	
	@Override
	public int getSlots() {
		return this.blockEntity.getContainer().getContainerSize();
	}
	
	@Override
	public void init() {
		super.init();
		initPlayerInventory(playerInv, 0, 0);
		
		FluidContainer container = this.blockEntity.getContainer();
		
		addSlot(new FuelSlot(container, container.getFirstAdditional(), 53, 54));
		
		FluidSlot fluidSlot = addFluidSlot(new WaterFluidSlot(container, 0, 17, 18));
		addSlot(fluidSlot.makeFillSlot(container));
		addSlot(fluidSlot.makeDrainSlot(container));
		
		this.blockEntity.getContainer().addFillListener(this::playFillSound);
		this.blockEntity.getContainer().addDrainListener(this::playDrainSound);
		
	}

	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		this.clearContainerBucketsOnly(pPlayer, this.blockEntity.getContainer());
	}
	
	public static class WaterFluidSlot extends FluidSlot {

		public WaterFluidSlot(FluidContainer container, int id, int x, int y) {
			super(container, id, x, y);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public boolean mayPlace(FluidStack fluid) {
			return fluid.getFluid().is(FluidTags.WATER);
		}
		
	}
	
	public static class FuelSlot extends Slot {

		public FuelSlot(Container pContainer, int pSlot, int pX, int pY) {
			super(pContainer, pSlot, pX, pY);
		}
		
		@Override
		public boolean mayPlace(ItemStack pStack) {
			return ForgeHooks.getBurnTime(pStack, RecipeType.SMELTING) > 0;
		}
		
	}

	protected boolean isFuel(ItemStack itemStack) {
		return net.minecraftforge.common.ForgeHooks.getBurnTime(itemStack, RecipeType.SMELTING) > 0;
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		Slot slot = this.slots.get(pIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack itemStack = slot.getItem();
			if (pIndex < getFirstNonPlayerSlot()) {
				FluidSlot fluidSlot = this.getFluidSlots().get(0);
				if (isFuel(itemStack)) {
					if (!this.moveItemStackTo(itemStack, getFirstNonPlayerSlot() + 0)) {
						return ItemStack.EMPTY;
					}
				} else if (ItemFluidExchangeHelper.canBeDrained(itemStack, fluidSlot.getFluid()) && fluidSlot.getCapacity() > fluidSlot.getFluid().getAmount()) {
					if (!this.moveItemStackTo(itemStack, getFirstNonPlayerSlot() + 1)) {
						return ItemStack.EMPTY;
					}
				} else if (ItemFluidExchangeHelper.canBeFilled(itemStack, fluidSlot.getFluid()) && fluidSlot.getFluid().getAmount() > 0) {
					if (!this.moveItemStackTo(itemStack, getFirstNonPlayerSlot() + 2)) {
						return ItemStack.EMPTY;
					}
				}
			} else {
				if (!this.moveItemStackTo(itemStack, 0, getFirstNonPlayerSlot(), true)) {
					return ItemStack.EMPTY;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return Container.stillValidBlockEntity(this.blockEntity, pPlayer);
	}
	
}
