package de.m_marvin.industria.core.physics.engine.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;

public class WorldVec3Relative extends WorldCoordinates implements Vec3Relative {

	public WorldVec3Relative(WorldCoordinate pX, WorldCoordinate pY, WorldCoordinate pZ) {
		super(pX, pY, pZ);
	}
	
	public static WorldCoordinates parseDouble(StringReader pReader, boolean pCenterCorrect) throws CommandSyntaxException {
		int i = pReader.getCursor();
		WorldCoordinate worldcoordinate = WorldCoordinate.parseDouble(pReader, pCenterCorrect);
		if (pReader.canRead() && pReader.peek() == ' ') {
			pReader.skip();
			WorldCoordinate worldcoordinate1 = WorldCoordinate.parseDouble(pReader, false);
			if (pReader.canRead() && pReader.peek() == ' ') {
				pReader.skip();
				WorldCoordinate worldcoordinate2 = WorldCoordinate.parseDouble(pReader, pCenterCorrect);
				return new WorldVec3Relative(worldcoordinate, worldcoordinate1, worldcoordinate2);
			} else {
				pReader.setCursor(i);
				throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
			}
		} else {
			pReader.setCursor(i);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
		}
	}
	
	@Override
	public Vec3d getPosition(Vec3d sourceOffset, Vec3d sourceOrientation) {
		
		return new Vec3d(
				this.x.get(sourceOffset.x),
				this.y.get(sourceOffset.y),
				this.z.get(sourceOffset.z)
				);
		
	}
	
	@Override
	public Vec3d getRotation(Vec3d sourceRotation, Vec3d sourceOrientation) {
		return new Vec3d(
				this.x.get(sourceRotation.x),
				this.y.get(sourceRotation.y),
				this.z.get(sourceRotation.z)
				);
	}
	
}