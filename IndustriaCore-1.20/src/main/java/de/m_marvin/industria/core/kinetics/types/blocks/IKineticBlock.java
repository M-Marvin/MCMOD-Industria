package de.m_marvin.industria.core.kinetics.types.blocks;

import javax.annotation.Nullable;

import de.m_marvin.industria.core.kinetics.engine.transmission.AxleTransmission;
import de.m_marvin.industria.core.kinetics.engine.transmission.BeltTransmissions;
import de.m_marvin.industria.core.kinetics.engine.transmission.GearTransmissions;
import de.m_marvin.industria.core.kinetics.engine.transmission.ShaftTransmission;
import de.m_marvin.industria.core.kinetics.types.blockentities.IKineticBlockEntity;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.util.types.AxisOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface IKineticBlock {
	
	public static interface TransmissionType {
		public double apply(TransmissionNode a, TransmissionNode b);
		public BlockPos[] pos(TransmissionNode n);
	}
	
	public static record KineticReference(
			BlockPos pos, 
			int partId
	) {
		
		public BlockState state(Level level) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof IKineticBlock block && partId > 0) {
				return block.getPartState(level, pos, partId, state);
			}
			return state;
		}
		
		public static KineticReference simple(BlockPos pos) {
			return new KineticReference(pos, 0);
		}
		
		public static KineticReference subPart(BlockPos pos, int partId) {
			return new KineticReference(pos, partId);
		}
		
		public CompoundTag writeNbt() {
			CompoundTag nbt = new CompoundTag();
			nbt.put("Position", NbtUtils.writeBlockPos(pos));
			nbt.putInt("PartId", partId);
			return nbt;
		}
		
		public static KineticReference readNbt(CompoundTag nbt) {
			BlockPos position = NbtUtils.readBlockPos(nbt.getCompound("Position"));
			int partId = nbt.getInt("PartId");
			return new KineticReference(position, partId);
		}

		public void writeBuff(FriendlyByteBuf buff) {
			buff.writeBlockPos(this.pos);
			buff.writeInt(this.partId);
		}
		
		public static KineticReference readBuff(FriendlyByteBuf buff) {
			return new KineticReference(buff.readBlockPos(), buff.readInt());
		}
		
	}
	
	/**
	 * @param reference The reference to the main block to which this node belongs
	 * @param pos Position of this node in the world
	 * @param ratio The RPM ratio for this transmission node
	 * @param axis The axis of rotation of this node
	 * @param offset Where the node is placed along the axis (front/center/back) (only relevant for some types)
	 * @param arg An transmission type specific argument (only relevant for some types)
	 * @param type The type of transmission of this node
	 */
	public static record TransmissionNode(
			KineticReference reference,
			BlockPos pos, 
			double ratio, 
			Axis axis, 
			@Nullable AxisOffset offset,
			@Nullable Object arg,
			TransmissionType type
	) {
		public TransmissionNode withReference(KineticReference reference) {
			return new TransmissionNode(reference, pos, ratio, axis, offset, arg, type);
		}
	}
	
	
	/** Shaft which connects along its axis to other shafts, can be set to only connect in one AxisDirection by the node arg value **/
	public static final TransmissionType SHAFT = ShaftTransmission.SHAFT;
	/** Gear which connects only to touching neighbor gears with the same axis **/
	public static final TransmissionType GEAR = GearTransmissions.GEAR;
	/** Gear which connects on an 90 degree orthogonal axis to other gears of the same type **/
	public static final TransmissionType GEAR_ANGLE = GearTransmissions.GEAR_ANGLE;
	/** Gear which connects diagonally on the same plane to gears with same axis **/
	public static final TransmissionType GEAR_DIAG = GearTransmissions.GEAR_DIAG;
	/** Insert connection which connects to inserted axles (shafts) **/
	public static final TransmissionType ATTACHMENT = AxleTransmission.ATTACHMENT;
	/** Axle connection which connects to inserts (gears) placed onto the shaft **/
	public static final TransmissionType AXLE = AxleTransmission.AXLE;
	/** Belt connection which connects to other belts in the DiagonalDirection configured by the arg value of the node */
	public static final TransmissionType BELT = BeltTransmissions.BELT;
	/** Belt connection which connects to belts placed on top of this block **/
	public static final TransmissionType BELT_AXLE = BeltTransmissions.AXLE;
	/** Belt connection which connects to shafts placed inside a belt **/
	public static final TransmissionType BELT_ATTACHMENT = BeltTransmissions.ATTACHMENT;
	
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state);

	public default BlockState getPartState(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		return partId == 0 ? level.getBlockState(pos) : Blocks.ERROR_BLOCK.get().defaultBlockState();
	}
	
	public default double getSourceSpeed(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		return 0;
	}
	
	public default double getTorque(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		return 0.0;
	}

	public default void setRPM(LevelAccessor level, BlockPos pos, int partId, BlockState state, double rpm) {
		if (level.getBlockEntity(pos) instanceof IKineticBlockEntity kinetic)
			kinetic.setRPM(partId, rpm);
	}

	public default double getRPM(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		if (level.getBlockEntity(pos) instanceof IKineticBlockEntity kinetic)
			return kinetic.getRPM(partId);
		return 0;
	}
	
}
