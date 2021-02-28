package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.recipetypes.AlloyRecipe;
import de.redtec.recipetypes.BlendingRecipe;
import de.redtec.recipetypes.RifiningRecipe;
import de.redtec.recipetypes.SchredderRecipe;
import de.redtec.recipetypes.ThermalZentrifugeRecipe;
import de.redtec.recipetypes.WashingRecipe;
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
	
	static <T extends IRecipe<?>> IRecipeType<T> register(String key) {
		ResourceLocation registryKey = new ResourceLocation(RedTec.MODID, key);
		IRecipeType<T> recipeType = Registry.register(Registry.RECIPE_TYPE, registryKey, new IRecipeType<T>() {
			@Override
			public String toString() {
				return registryKey.toString();
			}
		});
		return recipeType;
	}
	
}
