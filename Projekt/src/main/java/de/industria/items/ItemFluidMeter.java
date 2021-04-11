package de.industria.items;

import de.industria.Industria;
import de.industria.util.blockfeatures.IFluidConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class ItemFluidMeter extends ItemBase {

	public ItemFluidMeter() {
		super("fluid_meter", Industria.TOOLS);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		ItemStack stack = playerIn.getHeldItem(handIn);
		RayTraceResult result = rayTrace(worldIn, playerIn, FluidMode.NONE);
		
		if (result.getType() == RayTraceResult.Type.MISS) {

			return super.onItemRightClick(worldIn, playerIn, handIn);
			
		} else {

			BlockPos pos = new BlockPos(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z);
			BlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			TileEntity te = worldIn.getTileEntity(pos);
			
			if (te instanceof IFluidConnective) {
				
				FluidStack fluid = ((IFluidConnective) te).getStorage();
				
				ItemStack blockItem = block.getItem(worldIn, pos, state);
				
				ITextComponent line = new TranslationTextComponent("industria.item.fluid_meter.meassure", blockItem.getDisplayName(), fluid.getDisplayName(), fluid.getAmount());
				playerIn.sendStatusMessage(line, true);
				
				return ActionResult.resultSuccess(stack);
				
			}
						
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
		
	}

}
