package de.m_marvin.industria.core.conduits.engine.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class NodeIdArgument implements ArgumentType<Integer> {

	public static final Collection<String> EXAMPLES = Arrays.asList("0", "1", "2");
	public static final DynamicCommandExceptionType ERROR_INVALID_NODE = new DynamicCommandExceptionType((object) -> {
		return Component.translatable("industriacore.argument.nodeid.invalid", object);
	});
	private final Function<CommandContext<CommandSourceStack>, ConduitNode[]> nodeSource;
	private final NodeType[] types;
	
	private NodeIdArgument(Function<CommandContext<CommandSourceStack>, ConduitNode[]> nodeSource, NodeType[] types) {
		this.nodeSource = nodeSource;
		this.types = types;
	}
	
	public static NodeIdArgument withoutSource() {
		return new NodeIdArgument(null, null);
	}
	
	public static NodeIdArgument onSource(Function<CommandContext<CommandSourceStack>, ConduitNode[]> nodeSource, NodeType... types) {
		return new NodeIdArgument(nodeSource, types);
	}

	public static NodeIdArgument onBlock(Function<CommandContext<CommandSourceStack>, BlockPos> blockSource, NodeType... types) {
		return onSource(ctx -> {
			BlockPos blockPos = blockSource.apply(ctx);
			if (blockPos == null)
				return null;
			BlockState nodeState = ctx.getSource().getLevel().getBlockState(blockPos);
			if (nodeState.getBlock() instanceof IConduitConnector connector)
				return connector.getConduitNodes(ctx.getSource().getLevel(), blockPos, nodeState);
			return null;
		}, types);
	}
	
	public static int getNodeId(CommandContext<CommandSourceStack> ctx, String name) {
		return ctx.getArgument(name, Integer.class);
	}
	
	@Override
	public Integer parse(StringReader reader) throws CommandSyntaxException {
		final int start = reader.getCursor();
		final int result = reader.readInt();
		if (result < 0) {
			reader.setCursor(start);
			throw ERROR_INVALID_NODE.create(result);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (this.nodeSource == null)
			return Suggestions.empty();
		ConduitNode[] nodes = this.nodeSource.apply((CommandContext<CommandSourceStack>) context);
		IntStream.range(0, nodes.length)
			.filter(i -> (this.types == null || this.types.length == 0) ? true : nodes[i].getType().canConnectWith(this.types))
			.forEach(i -> builder.suggest(i));
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
	
}
