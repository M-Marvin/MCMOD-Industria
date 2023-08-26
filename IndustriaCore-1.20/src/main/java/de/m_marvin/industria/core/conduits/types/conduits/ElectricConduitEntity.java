package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.Arrays;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class ElectricConduitEntity extends ConduitEntity {
	
	protected String[] wireLanes;
	
	public ElectricConduitEntity(ConduitPos position, Conduit conduit, double length, int laneCount) {
		super(position, conduit, length);
		this.wireLanes = new String[laneCount];
		Arrays.fill(this.wireLanes, "");
		if (laneCount >= 2) {
			this.wireLanes[0] = "L";
			this.wireLanes[1] = "N";
		}
	}
	
	public void setWireLanes(String[] laneLabels) {
		this.wireLanes = laneLabels;
	}
	
	public String[] getWireLanes() {
		return this.wireLanes;
	}
	
	@Override
	public void saveAdditional(CompoundTag tag) {
		ListTag wireLanesNBT = new ListTag();
		for (String s : this.wireLanes) wireLanesNBT.add(StringTag.valueOf(s));
		tag.put("Wires", wireLanesNBT);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag) {
		ListTag wireLanesNBT = tag.getList("Wires", ListTag.TAG_STRING);
		this.wireLanes = new String[wireLanesNBT.size()];
		for (int i = 0; i < wireLanesNBT.size(); i++) this.wireLanes[i] = wireLanesNBT.getString(i);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		saveAdditional(tag);
		return tag;
	}
	
	@Override
	public void readUpdateTag(CompoundTag tag) {
		loadAdditional(tag);
	}
	
}
