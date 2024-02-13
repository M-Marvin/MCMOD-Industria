package de.m_marvin.industria.core.physics.engine.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class Vec3RelativeArgument implements ArgumentType<Vec3Relative> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.pos3d.incomplete"));
   public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType(Component.translatable("argument.pos.mixed"));
   private final boolean centerCorrect;
   private final boolean suggestAbsoluteCoordinates;
   
   public Vec3RelativeArgument(boolean suggestAbsoluteCoordinates, boolean pCenterCorrect) {
	   this.suggestAbsoluteCoordinates = suggestAbsoluteCoordinates;
      this.centerCorrect = pCenterCorrect;
   }

   public static Vec3RelativeArgument vec3() {
      return new Vec3RelativeArgument(false, false);
   }

   public static Vec3RelativeArgument vec3position() {
      return new Vec3RelativeArgument(true, true);
   }

   public static Vec3RelativeArgument vec3position(boolean pCenterCorrect) {
      return new Vec3RelativeArgument(true, pCenterCorrect);
   }

   public static Vec3Relative getVec3Relative(CommandContext<CommandSourceStack> pContext, String pName) {
      return pContext.getArgument(pName, Vec3Relative.class);
   }

   public Vec3Relative parse(StringReader p_120843_) throws CommandSyntaxException {
      return (Vec3Relative)(p_120843_.canRead() && p_120843_.peek() == '^' ? LocalVec3Relative.parse(p_120843_) : WorldVec3Relative.parseDouble(p_120843_, this.centerCorrect));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
      if (!(pContext.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String s = pBuilder.getRemaining();
         Collection<SharedSuggestionProvider.TextCoordinates> collection;
         if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else if (suggestAbsoluteCoordinates) {
            collection = ((SharedSuggestionProvider)pContext.getSource()).getAbsoluteCoordinates();
         } else {
        	collection = Collections.emptyList();
         }

         return SharedSuggestionProvider.suggestCoordinates(s, collection, pBuilder, Commands.createValidator(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}