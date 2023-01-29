package de.m_marvin.industria.core.physics.engine.commands;

import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.AABB;

public class ContraptionCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("contraption").requires((source) -> {
			return source.hasPermission(2);
		})
		.then(
				Commands.literal("create")
				.then(
						Commands.argument("pos1", BlockPosArgument.blockPos())
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
				Commands.literal("remove")
				.then(
						Commands.argument("contraption", ContraptionIdArgument.contraption())
						.executes((source) ->
								removeContraption(source, ContraptionIdArgument.getContraption(source, "contraption"))
						)
				)
		));
	}
	
	public static int createContraption(CommandContext<CommandSourceStack> source, BlockPos pos1, BlockPos pos2, float scale) {
		
		AABB bounds = new AABB(MathUtility.getMinCorner(pos1, pos2), MathUtility.getMaxCorner(pos1, pos2));
		
		if (bounds.getXsize() > 128 || bounds.getYsize() > 128 || bounds.getZsize() > 128) {
			source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.create.toLarge", bounds.getXsize(), bounds.getYsize(), bounds.getZsize()));
			return 0;
		}
		
		Ship contraption = PhysicUtility.convertToContraption(source.getSource().getLevel(), bounds, true);
		
		if (contraption != null) {
			source.getSource().sendSuccess(new TranslatableComponent("industria.commands.contraption.create.success", pos1.getX(), pos2.getY()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.create.failedPlace", bounds.getXsize(), bounds.getYsize(), bounds.getZsize()));
		return 0;
		
	}
	
	public static int removeContraption(CommandContext<CommandSourceStack> source, Ship contraption) {
		
		if (contraption != null) {
			
			PhysicUtility.removeContraption(source.getSource().getLevel(), contraption);
			
			source.getSource().sendSuccess(new TranslatableComponent("industria.commands.contraption.remove.success", contraption.getId()), true);
			return Command.SINGLE_SUCCESS;
			
		}
		
		source.getSource().sendFailure(new TranslatableComponent("industria.commands.contraption.remove.invalid"));
		return 0;
		
	}
	
}

