package de.redtec.specialrecipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Serializer {
	
   public static final SpecialRecipeSerializer<ProzessorCopy> CRAFTING_PROCESSOR_COPY = register("crafting_processor_copy", new SpecialRecipeSerializer<>(ProzessorCopy::new));
   
   @SuppressWarnings("deprecation")
   static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
      return Registry.register(Registry.RECIPE_SERIALIZER, key, recipeSerializer);
   }
   
}