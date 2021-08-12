package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.recipetypes.WashingRecipe;
import de.industria.typeregistys.ModFluids;
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
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class RecipeCategoryWashingPlant implements IRecipeCategory<WashingRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/ore_washing_plant.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;

	protected final IGuiHelper helper;
	
	public RecipeCategoryWashingPlant(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 0, 166, 125, 38);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 0, 16, 16);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.LEFT, false);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.WASHING_PLANT;
	}

	@Override
	public Class<? extends WashingRecipe> getRecipeClass() {
		return WashingRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.washingPlant").getString();
	}
	
	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 5, 12, 166, 62);
	}
	
	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.ore_washing_plant));
	}
	
	@Override
	public void draw(WashingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		this.progressArrow.draw(matrixStack, 21, 3);
		this.powerSymbol.draw(matrixStack, 20, 43);
	}
	
	@Override
	public void setIngredients(WashingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getItemIn().getItems()));
		ingredients.setInputs(VanillaTypes.FLUID, UtilHelper.toCollection(new FluidStack(Fluids.WATER, 500)));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(recipe.getResultItem(), recipe.getResultItem2(), recipe.getResultItem3()));
		ingredients.setOutputs(VanillaTypes.FLUID, UtilHelper.toCollection(new FluidStack(ModFluids.CHEMICAL_WATER, 500)));
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, WashingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		fluidStacks.init(0, true, 3, 3, 16, 56, 3000, true, null);
		fluidStacks.init(1, false, 147, 3, 16, 56, 3000, true, null);
		itemStacks.init(0, true, 28, 19);
		itemStacks.init(1, true, 79, 42);
		itemStacks.init(2, true, 97, 42);
		itemStacks.init(3, true, 115, 42);
		fluidStacks.set(0, new FluidStack(Fluids.WATER, 500));
		fluidStacks.set(1, new FluidStack(ModFluids.CHEMICAL_WATER, 500));
		itemStacks.set(0, UtilHelper.toCollection(recipe.getItemIn().getItems()));
		itemStacks.set(1, recipe.getResultItem());
		if (!recipe.getResultItem2().isEmpty()) itemStacks.set(2, recipe.getResultItem2());
		if (!recipe.getResultItem3().isEmpty()) itemStacks.set(3, recipe.getResultItem3());
		
		this.progressArrow = helper.createAnimatedDrawable(arrow, recipe.getWashingTime(), StartDirection.LEFT, false);
	}
	
}
