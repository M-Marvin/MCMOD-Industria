package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class NodeTypes {
	
	public static final NodeType ALL = NodeType.fromPredicate(ChatFormatting.LIGHT_PURPLE, new ResourceLocation(IndustriaCore.MODID, "textures/node/all.png"), type -> true);
	public static final NodeType ELECTRIC = NodeType.fromPredicate(ChatFormatting.YELLOW, new ResourceLocation(IndustriaCore.MODID, "textures/node/electric.png"), type -> type == NodeTypes.ELECTRIC);
	public static final NodeType FLUID = NodeType.fromPredicate(ChatFormatting.AQUA, new ResourceLocation(IndustriaCore.MODID, "textures/node/fluid.png"), type -> type == NodeTypes.FLUID);
	
}
