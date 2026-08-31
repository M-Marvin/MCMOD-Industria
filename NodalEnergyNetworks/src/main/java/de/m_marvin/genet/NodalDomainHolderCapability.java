package de.m_marvin.genet;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.sun.jdi.connect.spi.TransportService.Capabilities;

import de.m_marvin.genet.networks.NetworkDomain;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class NodalDomainHolderCapability implements ICapabilitySerializable<CompoundTag> {
	
	public static final Capability<NodalDomainHolderCapability> DOMAIN_HOLDER = CapabilityManager.get(new CapabilityToken<NodalDomainHolderCapability>() {});
	
	private LazyOptional<NodalDomainHolderCapability> holder = LazyOptional.of(() -> this);
	private Map<ResourceLocation, NetworkDomain> domains = new HashMap<ResourceLocation, NetworkDomain>();
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == DOMAIN_HOLDER) {
			return this.holder.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// TODO Auto-generated method stub
		
		
		
	}
	
	
	
}
