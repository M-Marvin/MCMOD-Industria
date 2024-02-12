package de.m_marvin.industria.core.physics.engine.commands.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ContraptionSelectorOptions {
	
	private static final Map<String, ContraptionSelectorOptions.Option> OPTIONS = Maps.newHashMap();
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((p_121520_) -> {
		return Component.translatable("industriacore.argument.contraption.options.unknown", p_121520_);
	});
	public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((p_121516_) -> {
		return Component.translatable("industriacore.argument.contraption.options.inapplicable", p_121516_);
	});
	public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.distance.negative"));
	public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.limit.toosmall"));
	public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((p_121508_) -> {
		return Component.translatable("industriacore.argument.contraption.options.sort.irreversible", p_121508_);
	});

	public static void register(String pId, ContraptionSelectorOptions.Modifier pHandler, Predicate<ContraptionSelectorParser> pPredicate, Component pTooltip) {
		OPTIONS.put(pId, new ContraptionSelectorOptions.Option(pHandler, pPredicate, pTooltip));
	}

	public static void bootStrap() {
		if (OPTIONS.isEmpty()) {
			register("name", (p_121425_) -> {
				int i = p_121425_.getReader().getCursor();
				boolean flag = p_121425_.shouldInvertValue();
				String s = p_121425_.getReader().readString();
				if (p_121425_.hasNameNotEquals() && !flag) {
					p_121425_.getReader().setCursor(i);
					throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_121425_.getReader(), "name");
				} else {
					if (flag) {
						p_121425_.setHasNameNotEquals(true);
					} else {
						p_121425_.setHasNameEquals(true);
					}

					p_121425_.addPredicate((p_175209_) -> {
						return p_175209_.getName().getString().equals(s) != flag;
					});
				}
			}, (p_121423_) -> {
				return !p_121423_.hasNameEquals();
			}, Component.translatable("industriacore.argument.contraption.options.name.description"));
			register("distance", (p_121421_) -> {
				int i = p_121421_.getReader().getCursor();
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(p_121421_.getReader());
				if ((minmaxbounds$doubles.getMin() == null || !(minmaxbounds$doubles.getMin() < 0.0D)) && (minmaxbounds$doubles.getMax() == null || !(minmaxbounds$doubles.getMax() < 0.0D))) {
					p_121421_.setDistance(minmaxbounds$doubles);
					p_121421_.setWorldLimited();
				} else {
					p_121421_.getReader().setCursor(i);
					throw ERROR_RANGE_NEGATIVE.createWithContext(p_121421_.getReader());
				}
			}, (p_121419_) -> {
				return p_121419_.getDistance().isAny();
			}, Component.translatable("industriacore.argument.contraption.options.distance.description"));
			register("x", (p_121413_) -> {
				p_121413_.setWorldLimited();
				p_121413_.setX(p_121413_.getReader().readDouble());
			}, (p_121411_) -> {
				return p_121411_.getX() == null;
			}, Component.translatable("industriacore.argument.contraption.options.x.description"));
			register("y", (p_121409_) -> {
				p_121409_.setWorldLimited();
				p_121409_.setY(p_121409_.getReader().readDouble());
			}, (p_121407_) -> {
				return p_121407_.getY() == null;
			}, Component.translatable("industriacore.argument.contraption.options.y.description"));
			register("z", (p_121405_) -> {
				p_121405_.setWorldLimited();
				p_121405_.setZ(p_121405_.getReader().readDouble());
			}, (p_121403_) -> {
				return p_121403_.getZ() == null;
			}, Component.translatable("industriacore.argument.contraption.options.z.description"));
			register("dx", (p_121401_) -> {
				p_121401_.setWorldLimited();
				p_121401_.setDeltaX(p_121401_.getReader().readDouble());
			}, (p_121399_) -> {
				return p_121399_.getDeltaX() == null;
			}, Component.translatable("industriacore.argument.contraption.options.dx.description"));
			register("dy", (p_121397_) -> {
				p_121397_.setWorldLimited();
				p_121397_.setDeltaY(p_121397_.getReader().readDouble());
			}, (p_121395_) -> {
				return p_121395_.getDeltaY() == null;
			}, Component.translatable("industriacore.argument.contraption.options.dy.description"));
			register("dz", (p_121562_) -> {
				p_121562_.setWorldLimited();
				p_121562_.setDeltaZ(p_121562_.getReader().readDouble());
			}, (p_121560_) -> {
				return p_121560_.getDeltaZ() == null;
			}, Component.translatable("industriacore.argument.contraption.options.dz.description"));
			register("x_rotation", (p_121558_) -> {
				p_121558_.setRotX(WrappedMinMaxBounds.fromReader(p_121558_.getReader(), true, Mth::wrapDegrees));
			}, (p_121556_) -> {
				return p_121556_.getRotX() == WrappedMinMaxBounds.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.x_rotation.description"));
			register("y_rotation", (p_121554_) -> {
				p_121554_.setRotY(WrappedMinMaxBounds.fromReader(p_121554_.getReader(), true, Mth::wrapDegrees));
			}, (p_121552_) -> {
				return p_121552_.getRotY() == WrappedMinMaxBounds.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.y_rotation.description"));
			register("z_rotation", (p_121554_) -> {
				 p_121554_.setRotZ(WrappedMinMaxBounds.fromReader(p_121554_.getReader(), true, Mth::wrapDegrees));
			 }, (p_121552_) -> {
				 return p_121552_.getRotZ() == WrappedMinMaxBounds.ANY;
			 }, Component.translatable("industriacore.argument.contraption.options.z_rotation.description"));
			register("limit", (p_121550_) -> {
				int i = p_121550_.getReader().getCursor();
				int j = p_121550_.getReader().readInt();
				if (j < 1) {
					p_121550_.getReader().setCursor(i);
					throw ERROR_LIMIT_TOO_SMALL.createWithContext(p_121550_.getReader());
				} else {
					p_121550_.setMaxResults(j);
					p_121550_.setLimited(true);
				}
			}, (p_121548_) -> {
				return !p_121548_.isCurrentContraption() && !p_121548_.isLimited();
			}, Component.translatable("industriacore.argument.contraption.options.limit.description"));
			register("sort", (p_247983_) -> {
				int i = p_247983_.getReader().getCursor();
				String s = p_247983_.getReader().readUnquotedString();
				p_247983_.setSuggestions((p_175153_, p_175154_) -> {
					return SharedSuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), p_175153_);
				});
				BiConsumer<Vec3, List<? extends Contraption>> biconsumer;
				switch (s) {
					case "nearest":
						biconsumer = ContraptionSelectorParser.ORDER_NEAREST;
						break;
					case "furthest":
						biconsumer = ContraptionSelectorParser.ORDER_FURTHEST;
						break;
					case "random":
						biconsumer = ContraptionSelectorParser.ORDER_RANDOM;
						break;
					case "arbitrary":
						biconsumer = ContraptionSelector.ORDER_ARBITRARY;
						break;
					default:
						p_247983_.getReader().setCursor(i);
						throw ERROR_SORT_UNKNOWN.createWithContext(p_247983_.getReader(), s);
				}

				p_247983_.setOrder(biconsumer);
				p_247983_.setSorted(true);
			}, (p_121544_) -> {
				return !p_121544_.isCurrentContraption() && !p_121544_.isSorted();
			}, Component.translatable("industriacore.argument.contraption.options.sort.description"));
			
			register("tag", (p_121530_) -> {
				boolean flag = p_121530_.shouldInvertValue();
				String s = p_121530_.getReader().readUnquotedString();
				p_121530_.addPredicate((p_175166_) -> {
					if ("".equals(s)) {
						return p_175166_.getTags().isEmpty() != flag;
					} else {
						return p_175166_.getTags().contains(s) != flag;
					}
				});
			}, (p_121528_) -> {
				return true;
			}, Component.translatable("industriacore.argument.contraption.options.tag.description"));
			
		}
	}

	public static ContraptionSelectorOptions.Modifier get(ContraptionSelectorParser pParser, String pId, int pCursor) throws CommandSyntaxException {
		ContraptionSelectorOptions.Option entityselectoroptions$option = OPTIONS.get(pId);
		if (entityselectoroptions$option != null) {
			if (entityselectoroptions$option.canUse.test(pParser)) {
				return entityselectoroptions$option.modifier;
			} else {
				throw ERROR_INAPPLICABLE_OPTION.createWithContext(pParser.getReader(), pId);
			}
		} else {
			pParser.getReader().setCursor(pCursor);
			throw ERROR_UNKNOWN_OPTION.createWithContext(pParser.getReader(), pId);
		}
	}

	public static void suggestNames(ContraptionSelectorParser pParser, SuggestionsBuilder pBuilder) {
		String s = pBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for(Map.Entry<String, ContraptionSelectorOptions.Option> entry : OPTIONS.entrySet()) {
			if ((entry.getValue()).canUse.test(pParser) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(s)) {
				pBuilder.suggest((String)entry.getKey() + "=", (entry.getValue()).description);
			}
		}

	}

	public interface Modifier {
		void handle(ContraptionSelectorParser pParser) throws CommandSyntaxException;
	}

	static record Option(ContraptionSelectorOptions.Modifier modifier, Predicate<ContraptionSelectorParser> canUse, Component description) {}
	
}