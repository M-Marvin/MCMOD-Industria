package de.industria.specialrecipes;

import de.industria.blocks.BlockBurnedCable;
import de.industria.recipetypes.SchredderRecipe;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModSerializer;
import de.industria.util.handler.UtilHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeMacerateBurnedCable extends SchredderRecipe {
	
	public RecipeMacerateBurnedCable(ResourceLocation id, ItemStack burnedCableItem, ItemStack resultMaterial) {
		super(id, UtilHelper.toIngredient(burnedCableItem), new ItemStack(ModItems.polymer_resin), resultMaterial, ItemStack.EMPTY, 70, ModItems.schredder_macerator, 8);
	}
	
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		
		ItemStack burnedCable = inv.getItem(0);
		if (burnedCable.isEmpty()) return false;
		Block burnedBlock = BlockBurnedCable.getBurnedCableFromStack(burnedCable);
		if (burnedBlock == null) burnedBlock = ModItems.copper_cable;
		Block burnedBlockRecipe = BlockBurnedCable.getBurnedCableFromStack(this.itemIn.getItems()[0]);
		
		

		System.out.println(burnedBlockRecipe);
		
		return burnedBlockRecipe == burnedBlock;
		
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.MACERATE_BURNED_CABLE;
	}
	
}
