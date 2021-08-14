package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.SchredderRecipe;
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

public class RecipeCategorySchredder implements IRecipeCategory<SchredderRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/schredder.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;
	protected int schredderDammage;
	
	protected final IGuiHelper helper;
	
	public RecipeCategorySchredder(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 176, 0, 48, 19);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.RIGHT, true);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 19, 16, 16);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.SCHREDDER;
	}
	
	@Override
	public Class<? extends SchredderRecipe> getRecipeClass() {
		return SchredderRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.schredder").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 41, 12, 94, 62);
	}
	
	@Override
	public void draw(SchredderRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		progressArrow.draw(matrixStack, 23, 20);
		powerSymbol.draw(matrixStack, 3, 41);
		
		// TOTO draw schredderDammage
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.schredder));
	}
	
	@Override
	public void setIngredients(SchredderRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getIngredient().getItems(), new ItemStack(recipe.getSchredderTool())));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem(), recipe.getResultItem2(), recipe.getResultItem3()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SchredderRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 2, 21);
		stacks.init(1, true, 38, 21);
		stacks.init(2, false, 74, 2);
		stacks.init(3, false, 74, 22);
		stacks.init(4, false, 74, 41);
		stacks.set(0, UtilHelper.toCollection(recipe.getIngredient().getItems()));
		stacks.set(1, new ItemStack(recipe.getSchredderTool()));
		stacks.set(2, recipe.getResultItem());
		if (!recipe.getResultItem2().isEmpty()) stacks.set(3, recipe.getResultItem2());
		if (!recipe.getResultItem3().isEmpty()) stacks.set(4, recipe.getResultItem3());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getSchredderTime(), StartDirection.LEFT, false);
		this.schredderDammage = recipe.getSchredderDamage();
	}
	
}
