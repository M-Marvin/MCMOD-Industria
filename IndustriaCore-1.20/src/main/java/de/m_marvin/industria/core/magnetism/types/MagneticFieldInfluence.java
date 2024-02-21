package de.m_marvin.industria.core.magnetism.types;

import com.google.common.base.Objects;

import de.m_marvin.industria.core.magnetism.types.blocks.IMagneticBlock;
import de.m_marvin.industria.core.parametrics.BlockParametricsManager;
import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MagneticFieldInfluence {
	
	private final BlockPos pos;
	private Vec3d fieldVector = new Vec3d();
	private Vec3d inducedVector = new Vec3d();
	private double magneticCoefficient = 0.0;
	
	public MagneticFieldInfluence(BlockPos pos) {
		this.pos = pos;
	}
	
	public void update(Level level) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof IMagneticBlock magnetic) {
			this.fieldVector = magnetic.getFieldVector(level, state, pos);
			this.magneticCoefficient = magnetic.getCoefficient(level, state, pos);
		} else {
			this.magneticCoefficient = BlockParametricsManager.getInstance().getParametrics(state.getBlock()).getMagneticCoefficient();
		}
	}
	
	public void notifyInductionChange(Level level) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof IMagneticBlock magnetic) {
			magnetic.onInductionNotify(level, state, pos, this.inducedVector);
		}
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public Vec3d getVector() {
		return this.fieldVector;
	}
	
	public Vec3d getInducedVector() {
		return inducedVector;
	}
	
	public double getMagneticCoefficient() {
		return magneticCoefficient;
	}
	
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.put("Pos", NbtUtils.writeBlockPos(pos));
		tag.put("FieldVector", NBTUtility.writeVector3d(fieldVector));
		tag.put("InductionVector", NBTUtility.writeVector3d(inducedVector));
		tag.putDouble("Coefficient", this.magneticCoefficient);
		return tag;
	}
	
	public static MagneticFieldInfluence deserialize(CompoundTag tag) {
		BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("Pos"));
		MagneticFieldInfluence influence = new MagneticFieldInfluence(pos);
		influence.fieldVector = NBTUtility.loadVector3d(tag.getCompound("FieldVector"));
		influence.inducedVector = NBTUtility.loadVector3d(tag.getCompound("InductionVector"));
		influence.magneticCoefficient = tag.getDouble("Coefficient");
		return influence;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MagneticFieldInfluence other) {
			return Objects.equal(this.pos, other.pos);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// WARNING: Don't implement hashCode(), it undefined behavior in HashSet<> because of an bug in the API!
		return super.hashCode();
	}
	
}
