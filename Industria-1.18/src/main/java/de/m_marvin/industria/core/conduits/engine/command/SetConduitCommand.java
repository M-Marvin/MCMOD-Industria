package de.m_marvin.industria.core.conduits.engine.command;

import java.util.Optional;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class SetConduitCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("setconduit").requires((source) -> 
			source.hasPermission(2)
		)
		.then(
				Commands.argument("nodeApos", BlockPosArgument.blockPos())
				.then(
						Commands.argument("nodeAid", IntegerArgumentType.integer(0))
						.then(
								Commands.argument("nodeBpos", BlockPosArgument.blockPos())
								.then(
										Commands.argument("nodeBid", IntegerArgumentType.integer(0))
										.then(
												Commands.argument("conduit", ConduitArgument.conduit())
												.executes((source) ->
														setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), IntegerArgumentType.getInteger(source, "nodeAid"), IntegerArgumentType.getInteger(source, "nodeBid"), ConduitArgument.getConduit(source, "conduit"), 1, false)
												)
												.then(
														Commands.argument("length", FloatArgumentType.floatArg(1F, 3F))
														.executes((source) -> 
																setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), IntegerArgumentType.getInteger(source, "nodeAid"), IntegerArgumentType.getInteger(source, "nodeBid"), ConduitArgument.getConduit(source, "conduit"), FloatArgumentType.getFloat(source, "length"), false)
														)
														.then(
																Commands.literal("destroy")
																.executes((source) ->
																		setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), IntegerArgumentType.getInteger(source, "nodeAid"), IntegerArgumentType.getInteger(source, "nodeBid"), ConduitArgument.getConduit(source, "conduit"), FloatArgumentType.getFloat(source, "length"), true)
																)
														)
												)
										)
								)
						)
				)
		));		
	}
	
	public static int setConduit(CommandContext<CommandSourceStack> source, BlockPos nodeApos, BlockPos nodeBpos, int nodeAid, int nodeBid, ResourceLocation conduitKey, float length, boolean drop) {
		ServerLevel level = source.getSource().getLevel();
		ConduitPos position = new ConduitPos(nodeApos, nodeBpos, nodeAid, nodeBid);
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitKey);
		
		Optional<ConduitEntity> existingConduit = ConduitUtility.getConduit(level, position);
		if (existingConduit.isPresent()) {
			ConduitUtility.removeConduit(level, position, drop);
		}
		
		double conduitLength = Math.ceil(position.calculateMinConduitLength(level)) * length;
		
		if (conduit != Conduits.NONE.get()) {
			if (ConduitUtility.setConduit(level, position, conduit, conduitLength)) {
				source.getSource().sendSuccess(Component.translatable("industria.commands.setconduit.success", nodeApos.getX(), nodeApos.getY(), nodeApos.getZ()), true);
				return Command.SINGLE_SUCCESS;
			} else {
				source.getSource().sendFailure(Component.translatable("industria.commands.setconduit.failure"));
			}
		} else if (existingConduit.isPresent()) {
			source.getSource().sendSuccess(Component.translatable("industria.commands.setconduit.success", nodeApos.getX(), nodeApos.getY(), nodeApos.getZ()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		return 0;
	}
	
}
