package de.industria.items;

import java.util.List;

import de.industria.blocks.BlockSignalAntennaConector;
import de.industria.tileentity.TileEntityRSignalAntenna;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemRemoteControll extends ItemBase {

	public ItemRemoteControll() {
		super("remote_control", ItemGroup.TAB_REDSTONE, 1);
	}
	
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		if (!worldIn.isClientSide() && handIn == Hand.MAIN_HAND) {
			
			ItemStack setItem = playerIn.getOffhandItem();
			ItemStack stack = playerIn.getMainHandItem();
			
			if ((setItem != null ? !setItem.isEmpty() : false) && setItem.getCount() == 1 && playerIn.isShiftKeyDown()) {
				
				CompoundNBT tag = stack.getTag();
				if (tag == null) {
					tag = new CompoundNBT();
					tag.put("ChanelItem", setItem.save(new CompoundNBT()));
				} else {
					tag.put("ChanelItem", setItem.save(new CompoundNBT()));
				}
				
				stack.setTag(tag);
				
				return ActionResult.success(stack);
				
			} else {
				
				CompoundNBT tag = stack.getTag();
				CompoundNBT chanelItemTag = tag.getCompound("ChanelItem");
				ItemStack chanelItem = chanelItemTag == null ? null : ItemStack.of(chanelItemTag);
				boolean toggle = !tag.getBoolean("Toggle");
				tag.putBoolean("Toggle", toggle);
				tag.putInt("CustomModelData", toggle ? 1 : 0);;
				stack.setTag(tag);
				
				RedstoneControlSignal signal = new RedstoneControlSignal(chanelItem, toggle ? 15 : 0);
				BlockPos pos = new BlockPos(playerIn.getX(), playerIn.getY(), playerIn.getZ());
				
				sendSignal(worldIn, pos, 25, signal);
				
				return ActionResult.success(stack);
				
			}
			
		}
		
		return ActionResult.success(playerIn.getMainHandItem());
		
	}
	
	public static void sendSignal(World worldIn, BlockPos pos, int range1, RedstoneControlSignal signal) {
		
		ItemStack chanelItem = null;
		
		for (TileEntity te2 : worldIn.blockEntityList) {
			
			if (te2 instanceof TileEntityRSignalAntenna) {
				
				TileEntityRSignalAntenna antenna = (TileEntityRSignalAntenna) te2;
				
				int range2 = antenna.getRange();
				int distance = BlockSignalAntennaConector.getDistance(pos, antenna.getBlockPos());
				ItemStack chanelItem2 = antenna.getChanelItem();
				boolean isInRange = distance <= range1 + range2;
				boolean isSameChanel = chanelItem != null && chanelItem2 != null ? chanelItem.equals(chanelItem2, false) : chanelItem == null && chanelItem2 == null;
				
				if (isInRange && isSameChanel) {
					
					antenna.reciveSignal(signal);
					
				}
				
			}
			
		}
		
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("\u00A77" + new TranslationTextComponent("industria.item.info.remoteControl").getString()));
	}
	
}
