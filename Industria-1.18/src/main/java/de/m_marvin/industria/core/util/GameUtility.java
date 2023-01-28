package de.m_marvin.industria.core.util;

import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class GameUtility {
	
	public static Vec3f getWorldGravity(BlockGetter level) {
		return new Vec3f(0, 0.1F, 0); // TODO
	}
	
	public static <T extends Capability<C>, C extends ICapabilitySerializable<?>> C getCapability(Level level, T cap) {
		LazyOptional<C> conduitHolder = level.getCapability(cap);
		if (!conduitHolder.isPresent()) throw new IllegalStateException("Capability " + cap + " not attached on level " + level);
		return conduitHolder.resolve().get();
	}
	
	public static void dropItem(Level level, ItemStack stack, Vec3f position, float spreadFactH, float spreadFactV) {
		ItemEntity drop = new ItemEntity(level, position.x, position.y, position.z, stack);
		Vec3f spread = new Vec3f(
				(level.random.nextFloat() - 0.5F) * spreadFactH,
				level.random.nextFloat() * spreadFactV,
				(level.random.nextFloat() - 0.5F) * spreadFactH
				);
		drop.setDeltaMovement(spread.writeTo(new Vec3(0, 0, 0)));
		level.addFreshEntity(drop);
	}
	
}
