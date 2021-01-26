package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.recipetypes.SchredderRecipe;
import de.redtec.recipetypes.SchredderRecipeSerializer;
import de.redtec.specialrecipes.ProzessorCopy;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModSerializer {
	
	public static final SpecialRecipeSerializer<ProzessorCopy> CRAFTING_PROCESSOR_COPY = register("crafting_processor_copy", new SpecialRecipeSerializer<>(ProzessorCopy::new));
	public static final SchredderRecipeSerializer<SchredderRecipe> SCHREDDER = register("schredder", new SchredderRecipeSerializer<SchredderRecipe>(SchredderRecipe::new));
	
	static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
		recipeSerializer.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.RECIPE_SERIALIZERS.register(recipeSerializer);
		return recipeSerializer;
	}

}