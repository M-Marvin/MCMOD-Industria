package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.recipetypes.SchredderRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipeTypes {
	
	public static final IRecipeType<SchredderRecipe> SCHREDDER = register("schredder");
	
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
