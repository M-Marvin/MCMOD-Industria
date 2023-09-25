package de.m_marvin.industria.content.magnetism.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.content.Industria;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetismHandlerCapability implements ICapabilitySerializable<ListTag> {

	/* Capability handling */
	
	private LazyOptional<MagnetismHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == de.m_marvin.industria.content.magnetism.engine.Capabilities.MAGNETISM_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	protected Level level;
	
	@Override
	public ListTag serializeNBT() {
		// TODO Auto-generated method stub
		return new ListTag();
	}

	@Override
	public void deserializeNBT(ListTag nbt) {
		// TODO Auto-generated method stub
		
	}
	
	public MagnetismHandlerCapability(Level level) {
		this.level = level;
	}

	/* Event handling */
	
	
	
}
