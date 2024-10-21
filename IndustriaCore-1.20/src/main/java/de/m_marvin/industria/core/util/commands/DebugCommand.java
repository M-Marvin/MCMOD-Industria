package de.m_marvin.industria.core.util.commands;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
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
	
	@SuppressWarnings("resource")
	public static int dumpCircuit(CommandContext<CommandSourceStack> source, BlockPos position) {
		ServerLevel level = source.getSource().getLevel();
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		ElectricNetworkHandlerCapability.Component<Object, BlockPos, Object> component = handler.getComponentAt(position);
		if (component == null) return 0;
		ElectricNetwork network = handler.getCircuitWithComponent(component);
		if (network == null) return 0;
		String circuit = network.toString();
		
		Minecraft.getInstance().keyboardHandler.setClipboard(circuit);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.circuit_copied"), false);
		return 1;
	}
	
	public static int printNodes(CommandContext<CommandSourceStack> source, BlockPos position) {
		ServerLevel level = source.getSource().getLevel();
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		
		ElectricNetworkHandlerCapability.Component<Object, BlockPos, Object> component = handler.getComponentAt(position);
		if (component == null) return 0;
		NodePos[] nodes = component.getNodes(level);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.node_voltages.title", nodes.length), false);
		for (NodePos node : nodes) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.node_voltages.node", node.getNode()), false);
			String[] lanes = ElectricUtility.getLaneLabelsSummarized(level, node);
			for (int i = 0; i < lanes.length; i++) {
				Optional<Double> potential = handler.getFloatingNodeVoltage(node, i, lanes[i]);
				final int id = i;
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.debug.node_voltages.lane", id, lanes[id], potential.isPresent() ? Double.toString(potential.get()) : "N/A"), false);
			}
		}
		return 1;
	}
	
}
