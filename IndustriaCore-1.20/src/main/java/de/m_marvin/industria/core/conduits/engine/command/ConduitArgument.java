package de.m_marvin.industria.core.conduits.engine.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConduitArgument implements ArgumentType<ResourceLocation> {
	
	public static final Collection<String> EXAMPLES = Arrays.asList("industria:todo", "work_in_progress");
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_CONDUIT = new DynamicCommandExceptionType((object) -> {
		return Component.translatable("industriacore.command.setconduit.notFound", object);
	});
	
	public static ConduitArgument conduit() {
		return new ConduitArgument();
	}
	
	public static ResourceLocation getConduit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return verifyConduitType(context.getArgument(name, ResourceLocation.class));
	}
	
	@Override
	public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
		return verifyConduitType(ResourceLocation.read(reader));
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		Conduits.CONDUITS_REGISTRY.get().getValues().stream().forEach((conduit) -> builder.suggest(Conduits.CONDUITS_REGISTRY.get().getKey(conduit).toString()));
		return builder.buildFuture();
	}
	
	public static ResourceLocation verifyConduitType(ResourceLocation registryName) throws CommandSyntaxException {
		if (Conduits.CONDUITS_REGISTRY.get().containsKey(registryName)) {
			return registryName;
		} else {
			throw ERROR_UNKNOWN_CONDUIT.create(registryName);
		}
	}
	
	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
	
}
