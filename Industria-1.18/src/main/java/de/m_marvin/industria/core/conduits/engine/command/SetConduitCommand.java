package de.m_marvin.industria.core.conduits.engine.command;

import java.util.Optional;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class SetConduitCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("setconduit").requires((source) -> {
			return source.hasPermission(2);
		}).then(Commands.argument("nodeApos", BlockPosArgument.blockPos()).then(Commands.argument("nodeAid", IntegerArgumentType.integer(0)).then(Commands.argument("nodeBpos", BlockPosArgument.blockPos()).then(Commands.argument("nodeBid", IntegerArgumentType.integer(0)).then(Commands.argument("conduit", ConduitArgument.conduit()).executes((source) -> {
			return setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), IntegerArgumentType.getInteger(source, "nodeAid"), IntegerArgumentType.getInteger(source, "nodeBid"), ConduitArgument.getConduit(source, "conduit"), 1, false);
		}).then(Commands.argument("length", IntegerArgumentType.integer(1, 12)).executes((source) -> {
			return setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), IntegerArgumentType.getInteger(source, "nodeAid"), IntegerArgumentType.getInteger(source, "nodeBid"), ConduitArgument.getConduit(source, "conduit"), IntegerArgumentType.getInteger(source, "length"), false);
		}).then(Commands.literal("destroy").executes((source) -> {
			return setConduit(source, BlockPosArgument.getLoadedBlockPos(source, "nodeApos"), BlockPosArgument.getLoadedBlockPos(source, "nodeBpos"), IntegerArgumentType.getInteger(source, "nodeAid"), IntegerArgumentType.getInteger(source, "nodeBid"), ConduitArgument.getConduit(source, "conduit"), IntegerArgumentType.getInteger(source, "length"), true);
		})))))))));		
	}
	
	public static int setConduit(CommandContext<CommandSourceStack> source, BlockPos nodeApos, BlockPos nodeBpos, int nodeAid, int nodeBid, ResourceLocation conduitKey, int nodesPerBlock, boolean drop) {
		ServerLevel level = source.getSource().getLevel();
		ConduitPos position = new ConduitPos(nodeApos, nodeBpos, nodeAid, nodeBid);
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitKey);
		
		Optional<PlacedConduit> existingConduit = ConduitUtility.getConduit(level, position);
		if (existingConduit.isPresent()) {
			ConduitUtility.removeConduit(level, position, drop);
		}
		
		int conduitLength = (int) Math.ceil(position.calculateMinConduitLength(level));
		
		if (conduit != Conduits.NONE.get()) {
			if (ConduitUtility.setConduit(level, position, conduit, nodesPerBlock, conduitLength)) {
				source.getSource().sendSuccess(new TranslatableComponent("industria.commands.setconduit.success", nodeApos.getX(), nodeApos.getY(), nodeApos.getZ()), true);
				return Command.SINGLE_SUCCESS;
			} else {
				source.getSource().sendFailure(new TranslatableComponent("industria.commands.setconduit.failure"));
			}
		} else if (existingConduit.isPresent()) {
			source.getSource().sendSuccess(new TranslatableComponent("industria.commands.setconduit.success", nodeApos.getX(), nodeApos.getY(), nodeApos.getZ()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		return 0;
	}
	
}
