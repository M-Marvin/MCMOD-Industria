package de.industria.items;

import de.industria.gui.ContainerNameBlueprint;
import de.industria.typeregistys.ModTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemEmptyBlueprint extends ItemBase implements INamedContainerProvider {
	
	public ItemEmptyBlueprint() {
		super("empty_blueprint", ModTabs.TOOLS);
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.isShiftKeyDown() && hand == Hand.MAIN_HAND) {
			return handlePositionSet(world, player, player.blockPosition(), false) ? ActionResult.success(player.getMainHandItem()) : ActionResult.pass(player.getMainHandItem());
		}
		return ActionResult.pass(player.getItemInHand(hand));
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getHand() == Hand.MAIN_HAND) {
			return handlePositionSet(context.getLevel(), context.getPlayer(), context.getClickedPos(), true) ? ActionResultType.SUCCESS : ActionResultType.PASS;
		}
		return ActionResultType.PASS;
	}
	
	public boolean handlePositionSet(World world, PlayerEntity player, BlockPos position, boolean removePositions) {
		
		ItemStack heldStack = player.getMainHandItem();
		
		if (heldStack.getItem() == this) {
			
			CompoundNBT tag = heldStack.hasTag() ? heldStack.getTag() : new CompoundNBT();
			
			if (player.isShiftKeyDown() && removePositions) {
				
				tag.remove("CornerA");
				tag.remove("CornerB");
				heldStack.setTag(tag);
				return true;
				
			} else {
				
				if (tag.contains("CornerB")) {
					
					if (!world.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> {buf.writeItem(heldStack);});
					tag.remove("CornerA");
					tag.remove("CornerB");
					heldStack.setTag(tag);
					return true;
					
				} else if (tag.contains("CornerA")) {

					BlockPos posB = position;
					tag.put("CornerB", NBTUtil.writeBlockPos(posB));
					heldStack.setTag(tag);
					return true;
					
				} else {
					
					BlockPos posA = position;
					tag.put("CornerA", NBTUtil.writeBlockPos(posA));
					heldStack.setTag(tag);
					return true;
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public static BlockPos getPositionA(ItemStack blueprint) {
		if (blueprint.hasTag()) {
			CompoundNBT tag = blueprint.getTag();
			return tag.contains("CornerA") ? NBTUtil.readBlockPos(tag.getCompound("CornerA")) : null;
		}
		return null;
	}
	public static BlockPos getPositionB(ItemStack blueprint) {
		if (blueprint.hasTag()) {
			CompoundNBT tag = blueprint.getTag();
			return tag.contains("CornerB") ? NBTUtil.readBlockPos(tag.getCompound("CornerB")) : null;
		}
		return null;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerNameBlueprint(id, playerInv, player.getMainHandItem());
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("industria.blueprint.screen");
	}
	
}
