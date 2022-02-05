package de.industria.items;

import de.industria.gui.ContainerLoadBlueprint;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemWritableBlueprint extends ItemBase implements INamedContainerProvider {

	public ItemWritableBlueprint() {
		super("writable_blueprint", ModTabs.TOOLS, 1);
	}
		
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			if (!world.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> {buf.writeItem(player.getMainHandItem());});
			return ActionResult.success(player.getMainHandItem());
		}
		return ActionResult.pass(player.getItemInHand(hand));
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerLoadBlueprint(id, playerInv, player.getMainHandItem());
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("industria.blueprint.load");
	}
	
	public static ItemStack writeBlueprint(ItemStack blueprint, String blueprintFile, BlockPos size) {
		ItemStack writtenBlueprint = new ItemStack(ModItems.blueprint, blueprint.getCount());
		CompoundNBT tag = blueprint.getOrCreateTag();
		tag.putString("Blueprint", blueprintFile);
		tag.putInt("SizeX", size.getX());
		tag.putInt("SizeY", size.getY());
		tag.putInt("SizeZ", size.getZ());
		writtenBlueprint.setTag(tag);
		return writtenBlueprint;
	}

}
