package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.recipes.GeneratorFuelRecipeType;
import de.m_marvin.industria.content.recipes.WireCoilWindingRecipeType;
import de.m_marvin.industria.core.util.recipes.UniversalRecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

	private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Industria.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Industria.MODID);
	public static void register() {
		RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static <T extends Recipe<?>> RecipeType<T> buildType(String name) {
		return new RecipeType<T>() {
			@Override
			public String toString() {
				return name;
			}
		};
	}
	
	public static final RegistryObject<RecipeType<GeneratorFuelRecipeType>> 		GENERATOR_FUEL 					= RECIPE_TYPES.register("generator_fuel", () -> buildType("generator_fuel"));
	public static final RegistryObject<RecipeSerializer<GeneratorFuelRecipeType>> 	GENERATOR_FUEL_SERIALIZER 		= RECIPE_SERIALIZERS.register("generator_fuel", () -> new UniversalRecipeSerializer<>(GeneratorFuelRecipeType::fromNetwork, GeneratorFuelRecipeType::toNetwork, GeneratorFuelRecipeType::fromJson));
	public static final RegistryObject<RecipeSerializer<WireCoilWindingRecipeType>> WIRE_COIL_WINDING_SERIALIZER 	= RECIPE_SERIALIZERS.register("wire_coil_winding", () -> new UniversalRecipeSerializer<>(WireCoilWindingRecipeType::fromNetwork, WireCoilWindingRecipeType::toNetwork, WireCoilWindingRecipeType::fromJson));
	
}
