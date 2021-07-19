package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.FluidBathRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class RecipeCategoryFluidBath implements IRecipeCategory<FluidBathRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/fluid_bath.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableStatic fluidBar;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;

	protected final IGuiHelper helper;
	
	public RecipeCategoryFluidBath(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 176, 16, 71, 16);
		this.fluidBar = helper.createDrawable(TEXTURES, 176, 32, 52, 5);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 0, 16, 16);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.LEFT, false);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.FLUID_BATH;
	}

	@Override
	public Class<? extends FluidBathRecipe> getRecipeClass() {
		return FluidBathRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.fluidBath").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 5, 12, 166, 62);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.fluid_bath));
	}
	
	@Override
	public void draw(FluidBathRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		this.progressArrow.draw(matrixStack, 48, 3);
		this.fluidBar.draw(matrixStack, 53, 48);
		this.powerSymbol.draw(matrixStack, 20, 43);
	}
	
	@Override
	public void setIngredients(FluidBathRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.FLUID, recipe.getFluidIn());
		if (!recipe.getItemIn().isEmpty()) ingredients.setInput(VanillaTypes.ITEM, recipe.getItemIn());
		if (!recipe.getFluidOut().isEmpty()) ingredients.setOutput(VanillaTypes.FLUID, recipe.getFluidOut());
		if (!recipe.getResultItem().isEmpty()) ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluidBathRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		itemStacks.init(0, true, 29, 2);
		itemStacks.init(1, false, 119, 2);
		fluidStacks.init(0, true, 3, 3, 16, 56, 3000, true, null);
		fluidStacks.init(1, false, 147, 3, 16, 56, 3000, true, null);
		if (!recipe.getItemIn().isEmpty()) itemStacks.set(0, recipe.getItemIn());
		if (!recipe.getResultItem().isEmpty()) itemStacks.set(1, recipe.getResultItem());
		fluidStacks.set(0, recipe.getFluidIn());
		if (!recipe.getFluidOut().isEmpty()) fluidStacks.set(1, recipe.getFluidOut());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getProcessTime(), StartDirection.LEFT, false);
	}
	
}
