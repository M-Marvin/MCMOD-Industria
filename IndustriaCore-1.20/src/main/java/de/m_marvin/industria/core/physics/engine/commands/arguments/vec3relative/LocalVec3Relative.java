package de.m_marvin.industria.core.physics.engine.commands.arguments.vec3relative;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;

public class LocalVec3Relative extends LocalCoordinates implements Vec3Relative {
	
	public LocalVec3Relative(double pLeft, double pUp, double pForwards) {
		super(pLeft, pUp, pForwards);
	}

	public static LocalCoordinates parse(StringReader pReader) throws CommandSyntaxException {
		int i = pReader.getCursor();
		double d0 = readDouble(pReader, i);
		if (pReader.canRead() && pReader.peek() == ' ') {
			pReader.skip();
			double d1 = readDouble(pReader, i);
			if (pReader.canRead() && pReader.peek() == ' ') {
				pReader.skip();
				double d2 = readDouble(pReader, i);
				return new LocalVec3Relative(d0, d1, d2);
			} else {
				pReader.setCursor(i);
				throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
			}
		} else {
			pReader.setCursor(i);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
		}
	}
	
	private static double readDouble(StringReader pReader, int pStart) throws CommandSyntaxException {
		if (!pReader.canRead()) {
			throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(pReader);
		} else if (pReader.peek() != '^') {
			pReader.setCursor(pStart);
			throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(pReader);
		} else {
			pReader.skip();
			return pReader.canRead() && pReader.peek() != ' ' ? pReader.readDouble() : 0.0D;
		}
	}
	
	@Override
	public Vec3d getPosition(Vec3d sourceOffset, Vec3d sourceOrientation) {
		
		Quaterniond rotation = new Quaterniond(sourceOrientation, EulerOrder.XYZ, true);
		return sourceOffset.add(new Vec3d(this.left, this.up, this.forwards).transform(rotation));
		
	}
	
	@Override
	public Vec3d getRotation(Vec3d sourceRotation, Vec3d sourceOrientation) {
		
		Quaterniond rotation = new Quaterniond(sourceOrientation, EulerOrder.XYZ, true);
		Quaterniond localRotation = new Quaterniond(new Vec3d(this.left, this.up, this.forwards), EulerOrder.XYZ, true);
		Quaterniond currentRotation = new Quaterniond(sourceRotation, EulerOrder.XYZ, true);
		return rotation.mul(localRotation).mul(currentRotation).euler(EulerOrder.XYZ, true);
		
	}
	
}