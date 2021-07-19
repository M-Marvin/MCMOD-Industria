package de.industria.jeiplugin.recipetypes;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.jeiplugin.JEIRecipeCategories;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class RecipeCategoryAirCompressor implements IRecipeCategory<FluidStack> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/air_compressor.png");
	
	protected IDrawableStatic arrow;
	protected IDrawableAnimated progressArrow;
	protected IDrawableStatic powerSymbol;

	protected final IGuiHelper helper;
	
	public RecipeCategoryAirCompressor(IGuiHelper helper) {
		this.helper = helper;
		this.arrow = helper.createDrawable(TEXTURES, 0, 200, 69, 16);
		this.powerSymbol = helper.createDrawable(TEXTURES, 176, 0, 16, 15);
		this.progressArrow = helper.createAnimatedDrawable(arrow, 100, StartDirection.LEFT, false);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.AIR_COMPRESSOR;
	}

	@Override
	public Class<? extends FluidStack> getRecipeClass() {
		return FluidStack.class;
	}

	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.airCompressor").getString();
	}

	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 42, 12, 93, 62);
	}

	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.air_compressor));
	}
	
	@Override
	public void draw(FluidStack recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		this.progressArrow.draw(matrixStack, 3, 22);
		this.powerSymbol.draw(matrixStack, 32, 44);
	}
	
	@Override
	public void setIngredients(FluidStack recipe, IIngredients ingredients) {
		ingredients.setOutput(VanillaTypes.FLUID, recipe);
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluidStack recipe, IIngredients ingredients) {
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		
		fluidStacks.init(0, false, 74, 3, 16, 56, 3000, true, null);
		fluidStacks.set(0, recipe);
	}

}
