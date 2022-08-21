package de.m_marvin.industria.util.conduit;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IConduitConnector;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class MutableConnectionPointSupplier {
	
	private Vec3i BLOCK_MIDDLE = new Vec3i(8, 8, 8);
	
	private DirectionProperty stateProp;
	private List<Entry> nodes = new ArrayList<Entry>();
	private int count;
	
	public static MutableConnectionPointSupplier basedOnOrientation(DirectionProperty stateProp) {
		MutableConnectionPointSupplier supplier = new MutableConnectionPointSupplier();
		supplier.stateProp = stateProp;
		return supplier;
	}
	
	public MutableConnectionPointSupplier rotateBase(Direction orientation) {
		this.nodes.forEach((entry) -> {
			Vec3i v;
			if (orientation.getAxis() != Axis.Y) {
				v = UtilityHelper.rotatePoint(entry.pos.sub(BLOCK_MIDDLE), ((orientation.get2DDataValue() + 2) % 4) * (Math.PI / 2), Axis.Y).add(BLOCK_MIDDLE);
			} else {
				v = UtilityHelper.rotatePoint(entry.pos.sub(BLOCK_MIDDLE), orientation == Direction.UP ? Math.PI / 2 : -Math.PI / 2, Axis.X).add(BLOCK_MIDDLE);
			}
			entry.pos.set(v.x, v.y, v.z);
		});
		return this;
	}
	
	public static MutableConnectionPointSupplier staticOrientation() {
		return new MutableConnectionPointSupplier();
	}
	
	public MutableConnectionPointSupplier addPoint(Vec3i position, ResourceLocation nodeType, int count) {
		for (int i = 0; i < count; i++) this.nodes.add(new Entry(position, nodeType));
		return this;
	}
	
	public MutableConnectionPointSupplier addOnSides(Vec3i position, ResourceLocation nodeType, int count) {
		for (Direction f : Direction.values()) addOnFace(position, nodeType, count, f);
		return this;
	}
	
	public MutableConnectionPointSupplier addOnSidesOfAxis(Vec3i position, ResourceLocation nodeType, int count, Axis axis) {
		for (Direction f : Direction.values()) if (f.getAxis() != axis) addOnFace(position, nodeType, count, f);
		return this;
	}
	
	public MutableConnectionPointSupplier addOnFace(Vec3i position, ResourceLocation nodeType, int count, Direction face) {
		if (face.getAxis() != Axis.Y) {
			Vec3i v = UtilityHelper.rotatePoint(position.copy().sub(BLOCK_MIDDLE), ((face.get2DDataValue() + 2) % 4) * (Math.PI / 2), Axis.Y).add(BLOCK_MIDDLE);
			addPoint(v, nodeType, count);
		} else {
			Vec3i v = UtilityHelper.rotatePoint(position.copy().sub(BLOCK_MIDDLE), face == Direction.UP ? Math.PI / 2 : -Math.PI / 2, Axis.X).add(BLOCK_MIDDLE);
			addPoint(v, nodeType, count);
		}
		return this;
	}
	
	public ConnectionPoint[] getNodes(BlockPos pos, BlockState state) {
		this.count = 0;
		if (this.stateProp != null) {
			Direction orientation = state.getValue(this.stateProp);
			return this.nodes.stream()
					.map((entry) -> {
						Vec3i v;
						if (orientation.getAxis() != Axis.Y) {
							v = UtilityHelper.rotatePoint(entry.pos.copy().sub(BLOCK_MIDDLE), ((orientation.get2DDataValue() + 2) % 4) * (Math.PI / 2), Axis.Y).add(BLOCK_MIDDLE);
						} else {
							v = UtilityHelper.rotatePoint(entry.pos.copy().sub(BLOCK_MIDDLE), orientation == Direction.UP ? Math.PI / 2 : -Math.PI / 2, Axis.X).add(BLOCK_MIDDLE);
						}
						return new ConnectionPoint(pos, count++, v, entry.type);
					})
					.toArray((length) -> new ConnectionPoint[length]);
		} else {
			return this.nodes.stream()
					.map((entry) -> new ConnectionPoint(pos, count++, entry.pos, entry.type))
					.toArray((length) -> new ConnectionPoint[length]);
		}
	}
	
	public static class ConnectionPoint {
		public BlockPos position;
		public int connectionId;
		public Vec3i offset;
		public ResourceLocation nodeType;
		
		public ConnectionPoint(BlockPos position, int connectionId, Vec3i offset, ResourceLocation nodeType) {
			super();
			this.position = position;
			this.connectionId = connectionId;
			this.offset = offset;
			this.nodeType = nodeType;
		}
		
		public String getKeyString() {
			return "Node{pos=" + this.position.asLong() + ",id=" + connectionId + "}";
		}
		
		public static ConnectionPoint getFromKeyString(Level level, String keyString) {
			try {
				String[] s = keyString.split("pos=")[1].split("}")[0].split(",id=");
				BlockPos position = BlockPos.of(Long.valueOf(s[0]));
				int connectionId = Integer.valueOf(s[1]);
				BlockState state = level.getBlockState(position);
				if (state.getBlock() instanceof IConduitConnector connector) return connector.getConnectionPoints(position, state)[connectionId];
				return null;
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = prime * result + ((position != null) ? position.hashCode() : 0);
			result = prime * result + ((offset != null) ? offset.hashCode() : 0);
			result = prime * result + ((nodeType != null) ? nodeType.hashCode() : 0);
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConnectionPoint) {
				ConnectionPoint other = (ConnectionPoint) obj;
				return	position.equals(other.position) &&
						offset.equals(other.offset) &&
						nodeType.equals(other.nodeType);
			}
			return false;
		}
	}
	
	public static record Entry(
		Vec3i pos,
		ResourceLocation type
	) {};
	
}
