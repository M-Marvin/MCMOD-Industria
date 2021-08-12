package de.industria.jeiplugin;

import java.util.Collection;
import java.util.stream.Collectors;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.jeiplugin.recipetypes.RecipeCategoryAirCompressor;
import de.industria.jeiplugin.recipetypes.RecipeCategoryAlloy;
import de.industria.jeiplugin.recipetypes.RecipeCategoryBlastFurnace;
import de.industria.jeiplugin.recipetypes.RecipeCategoryBlender;
import de.industria.jeiplugin.recipetypes.RecipeCategoryFluidBath;
import de.industria.jeiplugin.recipetypes.RecipeCategoryMetalFormer;
import de.industria.jeiplugin.recipetypes.RecipeCategoryRifining;
import de.industria.jeiplugin.recipetypes.RecipeCategorySchredder;
import de.industria.jeiplugin.recipetypes.RecipeCategoryThermalZentrifuge;
import de.industria.jeiplugin.recipetypes.RecipeCategoryWashingPlant;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.util.handler.UtilHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	
	private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Industria.MODID, "jei_plugin");
	
	public IGuiHelper guiHelper;
	
	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_ID;
	}

	@SuppressWarnings("resource")
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
		
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.ALLOY), JEIRecipeCategories.ALLOY_FURNACE);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.THERMAL_ZENTRIFUGE), JEIRecipeCategories.THERMAL_ZENTRIFUGE);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.SCHREDDER), JEIRecipeCategories.SCHREDDER);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.BLENDING), JEIRecipeCategories.BLENDER);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.RIFINING), JEIRecipeCategories.RIFINING);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.FLUID_BATH), JEIRecipeCategories.FLUID_BATH);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.WASHING), JEIRecipeCategories.WASHING_PLANT);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.METAL_FORM), JEIRecipeCategories.METAL_FORMER);
		registration.addRecipes(getRecipes(manager, ModRecipeTypes.BLAST_FURNACE), JEIRecipeCategories.BLAST_FURNACE);
		registration.addRecipes(UtilHelper.toCollection(new FluidStack(ModFluids.COMPRESSED_AIR, 1500)), JEIRecipeCategories.AIR_COMPRESSOR);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		final IJeiHelpers helpers = registration.getJeiHelpers();
		final IGuiHelper guiHelper = helpers.getGuiHelper();
		
		registration.addRecipeCategories(new RecipeCategoryAlloy(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryThermalZentrifuge(guiHelper));
		registration.addRecipeCategories(new RecipeCategorySchredder(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryBlender(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryRifining(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryFluidBath(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryWashingPlant(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryMetalFormer(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryAirCompressor(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryBlastFurnace(guiHelper));
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModItems.alloy_furnace), JEIRecipeCategories.ALLOY_FURNACE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.thermal_zentrifuge), JEIRecipeCategories.THERMAL_ZENTRIFUGE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.schredder), JEIRecipeCategories.SCHREDDER);
		registration.addRecipeCatalyst(new ItemStack(ModItems.blender), JEIRecipeCategories.BLENDER);
		registration.addRecipeCatalyst(new ItemStack(ModItems.raffinery), JEIRecipeCategories.RIFINING);
		registration.addRecipeCatalyst(new ItemStack(ModItems.fluid_bath), JEIRecipeCategories.FLUID_BATH);
		registration.addRecipeCatalyst(new ItemStack(ModItems.ore_washing_plant), JEIRecipeCategories.WASHING_PLANT);
		registration.addRecipeCatalyst(new ItemStack(ModItems.metal_former), JEIRecipeCategories.METAL_FORMER);
		registration.addRecipeCatalyst(new ItemStack(ModItems.air_compressor), JEIRecipeCategories.AIR_COMPRESSOR);
		registration.addRecipeCatalyst(new ItemStack(ModItems.blast_furnace), JEIRecipeCategories.BLAST_FURNACE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.electric_heater), JEIRecipeCategories.BLAST_FURNACE);
	}
	
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		//registration.addRecipeTransferHandler(recipeTransferHandler, recipeCategoryUid);
	}
	
	private static Collection<?> getRecipes(RecipeManager manager, IRecipeType<?> type) {
		return manager.getRecipes().parallelStream().filter(recipe -> recipe.getType() == type).collect(Collectors.toList());
	}
	
}
