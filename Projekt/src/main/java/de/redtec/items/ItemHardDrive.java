package de.redtec.items;

import de.redtec.RedTec;
import de.redtec.util.DriveManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

public class ItemHardDrive extends ItemBase {
	
	public ItemHardDrive() {
		super("hard_drive", RedTec.MACHINES, 1);
	}
	
	public static String getDriveName(ItemStack stack, ServerWorld world) {
		if (stack.getItem() instanceof ItemHardDrive) {
			CompoundNBT tag = stack.getTag();
			if (tag == null) tag = new CompoundNBT();
			if (tag.contains("DriveName")) {
				return tag.getString("DriveName");
			} else {
				String driveId = DriveManager.createNextDriveId(world);
				tag.putString("DriveName", driveId);
				stack.setTag(tag);
				return driveId;
			}
		}
		return null;
	}
	
}
