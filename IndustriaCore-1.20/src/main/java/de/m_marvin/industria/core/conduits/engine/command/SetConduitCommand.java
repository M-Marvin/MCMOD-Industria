package de.m_marvin.industria.core.conduits.engine.command;

import java.util.Optional;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitInput;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class SetConduitCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("setconduit").requires((source) -> 
			source.hasPermission(2)
		)
		.then(
				Commands.argument("nodeApos", BlockPosArgument.blockPos())
				.then(  // TODO context aware arguments seem not to work
						Commands.argument("nodeAid", NodeIdArgument.onBlock(ctx -> BlockPosArgument.getBlockPos(ctx, "nodeApos")))
						.then(
								Commands.argument("nodeBpos", BlockPosArgument.blockPos())
								.then(
										Commands.argument("nodeBid", NodeIdArgument.onBlock(ctx -> BlockPosArgument.getBlockPos(ctx, "nodeBpos")))
										.then(
												Commands.argument("conduit", ConduitStateArgument.conduit())
												.executes((source) ->
														setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), NodeIdArgument.getNodeId(source, "nodeAid"), NodeIdArgument.getNodeId(source, "nodeBid"), ConduitStateArgument.getConduit(source, "conduit"), 1, false)
												)
												.then(
														Commands.argument("length", FloatArgumentType.floatArg(1F, 3F))
														.executes((source) -> 
																setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), NodeIdArgument.getNodeId(source, "nodeAid"), NodeIdArgument.getNodeId(source, "nodeBid"), ConduitStateArgument.getConduit(source, "conduit"), FloatArgumentType.getFloat(source, "length"), false)
														)
														.then(
																Commands.literal("destroy")
																.executes((source) ->
																		setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), NodeIdArgument.getNodeId(source, "nodeAid"), NodeIdArgument.getNodeId(source, "nodeBid"), ConduitStateArgument.getConduit(source, "conduit"), FloatArgumentType.getFloat(source, "length"), true)
																)
														)
												)
										)
								)
						)
				)
		));		
	}
	
	public static int setConduit(CommandContext<CommandSourceStack> source, BlockPos nodeApos, BlockPos nodeBpos, int nodeAid, int nodeBid, ConduitInput conduit, float length, boolean drop) {
		ServerLevel level = source.getSource().getLevel();
		ConduitPos position = new ConduitPos(nodeApos, nodeBpos, nodeAid, nodeBid);
		
		Optional<ConduitEntity> existingConduit = ConduitUtility.getConduit(level, position);
		if (existingConduit.isPresent()) {
			ConduitUtility.removeConduit(level, position, drop);
		}
		
		float conduitLength = (float) (Math.ceil(position.calculateMinConduitLength(level)) * length);
		
		if (!conduit.getState().isNone()) {
			if (conduit.place(level, position, conduitLength)) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.setconduit.success", nodeApos.getX(), nodeApos.getY(), nodeApos.getZ()), true);
				return Command.SINGLE_SUCCESS;
			} else {
				source.getSource().sendFailure(Component.translatable("industriacore.commands.setconduit.failure"));
			}
		} else if (existingConduit.isPresent()) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.setconduit.success", nodeApos.getX(), nodeApos.getY(), nodeApos.getZ()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		return 0;
	}
	
}
