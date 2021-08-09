package de.industria.items;

import de.industria.typeregistys.ModTabs;
import de.industria.util.blockfeatures.ITEFluidConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class ItemFluidMeter extends ItemBase {

	public ItemFluidMeter() {
		super("fluid_meter", ModTabs.TOOLS, 1);
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		
		World worldIn = context.getLevel();
		PlayerEntity playerIn = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		
		BlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity te = worldIn.getBlockEntity(pos);
		
		if (te instanceof ITEFluidConnective) {
			
			FluidStack fluid = ((ITEFluidConnective) te).getStorage();
			
			@SuppressWarnings("deprecation")
			ItemStack blockItem = block.getCloneItemStack(worldIn, pos, state);
			
			ITextComponent line = new TranslationTextComponent("industria.item.fluid_meter.meassure", blockItem.getHoverName(), fluid.getDisplayName(), fluid.getAmount());
			playerIn.displayClientMessage(line, true);
			
			return ActionResultType.SUCCESS;
			
		}
		
		return super.useOn(context);
		
	}
	
}
