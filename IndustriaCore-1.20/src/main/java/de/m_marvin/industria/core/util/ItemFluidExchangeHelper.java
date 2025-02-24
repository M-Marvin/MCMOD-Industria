package de.m_marvin.industria.core.util;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class ItemFluidExchangeHelper {
	
	private ItemFluidExchangeHelper() {}
	
	public static ItemStack drainFluid(ItemStack sourceStack, FluidStack fluidStack, int maxCapacity, Consumer<FluidStack> fluidStackConsumer) {
		@NotNull LazyOptional<IFluidHandlerItem> fluidHandlerCap = sourceStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (fluidHandlerCap.isPresent()) {
			IFluidHandlerItem fluidHandler = fluidHandlerCap.resolve().get();
			FluidStack capacity = null;
			if (fluidStack.isEmpty()) {
				capacity = new FluidStack(getFluidType(sourceStack), maxCapacity);
			} else {
				capacity = new FluidStack(fluidStack.getFluid(), maxCapacity - fluidStack.getAmount());
			}
			FluidStack drainedFluid = fluidHandler.drain(capacity, FluidAction.EXECUTE);
			if (!drainedFluid.isEmpty()) fluidStackConsumer.accept(new FluidStack(drainedFluid.getFluid(), fluidStack.getAmount() + drainedFluid.getAmount()));
			return fluidHandler.getContainer();
		}
		return sourceStack;
	}
	
	public static Fluid getFluidType(ItemStack stack) {
		@NotNull LazyOptional<IFluidHandlerItem> fluidHandlerCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (fluidHandlerCap.isPresent()) {
			IFluidHandlerItem fluidHandler = fluidHandlerCap.resolve().get();
			for (int i = 0; i < fluidHandler.getTanks(); i++) {
				FluidStack content = fluidHandler.getFluidInTank(i);
				if (!content.isEmpty()) return content.getFluid();
			}
		}
		return Fluids.EMPTY;
	}
	
	public static ItemStack fillFluid(ItemStack containerStack, FluidStack fluidStack, Consumer<FluidStack> fluidStackConsumer) {
		if (fluidStack.isEmpty()) return containerStack;
		@NotNull LazyOptional<IFluidHandlerItem> fluidHandlerCap = containerStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (fluidHandlerCap.isPresent()) {
			IFluidHandlerItem fluidHandler = fluidHandlerCap.resolve().get();
			int filledFluid = fluidHandler.fill(fluidStack, FluidAction.EXECUTE);
			fluidStackConsumer.accept(new FluidStack(fluidStack.getFluid(), fluidStack.getAmount() - filledFluid));
			return fluidHandler.getContainer();
		}
		return containerStack;
	}

	public static FluidStack getFluidContent(ItemStack stack) {
		@NotNull LazyOptional<IFluidHandlerItem> fluidHandlerCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (fluidHandlerCap.isPresent()) {
			IFluidHandlerItem fluidHandler = fluidHandlerCap.resolve().get();
			for (int i = 0; i < fluidHandler.getTanks(); i++) {
				FluidStack content = fluidHandler.getFluidInTank(i);
				if (!content.isEmpty()) return content;
			}
		}
		return FluidStack.EMPTY;
	}
	
	public static boolean canBeDrained(ItemStack stack, FluidStack fluidType) {
		@NotNull LazyOptional<IFluidHandlerItem> fluidHandlerCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (fluidHandlerCap.isPresent()) {
			IFluidHandlerItem fluidHandler = fluidHandlerCap.resolve().get();
			for (int i = 0; i < fluidHandler.getTanks(); i++) {
				FluidStack content = fluidHandler.getFluidInTank(i);
				if (!content.isEmpty() && (content.isFluidEqual(fluidType) || fluidType.isEmpty())) return true;
			}
		}
		return false;
	}
	
	public static boolean canBeFilled(ItemStack stack, FluidStack fluidType) {
		if (fluidType.isEmpty()) return false;
		@NotNull LazyOptional<IFluidHandlerItem> fluidHandlerCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (fluidHandlerCap.isPresent()) {
			IFluidHandlerItem fluidHandler = fluidHandlerCap.resolve().get();
			for (int i = 0; i < fluidHandler.getTanks(); i++) {
				if (!fluidHandler.isFluidValid(i, fluidType)) continue;
				if (fluidHandler.getTankCapacity(i) <= fluidHandler.getFluidInTank(i).getAmount()) continue;
				return true;
			}
		}
		return false;
	}
	
}
