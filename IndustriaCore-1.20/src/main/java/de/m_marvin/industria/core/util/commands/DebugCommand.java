package de.m_marvin.industria.core.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitNode;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class DebugCommand {
	
	public static final int MAX_AREA_SIZE = 110592;
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("debug").requires((source) -> 
			source.hasPermission(2)
		)	
		.then(
				Commands.literal("dump_circuit")
				.then(
						Commands.argument("pos", BlockPosArgument.blockPos())
						.executes((source) -> 
								dumpCircuit(source, BlockPosArgument.getLoadedBlockPos(source, "pos"))
						)
				)
		)
		.then(
				Commands.literal("print_nodes")
				.then(
						Commands.argument("pos", BlockPosArgument.blockPos())
						.executes((source) ->
								printNodes(source, BlockPosArgument.getBlockPos(source, "pos"))
						)
				)
		));
	}
	
	public static int dumpCircuit(CommandContext<CommandSourceStack> source, BlockPos position) {
		ServerLevel level = source.getSource().getLevel();
		
		ElectricNetwork network = ElectricUtility.findNetworkAt(level, ElectricReference.block(position));
		if (network == null) return 0;
		String circuit = network.toString();
		
		Minecraft.getInstance().keyboardHandler.setClipboard(circuit);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.circuit_copied"), false);
		return 1;
	}
	
	public static int printNodes(CommandContext<CommandSourceStack> source, BlockPos position) {
		ServerLevel level = source.getSource().getLevel();
		
		ElectricComponent<Object, Object> component = ElectricUtility.findComponentAt(level, ElectricReference.block(position));
		if (component == null) return 0;
		NodePos[] nodes = component.getNodes(level);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.node_voltages.title", nodes.length), false);
		for (NodePos node : nodes) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.node_voltages.node", node.getNode()), false);
			String[] lanes = ElectricUtility.getLaneLabelsSummarized(level, node);
			for (int i = 0; i < lanes.length; i++) {
				double potential = ElectricUtility.getFloatingNodeVoltage(level, CircuitNode.node(node, lanes[i]));
				final int id = i;
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.node_voltages.lane", id, lanes[id], Double.toString(potential)), false);
			}
		}
		return 1;
	}
	
}
