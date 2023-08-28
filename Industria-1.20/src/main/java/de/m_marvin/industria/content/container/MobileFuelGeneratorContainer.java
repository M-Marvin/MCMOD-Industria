package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.machines.MobileFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.core.util.container.AbstractBlockEntityFluidContainerBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MobileFuelGeneratorContainer extends AbstractBlockEntityFluidContainerBase<MobileFuelGeneratorBlockEntity> {
	
	protected Container container;
	
	public MobileFuelGeneratorContainer(int id, Inventory playerInv, MobileFuelGeneratorBlockEntity tileEntity) {
		super(ModMenuTypes.MOBILE_FUEL_GENERATOR.get(), id, playerInv, tileEntity);
	}

	public MobileFuelGeneratorContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(ModMenuTypes.MOBILE_FUEL_GENERATOR.get(), id, playerInv, data);
	}
	
	@Override
	public int getSlots() {
		return 2;
	}
	
	@Override
	public void init() {
		
		this.container = new SimpleContainer(getSlots());
		
		initPlayerInventory(playerInv, 0, 0);
		
		addSlot(new Slot(this.container, 0, 125, 18));
		addSlot(new Slot(this.container, 1, 125, 51));
		
		System.out.println(this.fluidSlots);
		//addFluidSlot(new FluidSlot(30, 18));
		
	}
	
}
