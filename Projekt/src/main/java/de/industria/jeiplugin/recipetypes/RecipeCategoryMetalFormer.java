package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.MetalFormRecipe;
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

public class RecipeCategoryMetalFormer implements IRecipeCategory<MetalFormRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/metal_former.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;
	
	protected final IGuiHelper helper;
	
	public RecipeCategoryMetalFormer(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 176, 0, 24, 17);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.RIGHT, true);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 17, 16, 16);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.METAL_FORMER;
	}
	
	@Override
	public Class<? extends MetalFormRecipe> getRecipeClass() {
		return MetalFormRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.metalFormer").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 53, 28, 86, 42);
	}
	
	@Override
	public void draw(MetalFormRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		progressArrow.draw(matrixStack, 26, 6);
		powerSymbol.draw(matrixStack, 3, 25);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.metal_former));
	}
	
	@Override
	public void setIngredients(MetalFormRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getItemIn()));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MetalFormRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 2, 5);
		stacks.init(1, false, 62, 6);
		stacks.set(0, recipe.getItemIn());
		stacks.set(1, recipe.getResultItem());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getProcessTime(), StartDirection.LEFT, false);
	}
	
}
