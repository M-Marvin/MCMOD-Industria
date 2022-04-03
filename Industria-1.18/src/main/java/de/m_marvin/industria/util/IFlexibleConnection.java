package de.m_marvin.industria.util;

import java.util.List;
import java.util.UUID;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IFlexibleConnection {
	
	public List<FlexConnection> getConnections();
	public void addConnection(FlexConnection connection);
	public void removeConnection(UUID uuid);
	public float[] getAviableAngles();
	public default boolean anyAngleAviable() {return false;};
	
	public default boolean angleAviable() {
		return this.angleAviable() ? true : this.getAviableAngles().length > 0;
	}
	
	public default boolean connectedWith(FlexConnection connection) {
		for (FlexConnection con : getConnections()) {
			if (con.equals(connection)) return true;
		}
		return false;
	}
	
	public default boolean connectWith(IFlexibleConnection other, Conduit conduit) {
		if (other != this) {
			FlexConnection connection = new FlexConnection(other, this, conduit);
			this.addConnection(connection);
			other.addConnection(connection);
			return true;
		}
		return false;
	}
	
	public default boolean disconnect(Level level, UUID uuid) {
		FlexConnection connection = null;
		for (FlexConnection con : getConnections()) {
			if (con.getUUID().equals(uuid)) {
				connection = con;
				break;
			}
		}
		if (connection != null) {
			connection.getBlock1(level).removeConnection(uuid);
			connection.getBlock1(level).removeConnection(uuid);
			return true;
		}
		return false;
	}
	
	public static class FlexConnection {
		private UUID uuid;
		private BlockPos savedPos1;
		private BlockPos savedPos2;
		private IFlexibleConnection block1;
		private IFlexibleConnection block2;
		private Conduit conduit;
		
		public FlexConnection(IFlexibleConnection block1, IFlexibleConnection block2, Conduit conduit) {
			this.block1 = block1;
			this.block2 = block2;
			this.uuid = UUID.randomUUID();
			this.conduit = conduit;
		}
		
		private FlexConnection(BlockPos pos1, BlockPos pos2, UUID uuid, Conduit conduit) {
			this.savedPos1 = pos1;
			this.savedPos2 = pos2;
			this.uuid = uuid;
			this.conduit = conduit;
		}
		
		public CompoundTag save() {
			CompoundTag tag = new CompoundTag();
			BlockPos pos1 = this.block1 != null ? ((BlockEntity) this.block1).getBlockPos() : this.savedPos1;
			if (pos1 == null) return null;
			tag.put("Pos1", NbtUtils.writeBlockPos(pos1));
			BlockPos pos2 = this.block2 != null ? ((BlockEntity) this.block2).getBlockPos() : this.savedPos2;
			if (pos2 == null) return null;
			tag.put("Pos2", NbtUtils.writeBlockPos(pos1));
			tag.putUUID("UUID", this.uuid);
			tag.putString("Conduit", this.conduit.getRegistryName().toString());
			return tag;
		}
		
		public static FlexConnection load(CompoundTag tag) {
			BlockPos pos1 = NbtUtils.readBlockPos(tag.getCompound("Pos1"));
			BlockPos pos2 = NbtUtils.readBlockPos(tag.getCompound("Pos2"));
			UUID uuid = tag.getUUID("UUID");
			ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
			return new FlexConnection(pos1, pos2, uuid, ModRegistries.CONDUITES.get().getValue(conduitName));
		}
		
		public Conduit getConduit() {
			return conduit;
		}
		
		public UUID getUUID() {
			return uuid;
		}
		
		public IFlexibleConnection getBlock1(Level level) {
			if (this.block1 == null) {
				if (this.savedPos1 == null) return null;
				BlockEntity blockEntity = level.getBlockEntity(savedPos1);
				if (blockEntity instanceof IFlexibleConnection && ((IFlexibleConnection) blockEntity).connectedWith(this)) {
					this.block1 = (IFlexibleConnection) blockEntity;
					this.savedPos1 = null;
				}
			}
			return this.block1;
		}
		
		public IFlexibleConnection getBlock2(Level level) {
			if (this.block2 == null) {
				if (this.savedPos2 == null) return null;
				BlockEntity blockEntity = level.getBlockEntity(savedPos2);
				if (blockEntity instanceof IFlexibleConnection && ((IFlexibleConnection) blockEntity).connectedWith(this)) {
					this.block2 = (IFlexibleConnection) blockEntity;
					this.savedPos2 = null;
				}
			}
			return this.block2;
		}
		
		public boolean isStillValid(Level level) {
			return getBlock1(level) != null && getBlock2(level) != null;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FlexConnection) {
				return ((FlexConnection) obj).uuid.equals(uuid);
			} else if (obj instanceof UUID) {
				return ((UUID) obj).equals(uuid);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return this.uuid.hashCode();
		}
	}
	
}
