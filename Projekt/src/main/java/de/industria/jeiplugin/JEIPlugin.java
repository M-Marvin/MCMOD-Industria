package de.industria.jeiplugin;

import java.util.Collection;
import java.util.stream.Collectors;

import de.industria.Industria;
import de.industria.jeiplugin.recipetypes.RecipeCategoryAlloy;
import de.industria.jeiplugin.recipetypes.RecipeCategoryThermalZentrifuge;
import de.industria.typeregistys.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.gui.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	
	private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Industria.MODID, "jei_plugin");
	
	public GuiHelper guiHelper;
	
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
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		final IJeiHelpers helpers = registration.getJeiHelpers();
		final IGuiHelper guiHelper = helpers.getGuiHelper();
		
		registration.addRecipeCategories(new RecipeCategoryAlloy(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryThermalZentrifuge(guiHelper));
	}
	
	private static Collection<?> getRecipes(RecipeManager manager, IRecipeType<?> type) {
		return manager.getRecipes().parallelStream().filter(recipe -> recipe.getType() == type).collect(Collectors.toList());
	}
	
}
