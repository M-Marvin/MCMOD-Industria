package de.m_marvin.industria.util.electricity;

import java.util.function.Supplier;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.Conduits;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IElectric<I, P, T> extends IForgeRegistryEntry<T> {
	
	public void plotCircuit(Level level, I instance, P position, CircuitConfiguration circuit);
	public void serializeNBT(I instance, P position, CompoundTag nbt);
	public I deserializeNBTInstance(CompoundTag nbt);
	public P deserializeNBTPosition(CompoundTag nbt);
	
	public ConnectionPoint[] getConnections(Level level, P pos, I instance);
	
	public static enum Type {
		
		BLOCK(() -> ForgeRegistries.BLOCKS),CONDUIT(Conduits.CONDUITS_REGISTRY::get);
		
		private Supplier<IForgeRegistry<?>> registry;
		
		private Type(Supplier<IForgeRegistry<?>> registry) {
			this.registry = registry;
		}
		
		public IForgeRegistry<?> getRegistry() {
			return this.registry.get();
		}
		
		public static Type getType(Object type) {
			if (type instanceof Block) return BLOCK;
			if (type instanceof Conduit) return CONDUIT;
			return BLOCK;
		}
		
	}
	
}
