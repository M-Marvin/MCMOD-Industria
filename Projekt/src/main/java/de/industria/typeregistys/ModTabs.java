package de.industria.typeregistys;

import de.industria.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModTabs {

	public static final ItemGroup MACHINES = new ItemGroup("machines") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.generator);
		}
	};
	public static final ItemGroup BUILDING_BLOCKS = new ItemGroup("building_blocks") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.cracked_polished_granite_bricks);
		}
	};
	public static final ItemGroup DECORATIONS = new ItemGroup("decorations") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.rubber_leaves);
		}
	};
	public static final ItemGroup TOOLS = new ItemGroup("tools") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.hammer);
		}
	};
	public static final ItemGroup MATERIALS = new ItemGroup("materials") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.rubber);
		}
	};
	
}
