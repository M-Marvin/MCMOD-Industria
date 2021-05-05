package de.industria.items;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;

import de.industria.Industria;
import de.industria.util.blockfeatures.IElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemEnergyMeter extends ItemBase {

	public ItemEnergyMeter() {
		super("energy_meter", Industria.TOOLS, 1);
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
			
			if (block instanceof IElectricConnectiveBlock) {
				
				ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
				ElectricityNetwork network = handler.getNetwork(pos);
				
				ItemStack blockItem = block.getItem(worldIn, pos, state);
				
				NumberFormat format = new DecimalFormat("0.00");
				
				float currentPower = network.getCurrent() * network.getVoltage().getVoltage();
				float capacityPower = network.getCapacity() * network.getVoltage().getVoltage();
				String currentPowerS = currentPower >= 1000 ? (format.format((currentPower / 1000)) + "k") : format.format(currentPower);
				String capacityPowerS = capacityPower >= 1000 ? (format.format((capacityPower / 1000)) + "k") : format.format(capacityPower);
				
				ITextComponent line = new TranslationTextComponent("industria.item.energy_meter.meassure", blockItem.getDisplayName(), network.getVoltage().getVoltage(), format.format(network.getCurrent()), currentPowerS, capacityPowerS, format.format(network.getCapacity()));
				
				playerIn.sendStatusMessage(line, true);
				
				return ActionResult.resultSuccess(stack);
				
			}
						
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
		
	}

}
