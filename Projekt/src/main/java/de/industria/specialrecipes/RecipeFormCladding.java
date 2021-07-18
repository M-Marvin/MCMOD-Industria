
package de.industria.specialrecipes;

import de.industria.ModItems;
import de.industria.items.ItemStructureCladdingPane;
import de.industria.recipetypes.MetalFormRecipe;
import de.industria.tileentity.TileEntityMMetalFormer;
import de.industria.typeregistys.ModSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeFormCladding extends MetalFormRecipe {

	public RecipeFormCladding(ResourceLocation id, int processTime) {
		super(id, null, null, processTime);
	}
	
	@Override
	public boolean matches(TileEntityMMetalFormer inv, World worldIn) {
		
		ItemStack itemIn = inv.getItem(0);
		if (itemIn.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) itemIn.getItem()).getBlock();
			if (block.defaultBlockState().isSolidRender(null, null)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public ItemStack getResultItem() {
		return new ItemStack(ModItems.structure_cladding_pane);
	}
	
	@Override
	public ItemStack assemble(TileEntityMMetalFormer inv) {
		ItemStack itemIn = inv.getItem(0);
		if (itemIn.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) itemIn.getItem()).getBlock();
			if (block.defaultBlockState().isSolidRender(null, null)) {
				return ItemStructureCladdingPane.createBlockPane(block.defaultBlockState(), 16);
			}
		}
		return getResultItem();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemIn() {
		return new ItemStack(Item.byBlock(Blocks.OAK_PLANKS), 1);
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.FORM_CLADDING;
	}
	
}
