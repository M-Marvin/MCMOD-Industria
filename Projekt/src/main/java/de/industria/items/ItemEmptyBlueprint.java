package de.industria.items;

import de.industria.ModItems;
import de.industria.typeregistys.ModTabs;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemEmptyBlueprint extends ItemBase {
	
	public ItemEmptyBlueprint() {
		super("empty_blueprint", ModTabs.TOOLS);
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		
		ItemStack heldStack = context.getPlayer().getMainHandItem();
		
		if (heldStack.getItem() == this) {
			
			CompoundNBT tag = heldStack.hasTag() ? heldStack.getTag() : new CompoundNBT();
			
			if (tag.contains("BeginPos")) {
				
				BlockPos pos1 = NBTUtil.readBlockPos(tag.getCompound("BeginPos"));
				BlockPos pos2 = context.getClickedPos();
				ItemStack newBlueprint = setupNewBlueprint(context.getLevel(), pos1, pos2);
				
				if (newBlueprint != null) {
					
					if (!context.getPlayer().addItem(newBlueprint)) {
						context.getPlayer().drop(newBlueprint, false);
					}
					
					tag.remove("BeginPos");
					heldStack.setTag(tag);
					if (!context.getPlayer().isCreative()) heldStack.shrink(1);
					
					context.getPlayer().playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 1.0F);
					
					return ActionResultType.CONSUME;
					
				}
				
			} else {
				
				BlockPos beginPos = context.getClickedPos();
				tag.put("BeginPos", NBTUtil.writeBlockPos(beginPos));
				heldStack.setTag(tag);
				return ActionResultType.CONSUME;
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack setupNewBlueprint(World world, BlockPos pos1, BlockPos pos2) {
		
		BlockPos origin = new BlockPos(
				Math.min(pos1.getX(), pos2.getX()),
				Math.min(pos1.getY(), pos2.getY()),
				Math.min(pos1.getZ(), pos2.getZ()));
		BlockPos size = new BlockPos(
				Math.max(pos1.getX(), pos2.getX()) - origin.getX(),
				Math.max(pos1.getY(), pos2.getY()) - origin.getY(),
				Math.max(pos1.getZ(), pos2.getZ()) - origin.getZ());

		ListNBT blueprint = new ListNBT();
		
		if ((size.getX() + 1) * (size.getY() + 1) * (size.getZ() + 1) > 1) {
			
			for (int x = 0; x <= size.getX(); x++) {
				for (int y = 0; y <= size.getY(); y++) {
					for (int z = 0; z <= size.getZ(); z++) {
						
						CompoundNBT block = new CompoundNBT();
						BlockPos scannPos = new BlockPos(x, y, z).offset(origin);
						BlockState scannState = world.getBlockState(scannPos);
						
						if (!scannState.isAir()) {
							
							block.put("Pos", NBTUtil.writeBlockPos(scannPos.subtract(origin)));
							block.put("BlockState", NBTUtil.writeBlockState(scannState));
							
							if (scannState.hasTileEntity()) {
								
								TileEntity tileEntity = world.getBlockEntity(scannPos);
								if (tileEntity != null) {
									CompoundNBT tileData = tileEntity.save(new CompoundNBT());
									block.put("TileData", tileData);
								}
								
							}
							
							blueprint.add(block);
							
						}
						
					}
				}
			}
			
			ItemStack blueprintItem = new ItemStack(ModItems.blueprint);
			blueprintItem.addTagElement("Blueprint", blueprint);
			return blueprintItem;
			
		}
		
		return null;
		
	}
	
}
