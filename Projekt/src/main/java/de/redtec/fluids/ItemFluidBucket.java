package de.redtec.fluids;

import de.redtec.RedTec;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

public class ItemFluidBucket extends BucketItem {
	
	@SuppressWarnings("deprecation")
	public ItemFluidBucket(Fluid containedFluidIn, String name, ItemGroup group) {
		super(containedFluidIn, new Properties().group(group).maxStackSize(1));
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
	}
	
}
