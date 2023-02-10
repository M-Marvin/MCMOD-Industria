import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.engine.commands.ContraptionIdArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;

public class TEST {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		
		// constraint create AttachmentConstraint schiff1 schiff2 pos1 pos2 distance
		
		dispatcher.register(Commands.literal("constraint").requires((source) -> {
			return source.hasPermission(2);
		})
		.then(
				Commands.literal("create")
				.then(
						
						// Section for the "AttachmentConstrain"
						
						Commands.literal("AttachmentConstraint")
						.then(
								Commands.argument("contraption1", ContraptionIdArgument.contraption())
								.then(
										Commands.argument("contraption2", ContraptionIdArgument.contraption())
										.then(
												Commands.argument("pos1", Vec3Argument.vec3())
												.then(
														Commands.argument("pos2", Vec3Argument.vec3())
														.then(
																Commands.argument("distance", DoubleArgumentType.doubleArg())
																.executes((source) ->
																		executeCreateAttachmentConstraint(source, ContraptionIdArgument.getContraption(source, "contraption1"), ContraptionIdArgument.getContraption(source, "contraption2"), Vec3Argument.getVec3(source, "pos1"), Vec3Argument.getVec3(source, "pos2"), DoubleArgumentType.getDouble(source, "distance"))
																)
														)
												)
										)
								)
						)
						
						// End of Section for the "AttachmentConstrain" (Copy and paste directly below for more constraint types)
				)
		));
		
	}
	
	public static int executeCreateAttachmentConstraint(CommandContext<CommandSourceStack> source, Ship ship1, Ship ship2, Vec3 pos1, Vec3 pos2, double distance) {
		
		return 0; // Implementation of command, return 1 if success, 0 otherwise
		
	}
	
}
