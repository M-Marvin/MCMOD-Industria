package de.m_marvin.industria.core.physics.engine.commands;

import java.util.List;
import java.util.Optional;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.StructureFinder;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ContraptionCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("contraption").requires((source) -> {
			return source.hasPermission(2);
		})
		.then(
				Commands.literal("create")
				.then(
						Commands.argument("pos1", BlockPosArgument.blockPos())
						.executes((source) -> 
								createContraption(source, BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos1"), 1F)
						)
						.then(
								Commands.argument("pos2", BlockPosArgument.blockPos())
								.executes((source) -> 
										createContraption(source, BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos2"), 1F)
								)
								.then(
										Commands.argument("scale", FloatArgumentType.floatArg(0.0625F, 16F))
										.executes((source) ->
												createContraption(source, BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos2"), FloatArgumentType.getFloat(source, "scale"))
										)
								)
						)
				)
		)
		.then(
				Commands.literal("assemble")
				.then(
						Commands.argument("startPos", BlockPosArgument.blockPos())
						.executes((source) -> 
								assembleContraption(source, BlockPosArgument.getLoadedBlockPos(source, "startPos"), 1F)
						)
						.then(
								Commands.argument("scale", FloatArgumentType.floatArg(0.0625F, 16F))
								.executes((source) -> 
										assembleContraption(source, BlockPosArgument.getLoadedBlockPos(source, "startPos"), FloatArgumentType.getFloat(source, "scale"))
								)
						)
				)
		)
		.then(
				Commands.literal("remove")
				.then(
						Commands.argument("contraption", ContraptionIdArgument.contraption())
						.executes((source) ->
								removeContraption(source, ContraptionIdArgument.getContraption(source, "contraption"))
						)
				)
		)
		.then(
				Commands.literal("teleport")
				.then(
						Commands.argument("contraption", ContraptionIdArgument.contraption())
						.then(
								Commands.argument("position", Vec3Argument.vec3())
								.executes((source) -> 
										teleportContraption(source, ContraptionIdArgument.getContraption(source, "contraption"), Vec3Argument.getVec3(source, "position"), false, 0F, 0F, 0F)
								)
						)
				)
		));
	}
	
	public static int teleportContraption(CommandContext<CommandSourceStack> source, Ship contraption, Vec3 position, boolean rotate, float rotationX, float rotationY, float rotationZ) {
		
		ContraptionPosition contraptionPos = null;
		if (rotate) {
			contraptionPos = new ContraptionPosition(Quaternion.fromXYZDegrees(rotationX, rotationY, rotationZ), Vec3d.fromVec(position));
		} else {
			contraptionPos = PhysicUtility.getPosition((ServerShip) contraption, false);
			contraptionPos.getPosition().setI(Vec3d.fromVec(position));
		}
		
		PhysicUtility.setPosition((ServerShip) contraption, contraptionPos, false);
		source.getSource().sendSuccess(new TranslatableComponent("industria.commands.contraption.teleport.success", (int) position.x(), (int) position.y(), (int) position.z()), true);
		return Command.SINGLE_SUCCESS;
		
	}
	
	public static int createContraption(CommandContext<CommandSourceStack> source, BlockPos pos1, BlockPos pos2, float scale) {
		
		AABB bounds = new AABB(MathUtility.getMinCorner(pos1, pos2), MathUtility.getMaxCorner(pos1, pos2));
		
		if (bounds.getXsize() > 32 || bounds.getYsize() > 32 || bounds.getZsize() > 32) {
			source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.create.toLarge", (int) bounds.getXsize(), (int) bounds.getYsize(), (int) bounds.getZsize(), 32, 32, 32));
			return 0;
		}
		
		Ship contraption = PhysicUtility.convertToContraption(source.getSource().getLevel(), bounds, true, scale);
		
		BlockPos minCorner = MathUtility.getMinCorner(pos1, pos2);
		
		if (contraption != null) {
			source.getSource().sendSuccess(new TranslatableComponent("industria.commands.contraption.create.success", (int) minCorner.getX(), (int) minCorner.getY(), (int) minCorner.getZ()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.create.failed"));
		return 0;
		
	}
	
	public static int assembleContraption(CommandContext<CommandSourceStack> source, BlockPos startPos, float scale) {

		Optional<List<BlockPos>> structureBlocks = StructureFinder.findStructure(source.getSource().getLevel(), startPos, 16 * 16 * 16, state -> PhysicUtility.isValidContraptionBlock(state));
		
		if (structureBlocks.isEmpty()) {
			
			source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.assemble.toLarge", 16 * 16 * 16)); // TODO
			return 0;
			
		}
		
		ServerShip contraption = PhysicUtility.assembleToContraption(source.getSource().getLevel(), structureBlocks.get(), true, scale);
		
		if (contraption != null) {
			source.getSource().sendSuccess(new TranslatableComponent("industria.commands.contraption.assemble.success", startPos.getX(), startPos.getY(), startPos.getZ(), startPos), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.assemble.failed"));
		return 0;
		
	}
	
	public static int removeContraption(CommandContext<CommandSourceStack> source, Ship contraption) {

		PhysicUtility.removeContraption(source.getSource().getLevel(), contraption);
		
		source.getSource().sendSuccess(new TranslatableComponent("industria.commands.contraption.remove.success", contraption.getId()), true);
		return Command.SINGLE_SUCCESS;
		
	}
	
}

