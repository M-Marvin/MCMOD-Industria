package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.recipetypes.AlloyRecipe;
import de.industria.recipetypes.AlloyRecipeSerializer;
import de.industria.recipetypes.BlastFurnaceRecipe;
import de.industria.recipetypes.BlastFurnaceRecipeSerializer;
import de.industria.recipetypes.BlendingRecipe;
import de.industria.recipetypes.BlendingRecipeSerializer;
import de.industria.recipetypes.FluidBathRecipe;
import de.industria.recipetypes.FluidBathRecipeSerializer;
import de.industria.recipetypes.MetalFormRecipe;
import de.industria.recipetypes.MetalFormRecipeSerializer;
import de.industria.recipetypes.RifiningRecipe;
import de.industria.recipetypes.RifiningRecipeSerializer;
import de.industria.recipetypes.SchredderRecipe;
import de.industria.recipetypes.SchredderRecipeSerializer;
import de.industria.recipetypes.ThermalZentrifugeRecipe;
import de.industria.recipetypes.ThermalZentrifugeRecipeSerializer;
import de.industria.recipetypes.WashingRecipe;
import de.industria.recipetypes.WashingRecipeSerializer;
import de.industria.specialrecipes.ProzessorCopy;
import de.industria.specialrecipes.RecipeDyeBrush;
import de.industria.specialrecipes.RecipeFormCladding;
import de.industria.specialrecipes.SpecialMetalFormRecipeSerializer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModSerializer {
	
	public static final SpecialMetalFormRecipeSerializer<RecipeFormCladding> FORM_CLADDING = register("form_special_cladding", new SpecialMetalFormRecipeSerializer<RecipeFormCladding>(RecipeFormCladding::new));
	public static final SpecialRecipeSerializer<RecipeDyeBrush> DYE_BRUSH = register("crafting_special_dye_brush", new SpecialRecipeSerializer<>(RecipeDyeBrush::new));
	public static final SpecialRecipeSerializer<ProzessorCopy> CRAFTING_PROCESSOR_COPY = register("crafting_special_processor_cloning", new SpecialRecipeSerializer<>(ProzessorCopy::new));
	public static final SchredderRecipeSerializer<SchredderRecipe> SCHREDDER = register("schredder", new SchredderRecipeSerializer<SchredderRecipe>(SchredderRecipe::new));
	public static final BlendingRecipeSerializer<BlendingRecipe> BLENDING = register("blending", new BlendingRecipeSerializer<BlendingRecipe>(BlendingRecipe::new));
	public static final RifiningRecipeSerializer<RifiningRecipe> RIFINING = register("rifining", new RifiningRecipeSerializer<RifiningRecipe>(RifiningRecipe::new));
	public static final ThermalZentrifugeRecipeSerializer<ThermalZentrifugeRecipe> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", new ThermalZentrifugeRecipeSerializer<ThermalZentrifugeRecipe>(ThermalZentrifugeRecipe::new));
	public static final WashingRecipeSerializer<WashingRecipe> WASHING = register("washing", new WashingRecipeSerializer<WashingRecipe>(WashingRecipe::new));
	public static final AlloyRecipeSerializer<AlloyRecipe> ALLOY = register("alloy", new AlloyRecipeSerializer<AlloyRecipe>(AlloyRecipe::new));
	public static final FluidBathRecipeSerializer<FluidBathRecipe> FLUID_BATH = register("fluid_bath", new FluidBathRecipeSerializer<FluidBathRecipe>(FluidBathRecipe::new));
	public static final BlastFurnaceRecipeSerializer<BlastFurnaceRecipe> BLAST_FURNACE = register("blast_furnace", new BlastFurnaceRecipeSerializer<BlastFurnaceRecipe>(BlastFurnaceRecipe::new));
	public static final MetalFormRecipeSerializer<MetalFormRecipe> METAL_FORM = register("metal_form", new MetalFormRecipeSerializer<MetalFormRecipe>(MetalFormRecipe::new));
	
	static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
		recipeSerializer.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.RECIPE_SERIALIZERS.register(recipeSerializer);
		return recipeSerializer;
	}
	
}