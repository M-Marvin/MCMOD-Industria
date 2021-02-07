package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.recipetypes.BlendingRecipe;
import de.redtec.recipetypes.BlendingRecipeSerializer;
import de.redtec.recipetypes.RifiningRecipe;
import de.redtec.recipetypes.RifiningRecipeSerializer;
import de.redtec.recipetypes.SchredderRecipe;
import de.redtec.recipetypes.SchredderRecipeSerializer;
import de.redtec.recipetypes.ThermalZentrifugeRecipe;
import de.redtec.recipetypes.ThermalZentrifugeRecipeSerializer;
import de.redtec.recipetypes.WashingRecipe;
import de.redtec.recipetypes.WashingRecipeSerialize;
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
	public static final BlendingRecipeSerializer<BlendingRecipe> BLENDING = register("blending", new BlendingRecipeSerializer<BlendingRecipe>(BlendingRecipe::new));
	public static final RifiningRecipeSerializer<RifiningRecipe> RIFINING = register("rifining", new RifiningRecipeSerializer<RifiningRecipe>(RifiningRecipe::new));
	public static final ThermalZentrifugeRecipeSerializer<ThermalZentrifugeRecipe> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", new ThermalZentrifugeRecipeSerializer<ThermalZentrifugeRecipe>(ThermalZentrifugeRecipe::new));
	public static final WashingRecipeSerialize<WashingRecipe> WASHING = register("washing", new WashingRecipeSerialize<WashingRecipe>(WashingRecipe::new));
	
	static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
		recipeSerializer.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.RECIPE_SERIALIZERS.register(recipeSerializer);
		return recipeSerializer;
	}

}