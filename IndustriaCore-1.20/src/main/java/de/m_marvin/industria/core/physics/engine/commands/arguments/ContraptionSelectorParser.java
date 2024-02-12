package de.m_marvin.industria.core.physics.engine.commands.arguments;

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
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

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
	private static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((p_121301_) -> {
		return Component.translatable("industriacore.argument.contraption.selector.unknown", p_121301_);
	});
	private static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.selector.not_allowed"));
	private static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.selector.missing"));
	private static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(Component.translatable("industriacore.argument.contraption.options.unterminated"));
	private static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((p_121267_) -> {
		return Component.translatable("industriacore.argument.contraption.options.valueless", p_121267_);
	});
	
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_121363_, p_121364_) -> {
		return p_121363_.buildFuture();
	};
	
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_NEAREST = (p_121313_, p_121314_) -> {
		p_121314_.sort((p_175140_, p_175141_) -> {
			return Doubles.compare(p_175140_.distanceToSqr(p_121313_), p_175141_.distanceToSqr(p_121313_));
		});
	};
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_FURTHEST = (p_121298_, p_121299_) -> {
		p_121299_.sort((p_175131_, p_175132_) -> {
			return Doubles.compare(p_175132_.distanceToSqr(p_121298_), p_175131_.distanceToSqr(p_121298_));
		});
	};
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_RANDOM = (p_121264_, p_121265_) -> {
		Collections.shuffle(p_121265_);
	};
			
	private final StringReader reader;
	private final boolean allowSelectors;
	
	private int maxResults;
	private boolean worldLimited;
	private MinMaxBounds.Doubles distance = MinMaxBounds.Doubles.ANY;
	private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
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
	private Predicate<Contraption> predicate = (p_121321_) -> {
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

	public MinMaxBounds.Ints getLevel() {
		return this.level;
	}

	public void setLevel(MinMaxBounds.Ints pLevel) {
		this.level = pLevel;
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

		Function<Vec3, Vec3> function;
		if (this.x == null && this.y == null && this.z == null) {
			function = (p_121292_) -> {
				return p_121292_;
			};
		} else {
			function = (p_121258_) -> {
				return new Vec3(this.x == null ? p_121258_.x : this.x, this.y == null ? p_121258_.y : this.y, this.z == null ? p_121258_.z : this.z);
			};
		}

		return new ContraptionSelector(this.maxResults, this.worldLimited, this.predicate, this.distance, function, aabb, this.order, this.currentContraption, this.contraptionName, this.contraptionID, this.usesSelectors);
		
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

	}
		
	private Predicate<Contraption> createRotationPredicate(WrappedMinMaxBounds pAngleBounds, ToDoubleFunction<Contraption> pAngleFunction) {
		double d0 = (double)Mth.wrapDegrees(pAngleBounds.getMin() == null ? 0.0F : pAngleBounds.getMin());
		double d1 = (double)Mth.wrapDegrees(pAngleBounds.getMax() == null ? 359.0F : pAngleBounds.getMax());
		return (p_175137_) -> {
			double d2 = Mth.wrapDegrees(pAngleFunction.applyAsDouble(p_175137_));
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
			this.parseNameOrUUID();
		}

		this.finalizePredicates();
		return this.getSelector();
	}

	protected void parseNameOrUUID() throws CommandSyntaxException {
		if (this.reader.canRead()) {
			this.suggestions = this::suggestName;
		}

		int i = this.reader.getCursor();
		String s = this.reader.readString();
		

		this.contraptionID = Contraption.parseIdString(s);
		if (this.contraptionID.isEmpty()) {
			if (s.isEmpty() || s.length() > 16) {
				this.reader.setCursor(i);
				throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
			}

			this.contraptionName = s;
		}
		
		this.maxResults = 1;
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

	private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder p_121287_, Consumer<SuggestionsBuilder> p_121288_) {
		p_121288_.accept(p_121287_);
		if (this.allowSelectors) {
			fillSelectorSuggestions(p_121287_);
		}

		return p_121287_.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder p_121310_, Consumer<SuggestionsBuilder> p_121311_) {
		SuggestionsBuilder suggestionsbuilder = p_121310_.createOffset(this.startPosition);
		p_121311_.accept(suggestionsbuilder);
		return p_121310_.add(suggestionsbuilder).buildFuture();
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
