package de.m_marvin.industria.core.physics.engine.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult.Type;

public class ContraptionIdArgument2 implements ArgumentType<ContraptionSelector> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
	public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(Component.translatable("argument.entity.toomany"));
	public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.toomany"));
	public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.player.entities"));
	public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.entity"));
	public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.player"));
	public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.entity.selector.not_allowed"));
	final boolean single;
	
	protected ContraptionIdArgument2(boolean pSingle) {
		this.single = pSingle;
	}

	public static ContraptionIdArgument2 entity() {
		return new ContraptionIdArgument2(true);
	}

	public static Contraption getEntity(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {
		return pContext.getArgument(pName, ContraptionSelector.class).findSingleEntity(pContext.getSource());
	}

	public static ContraptionIdArgument2 entities() {
		return new ContraptionIdArgument2(false);
	}

	public static Collection<? extends Contraption> getEntities(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {
		Collection<? extends Contraption> collection = getOptionalEntities(pContext, pName);
		if (collection.isEmpty()) {
			throw NO_ENTITIES_FOUND.create();
		} else {
			return collection;
		}
	}

	public static Collection<? extends Contraption> getOptionalEntities(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {
		return pContext.getArgument(pName, ContraptionSelector.class).findEntities(pContext.getSource());
	}

	public ContraptionSelector parse(StringReader pReader) throws CommandSyntaxException {
		ContraptionSelectorParser entityselectorparser = new ContraptionSelectorParser(pReader);
		ContraptionSelector entityselector = entityselectorparser.parse();
		if (entityselector.getMaxResults() > 1 && this.single) {
			pReader.setCursor(0);
			throw ERROR_NOT_SINGLE_ENTITY.createWithContext(pReader);
		} else {
			return entityselector;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
		S s = pContext.getSource();
		if (s instanceof SharedSuggestionProvider sharedsuggestionprovider) {
			StringReader stringreader = new StringReader(pBuilder.getInput());
			stringreader.setCursor(pBuilder.getStart());
			ContraptionSelectorParser entityselectorparser = new ContraptionSelectorParser(stringreader, net.minecraftforge.common.ForgeHooks.canUseEntitySelectors(sharedsuggestionprovider));

			try {
				entityselectorparser.parse();
			} catch (CommandSyntaxException commandsyntaxexception) {
			}

			return entityselectorparser.fillSuggestions(pBuilder, (p_91457_) -> {
				
				if (s instanceof ClientSuggestionProvider clientprovider) {
					Level level = clientprovider.minecraft.level;
					Player player = clientprovider.minecraft.player;
					List<Contraption> contraptions = Contraption.fromShipList(level, PhysicUtility.getLoadedContraptions(level));
					
					Iterable<String> contraptionSuggestions = contraptions.stream().map(Contraption::getName).map(Component::getString).filter(x -> x.length() > 0).toList();
					
					Vec3d eyePos = Vec3d.fromVec(player.getEyePosition());
					Vec3d direction = Vec3d.fromVec(player.getViewVector(0));
					double range = player.getBlockReach();
					
					ContraptionHitResult result = PhysicUtility.clipForContraption(level, eyePos, direction, range);
					
					if (result.getType() != Type.MISS) {
						
						Contraption contraption = new Contraption(level, result.getContraption());
						contraptionSuggestions = Iterables.concat(contraptionSuggestions, Collections.singleton(contraption.getIdString()));
						
					}
					
					SharedSuggestionProvider.suggest(contraptionSuggestions, p_91457_);
					
				}
				
			});
		} else {
			return Suggestions.empty();
		}
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Info implements ArgumentTypeInfo<ContraptionIdArgument2, ContraptionIdArgument2.Info.Template> {

		public void serializeToNetwork(ContraptionIdArgument2.Info.Template pTemplate, FriendlyByteBuf pBuffer) {
			int i = 0;
			if (pTemplate.single) {
				i |= 1;
			}
			pBuffer.writeByte(i);
		}

		public ContraptionIdArgument2.Info.Template deserializeFromNetwork(FriendlyByteBuf pBuffer) {
			byte b0 = pBuffer.readByte();
			return new ContraptionIdArgument2.Info.Template((b0 & 1) != 0);
		}

		public void serializeToJson(ContraptionIdArgument2.Info.Template pTemplate, JsonObject pJson) {
			pJson.addProperty("amount", pTemplate.single ? "single" : "multiple");
		}

		public ContraptionIdArgument2.Info.Template unpack(ContraptionIdArgument2 pArgument) {
			return new ContraptionIdArgument2.Info.Template(pArgument.single);
		}

		public final class Template implements ArgumentTypeInfo.Template<ContraptionIdArgument2> {
			final boolean single;

			Template(boolean pSingle) {
				this.single = pSingle;
			}

			public ContraptionIdArgument2 instantiate(CommandBuildContext pContext) {
				return new ContraptionIdArgument2(this.single);
			}

			public ArgumentTypeInfo<ContraptionIdArgument2, ?> type() {
				return Info.this;
			}
		}
	}
}
