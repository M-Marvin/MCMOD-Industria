package de.m_marvin.industria.util.conduit;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.registries.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class PlacedConduit {
	
	private BlockPos pos1;
	private BlockPos pos2;
	private int nodesPerBlock;
	private int connectionPoint1;
	private int connectionPoint2;
	private Conduit conduit;
	private ConduitShape shape;
	
	public ConduitShape conduitShape;
	
	public PlacedConduit(BlockPos pos1, int connectionPoint1, BlockPos pos2, int connectionPoint2, Conduit conduit, int nodesPerBlock) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.connectionPoint1 = connectionPoint1;
		this.connectionPoint2 = connectionPoint2;
		this.conduit = conduit;
		this.nodesPerBlock = nodesPerBlock;
	}
	
	public PlacedConduit build(Level level) {
		this.shape = conduit.buildShape(level, this, nodesPerBlock);
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
		tag.putInt("Nodes", this.nodesPerBlock);
		return tag;
	}
	
	public static PlacedConduit load(CompoundTag tag) {
		BlockPos pos1 = NbtUtils.readBlockPos(tag.getCompound("PosA"));
		BlockPos pos2 = NbtUtils.readBlockPos(tag.getCompound("PosB"));
		int connectionPoint1 = tag.getInt("ConnectionPointA");
		int connectionPoint2 = tag.getInt("ConnectionPointB");
		ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
		Conduit conduit = ModRegistries.CONDUITS.get().getValue(conduitName);
		int nodesPerBlock = tag.getInt("Nodes");
		if (conduit == null) return null;
		return new PlacedConduit(pos1, connectionPoint1, pos2, connectionPoint2, conduit, nodesPerBlock);
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

	public BlockPos getNodeB() {
		return this.pos2;
	}
	
	public int getConnectionPointA() {
		return connectionPoint1;
	}
	
	public int getConnectionPointB() {
		return connectionPoint2;
	}
	
	public int getNodesPerBlock() {
		return nodesPerBlock;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlacedConduit) {
			PlacedConduit other = (PlacedConduit) obj;
			return 	other.pos1.equals(this.pos1) && other.pos2.equals(this.pos2) && 
					other.connectionPoint1 == this.connectionPoint1 && other.connectionPoint2 == connectionPoint2 &&
					other.conduit == this.conduit;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PlacedConduit{conduit=" + this.conduit.getRegistryName() + 
				",nodeA=[" + this.pos1.getX() + "," + this.pos1.getY() + "," + this.pos1.getZ() + "@" + this.connectionPoint1 + "]" +
				",nodeB=[" + this.pos2.getX() + "," + this.pos2.getY() + "," + this.pos2.getZ() + "@" + this.connectionPoint2 + "]" +
				"}";
 	}
	
}
