package de.m_marvin.industria.core.physics.engine.commands.arguments.contraption;

import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nullable;

import com.google.common.primitives.Doubles;
import com.machinezoo.noexception.optional.OptionalBoolean;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.m_marvin.industria.core.physics.types.Contraption;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ContraptionSelectorParser {
	
	public static final char SELECTOR_ALL = 'a';
	public static final char SELECTOR_RANDOM = 'r';
	public static final char SELECTOR_NEAREST = 'n';
	public static final char SELECTOR_SELF = 's';
	
	private static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.invalid"));
	private static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((obj) -> {
		return Component.translatable("industriacore.argument.contraption.selector.unknown", obj);
	});
	private static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.selector.not_allowed"));
	private static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.selector.missing"));
	private static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.unterminated"));
	private static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((obj) -> {
		return Component.translatable("industriacore.argument.contraption.options.valueless", obj);
	});
	
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (builder, consumer) -> {
		return builder.buildFuture();
	};
	
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_NEAREST = (pos, sort) -> {
		sort.sort((a, b) -> {
			return Doubles.compare(a.distanceToSqr(pos), b.distanceToSqr(pos));
		});
	};
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_FURTHEST = (pos, sort) -> {
		sort.sort((a, b) -> {
			return Doubles.compare(b.distanceToSqr(pos), a.distanceToSqr(pos));
		});
	};
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_RANDOM = (pos, sort) -> {
		Collections.shuffle(sort);
	};
			
	private final StringReader reader;
	private final boolean allowSelectors;
	
	private int maxResults;
	private boolean worldLimited;
	
	private MinMaxBounds.Doubles mass = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles size = MinMaxBounds.Doubles.ANY;
	private OptionalBoolean isStatic = OptionalBoolean.empty();
	
	private MinMaxBounds.Doubles velocity_x = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles velocity_y = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles velocity_z = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles velocity = MinMaxBounds.Doubles.ANY;
	
	private MinMaxBounds.Doubles omega_x = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles omega_y = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles omega_z = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Doubles omega = MinMaxBounds.Doubles.ANY;
	
	private MinMaxBounds.Doubles distance = MinMaxBounds.Doubles.ANY;
	@Nullable
	private Double x;
	@Nullable
	private Double y;
	@Nullable
	private Double z;
	@Nullable
	private Double deltaX;
	@Nullable
	private Double deltaY;
	@Nullable
	private Double deltaZ;
	private WrappedMinMaxBounds rotX = WrappedMinMaxBounds.ANY;
	private WrappedMinMaxBounds rotY = WrappedMinMaxBounds.ANY;
	private WrappedMinMaxBounds rotZ = WrappedMinMaxBounds.ANY;
	private Predicate<Contraption> predicate = (contraption) -> {
		return true;
	};
	private BiConsumer<Vec3, List<? extends Contraption>> order = ContraptionSelector.ORDER_ARBITRARY;
	private boolean currentContraption;
	@Nullable
	private String contraptionName;
	private int startPosition;
	@Nullable
	private OptionalLong contraptionID = OptionalLong.empty();
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
	private boolean hasNameEquals;
	private boolean hasNameNotEquals;
	private boolean isLimited;
	private boolean isSorted;
	@Nullable
	private boolean usesSelectors;
	
	public ContraptionSelectorParser(StringReader reader) {
		this(reader, true);
	}
	
	public ContraptionSelectorParser(StringReader reader, boolean allowSelectors) {
		this.reader = reader;
		this.allowSelectors = allowSelectors;
	}
	
	public StringReader getReader() {
		return reader;
	}
	
	public boolean allowSelectors() {
		return allowSelectors;
	}
	
	public boolean shouldInvertValue() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == '!') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public boolean hasNameEquals() {
		return this.hasNameEquals;
	}

	public void setHasNameEquals(boolean pHasNameEquals) {
		this.hasNameEquals = pHasNameEquals;
	}

	public boolean hasNameNotEquals() {
		return this.hasNameNotEquals;
	}

	public void setHasNameNotEquals(boolean pHasNameNotEquals) {
		this.hasNameNotEquals = pHasNameNotEquals;
	}

	public void addPredicate(Predicate<Contraption> pPredicate) {
		this.predicate = this.predicate.and(pPredicate);
	}

	public void setWorldLimited() {
		this.worldLimited = true;
	}

	public MinMaxBounds.Doubles getDistance() {
		return this.distance;
	}

	public void setDistance(MinMaxBounds.Doubles pDistance) {
		this.distance = pDistance;
	}

	public void setMass(MinMaxBounds.Doubles mass) {
		this.mass = mass;
	}
	
	public void setSize(MinMaxBounds.Doubles size) {
		this.size = size;
	}
	
	public void setStatic(OptionalBoolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public MinMaxBounds.Doubles getMass() {
		return mass;
	}
	
	public MinMaxBounds.Doubles getSize() {
		return size;
	}
	
	public OptionalBoolean getStatic() {
		return isStatic;
	}
	
	public WrappedMinMaxBounds getRotX() {
		return this.rotX;
	}

	public void setRotX(WrappedMinMaxBounds pRotX) {
		this.rotX = pRotX;
	}

	public WrappedMinMaxBounds getRotY() {
		return this.rotY;
	}

	public void setRotY(WrappedMinMaxBounds pRotY) {
		this.rotY = pRotY;
	}

	public WrappedMinMaxBounds getRotZ() {
		return this.rotZ;
	}

	public void setRotZ(WrappedMinMaxBounds pRotZ) {
		this.rotZ = pRotZ;
	}

	@Nullable
	public Double getX() {
		return this.x;
	}

	@Nullable
	public Double getY() {
		return this.y;
	}

	@Nullable
	public Double getZ() {
		return this.z;
	}

	public void setX(double pX) {
		this.x = pX;
	}

	public void setY(double pY) {
		this.y = pY;
	}

	public void setZ(double pZ) {
		this.z = pZ;
	}

	public void setDeltaX(double pDeltaX) {
		this.deltaX = pDeltaX;
	}

	public void setDeltaY(double pDeltaY) {
		this.deltaY = pDeltaY;
	}

	public void setDeltaZ(double pDeltaZ) {
		this.deltaZ = pDeltaZ;
	}

	@Nullable
	public Double getDeltaX() {
		return this.deltaX;
	}

	@Nullable
	public Double getDeltaY() {
		return this.deltaY;
	}

	@Nullable
	public Double getDeltaZ() {
		return this.deltaZ;
	}

	public void setVelocityX(MinMaxBounds.Doubles velocity_x) {
		this.velocity_x = velocity_x;
	}
	
	public void setVelocityY(MinMaxBounds.Doubles velocity_y) {
		this.velocity_y = velocity_y;
	}
	
	public void setVelocityZ(MinMaxBounds.Doubles velocity_z) {
		this.velocity_z = velocity_z;
	}
	
	public void setVelocity(MinMaxBounds.Doubles velocity) {
		this.velocity = velocity;
	}
	
	public MinMaxBounds.Doubles getVelocityX() {
		return velocity_x;
	}
	
	public MinMaxBounds.Doubles getVelocityY() {
		return velocity_y;
	}
	
	public MinMaxBounds.Doubles getVelocityZ() {
		return velocity_z;
	}
	
	public MinMaxBounds.Doubles getVelocity() {
		return velocity;
	}
	
	public void setOmegaX(MinMaxBounds.Doubles omega_x) {
		this.omega_x = omega_x;
	}
	
	public void setOmegaY(MinMaxBounds.Doubles omega_y) {
		this.omega_y = omega_y;
	}
	
	public void setOmegaZ(MinMaxBounds.Doubles omega_z) {
		this.omega_z = omega_z;
	}
	
	public void setOmega(MinMaxBounds.Doubles omega) {
		this.omega = omega;
	}
	
	public MinMaxBounds.Doubles getOmegaX() {
		return omega_x;
	}
	
	public MinMaxBounds.Doubles getOmegaY() {
		return omega_y;
	}
	
	public MinMaxBounds.Doubles getOmegaZ() {
		return omega_z;
	}
	
	public MinMaxBounds.Doubles getOmega() {
		return omega;
	}
	
	public void setMaxResults(int pMaxResults) {
		this.maxResults = pMaxResults;
	}

	public BiConsumer<Vec3, List<? extends Contraption>> getOrder() {
		return this.order;
	}

	public void setOrder(BiConsumer<Vec3, List<? extends Contraption>> pOrder) {
		this.order = pOrder;
	}

	public boolean isCurrentContraption() {
		return this.currentContraption;
	}

	public boolean isLimited() {
		return this.isLimited;
	}

	public void setLimited(boolean pIsLimited) {
		this.isLimited = pIsLimited;
	}

	public boolean isSorted() {
		return this.isSorted;
	}

	public void setSorted(boolean pIsSorted) {
		this.isSorted = pIsSorted;
	}

	public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> pSuggestionHandler) {
		this.suggestions = pSuggestionHandler;
	}

	public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder pBuilder, Consumer<SuggestionsBuilder> pConsumer) {
		return this.suggestions.apply(pBuilder.createOffset(this.reader.getCursor()), pConsumer);
	}

	public ContraptionSelector getSelector() {
		
		AABB aabb;
		if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
			if (this.distance.getMax() != null) {
				double d0 = this.distance.getMax();
				aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
			} else {
				aabb = null;
			}
		} else {
			aabb = this.createAabb(this.deltaX == null ? 0.0D : this.deltaX, this.deltaY == null ? 0.0D : this.deltaY, this.deltaZ == null ? 0.0D : this.deltaZ);
		}

		Function<Vec3, Vec3> posFunc;
		if (this.x == null && this.y == null && this.z == null) {
			posFunc = (pos) -> {
				return pos;
			};
		} else {
			posFunc = (pos) -> {
				return new Vec3(this.x == null ? pos.x : this.x, this.y == null ? pos.y : this.y, this.z == null ? pos.z : this.z);
			};
		}
		
		return new ContraptionSelector(this.maxResults, this.worldLimited, this.predicate, this.distance, this.mass, this.size, this.isStatic, posFunc, aabb, this.order, this.currentContraption, this.contraptionName, this.contraptionID, this.usesSelectors);
		
	}
	
	private AABB createAabb(double pSizeX, double pSizeY, double pSizeZ) {
		boolean flag = pSizeX < 0.0D;
		boolean flag1 = pSizeY < 0.0D;
		boolean flag2 = pSizeZ < 0.0D;
		double d0 = flag ? pSizeX : 0.0D;
		double d1 = flag1 ? pSizeY : 0.0D;
		double d2 = flag2 ? pSizeZ : 0.0D;
		double d3 = (flag ? 0.0D : pSizeX) + 1.0D;
		double d4 = (flag1 ? 0.0D : pSizeY) + 1.0D;
		double d5 = (flag2 ? 0.0D : pSizeZ) + 1.0D;
		return new AABB(d0, d1, d2, d3, d4, d5);
	}
	
	public void finalizePredicates() {
		if (this.rotX != WrappedMinMaxBounds.ANY) {
			this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, Contraption::getXRot));
		}

		if (this.rotY != WrappedMinMaxBounds.ANY) {
			this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, Contraption::getYRot));
		}

		if (this.rotZ != WrappedMinMaxBounds.ANY) {
			this.predicate = this.predicate.and(this.createRotationPredicate(this.rotZ, Contraption::getZRot));
		}

		if (this.velocity != MinMaxBounds.Doubles.ANY) {
			this.predicate = this.predicate.and(this.createVelocityPredicate(this.velocity, Contraption::getVelocity));
		}

		if (this.velocity_x != MinMaxBounds.Doubles.ANY || this.velocity_y != MinMaxBounds.Doubles.ANY || this.velocity_z != MinMaxBounds.Doubles.ANY) {
			this.predicate = this.predicate.and(this.createVelocityPredicateXYZ(this.velocity_x, this.velocity_y, this.velocity_z, Contraption::getVelocityVec));
		}

		if (this.omega != MinMaxBounds.Doubles.ANY) {
			this.predicate = this.predicate.and(this.createVelocityPredicate(this.omega, Contraption::getOmega));
		}

		if (this.omega_x != MinMaxBounds.Doubles.ANY || this.omega_y != MinMaxBounds.Doubles.ANY || this.omega_z != MinMaxBounds.Doubles.ANY) {
			this.predicate = this.predicate.and(this.createVelocityPredicateXYZ(this.omega_x, this.omega_y, this.omega_z, Contraption::getOmegaVec));
		}
	}
	
	private Predicate<Contraption> createVelocityPredicate(MinMaxBounds.Doubles velocityBounds, ToDoubleFunction<Contraption> velocityFunction) {
		return (contraption) -> {
			return velocityBounds.matches(Math.toDegrees(velocityFunction.applyAsDouble(contraption)));
		};
	}
	
	private Predicate<Contraption> createVelocityPredicateXYZ(MinMaxBounds.Doubles velocityBoundsX, MinMaxBounds.Doubles velocityBoundsY, MinMaxBounds.Doubles velocityBoundsZ, Function<Contraption, Vec3d> velocityFunction) {
		return (contraption) -> {
			Vec3d velocity = velocityFunction.apply(contraption);
			return	velocityBoundsX.matches(velocity.x * MathUtility.ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND) &&
					velocityBoundsY.matches(velocity.y * MathUtility.ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND) &&
					velocityBoundsZ.matches(velocity.z * MathUtility.ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND);
		};
	}
	
	private Predicate<Contraption> createRotationPredicate(WrappedMinMaxBounds pAngleBounds, ToDoubleFunction<Contraption> pAngleFunction) {
		double d0 = (double)Mth.wrapDegrees(pAngleBounds.getMin() == null ? 0.0F : pAngleBounds.getMin());
		double d1 = (double)Mth.wrapDegrees(pAngleBounds.getMax() == null ? 359.0F : pAngleBounds.getMax());
		return (contraption) -> {
			double d2 = Mth.wrapDegrees(pAngleFunction.applyAsDouble(contraption));
			if (d0 > d1) {
				return d2 >= d0 || d2 <= d1;
			} else {
				return d2 >= d0 && d2 <= d1;
			}
		};
	}
		
	public ContraptionSelector parse() throws CommandSyntaxException {
		this.startPosition = this.reader.getCursor();
		this.suggestions = this::suggestNameOrSelector;
		if (this.reader.canRead() && this.reader.peek() == '@') {
			if (!this.allowSelectors) {
				throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
			}

			this.reader.skip();
			this.parseSelector();
		} else {
			this.parseNameOrID();
		}

		this.finalizePredicates();
		return this.getSelector();
	}

	protected void parseNameOrID() throws CommandSyntaxException {
		if (this.reader.canRead()) {
			this.suggestions = this::suggestName;
		}

		int i = this.reader.getCursor();
		String s = this.reader.readString();
		if (s.isEmpty()) s = this.reader.readStringUntil('}');
		
		this.contraptionID = Contraption.parseIdString(s);
		if (this.contraptionID.isEmpty()) {
			if (s.isEmpty()) {
				this.reader.setCursor(i);
				throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
			}

			this.contraptionName = s;
		} else {
			this.maxResults = 1;
		}
	}

	protected void parseSelector() throws CommandSyntaxException {
		
		this.usesSelectors = true;
		this.suggestions = this::suggestSelector;
		if (!this.reader.canRead()) {
			throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
		} else {
			int i = this.reader.getCursor();
			char c0 = this.reader.read();
			
			if (c0 == SELECTOR_ALL) {
					this.maxResults = Integer.MAX_VALUE;
					this.order = ContraptionSelector.ORDER_ARBITRARY;
			} else if (c0 == SELECTOR_NEAREST) {
					this.maxResults = 1;
					this.order = ORDER_NEAREST;
			} else if (c0 == SELECTOR_RANDOM) {
					this.maxResults = 1;
					this.order = ORDER_RANDOM;
			} else if (c0 == SELECTOR_SELF) {
					this.maxResults = 1;
					this.currentContraption = true;
			} else {
					this.reader.setCursor(i);
					throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + String.valueOf(c0));
			}
			
			this.suggestions = this::suggestOpenOptions;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.reader.skip();
				this.suggestions = this::suggestOptionsKeyOrClose;
				this.parseOptions();
			}
		}
		
	}
	
	public void parseOptions() throws CommandSyntaxException {
		this.suggestions = this::suggestOptionsKey;
		this.reader.skipWhitespace();
	
		while(true) {
			if (this.reader.canRead() && this.reader.peek() != ']') {
				this.reader.skipWhitespace();
				int i = this.reader.getCursor();
				String s = this.reader.readString();
				ContraptionSelectorOptions.Modifier entityselectoroptions$modifier = ContraptionSelectorOptions.get(this, s, i);
				this.reader.skipWhitespace();
				if (!this.reader.canRead() || this.reader.peek() != '=') {
					this.reader.setCursor(i);
					throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, s);
				}
	
				this.reader.skip();
				this.reader.skipWhitespace();
				this.suggestions = SUGGEST_NOTHING;
				entityselectoroptions$modifier.handle(this);
				this.reader.skipWhitespace();
				this.suggestions = this::suggestOptionsNextOrClose;
				if (!this.reader.canRead()) {
					continue;
				}
	
				if (this.reader.peek() == ',') {
					this.reader.skip();
					this.suggestions = this::suggestOptionsKey;
					continue;
				}
	
				if (this.reader.peek() != ']') {
					throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
				}
			}
	
			if (this.reader.canRead()) {
				this.reader.skip();
				this.suggestions = SUGGEST_NOTHING;
				return;
			}
	
			throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
		}
	}
	
	private static void fillSelectorSuggestions(SuggestionsBuilder builder) {
		builder.suggest("@" + SELECTOR_ALL, Component.translatable("industriacore.argument.contraption.selector.all"));
		builder.suggest("@" + SELECTOR_NEAREST, Component.translatable("industriacore.argument.contraption.selector.nearest"));
		builder.suggest("@" + SELECTOR_RANDOM, Component.translatable("industriacore.argument.contraption.selector.random"));
		builder.suggest("@" + SELECTOR_SELF, Component.translatable("industriacore.argument.contraption.selector.self"));
	}
	
	private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionbuilder = builder.createOffset(builder.getStart() - 1);
		fillSelectorSuggestions(suggestionbuilder);
		builder.add(suggestionbuilder);
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
		consumer.accept(builder);
		if (this.allowSelectors) {
			fillSelectorSuggestions(builder);
		}
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsbuilder = builder.createOffset(this.startPosition);
		consumer.accept(suggestionsbuilder);
		return builder.add(suggestionsbuilder).buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
		builder.suggest(String.valueOf('['));
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
		builder.suggest(String.valueOf(']'));
		ContraptionSelectorOptions.suggestNames(this, builder);
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
	  ContraptionSelectorOptions.suggestNames(this, builder);
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
		builder.suggest(String.valueOf(','));
		builder.suggest(String.valueOf(']'));
		return builder.buildFuture();
	}

}
