package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.machines.PortableCoalGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
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
		
		FluidSlot fluidSlot = addFluidSlot(new WaterFluidSlot(container, 0, 17, 18));
		addSlot(fluidSlot.makeFillSlot(container));
		addSlot(fluidSlot.makeDrainSlot(container));
		
		addSlot(new FuelSlot(container, container.getFirstAdditional(), 53, 54));
		
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

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {

		return ItemStack.EMPTY;
		
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		// TODO inventory still valid
		return true;
	}

}
