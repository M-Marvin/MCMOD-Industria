package de.m_marvin.industria.core.physics.engine.commands.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.google.common.collect.Maps;
import com.machinezoo.noexception.optional.OptionalBoolean;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.physics.types.Contraption;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ContraptionSelectorOptions {
	
	private static final Map<String, ContraptionSelectorOptions.Option> OPTIONS = Maps.newHashMap();
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((obj) -> {
		return Component.translatable("industriacore.argument.contraption.options.unknown", obj);
	});
	public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((obj) -> {
		return Component.translatable("industriacore.argument.contraption.options.inapplicable", obj);
	});
	public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.distance.negative"));
	public static final SimpleCommandExceptionType ERROR_SIZE_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.size.negative"));
	public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.limit.toosmall"));
	public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((obj) -> {
		return Component.translatable("industriacore.argument.contraption.options.sort.irreversible", obj);
	});

	public static void register(String pId, ContraptionSelectorOptions.Modifier pHandler, Predicate<ContraptionSelectorParser> pPredicate, Component pTooltip) {
		OPTIONS.put(pId, new ContraptionSelectorOptions.Option(pHandler, pPredicate, pTooltip));
	}

	public static void bootStrap() {
		if (OPTIONS.isEmpty()) {
			register("name", (parser) -> {
				int i = parser.getReader().getCursor();
				boolean flag = parser.shouldInvertValue();
				String s = parser.getReader().readString();
				if (parser.hasNameNotEquals() && !flag) {
					parser.getReader().setCursor(i);
					throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "name");
				} else {
					if (flag) {
						parser.setHasNameNotEquals(true);
					} else {
						parser.setHasNameEquals(true);
					}

					parser.addPredicate((contraption) -> {
						return contraption.getName().getString().equals(s) != flag;
					});
				}
			}, (parser) -> {
				return !parser.hasNameEquals();
			}, Component.translatable("industriacore.argument.contraption.options.name.description"));
			register("distance", (parser) -> {
				int i = parser.getReader().getCursor();
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				if ((minmaxbounds$doubles.getMin() == null || !(minmaxbounds$doubles.getMin() < 0.0D)) && (minmaxbounds$doubles.getMax() == null || !(minmaxbounds$doubles.getMax() < 0.0D))) {
					parser.setDistance(minmaxbounds$doubles);
					parser.setWorldLimited();
				} else {
					parser.getReader().setCursor(i);
					throw ERROR_RANGE_NEGATIVE.createWithContext(parser.getReader());
				}
			}, (parser) -> {
				return parser.getDistance().isAny();
			}, Component.translatable("industriacore.argument.contraption.options.distance.description"));
			register("x", (parser) -> {
				parser.setWorldLimited();
				parser.setX(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getX() == null;
			}, Component.translatable("industriacore.argument.contraption.options.x.description"));
			register("y", (parser) -> {
				parser.setWorldLimited();
				parser.setY(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getY() == null;
			}, Component.translatable("industriacore.argument.contraption.options.y.description"));
			register("z", (parser) -> {
				parser.setWorldLimited();
				parser.setZ(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getZ() == null;
			}, Component.translatable("industriacore.argument.contraption.options.z.description"));
			register("dx", (parser) -> {
				parser.setWorldLimited();
				parser.setDeltaX(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getDeltaX() == null;
			}, Component.translatable("industriacore.argument.contraption.options.dx.description"));
			register("dy", (parser) -> {
				parser.setWorldLimited();
				parser.setDeltaY(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getDeltaY() == null;
			}, Component.translatable("industriacore.argument.contraption.options.dy.description"));
			register("dz", (parser) -> {
				parser.setWorldLimited();
				parser.setDeltaZ(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getDeltaZ() == null;
			}, Component.translatable("industriacore.argument.contraption.options.dz.description"));
			register("x_rotation", (parser) -> {
				parser.setRotX(WrappedMinMaxBounds.fromReader(parser.getReader(), true, Mth::wrapDegrees));
			}, (parser) -> {
				return parser.getRotX() == WrappedMinMaxBounds.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.x_rotation.description"));
			register("y_rotation", (parser) -> {
				parser.setRotY(WrappedMinMaxBounds.fromReader(parser.getReader(), true, Mth::wrapDegrees));
			}, (parser) -> {
				return parser.getRotY() == WrappedMinMaxBounds.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.y_rotation.description"));
			register("z_rotation", (parser) -> {
				parser.setRotZ(WrappedMinMaxBounds.fromReader(parser.getReader(), true, Mth::wrapDegrees));
			}, (parser) -> {
				return parser.getRotZ() == WrappedMinMaxBounds.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.z_rotation.description"));
			
			register("x_velocity", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setVelocityX(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getVelocityX() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.x_velocity.description"));
			register("y_velocity", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setVelocityY(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getVelocityY() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.y_velocity.description"));
			register("z_velocity", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setVelocityZ(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getVelocityZ() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.z_velocity.description"));
			
			register("velocity", (parser) -> {
				int i = parser.getReader().getCursor();
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				if ((minmaxbounds$doubles.getMin() == null || (minmaxbounds$doubles.getMin() >= 0.0D)) && (minmaxbounds$doubles.getMax() == null || (minmaxbounds$doubles.getMax() >= 0.0D))) {
					parser.setVelocity(minmaxbounds$doubles);
				} else {
					parser.getReader().setCursor(i);
					throw ERROR_RANGE_NEGATIVE.createWithContext(parser.getReader());
				}
			}, (parser) -> {
				return parser.getVelocity() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.velocity.description"));
			
			register("x_omega", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setOmegaX(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getOmegaX() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.x_omega.description"));
			register("y_omega", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setOmegaY(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getOmegaY() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.y_omega.description"));
			register("z_omega", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setOmegaZ(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getOmegaZ() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.z_omega.description"));

			register("omega", (parser) -> {
				int i = parser.getReader().getCursor();
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				if ((minmaxbounds$doubles.getMin() == null || (minmaxbounds$doubles.getMin() >= 0.0D)) && (minmaxbounds$doubles.getMax() == null || (minmaxbounds$doubles.getMax() >= 0.0D))) {
					parser.setOmega(minmaxbounds$doubles);
				} else {
					parser.getReader().setCursor(i);
					throw ERROR_RANGE_NEGATIVE.createWithContext(parser.getReader());
				}
			}, (parser) -> {
				return parser.getOmega() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.omega.description"));

			register("size", (parser) -> {
				int i = parser.getReader().getCursor();
				MinMaxBounds.Doubles minmaxbounds$ints = MinMaxBounds.Doubles.fromReader(parser.getReader());
				if ((minmaxbounds$ints.getMin() == null || (minmaxbounds$ints.getMin() >= 0)) && (minmaxbounds$ints.getMax() == null || (minmaxbounds$ints.getMax() >= 0))) {
					parser.setSize(minmaxbounds$ints);
				} else {
					parser.getReader().setCursor(i);
					throw ERROR_RANGE_NEGATIVE.createWithContext(parser.getReader());
				}
			}, (parser) -> {
				return parser.getSize() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.size.description"));
			
			register("static", (parser) -> {
				boolean isStatic = parser.getReader().readBoolean();
				parser.setStatic(OptionalBoolean.of(isStatic));
			}, (parser) -> {
				return !parser.getStatic().isPresent();
			}, Component.translatable("industriacore.argument.contraption.option.static.description"));

			register("mass", (parser) -> {
				MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromReader(parser.getReader());
				parser.setMass(minmaxbounds$doubles);
			}, (parser) -> {
				return parser.getMass() == MinMaxBounds.Doubles.ANY;
			}, Component.translatable("industriacore.argument.contraption.options.mass.description"));
			
			
			register("limit", (parser) -> {
				int i = parser.getReader().getCursor();
				int j = parser.getReader().readInt();
				if (j < 1) {
					parser.getReader().setCursor(i);
					throw ERROR_LIMIT_TOO_SMALL.createWithContext(parser.getReader());
				} else {
					parser.setMaxResults(j);
					parser.setLimited(true);
				}
			}, (parser) -> {
				return !parser.isCurrentContraption() && !parser.isLimited();
			}, Component.translatable("industriacore.argument.contraption.options.limit.description"));
			register("sort", (parser) -> {
				int i = parser.getReader().getCursor();
				String s = parser.getReader().readUnquotedString();
				parser.setSuggestions((builder, consumer) -> {
					return SharedSuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), builder);
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
						parser.getReader().setCursor(i);
						throw ERROR_SORT_UNKNOWN.createWithContext(parser.getReader(), s);
				}

				parser.setOrder(biconsumer);
				parser.setSorted(true);
			}, (parser) -> {
				return !parser.isCurrentContraption() && !parser.isSorted();
			}, Component.translatable("industriacore.argument.contraption.options.sort.description"));
			
			register("tag", (parser) -> {
				boolean flag = parser.shouldInvertValue();
				String s = parser.getReader().readUnquotedString();
				parser.addPredicate((contraption) -> {
					if ("".equals(s)) {
						return contraption.getTags().isEmpty() != flag;
					} else {
						return contraption.getTags().contains(s) != flag;
					}
				});
			}, (parser) -> {
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