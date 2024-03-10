package de.industria.jeiplugin.recipetypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.jeiplugin.JEIRecipeCategories;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.MultipartBuildRecipes.MultipartBuildRecipe;
import de.industria.util.handler.UtilHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.data.IModelData;

public class RecipeCategoryMultipartBuild implements IRecipeCategory<MultipartBuildRecipe> {
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/multipart_build_recipe.png");

	protected final IGuiHelper helper;
	protected final BlockRendererDispatcher blockRenderer;
	
	public RecipeCategoryMultipartBuild(IGuiHelper helper, BlockRendererDispatcher blockRenderer) {
		this.helper = helper;
		this.blockRenderer = blockRenderer;
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIRecipeCategories.MULTIPART_BUILD;
	}

	@Override
	public Class<? extends MultipartBuildRecipe> getRecipeClass() {
		return MultipartBuildRecipe.class;
	}

	@Override
	public String getTitle() {
		return new TranslationTextComponent("jei.recipe.title.multipartBuild").getString();
	}

	@Override
	public IDrawable getBackground() {
		return helper.createDrawable(TEXTURES, 0, 0, 166, 80);
	}

	@Override
	public IDrawable getIcon() {
		return helper.createDrawableIngredient(new ItemStack(ModItems.wrench));
	}
	
	protected int timer;
	protected int tickCounter;
	
	@SuppressWarnings({ "resource" })
	@Override
	public void draw(MultipartBuildRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		
		this.timer = (this.timer + 1) % 120;
		if (this.timer == 0) tickCounter = (tickCounter + 1) % 1000;
		
		matrixStack.pushPose();
		
		List<String[]> pattern = recipe.getStructureBuilder().getPattern();
		HashMap<String, Block[]> keys = recipe.getStructureBuilder().getKeys();
		
		float offsetY = (recipe.getStructureBuilder().getSizeY() / 2F - 1) * 12;
		float offsetX = (recipe.getStructureBuilder().getSizeX() / 2F - 1) * 12;
		float offsetZ = (recipe.getStructureBuilder().getSizeZ() / 2F - 1) * 12;

		//matrixStack.translate(-180, -0, 0);
		
		matrixStack.translate(150 + offsetX, 45 + offsetY, 20 + offsetZ);
		matrixStack.scale(12, 12, -12);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(210));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(225));
		
		IRenderTypeBuffer bufferIn = Minecraft.getInstance().renderBuffers().bufferSource();
		
		for (int layerY = 0; layerY < (tickCounter % pattern.size() + 1); layerY++) {
			String[] layer = pattern.get(layerY);
			for (int rowZ = layer.length - 1; rowZ >= 0; rowZ--) {
				String row = layer[rowZ];
				for (int rowX = row.length() - 1; rowX >= 0 ; rowX--) {
					char key = row.charAt((row.length() - 1) - rowX);
					
					Block[] possibleItems = keys.get("" + key);
					
					Block block = possibleItems[tickCounter % possibleItems.length]; // TODO
					BlockPos blueprintPos = new BlockPos(rowX, layerY, rowZ);
					
					matrixStack.pushPose();
					matrixStack.translate(blueprintPos.getX(), blueprintPos.getY(), blueprintPos.getZ());
					
					if (!block.isAir(block.defaultBlockState(), Minecraft.getInstance().level, BlockPos.ZERO)) {
						BlockState renderState = block.defaultBlockState();
						IBakedModel model = blockRenderer.getBlockModel(renderState);
						IModelData modelData = model.getModelData(Minecraft.getInstance().level, blueprintPos, renderState, null);
						
						for (RenderType type : RenderType.chunkBufferLayers()) {
							blockRenderer.getModelRenderer().renderModelFlat(Minecraft.getInstance().level, model, renderState, new BlockPos(0, 256, 0), matrixStack, bufferIn.getBuffer(type), false, Minecraft.getInstance().level.getRandom(), renderState.getSeed(blueprintPos), 0, modelData);
						}
					}
					
					matrixStack.popPose();
					
				}
			}
		}
		
		matrixStack.popPose();
		
//		this.progressArrow.draw(matrixStack, 48, 3);
//		this.fluidBar.draw(matrixStack, 53, 48);
//		this.powerSymbol.draw(matrixStack, 20, 43);
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void setIngredients(MultipartBuildRecipe recipe, IIngredients ingredients) {
		List<String[]> pattern = recipe.getStructureBuilder().getPattern();
		HashMap<String, Block[]> keys = recipe.getStructureBuilder().getKeys();
		
		HashMap<Item, Integer> ingredientItems = new HashMap<Item, Integer>();
		for (String[] layer : pattern) {
			for (int rowZ = 0; rowZ < layer.length; rowZ++) {
				String row = layer[rowZ];
				for (int rowX = 0; rowX < row.length(); rowX++) {
					char key = row.charAt(rowX);
					Block[] possibleItems = keys.get("" + key);
					for (int i = 0; i < possibleItems.length; i++) {
						Block block = possibleItems[i];
						if (!block.isAir(block.defaultBlockState(), Minecraft.getInstance().level, BlockPos.ZERO)) {
							Item item = Item.byBlock(block);
							if (ingredientItems.containsKey(item)) {
								ingredientItems.put(item, ingredientItems.get(item) + 1);
							} else {
								ingredientItems.put(item, 1);
							}
						}
					}
				}
			}
		}
		
		List<ItemStack> ingredientStacks = new ArrayList<ItemStack>();
		
		for (Entry<Item, Integer> entry : ingredientItems.entrySet()) {
			ingredientStacks.add(new ItemStack(entry.getKey(), entry.getValue()));
		}
		
		ingredients.setInputs(VanillaTypes.ITEM, UtilHelper.toCollection(ingredientStacks.toArray(new ItemStack[ingredientStacks.size()])));
		ingredients.setOutputs(VanillaTypes.ITEM, UtilHelper.toCollection(new ItemStack(Item.byBlock(recipe.getStructureBuilder().getResultingState()))));
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MultipartBuildRecipe recipe, IIngredients ingredients) {
		
		List<String[]> pattern = recipe.getStructureBuilder().getPattern();
		HashMap<String, Block[]> keys = recipe.getStructureBuilder().getKeys();
		
		HashMap<List<Item>, Integer> ingredientItems = new HashMap<List<Item>, Integer>();
		for (int layerY = 0; layerY < pattern.size(); layerY++) {
			String[] layer = pattern.get(layerY);
			for (int rowZ = 0; rowZ < layer.length; rowZ++) {
				String row = layer[rowZ];
				for (int rowX = 0; rowX < row.length(); rowX++) {
					char key = row.charAt((row.length() - 1) - rowX);
					Block[] possibleItems = keys.get("" + key);
					List<Item> items = new ArrayList<Item>();
					for (int i = 0; i < possibleItems.length; i++) {
						Block block = possibleItems[i];
						if (!block.isAir(block.defaultBlockState(), Minecraft.getInstance().level, BlockPos.ZERO)) {
							Item item = Item.byBlock(block);
							items.add(item);
						}
					}
					if (items.size() > 0) {
						if (ingredientItems.containsKey(items)) {
							ingredientItems.put(items, ingredientItems.get(items) + 1);
						} else {
							ingredientItems.put(items, 1);
						}
					}
				}
			}
		}
		
		List<List<ItemStack>> ingredientStacks = new ArrayList<List<ItemStack>>();
		for (Entry<List<Item>, Integer> entry : ingredientItems.entrySet()) {
			int stackCount = entry.getValue();
			List<ItemStack> stackList = new ArrayList<ItemStack>();
			for (Item item : entry.getKey()) {
				stackList.add(new ItemStack(item, stackCount));
			}
			ingredientStacks.add(stackList);
		}
		
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		
		int slotCount = ingredientStacks.size();
		int slotCollums = Math.min(slotCount, 3);
		int slotRows = (int) Math.ceil(slotCount / 3F);
		
		for (int sy = 0; sy < slotRows; sy++) {
			for (int sx = 0; sx < slotCollums; sx++) {
				int slotIndex = sy * 3 + sx;
				int i = sx * 18 + 2;
				int j = sy * 18 + 2;
				
				if (slotIndex < slotCount) {
					List<ItemStack> content = ingredientStacks.get(slotIndex);
					itemStacks.init(slotIndex, true, i, j);
					itemStacks.set(slotIndex, content);
				}
			}
		}
		
		itemStacks.init(slotCount, true, 146, 62);
		itemStacks.set(slotCount, new ItemStack(Item.byBlock(recipe.getStructureBuilder().getResultingState()), 1));
		
	}
	
}
