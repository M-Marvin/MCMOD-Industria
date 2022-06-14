package de.m_marvin.industria.util.conduit;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.registries.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;

public class PlacedConduit {
	
	private BlockPos pos1;
	private BlockPos pos2;
	private int connectionPoint1;
	private int connectionPoint2;
	private Conduit conduit;
	private ConduitShape shape;
	
	public ConduitShape conduitShape;
	
	public PlacedConduit(BlockPos pos1, int connectionPoint1, BlockPos pos2, int connectionPoint2, Conduit conduit) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.connectionPoint1 = connectionPoint1;
		this.connectionPoint2 = connectionPoint2;
		this.conduit = conduit;
	}
	
	public PlacedConduit build(BlockGetter level) {
		this.shape = conduit.buildShape(level, this);
		updateShape(level);
		return this;
	}
	
	public void updateShape(BlockGetter level) {
		this.conduit.updatePhysicalNodes(level, this);
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("PosA", NbtUtils.writeBlockPos(pos1));
		tag.putInt("ConnectionPointA", connectionPoint1);
		tag.put("PosB", NbtUtils.writeBlockPos(pos2));
		tag.putInt("ConnectionPointB", connectionPoint2);
		tag.putString("Conduit", this.conduit.getRegistryName().toString());
		return tag;
	}
	
	public static PlacedConduit load(CompoundTag tag) {
		BlockPos pos1 = NbtUtils.readBlockPos(tag.getCompound("PosA"));
		BlockPos pos2 = NbtUtils.readBlockPos(tag.getCompound("PosB"));
		int connectionPoint1 = tag.getInt("ConnectionPointA");
		int connectionPoint2 = tag.getInt("ConnectionPointB");
		ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
		Conduit conduit = ModRegistries.CONDUITES.get().getValue(conduitName);
		if (conduit == null) return null;
		return new PlacedConduit(pos1, connectionPoint1, pos2, connectionPoint2, conduit);
	}
	
	public ConduitShape getShape() {
		return shape;
	}
	
	public Conduit getConduit() {
		return conduit;
	}
	
	public BlockPos getNodeA() {
		return this.pos1;
	}
	
	public int getConnectionPointA() {
		return connectionPoint1;
	}
	
	public BlockPos getNodeB() {
		return this.pos2;
	}
	
	public int getConnectionPointB() {
		return connectionPoint2;
	}
	
}