package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.BlendingRecipe;
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

public class RecipeCategoryBlender implements IRecipeCategory<BlendingRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/blender.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableStatic arrow2;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;

	protected final IGuiHelper helper;
	
	public RecipeCategoryBlender(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 176, 16, 16, 16);
		this.arrow2 = helper.createDrawable(TEXTURES, 176, 32, 16, 19);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 0, 16, 16);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.LEFT, false);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.BLENDER;
	}

	@Override
	public Class<? extends BlendingRecipe> getRecipeClass() {
		return BlendingRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.blending").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 5, 12, 166, 62);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.blender));
	}
	
	@Override
	public void draw(BlendingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		this.progressArrow.draw(matrixStack, 128, 22);
		this.arrow2.draw(matrixStack, 58, 22);
		this.powerSymbol.draw(matrixStack, 58, 43);
	}
	
	@Override
	public void setIngredients(BlendingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getItemsIn()));
		ingredients.setInputs(VanillaTypes.FLUID, UtilHelper.toCollection(recipe.getFluidsIn()));
		ingredients.setOutputs(VanillaTypes.FLUID, UtilHelper.toCollection(recipe.getFluidOut()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BlendingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		itemStacks.init(0, true, 40, 2);
		itemStacks.init(1, true, 40, 22);
		itemStacks.init(2, true, 40, 42);
		fluidStacks.init(0, true, 3, 3, 16, 56, 3000, true, null);
		fluidStacks.init(1, true, 22, 3, 16, 56, 3000, true, null);
		fluidStacks.init(2, false, 147, 3, 16, 56, 3000, true, null);
		if (recipe.getItemsIn().length >= 1) itemStacks.set(0, recipe.getItemsIn()[0]);
		if (recipe.getItemsIn().length >= 2) itemStacks.set(1, recipe.getItemsIn()[1]);
		if (recipe.getItemsIn().length >= 3) itemStacks.set(2, recipe.getItemsIn()[2]);
		if (recipe.getFluidsIn().length >= 1) fluidStacks.set(0, recipe.getFluidsIn()[0]);
		if (recipe.getFluidsIn().length >= 2) fluidStacks.set(1, recipe.getFluidsIn()[1]);
		fluidStacks.set(2, recipe.getFluidOut());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getMixingTime(), StartDirection.LEFT, false);
	}
	
}
