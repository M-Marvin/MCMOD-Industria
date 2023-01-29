package de.m_marvin.industria.core.physics.engine.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.physics.PhysicUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ContraptionIdArgument implements ArgumentType<Long> {
	
	public static final String CONTRAPTION_PREFIX = "contraption";
	public static final Collection<String> EXAMPLES = Arrays.asList("contraption_1", "contraption_42");
	public static final DynamicCommandExceptionType ERROR_NON_EXISTING_CONTRAPTION = new DynamicCommandExceptionType((object) -> {
		return new TranslatableComponent("industria.command.contraption.notFound", object);
	});
	
	public static ContraptionIdArgument contraption() {
		return new ContraptionIdArgument();
	}
	
	public static Ship getContraption(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		long id = getContraptionId(context, name);
		return PhysicUtility.getContraptionById(context.getSource().getLevel(), id);
	}
	
	public static Long getContraptionId(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return verifyContraptionId(context.getArgument(name, String.class));
	}
	
	@Override
	public Long parse(StringReader reader) throws CommandSyntaxException {
		return verifyContraptionId(reader.readString());
	}
	
	@SuppressWarnings("resource")
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		
		if (context.getSource() instanceof SharedSuggestionProvider) {
			
			ClientLevel level = Minecraft.getInstance().level;
			for (Ship contraption : PhysicUtility.getLoadedContraptions(level)) {
				builder.suggest(CONTRAPTION_PREFIX + contraption.getId());
			}
			// TODO
		}
		
		return builder.buildFuture();
	}
	
	public static Long verifyContraptionId(String conduitName) throws CommandSyntaxException {
		String idStr = conduitName.substring(CONTRAPTION_PREFIX.length());
		return Long.parseLong(idStr);
	}
	
	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
	
}
