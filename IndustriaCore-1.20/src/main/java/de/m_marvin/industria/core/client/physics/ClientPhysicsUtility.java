package de.m_marvin.industria.core.client.physics;

import org.valkyrienskies.mod.common.VSClientGameUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public class ClientPhysicsUtility {
	
	public static void ensureWorldTransformTo(ClientLevel level, PoseStack matrixStack, BlockPos pos) {
		
		VSClientGameUtils.transformRenderIfInShipyard(matrixStack, pos.getX(), pos.getY(), pos.getZ());
		
	}
	
}
