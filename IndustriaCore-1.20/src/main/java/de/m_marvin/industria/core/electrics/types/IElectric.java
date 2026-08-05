package de.m_marvin.industria.core.electrics.types;

import java.util.Optional;
import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public interface IElectric<I, T> {
	
	public static record ElectricReference(BlockPos block, ConduitPos conduit) {
		
		public ElectricReference(BlockPos block, ConduitPos conduit) {
			if ((block == null) == (conduit == null))
				throw new IllegalArgumentException("one of block or conduit need to be defined!");
			this.block = block;
			this.conduit = conduit;
		}
		
		public static ElectricReference block(BlockPos pos) {
			return new ElectricReference(pos, null);
		}
		
		public static ElectricReference conduit(ConduitPos pos) {
			return new ElectricReference(null, pos);
		}
		
		public boolean isBlock() {
			return this.block != null && this.conduit == null;
		}
		
		public boolean isConduit() {
			return this.conduit != null && this.block == null;
		}

		public CompoundTag writeNbt() {
			if (isBlock())
				return NbtUtils.writeBlockPos(this.block);
			else
				return this.conduit.writeNBT(new CompoundTag());
		}
		
		public static ElectricReference readNbt(CompoundTag nbt) {
			if (ConduitPos.isValidConduitPos(nbt))
				return conduit(ConduitPos.readNBT(nbt));
			else
				return block(NbtUtils.readBlockPos(nbt));
		}

		public void writeBuff(FriendlyByteBuf buff) {
			buff.writeBoolean(isBlock());
			if (isBlock())
				buff.writeBlockPos(this.block);
			else if (isConduit())
				this.conduit.writeBuff(buff);
		}
		
		public static ElectricReference readBuff(FriendlyByteBuf buff) {
			if (buff.readBoolean())
				return block(buff.readBlockPos());
			else
				return conduit(ConduitPos.readBuff(buff));
		}
		
	}
	
	public default void updateNetwork(Level level, ElectricReference reference) {
		ElectricUtility.updateNetwork(level, reference);
	}
	
	public void updateElectricElements(Level level, ElectricReference reference, I instance, ElectricNetwork.ComponentCircuitContext context, boolean initialInstall);
	public default void afterNetworkStep(Level level, ElectricReference reference, I instance, ElectricNetwork network) {}
	
	public void serializeNBT(I instance, CompoundTag nbt);
	public I deserializeNBT(CompoundTag nbt);
	
	public NodePos[] getElectricConnections(Level level, ElectricReference reference, I instance);
	public String[] getWireLanes(Level level, ElectricReference reference, I instance, NodePos node);
	public void setWireLanes(Level level, ElectricReference reference, I instance, NodePos node, String[] laneLabels);
	public boolean isWire();
	public ChunkPos getAffectedChunk(Level level, ElectricReference reference);
	public Optional<I> getInstance(Level level, ElectricReference reference);
	public boolean isInstanceValid(Level level, I instance);
	
	public static enum Type {
		
		BLOCK(() -> ForgeRegistries.BLOCKS),CONDUIT(Conduits.CONDUITS_REGISTRY::get);
		
		private Supplier<IForgeRegistry<?>> registry;
		
		private Type(Supplier<IForgeRegistry<?>> registry) {
			this.registry = registry;
		}
		
		@SuppressWarnings("unchecked")
		public IForgeRegistry<Object> getRegistry() {
			return (IForgeRegistry<Object>) this.registry.get();
		}
		
		public static Type getType(Object type) {
			if (type instanceof Block) return BLOCK;
			if (type instanceof Conduit) return CONDUIT;
			return BLOCK;
		}
		
	}
	
}
