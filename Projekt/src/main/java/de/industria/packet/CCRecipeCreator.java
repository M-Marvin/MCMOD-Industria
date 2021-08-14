package de.industria.packet;

import java.util.function.Supplier;

import de.industria.blocks.BlockRecipeCreator;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CCRecipeCreator {
	
	private BlockPos pos;
	private boolean shapeless;
	private ItemStack[] recipe;
	
	public CCRecipeCreator(BlockPos pos, boolean shapeless, Inventory recipe) {
		this.pos = pos;
		this.shapeless = shapeless;
		this.recipe = new ItemStack[10];
		for (int i = 0; i < 10; i++) {
			this.recipe[i] = recipe.getItem(i);
		}
	}
	
	public CCRecipeCreator(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.shapeless = buf.readBoolean();
		this.recipe = new ItemStack[buf.readInt()];
		for (int i = 0; i < 10; i++) this.recipe[i] = buf.readItem();
	}
		
	public static void encode(CCRecipeCreator packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeBoolean(packet.shapeless);
		buf.writeInt(packet.recipe.length);
		for (ItemStack stack : packet.recipe) buf.writeItem(stack);
	}
	
	public static void handle(final CCRecipeCreator packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().level;
			BlockPos devicePos = packet.pos;
			BlockState state = world.getBlockState(devicePos);
			
			if (state.getBlock() == ModItems.recipe_creator) {
				
				((BlockRecipeCreator) state.getBlock()).createRecipe(world, devicePos, packet.shapeless, packet.recipe);
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}
