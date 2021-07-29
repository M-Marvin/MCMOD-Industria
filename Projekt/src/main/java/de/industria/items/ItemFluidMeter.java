package de.industria.items;

import de.industria.Industria;
import de.industria.util.blockfeatures.ITEFluidConnective;
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
		super("fluid_meter", Industria.TOOLS, 1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		ItemStack stack = playerIn.getItemInHand(handIn);
		RayTraceResult result = getPlayerPOVHitResult(worldIn, playerIn, FluidMode.NONE);
		
		if (result.getType() == RayTraceResult.Type.MISS) {

			return super.use(worldIn, playerIn, handIn);
			
		} else {

			BlockPos pos = new BlockPos(result.getLocation().x, result.getLocation().y, result.getLocation().z);
			BlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			TileEntity te = worldIn.getBlockEntity(pos);
			
			if (te instanceof ITEFluidConnective) {
				
				FluidStack fluid = ((ITEFluidConnective) te).getStorage();
				
				ItemStack blockItem = block.getCloneItemStack(worldIn, pos, state);
				
				ITextComponent line = new TranslationTextComponent("industria.item.fluid_meter.meassure", blockItem.getHoverName(), fluid.getDisplayName(), fluid.getAmount());
				playerIn.displayClientMessage(line, true);
				
				return ActionResult.success(stack);
				
			}
						
		}
		
		return super.use(worldIn, playerIn, handIn);
		
	}

}
