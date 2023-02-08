package de.m_marvin.industria.core.physics.engine.commands;

import java.util.Arrays;
import java.util.Collection;
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
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult.Type;

public class ContraptionIdArgument implements ArgumentType<Long> {
	
	public static final String CONTRAPTION_PREFIX = "contraption";
	public static final Collection<String> EXAMPLES = Arrays.asList("contraption1", "contraption42");
	public static final DynamicCommandExceptionType ERROR_NON_EXISTING_CONTRAPTION = new DynamicCommandExceptionType((object) -> {
		return new TranslatableComponent("industria.argument.contraption.notFound", object);
	});
	public static final DynamicCommandExceptionType ERROR_MALEFORMED_CONTRAPTION = new DynamicCommandExceptionType((object) -> {
		return new TranslatableComponent("industria.argument.contraption.maleFormed", object);
	});
	
	public static ContraptionIdArgument contraption() {
		return new ContraptionIdArgument();
	}
	
	public static Ship getContraption(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		long id = getContraptionId(context, name);
		return PhysicUtility.getContraptionById(context.getSource().getLevel(), id);
	}
	
	public static Long getContraptionId(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return verifyContraptionId(context.getSource().getLevel(), context.getArgument(name, Long.class));
	}
	
	@Override
	public Long parse(StringReader reader) throws CommandSyntaxException {
		String inputStr = reader.readString();
		long id = tryParseName(inputStr);
		if (id == 0) {
			id = tryParseId(inputStr);
		}
		if (id > 0) {
			return id;
		}
		throw ERROR_MALEFORMED_CONTRAPTION.create(inputStr);
	}
	
	public Long tryParseId(String input) {
		String prefix = input.substring(0, CONTRAPTION_PREFIX.length());
		try {
			if (prefix.equals(CONTRAPTION_PREFIX)) {
				String idStr = input.substring(CONTRAPTION_PREFIX.length());
				return Long.parseLong(idStr);
			}
			return 0L;
		} catch (Exception e) {}
		return 0L;
	}
	
	@SuppressWarnings("resource")
	public Long tryParseName(String input) {
		return 0L; // FIXME How to access the name-map ???
		//return PhysicUtility.getFirstContraptionIdWithName(Minecraft.getInstance().level, input);
	}
	
	@SuppressWarnings("resource")
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		
		if (context.getSource() instanceof SharedSuggestionProvider) {
			
			ClientLevel level = Minecraft.getInstance().level;
			Player player = Minecraft.getInstance().player;
			Vec3d eyePos = Vec3d.fromVec(player.getEyePosition());
			Vec3d direction = Vec3d.fromVec(player.getViewVector(0));
			double range = player.getReachDistance();
			
			ContraptionHitResult result = PhysicUtility.clipForContraption(level, eyePos, direction, range);
			
			if (result.getType() != Type.MISS) {
				long contraptionId = result.getContraption().getId();
				builder.suggest(CONTRAPTION_PREFIX + contraptionId);
			} else {
				for (Ship contraption : PhysicUtility.getLoadedContraptions(level)) {
					builder.suggest(CONTRAPTION_PREFIX + contraption.getId());
				}
			}
			
		}
		
		return builder.buildFuture();
	}
	
	public static Long verifyContraptionId(Level level, long contraptionId) throws CommandSyntaxException {
		if (PhysicUtility.getContraptionById(level, contraptionId) != null) {
			return contraptionId;
		}
		throw ERROR_NON_EXISTING_CONTRAPTION.create(contraptionId);
	}
	
	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
	
}
