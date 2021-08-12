package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.BlastFurnaceRecipe;
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

public class RecipeCategoryBlastFurnace implements IRecipeCategory<BlastFurnaceRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/blast_furnace.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;

	protected final IGuiHelper helper;
	
	public RecipeCategoryBlastFurnace(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 176, 26, 21, 5);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 0, 7, 13);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.LEFT, false);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.BLAST_FURNACE;
	}

	@Override
	public Class<? extends BlastFurnaceRecipe> getRecipeClass() {
		return BlastFurnaceRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.blastFurnace").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 5, 12, 166, 66);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.blast_furnace));
	}
	
	@Override
	public void draw(BlastFurnaceRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		this.progressArrow.draw(matrixStack, 92, 50);
		this.powerSymbol.draw(matrixStack, 140, 47);
	}
	
	@Override
	public void setIngredients(BlastFurnaceRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getItemsIn()));
		ingredients.setInputs(VanillaTypes.FLUID, UtilHelper.toCollection(recipe.getConsumtionFluid()));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem(), recipe.getWasteOut()));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BlastFurnaceRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		itemStacks.init(0, true, 57, 2);
		itemStacks.init(1, true, 57, 22);
		itemStacks.init(2, true, 57, 42);
		itemStacks.init(3, false, 134, 2);
		itemStacks.init(4, false, 134, 22);
		fluidStacks.init(0, true, 18, 3, 16, 56, 3000, true, null);
		if (recipe.getItemsIn().length >= 1) itemStacks.set(0, recipe.getItemsIn()[0]);
		if (recipe.getItemsIn().length >= 2) itemStacks.set(1, recipe.getItemsIn()[1]);
		if (recipe.getItemsIn().length >= 3) itemStacks.set(2, recipe.getItemsIn()[2]);
		fluidStacks.set(0, recipe.getConsumtionFluid());
		itemStacks.set(3, recipe.getResultItem());
		itemStacks.set(4, recipe.getWasteOut());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getSmeltingTime(), StartDirection.LEFT, false);
	}
	
}
