package de.m_marvin.industria.core.conduits.engine.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.conduits.engine.ConduitStateParser;
import de.m_marvin.industria.core.conduits.types.ConduitInput;
import net.minecraft.commands.CommandSourceStack;

public class ConduitStateArgument implements ArgumentType<ConduitInput> {
	
	private static final Collection<String> EXAMPLES = Arrays.asList("electric_wire", "industria:electric_wire", "electric_wire[foo=bar]", "foo{bar=baz}");
	
	private ConduitStateArgument() {}

	public static ConduitStateArgument conduit() {
		return new ConduitStateArgument();
	}

	public ConduitInput parse(StringReader pReader) throws CommandSyntaxException {
		ConduitStateParser.ConduitResult blockstateparser$blockresult = ConduitStateParser.parseForConduit(pReader, true);
		return new ConduitInput(blockstateparser$blockresult.conduitState(), blockstateparser$blockresult.properties().keySet(), blockstateparser$blockresult.nbt());
	}

	public static ConduitInput getConduit(CommandContext<CommandSourceStack> pContext, String pName) {
		return pContext.getArgument(pName, ConduitInput.class);
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
		return ConduitStateParser.fillSuggestions(pBuilder, false, true);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
	
}