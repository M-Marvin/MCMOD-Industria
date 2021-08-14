package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.ThermalZentrifugeRecipe;
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

public class RecipeCategoryThermalZentrifuge implements IRecipeCategory<ThermalZentrifugeRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/thermal_zentrifuge.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;
	
	protected final IGuiHelper helper;
	
	public RecipeCategoryThermalZentrifuge(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 178, 0, 49, 59);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.RIGHT, true);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 65, 16, 16);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.THERMAL_ZENTRIFUGE;
	}
	
	@Override
	public Class<? extends ThermalZentrifugeRecipe> getRecipeClass() {
		return ThermalZentrifugeRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.thermalZentrifuge").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 41, 12, 94, 68);
	}
	
	@Override
	public void draw(ThermalZentrifugeRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		progressArrow.draw(matrixStack, 23, 1);
		powerSymbol.draw(matrixStack, 3, 41);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.thermal_zentrifuge));
	}
	
	@Override
	public void setIngredients(ThermalZentrifugeRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getIngredient()));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem(), recipe.getResultItem2(), recipe.getResultItem3()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ThermalZentrifugeRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 2, 21);
		stacks.init(1, false, 74, 2);
		stacks.init(2, false, 74, 22);
		stacks.init(3, false, 74, 41);
		stacks.set(0, recipe.getIngredient());
		stacks.set(1, recipe.getResultItem());
		if (!recipe.getResultItem2().isEmpty()) stacks.set(2, recipe.getResultItem2());
		if (!recipe.getResultItem3().isEmpty()) stacks.set(3, recipe.getResultItem3());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getRifiningTime(), StartDirection.LEFT, false);
	}
	
}