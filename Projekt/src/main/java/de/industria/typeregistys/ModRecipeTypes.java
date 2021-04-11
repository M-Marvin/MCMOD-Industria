package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.recipetypes.AlloyRecipe;
import de.industria.recipetypes.BlendingRecipe;
import de.industria.recipetypes.FluidBathRecipe;
import de.industria.recipetypes.RifiningRecipe;
import de.industria.recipetypes.SchredderRecipe;
import de.industria.recipetypes.ThermalZentrifugeRecipe;
import de.industria.recipetypes.WashingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipeTypes {
	
	public static final IRecipeType<SchredderRecipe> SCHREDDER = register("schredder");
	public static final IRecipeType<BlendingRecipe> BLENDING = register("blending");
	public static final IRecipeType<RifiningRecipe> RIFINING = register("rifining");
	public static final IRecipeType<ThermalZentrifugeRecipe> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge");
	public static final IRecipeType<WashingRecipe> WASHING = register("washing");
	public static final IRecipeType<AlloyRecipe> ALLOY = register("alloy");
	public static final IRecipeType<FluidBathRecipe> FLUID_BATH = register("fluid_bath");
	
	static <T extends IRecipe<?>> IRecipeType<T> register(String key) {
		ResourceLocation registryKey = new ResourceLocation(Industria.MODID, key);
		IRecipeType<T> recipeType = Registry.register(Registry.RECIPE_TYPE, registryKey, new IRecipeType<T>() {
			@Override
			public String toString() {
				return registryKey.toString();
			}
		});
		return recipeType;
	}
	
}
