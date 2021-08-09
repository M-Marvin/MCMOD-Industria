package de.industria.items;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.industria.typeregistys.ModTabs;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
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
		super("energy_meter", ModTabs.TOOLS, 1);
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
			
			if (block instanceof IBElectricConnectiveBlock) {
				
				ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
				ElectricityNetwork network = handler.getNetwork(pos);
				
				ItemStack blockItem = block.getCloneItemStack(worldIn, pos, state);
				
				NumberFormat format = new DecimalFormat("0.00");
				
				float currentPower = network.getCurrent() * network.getVoltage().getVoltage();
				float capacityPower = network.getCapacity() * network.getVoltage().getVoltage();
				String currentPowerS = currentPower >= 1000 ? (format.format((currentPower / 1000)) + "k") : format.format(currentPower);
				String capacityPowerS = capacityPower >= 1000 ? (format.format((capacityPower / 1000)) + "k") : format.format(capacityPower);
				
				ITextComponent line = new TranslationTextComponent("industria.item.energy_meter.meassure", blockItem.getHoverName(), network.getVoltage().getVoltage(), format.format(network.getCurrent()), currentPowerS, capacityPowerS, format.format(network.getCapacity()));
				
				playerIn.displayClientMessage(line, true);
				
				return ActionResult.success(stack);
				
			}
						
		}
		
		return super.use(worldIn, playerIn, handIn);
		
	}

}
