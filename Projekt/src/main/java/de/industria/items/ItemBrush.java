package de.industria.items;

import de.industria.Industria;
import de.industria.blocks.BlockStairsBase;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemBrush extends ItemBase {
	
	public ItemBrush() {
		super("brush", ModTabs.TOOLS, 1);
	}
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return false;
	}
	
	public String getPaintName(ItemStack brush) {
		if (brush.getItem() == this && brush.hasTag()) {
			return brush.getTag().getString("PaintName");
		}
		return "empty";
	}
	
	public ItemStack setPaintName(String name, ItemStack brush) {
		if (brush.getItem() == this) {
			CompoundNBT nbt = brush.getTag();
			if (nbt == null) nbt = new CompoundNBT();
			nbt.putString("PaintName", name);
			brush.setTag(nbt);
		}
		return brush;
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		
		World world = context.getLevel();
		BlockPos clickedBlockPos = context.getClickedPos();
		BlockState clickedBlock = world.getBlockState(clickedBlockPos);
		String paintName = getPaintName(context.getItemInHand());
		boolean flag = false;
		
		if (paintName.startsWith("dye.")) {
			String color = paintName.split("\\.")[1];
			if (clickedBlock.getBlock() == ModItems.concrete) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color + "_concrete"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == ModItems.concrete_slab) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Industria.MODID, color + "_concrete_slab"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, clickedBlock.getValue(BlockStateProperties.SLAB_TYPE)).setValue(BlockStateProperties.WATERLOGGED, clickedBlock.getValue(BlockStateProperties.WATERLOGGED)));
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == ModItems.concrete_stairs) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Industria.MODID, color + "_concrete_stairs"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState().setValue(BlockStairsBase.SHAPE, clickedBlock.getValue(BlockStairsBase.SHAPE)).setValue(BlockStairsBase.HALF, clickedBlock.getValue(BlockStairsBase.HALF)).setValue(BlockStairsBase.FACING, clickedBlock.getValue(BlockStairsBase.FACING)).setValue(BlockStateProperties.WATERLOGGED, clickedBlock.getValue(BlockStateProperties.WATERLOGGED)));
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == Blocks.WHITE_WOOL) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color + "_wool"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == Blocks.TERRACOTTA) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color + "_terracotta"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == Blocks.WHITE_CARPET) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color + "_terracotta"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == Blocks.GLASS) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color + "_stained_glass"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == Blocks.GLASS_PANE) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color + "_stained_glass_pane"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState().setValue(PaneBlock.EAST, clickedBlock.getValue(PaneBlock.EAST)).setValue(PaneBlock.WEST, clickedBlock.getValue(PaneBlock.WEST)).setValue(PaneBlock.SOUTH, clickedBlock.getValue(PaneBlock.SOUTH)).setValue(PaneBlock.NORTH, clickedBlock.getValue(PaneBlock.NORTH)).setValue(PaneBlock.WATERLOGGED, clickedBlock.getValue(PaneBlock.WATERLOGGED)));
					flag = true;
				}
			}
			if (clickedBlock.getBlock().is(BlockTags.PLANKS) && color.equals("white")) {
				Block dyedConcrete = ModItems.white_painted_planks;
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
			if (clickedBlock.getBlock() == ModItems.white_painted_planks) {
				Block dyedConcrete = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Industria.MODID, color + "_painted_planks"));
				if (dyedConcrete != null) {
					world.setBlockAndUpdate(clickedBlockPos, dyedConcrete.defaultBlockState());
					flag = true;
				}
			}
		}
		
		if (flag) {
			world.playSound(null, clickedBlockPos, SoundEvents.WEEPING_VINES_STEP, SoundCategory.BLOCKS, 1, 1);
			return ActionResultType.SUCCESS;
		}
		
		return super.useOn(context);
		
	}
	
}
