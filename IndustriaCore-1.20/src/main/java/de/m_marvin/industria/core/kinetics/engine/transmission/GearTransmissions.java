package de.m_marvin.industria.core.kinetics.engine.transmission;

import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.TransmissionNode;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.types.AxisOffset;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

public abstract class GearTransmissions implements IKineticBlock.TransmissionType {
	
	public static final IKineticBlock.TransmissionType GEAR = new GearTransmission();
	public static final IKineticBlock.TransmissionType GEAR_DIAG = new GearDiagonalTransmission();
	public static final IKineticBlock.TransmissionType GEAR_ANGLE = new GearAngleTransmission();
	
	public static class GearAngleTransmission extends GearTransmissions {
		
		private GearAngleTransmission() {}
		
		public int vaxis(BlockPos vec, Axis axis) {
			switch (axis) {
			default:
			case X: return vec.getX();
			case Y: return vec.getY();
			case Z: return vec.getZ();
			}
		}
		
		@Override
		public double apply(TransmissionNode a, TransmissionNode b) {
			if (a.type() != GEAR_ANGLE || b.type() != GEAR_ANGLE) return 0.0;
			if (a.offset() != AxisOffset.CENTER || a.offset() != AxisOffset.CENTER) return 0.0;
			if (a.axis() == b.axis()) return 0.0;
			if (Stream.of(pos(a)).filter(p -> p.equals(b.pos())).count() == 0) return 0.0;
			if (Stream.of(pos(b)).filter(p -> p.equals(a.pos())).count() == 0) return 0.0;
			int ia = vaxis(b.pos(), b.axis()) - vaxis(a.pos(), b.axis());
			int ib = vaxis(a.pos(), a.axis()) - vaxis(b.pos(), a.axis());
			return -(ia * ib) * a.ratio() / b.ratio();
		}
		
		@Override
		public BlockPos[] pos(TransmissionNode n) {
			Vec3i v = Vec3i.fromVec(n.pos()).sub(Vec3i.fromVec(n.reference().pos()));
			if (v.lengthSqr() != 0) {
				Direction offset = MathUtility.getVecDirection(v);
				return new BlockPos[] {
					n.pos().relative(offset).relative(n.axis(), +1),
					n.pos().relative(offset).relative(n.axis(), -1)
				};
			} else {
				Direction[] offset = MathUtility.getDirectionsOrthogonal(n.axis());
				return new BlockPos[] {
					n.pos().relative(offset[0]).relative(n.axis(), +1),
					n.pos().relative(offset[0]).relative(n.axis(), -1),
					n.pos().relative(offset[1]).relative(n.axis(), +1),
					n.pos().relative(offset[1]).relative(n.axis(), -1),
					n.pos().relative(offset[2]).relative(n.axis(), +1),
					n.pos().relative(offset[2]).relative(n.axis(), -1),
					n.pos().relative(offset[3]).relative(n.axis(), +1),
					n.pos().relative(offset[3]).relative(n.axis(), -1)
				};
			}
		}
		
	}
	
	public static class GearDiagonalTransmission extends GearTransmissions {

		private GearDiagonalTransmission() {}
		
		@Override
		public double apply(TransmissionNode a, TransmissionNode b) {
			if (a.offset() != b.offset()) return 0.0;
			if (a.type() == GEAR && b.type() == GEAR) {
				return GEAR.apply(a, b);
			} else if (a.type() == GEAR && b.type() == GEAR_DIAG) {
				if (a.axis() != b.axis()) return 0.0;
				if (Stream.of(gearPos(b)).filter(p -> p.equals(a.pos())).count() == 0) return 0.0;
				return -a.ratio() / b.ratio();
			} else if (a.type() == GEAR_DIAG && b.type() == GEAR) {
				if (a.axis() != b.axis()) return 0.0;
				if (Stream.of(gearPos(a)).filter(p -> p.equals(b.pos())).count() == 0) return 0.0;
				return -a.ratio() / b.ratio();
			}
			return 0.0;
		}
		
		public BlockPos[] gearPos(TransmissionNode n) {
			return MathUtility.getDiagonalPositionsAroundAxis(n.pos(), n.axis());
		}
		
		@Override
		public BlockPos[] pos(TransmissionNode n) {
			return ArrayUtils.addAll(gearPos(n), MathUtility.getPositionsAroundAxis(n.pos(), n.axis()));
		}
		
	}
	
	public static class GearTransmission extends GearTransmissions {

		private GearTransmission() {}
		
		@Override
		public double apply(TransmissionNode a, TransmissionNode b) {
			if (a.offset() != b.offset()) return 0.0;
			if (a.type() == GEAR && b.type() == GEAR) {
				if (a.axis() != b.axis()) return 0.0;
				if (Stream.of(gearPos(a)).filter(p -> p.equals(b.pos())).count() == 0) return 0.0;
				return -a.ratio() / b.ratio();
			} else if (a.type() == GEAR && b.type() == GEAR_DIAG || a.type() == GEAR_DIAG && b.type() == GEAR) {
				return GEAR_DIAG.apply(a, b);
			}
			return 0.0;
		}
		
		public BlockPos[] gearPos(TransmissionNode n) {
			return MathUtility.getPositionsAroundAxis(n.pos(), n.axis());
		}
		
		@Override
		public BlockPos[] pos(TransmissionNode n) {
			return ArrayUtils.addAll(gearPos(n), MathUtility.getDiagonalPositionsAroundAxis(n.pos(), n.axis()));
		}
		
	}
	
}
