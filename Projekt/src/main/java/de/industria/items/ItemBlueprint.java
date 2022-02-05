package de.industria.items;

import de.industria.typeregistys.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class ItemBlueprint extends ItemBase {
	
	public ItemBlueprint() {
		super("blueprint", new Item.Properties());
	}
	
	public ItemStack writeBlueprint(ItemStack blueprint, String structurePath) {
		if (blueprint.getItem() == ModItems.empty_blueprint) blueprint = new ItemStack(ModItems.blueprint, blueprint.getCount());
		CompoundNBT tag = blueprint.hasTag() ? blueprint.getTag() : new CompoundNBT();
		tag.putString("Structure", structurePath);
		blueprint.setTag(tag);
		return blueprint;
	}
	
	public String getStructure(ItemStack blueprint) {
		if (blueprint.hasTag()) {
			CompoundNBT tag = blueprint.getTag();
			if (tag.contains("Blueprint")) {
				return tag.getString("Blueprint");
			}
		}
		return null;
	}
	
	public BlockPos getStructureSize(ItemStack blueprint) {
		if (blueprint.hasTag()) {
			CompoundNBT tag = blueprint.getTag();
			if (tag.contains("SizeX") && tag.contains("SizeY") && tag.contains("SizeZ")) {
				return new BlockPos(tag.getInt("SizeX"), tag.getInt("SizeY"), tag.getInt("SizeZ"));
			}
		}
		return null;
	}
	
}
