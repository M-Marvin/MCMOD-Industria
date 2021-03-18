package de.redtec.items;

import java.util.List;

import de.redtec.RedTec;
import de.redtec.util.DriveManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		CompoundNBT tag = stack.getTag();
		if (tag != null) {
			if (tag.contains("DriveName")) tooltip.add(new StringTextComponent("\u00A77" + new TranslationTextComponent("redtec.item.info.name", tag.getString("DriveName")).getString()));
		}
		tooltip.add(new StringTextComponent("\u00A77" + new TranslationTextComponent("redtec.item.info.drive").getString()));
	}
	
}
