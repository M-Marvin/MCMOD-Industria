package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.AlloyRecipe;
import de.industria.typeregistys.ModItems;
import de.industria.util.handler.UtilHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class RecipeCategoryAlloy implements IRecipeCategory<AlloyRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/alloy_furnace.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;
	
	protected final IGuiHelper helper;
	
	public RecipeCategoryAlloy(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 176, 0, 49, 46);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.RIGHT, true);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 46, 16, 16);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.ALLOY_FURNACE;
	}

	@Override
	public Class<? extends AlloyRecipe> getRecipeClass() {
		return AlloyRecipe.class;
	}

	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.alloy").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 41, 12, 94, 62);
	}
	
	@Override
	public void draw(AlloyRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		progressArrow.draw(matrixStack, 23, 7);
		powerSymbol.draw(matrixStack, 76, 41);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.alloy_furnace));
	}
	
	@Override
	public void setIngredients(AlloyRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getItemsIn()));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AlloyRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 2, 2);
		stacks.init(1, true, 2, 22);
		stacks.init(2, true, 2, 42);
		stacks.init(3, false, 74, 21);
		stacks.set(0, recipe.getItemsIn()[0]);
		if (recipe.getItemsIn().length >= 2) stacks.set(1, recipe.getItemsIn()[1]);
		if (recipe.getItemsIn().length >= 3) stacks.set(2, recipe.getItemsIn()[2]);
		stacks.set(3, recipe.getResultItem());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getSmeltingTime(), StartDirection.LEFT, false);
	}

}
