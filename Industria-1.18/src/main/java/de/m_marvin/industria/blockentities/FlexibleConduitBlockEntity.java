package de.m_marvin.industria.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.m_marvin.industria.registries.ModBlockEntities;
import de.m_marvin.industria.util.IFlexibleConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FlexibleConduitBlockEntity extends BlockEntity implements IFlexibleConnection {
	
	protected List<FlexConnection> connections = new ArrayList<>();
	protected List<Float> connectionAngles = new ArrayList<>();
	
	public FlexibleConduitBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLEXIBLE_CONDUIT_NODE, pos, state);
		
	}
	
	@Override
	public List<FlexConnection> getConnections() {
		return this.connections;
	}

	@Override
	public void addConnection(FlexConnection connection) {
		this.connections.add(connection);
		this.connectionAngles.add(0F);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void removeConnection(UUID uuid) {
		connections.remove(uuid);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		ListTag connectionsTag = new ListTag();
		for (int i = 0; i < this.connections.size(); i++) {
			FlexConnection con = this.connections.get(i);
			float angle = this.connectionAngles.get(i);
			CompoundTag conTag = con.save();
			conTag.putFloat("Angle", angle);
			if (conTag != null) connectionsTag.add(conTag);
		}
		tag.put("Connections", connectionsTag);
		super.saveAdditional(tag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		ListTag connectionsTag = tag.getList("Connections", 10);
		this.connections.clear();
		this.connectionAngles.clear();
		for (int i = 0; i < connectionsTag.size(); i++) {
			CompoundTag conTag = connectionsTag.getCompound(i);
			this.connectionAngles.add(conTag.getFloat("Angle"));
			this.connections.add(FlexConnection.load(conTag));
		}
		super.load(tag);
	}
	
	@Override
	public float[] getAviableAngles() {
		int aviableConnections = 4 - this.connectionAngles.size();
		if (aviableConnections == 4) {
			return new float[] {};
		} else {
			float[] angles = new float[aviableConnections];
			float firstAngle = this.connectionAngles.get(0);
			int offset = 0;
			for (int i = 0; i < aviableConnections; i++) {
				while (true) {
					angles[i] = firstAngle + 90 * (i + offset);
					for (float usedAngle : this.connectionAngles) {
						if (usedAngle == angles[i]) {
							offset++;
							continue;
						}
					}
					break;
				}
			}
			return angles;
		}
	}
	
	@Override
	public boolean anyAngleAviable() {
		return this.connectionAngles.size() == 0;
	}
	
}
