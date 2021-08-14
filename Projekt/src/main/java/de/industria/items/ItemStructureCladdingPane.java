package de.industria.items;

import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.industria.Industria;
import de.industria.renderer.ItemStructureCladdingRenderer;
import de.industria.typeregistys.ModItems;
import de.industria.util.handler.ItemStackHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemStructureCladdingPane extends ItemBase {

	public ItemStructureCladdingPane() {
		super(new Properties().tab(ItemGroup.TAB_REDSTONE).setISTER(() -> ItemStructureCladdingRenderer::new));
		this.setRegistryName(Industria.MODID, "structure_cladding_pane");
	}
	
	public static void setBlockState(ItemStack stack, BlockState blockState) {
		stack.addTagElement("BlockState", StringNBT.valueOf(ItemStackHelper.getBlockStateString(blockState)));
	}
	
	public static ItemStack createBlockPane(BlockState blockState, int count) {
		ItemStack stack = new ItemStack(ModItems.structure_cladding_pane, count);
		setBlockState(stack, blockState);
		return stack;
	}
	
	public static BlockState getBlockState(ItemStack stack) {
		if (stack.hasTag()) {
			BlockStateParser parser = new BlockStateParser(new StringReader(stack.getTag().getString("BlockState")), false);
			try {
				parser.parse(false);
				return parser.getState();
			} catch (CommandSyntaxException e) {
				Industria.LOGGER.warn("Error on load BlockState from Structure Cladding!");
				e.printStackTrace();
			}
		}
		return Blocks.OAK_PLANKS.defaultBlockState();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("\u00A77" + new TranslationTextComponent("industria.item.info.structureCladdingPane", getBlockState(stack).getBlock().getName().getString()).getString()));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		
		if (context.getPlayer().isCreative()) {
			
			BlockPos pos = context.getClickedPos();
			BlockState state = context.getLevel().getBlockState(pos);
			setBlockState(context.getItemInHand(), state);
			
			return ActionResultType.CONSUME;
			
		}
		
		return super.useOn(context);
	}
	
}
