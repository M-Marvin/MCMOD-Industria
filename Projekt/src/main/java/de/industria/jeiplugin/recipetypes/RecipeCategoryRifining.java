package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.RifiningRecipe;
import de.industria.typeregistys.ModItems;
import de.industria.util.handler.UtilHelper;
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

public class RecipeCategoryRifining implements IRecipeCategory<RifiningRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/raffinery.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;

	protected final IGuiHelper helper;
	
	public RecipeCategoryRifining(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 0, 180, 124, 22);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 0, 16, 16);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.LEFT, false);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.RIFINING;
	}

	@Override
	public Class<? extends RifiningRecipe> getRecipeClass() {
		return RifiningRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.raffinery").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 5, 12, 166, 62);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.raffinery));
	}
	
	@Override
	public void draw(RifiningRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		this.progressArrow.draw(matrixStack, 22, 19);
		this.powerSymbol.draw(matrixStack, 20, 43);
	}
	
	@Override
	public void setIngredients(RifiningRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.FLUID, UtilHelper.toCollection(recipe.getFluidIn()));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem()));
		ingredients.setOutputs(VanillaTypes.FLUID, UtilHelper.toCollection(recipe.getFluidOut()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RifiningRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		itemStacks.init(0, true, 38, 42);
		itemStacks.init(1, true, 74, 42);
		itemStacks.init(2, true, 110, 42);
		fluidStacks.init(0, true, 3, 3, 16, 56, 3000, true, null);
		fluidStacks.init(1, true, 147, 3, 16, 56, 3000, true, null);
		itemStacks.set(0, recipe.getItemsOut()[0]);
		if (!recipe.getItemsOut()[1].isEmpty()) itemStacks.set(1, recipe.getItemsOut()[1]);
		if (!recipe.getItemsOut()[2].isEmpty()) itemStacks.set(2, recipe.getItemsOut()[2]);
		fluidStacks.set(0, recipe.getFluidIn());
		fluidStacks.set(1, recipe.getFluidOut());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getRifiningTime(), StartDirection.LEFT, false);
	}
	
}
