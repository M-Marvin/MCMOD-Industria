package de.m_marvin.industria.core.client.util;

import java.util.List;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitBlockItem;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.electrics.types.conduits.ElectricConduit;
import de.m_marvin.industria.core.magnetism.types.blocks.IMagneticBlock;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.registries.Tags;
import de.m_marvin.industria.core.util.Formatter;
import de.m_marvin.industria.core.util.items.ITooltipAdditionsModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class TooltipAdditions {

	private TooltipAdditions() {}
	
	public static final String TOOLTIP_PHYSICS = "physics";
	public static final String TOOLTIP_ELECTRICS = "electrics";
	public static final String TOOLTIP_MAGNETICS = "magnetics";
	public static final String TOOLTIP_CONDUTIS = "conduits";
	
	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		
		List<Component> tooltips = event.getToolTip();
		
		// Filter out singleplayer only vs2 mass info
		int vs2massIndex = -1;
		for (int i = 0; i < tooltips.size(); i++) {
			if (tooltips.get(i).toString().contains("tooltip.valkyrienskies.mass")) {
				vs2massIndex = i;
				break;
			}
		}
		if (vs2massIndex >= 0) tooltips.remove(vs2massIndex);
		
		boolean printAdditional = event.getFlags().isAdvanced();
		
		if (!printAdditional) return;
		
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		
		if (item instanceof ITooltipAdditionsModifier modifier) {
			modifier.addAdditionsTooltip(tooltips, stack);
		}
		
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			
			if (block instanceof ITooltipAdditionsModifier modifier) {
				modifier.addAdditionsTooltip(tooltips, stack);
			}
			
			addPhysicsTooltips(tooltips, block);
			
			if (block instanceof IElectricBlock) addElectricTooltips(tooltips, block);
			if (block.defaultBlockState().is(Tags.Blocks.MAGNETIC)) addMagnetismTooltips(tooltips, block);
		}
		
		if (item instanceof AbstractConduitItem conduitItem) {
			Conduit conduit = conduitItem.getConduit();
			
			if (conduit instanceof ITooltipAdditionsModifier modifier) {
				modifier.addAdditionsTooltip(tooltips, stack);
			}
			
			addConduitTooltips(tooltips, conduit);
		}
		
		if (item instanceof AbstractConduitBlockItem conduitItem) {
			Conduit conduit = conduitItem.getConduit();

			if (conduit instanceof ITooltipAdditionsModifier modifier) {
				modifier.addAdditionsTooltip(tooltips, stack);
			}
			
			addConduitTooltips(tooltips, conduit);
		}
		
	}
	
	public static boolean shouldShow(Object obj, String typeName) {
		if (obj instanceof ITooltipAdditionsModifier modifier) return modifier.showTooltipType(typeName);
		return true;
	}
	
	public static void addTooltip(List<Component> tooltips, Component c) {
		tooltips.add(Formatter.build().appand(c).withStyle(ChatFormatting.GRAY).component());
	}
	
	public static void addPhysicsTooltips(List<Component> tooltips, Block block) {
		if (!shouldShow(block, TOOLTIP_PHYSICS)) return;
		addTooltip(tooltips, Component.translatable("industriacore.tooltip.physics.mass", ContraptionUtility.getBlockMass(block.defaultBlockState())));
	}
	
	public static void addConduitTooltips(List<Component> tooltips, Conduit conduit) {
		if (!shouldShow(conduit, TOOLTIP_CONDUTIS)) return;
		ChatFormatting color = ChatFormatting.GRAY;
		if (conduit.getValidNodeTypes().length > 0) {
			color = conduit.getValidNodeTypes()[0].getColor();	
		}
		
		addTooltip(tooltips, Component.translatable("industriacore.tooltip.conduit.name", Formatter.build().appand(conduit.getName()).withStyle(color).component()));
		addTooltip(tooltips, Component.translatable("industriacore.tooltip.conduit.maxClampDistance", conduit.getConduitType().getClampingLength()));
		
		if (conduit instanceof ElectricConduit electricConduit) {
			addTooltip(tooltips, Component.translatable("industriacore.tooltip.electricconduit.resistance", electricConduit.getResistancePerBlock()));
		}
	}
	
	public static void addMagnetismTooltips(List<Component> tooltips, Block block) {
		if (!shouldShow(block, TOOLTIP_MAGNETICS)) return;
		if (block.defaultBlockState().is(Tags.Blocks.MAGNETIC)) {
			
			if (block instanceof IMagneticBlock magnetic) {
				addTooltip(tooltips, Component.translatable("industriacore.tooltip.magneticblock.fieldstrength", magnetic.getFieldVector(null, block.defaultBlockState(), BlockPos.ZERO).length()));
				addTooltip(tooltips, Component.translatable("industriacore.tooltip.magneticblock.coefficient", magnetic.getCoefficient(null, block.defaultBlockState(), BlockPos.ZERO)));
			} else {
				BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(block);	
				addTooltip(tooltips, Component.translatable("industriacore.tooltip.magneticblock.coefficient", parametrics.getMagneticCoefficient()));
			}
			
		}
	}
	
	public static void addElectricTooltips(List<Component> tooltips, Block block) {
		if (!shouldShow(block, TOOLTIP_ELECTRICS)) return;
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(block);
		addTooltip(tooltips, Component.translatable("industriacore.tooltip.electricblock.voltage", parametrics.getNominalVoltage()));
		addTooltip(tooltips, Component.translatable("industriacore.tooltip.electricblock.power", parametrics.getNominalPower()));
	}
	
}
