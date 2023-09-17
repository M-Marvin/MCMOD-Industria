package de.m_marvin.industria.core.util;

import java.awt.Color;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;

public class GameUtility {
	
	public static int toIntegerColor(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int toIntegerColor(float r, float g, float b, float a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int toIntegerColor(Vec4f color) {
		return new Color(color.x, color.y, color.z, color.w).getRGB();
	}
	
	public static Vec4f toVecColor(int color) {
		Color colorc = new Color(color);
		return new Vec4f(colorc.getRed() / 255F, colorc.getGreen() / 255F, colorc.getBlue() / 255F, colorc.getAlpha() / 255F);
	}
	
	public static <T extends BlockEntity & IJunctionEdit> AbstractContainerMenu openJunctionScreenOr(T blockEntity, int containerId, Player player, Inventory inventory, Supplier<AbstractContainerMenu> container) {
		return player.getItemInHand(InteractionHand.MAIN_HAND).is(IndustriaTags.Items.SCREW_DRIVERS) ? new JunctionBoxContainer<T>(containerId, inventory, blockEntity) : container.get();
	}

	public static InteractionResult openJunctionBlockEntityUI(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
		if (!pPlayer.getItemInHand(pHand).is(IndustriaTags.Items.SCREW_DRIVERS)) return InteractionResult.PASS;
		return openBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	public static InteractionResult openElectricBlockEntityUI(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
		if (pPlayer.getItemInHand(pHand).is(IndustriaTags.Items.CONDUITS)) return InteractionResult.PASS;
		return openBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	public static InteractionResult openBlockEntityUI(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
		if (!pLevel.isClientSide()) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof MenuProvider provider) {
				NetworkHooks.openScreen((ServerPlayer) pPlayer, provider, pPos);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}
	
	public static Direction getFacingDirection(Entity entity) {
		Vec3d viewVec = Vec3d.fromVec(entity.getViewVector(1));
		return MathUtility.getVecDirection(viewVec);
	}
	
	public static Vec3f getWorldGravity(BlockGetter level) {
		return new Vec3f(0, 0.1F, 0); // TODO
	}
	
	public static <T extends Capability<C>, C extends ICapabilitySerializable<?>> C getLevelCapability(Level level, T cap) {
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
	
	public static void setBlock(Level level, BlockPos pos, BlockState state) {
		LevelChunk chunk = (LevelChunk) level.getChunk(pos);
		LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
		BlockState oldState = level.getBlockState(pos);
		section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
		PhysicUtility.triggerBlockChange(level, pos, oldState, state);
	}

	public static void removeBlock(Level level, BlockPos pos) {
		level.removeBlockEntity(pos);
		setBlock(level, pos, Blocks.AIR.defaultBlockState());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void copyBlock(Level level, BlockPos from, BlockPos to) {
		BlockState state = level.getBlockState(from);
		BlockEntity blockentity = level.getBlockEntity(from);
		
		setBlock(level, to, state);
		
		// Transfer pending schedule-ticks
		if (level.getBlockTicks().hasScheduledTick(from, state.getBlock())) {
			level.getBlockTicks().schedule(new ScheduledTick(state.getBlock(), to, 0, 0));	
		}
		
		// Transfer block-entity data
		if (state.hasBlockEntity() && blockentity != null) {
			CompoundTag data = blockentity.serializeNBT();
			level.setBlockEntity(blockentity);
			BlockEntity newBlockentity = level.getBlockEntity(to);
			if (newBlockentity != null) {
				newBlockentity.deserializeNBT(data);
			}
		}
	}
	
	public static void relocateBlock(Level level, BlockPos from, BlockPos to) {
		copyBlock(level, from, to);
		removeBlock(level, from);
	}
	
	public static void triggerUpdate(Level level, BlockPos pos) {
		LevelChunk chunk = level.getChunkAt(pos);
		level.markAndNotifyBlock(pos, chunk, level.getBlockState(pos), level.getBlockState(pos), 3, 512);
	}
	
	public static void triggerClientSync(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
	}
	
	public static HitResult raycast(Level level, Vec3d from, Vec3d direction, double range) {
		return raycast(level, from, from.add(direction.mul(range)));
	}
	
	public static HitResult raycast(Level level, Vec3d from, Vec3d to) {
		ClipContext clipContext = new ClipContext(from.writeTo(new Vec3(0, 0, 0)), to.writeTo(new Vec3(0, 0, 0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
		return level.clip(clipContext);
	}
	
}
