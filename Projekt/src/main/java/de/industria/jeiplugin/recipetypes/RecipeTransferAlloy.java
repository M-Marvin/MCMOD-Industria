package de.industria.jeiplugin.recipetypes;

import de.industria.gui.ContainerMAlloyFurnace;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;

public class RecipeTransferAlloy implements IRecipeTransferHandler<ContainerMAlloyFurnace> {
	
	@Override
	public Class<ContainerMAlloyFurnace> getContainerClass() {
		return ContainerMAlloyFurnace.class;
	}
	
	@Override
	public IRecipeTransferError transferRecipe(ContainerMAlloyFurnace container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
		
		// TODO
		return null;
		
	}
	
}
