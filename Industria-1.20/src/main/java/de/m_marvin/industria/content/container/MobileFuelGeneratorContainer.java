package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.machines.MobileFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.core.util.container.AbstractBlockEntityFluidContainerBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class MobileFuelGeneratorContainer extends AbstractBlockEntityFluidContainerBase<MobileFuelGeneratorBlockEntity> {
	
	public MobileFuelGeneratorContainer(int id, Inventory playerInv, MobileFuelGeneratorBlockEntity tileEntity) {
		super(ModMenuTypes.MOBILE_FUEL_GENERATOR.get(), id, playerInv, tileEntity);
	}

	public MobileFuelGeneratorContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(ModMenuTypes.MOBILE_FUEL_GENERATOR.get(), id, playerInv, data);
	}
	
	@Override
	public int getSlots() {
		return this.blockEntity.getFluidContainer().getContainerSize();
	}
	
	@Override
	public void init() {
		super.init();
		
		initPlayerInventory(playerInv, 0, 0);
		
		FluidSlot fluidSlot = addFluidSlot(new FluidSlot(this.blockEntity.getFluidContainer(), 0, 125, 15));
		addSlot(fluidSlot.makeFillSlot(this.blockEntity.getFluidContainer()));
		addSlot(fluidSlot.makeDrainSlot(this.blockEntity.getFluidContainer()));
	}
	
	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		this.clearContainer(pPlayer, this.blockEntity.getFluidContainer());
	}
	
}
