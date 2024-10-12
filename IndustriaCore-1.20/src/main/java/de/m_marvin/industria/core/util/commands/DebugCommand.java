package de.m_marvin.industria.core.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
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
		dispatcher.register(Commands.literal("industria").requires((source) -> 
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
				Commands.literal("print_node")
				.then(
						Commands.argument("pos", BlockPosArgument.blockPos())
						.then(
								Commands.argument("node", IntegerArgumentType.integer())
								.executes((source) ->
										printNode(source, BlockPosArgument.getBlockPos(source, "pos"), IntegerArgumentType.getInteger(source, "node"))
								)
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
		
		source.getSource().sendSuccess(() -> Component.literal("Circuit net copied"), false);
		return 1;
	}
	
	public static int printNode(CommandContext<CommandSourceStack> source, BlockPos position, int node) {
		ServerLevel level = source.getSource().getLevel();
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		ElectricNetworkHandlerCapability.Component<Object, BlockPos, Object> component = handler.getComponentAt(position);
		if (component == null) return 0;
		ElectricNetwork network = handler.getCircuitWithComponent(component);
		if (network == null) return 0;
		
		NodePos nodePos = new NodePos(position, node);
		String[] lanes = component.getWireLanes(level, nodePos);
		
		source.getSource().sendSuccess(() -> Component.literal("List " + lanes.length + " available lane voltages"), false);
		for (int laneId = 0; laneId < lanes.length; laneId++) {
			String lane = lanes[laneId];
			double floatVolatage = network.getFloatingNodeVoltage(nodePos, laneId, lane).orElseGet(() -> 0.0);
			
			source.getSource().sendSuccess(() -> Component.literal(String.format("Node '%s' FV: %f", lane, floatVolatage)), false);
		}
		return 1;
	}
	
}
