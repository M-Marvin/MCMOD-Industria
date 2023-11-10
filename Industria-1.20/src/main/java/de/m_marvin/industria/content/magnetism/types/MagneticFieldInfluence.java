package de.m_marvin.industria.content.magnetism.types;

import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;

public class MagneticFieldInfluence {
	
	private final BlockPos pos;
	private Vec3d fieldVector = new Vec3d();
	private boolean isAlternating = false;
	
	public MagneticFieldInfluence(BlockPos pos) {
		this.pos = pos;
	}
	
	public void update(Level level) {
		
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public Vec3d getVector() {
		return this.fieldVector;
	}
	
	public boolean isAlternating() {
		return this.isAlternating;
	}
	
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.put("Pos", NbtUtils.writeBlockPos(pos));
		tag.put("FieldVector", NBTUtility.writeVector3d(fieldVector));
		tag.putBoolean("Alternating", this.isAlternating);
		return tag;
	}
	
	public static MagneticFieldInfluence deserialize(CompoundTag tag) {
		BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("Pos"));
		MagneticFieldInfluence influence = new MagneticFieldInfluence(pos);
		influence.fieldVector = NBTUtility.loadVector3d(tag.getCompound("FieldVector"));
		influence.isAlternating = tag.getBoolean("Alternating");
		return influence;
	}
	
}
