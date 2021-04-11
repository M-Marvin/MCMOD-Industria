package de.industria.items;

import de.industria.blocks.BlockSignalAntennaConector;
import de.industria.tileentity.TileEntityRSignalAntenna;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRemoteControll extends ItemBase {

	public ItemRemoteControll() {
		super("remote_control", ItemGroup.REDSTONE, 1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		if (!worldIn.isRemote() && handIn == Hand.MAIN_HAND) {
			
			ItemStack setItem = playerIn.getHeldItemOffhand();
			ItemStack stack = playerIn.getHeldItemMainhand();
			
			if ((setItem != null ? !setItem.isEmpty() : false) && setItem.getCount() == 1) {
				
				CompoundNBT tag = stack.getTag();
				if (tag == null) {
					tag = new CompoundNBT();
					tag.put("ChanelItem", setItem.write(new CompoundNBT()));
					setItem.shrink(1);
				} else {
					ItemStack oldItem = ItemStack.read(tag.getCompound("ChanelItem"));
					tag.put("ChanelItem", setItem.write(new CompoundNBT()));
					playerIn.setHeldItem(Hand.OFF_HAND, oldItem);
				}

				stack.setTag(tag);
				
				return ActionResult.resultSuccess(stack);
				
			} else {
				
				CompoundNBT tag = stack.getTag();
				CompoundNBT chanelItemTag = tag.getCompound("ChanelItem");
				ItemStack chanelItem = chanelItemTag == null ? null : ItemStack.read(chanelItemTag);
				boolean toggle = !tag.getBoolean("Toggle");
				tag.putBoolean("Toggle", toggle);
				stack.setTag(tag);
				
				RedstoneControlSignal signal = new RedstoneControlSignal(chanelItem, toggle);
				BlockPos pos = new BlockPos(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ());
				
				sendSignal(worldIn, pos, 25, signal);
				
				return ActionResult.resultSuccess(stack);
				
			}
			
		}
		
		return ActionResult.resultSuccess(playerIn.getHeldItemMainhand());
		
	}
	
	public static void sendSignal(World worldIn, BlockPos pos, int range1, RedstoneControlSignal signal) {
		
		ItemStack chanelItem = null;
		
		for (TileEntity te2 : worldIn.loadedTileEntityList) {
			
			if (te2 instanceof TileEntityRSignalAntenna) {
				
				TileEntityRSignalAntenna antenna = (TileEntityRSignalAntenna) te2;
				
				int range2 = antenna.getRange();
				int distance = BlockSignalAntennaConector.getDistance(pos, antenna.getPos());
				ItemStack chanelItem2 = antenna.getChanelItem();
				boolean isInRange = distance <= range1 + range2;
				boolean isSameChanel = chanelItem != null && chanelItem2 != null ? chanelItem.equals(chanelItem2, false) : chanelItem == null && chanelItem2 == null;
				
				if (isInRange && isSameChanel) {
					
					antenna.reciveSignal(signal);
					
				}
				
			}
			
		}
		
	}
	
}
