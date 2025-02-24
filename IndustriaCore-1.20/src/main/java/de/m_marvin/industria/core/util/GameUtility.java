package de.m_marvin.industria.core.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.Tags;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;

public class GameUtility {
	
	private GameUtility() {}
	
	public static <T extends BlockEntity & IJunctionEdit> AbstractContainerMenu openJunctionScreenOr(T blockEntity, int containerId, Player player, Inventory inventory, Supplier<AbstractContainerMenu> container) {
		return player.getItemInHand(InteractionHand.MAIN_HAND).is(Tags.Items.SCREW_DRIVERS) ? new JunctionBoxContainer<T>(containerId, inventory, blockEntity) : container.get();
	}

	public static InteractionResult openJunctionBlockEntityUI(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
		if (!pPlayer.getItemInHand(pHand).is(Tags.Items.SCREW_DRIVERS)) return InteractionResult.PASS;
		return openBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	public static InteractionResult openElectricBlockEntityUI(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
		if (pPlayer.getItemInHand(pHand).is(Tags.Items.CONDUITS)) return InteractionResult.PASS;
		return openBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	public static InteractionResult openBlockEntityUI(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand) {
		if (!pLevel.isClientSide()) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof MenuProvider provider) {
				triggerClientSync(pLevel, pPos);
				NetworkHooks.openScreen((ServerPlayer) pPlayer, provider, pPos);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}
	
	public static Vec3f getWorldGravity(BlockGetter level) {
		return new Vec3f(0, 10, 0); // FIXME [VS2dep] use gravity constant from VS2
	}
	
	public static <T extends Capability<C>, C extends ICapabilitySerializable<?>> C getLevelCapability(Level level, T cap) {
		LazyOptional<C> conduitHolder = level.getCapability(cap);
		if (!conduitHolder.isPresent()) throw new IllegalStateException("Capability " + cap.getName() + " not attached on level " + level);
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
	
	public static void removeBlocksAndConduits(Level level, BlockPos from, BlockPos to) {
		
		// Remove conduits
		List<ConduitEntity> conduits = ConduitUtility.getConduitsInBounds(level, from, to, false);
		for (ConduitEntity conduit : conduits) {
			ConduitUtility.removeConduit(level, conduit.getPosition(), false);
		}
		
		BlockState air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
		BlockState blk = Blocks.ERROR_BLOCK.get().defaultBlockState();//net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();

		// Replace with full blocks (prevent block drop and unintended ship removal)
		for (int y = from.getY(); y <= to.getY(); y++) {
			for (int z = from.getZ(); z <= to.getZ(); z++) {
				for (int x = from.getX(); x <= to.getX(); x++) {
					BlockPos pos = new BlockPos(x, y, z);
					level.setBlockAndUpdate(pos, blk);
				}
			}
		}
		
		// Remove blocks without updates (trick VS2 to prevent unintended ship splitting)
		for (int y = from.getY(); y <= to.getY(); y++) {
			for (int z = from.getZ(); z <= to.getZ(); z++) {
				for (int x = from.getX(); x <= to.getX(); x++) {
					BlockPos pos = new BlockPos(x, y, z);
					ChunkPos chunk = new ChunkPos(pos);
					BlockPos cpos = pos.subtract(new BlockPos(chunk.getMinBlockX(), 0, chunk.getMinBlockZ()));
					level.getChunk(pos).setBlockState(cpos, air, false);
				}
			}
		}

		// Notify removed blocks to update ship bounds
		for (int y = from.getY(); y <= to.getY(); y++) {
			for (int z = from.getZ(); z <= to.getZ(); z++) {
				for (int x = from.getX(); x <= to.getX(); x++) {
					BlockPos pos = new BlockPos(x, y, z);
					ContraptionUtility.triggerBlockChange(level, pos, blk, air);
				}
			}
		}
	
	}

	public static void removeBlocksAndConduits(Level level, Collection<BlockPos> positions) {
		
		// Remove conduits
		positions.stream()
			.flatMap(pos -> ConduitUtility.getConduitsAtBlock(level, pos).stream())
			.distinct()
			.filter(c -> 
				StreamSupport.stream(positions.spliterator(), false).filter(c.getPosition().getNodeApos()::equals).findAny().isPresent() &&
				StreamSupport.stream(positions.spliterator(), false).filter(c.getPosition().getNodeBpos()::equals).findAny().isPresent())
			.forEach(c -> ConduitUtility.removeConduit(level, c.getPosition(), false));

		BlockState air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
		BlockState blk = Blocks.ERROR_BLOCK.get().defaultBlockState();//net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();

		// Replace with full blocks (prevent block drop and unintended ship removal)
		for (BlockPos pos : positions) {
			level.setBlockAndUpdate(pos, blk);
		}

		// Remove blocks without updates (trick VS2 to prevent unintended ship splitting)
		for (BlockPos pos : positions) {
			ChunkPos chunk = new ChunkPos(pos);
			BlockPos cpos = pos.subtract(new BlockPos(chunk.getMinBlockX(), 0, chunk.getMinBlockZ()));
			level.getChunk(pos).setBlockState(cpos, air, false);
		}

		// Notify removed blocks to update ship bounds
		for (BlockPos pos : positions) {
			ContraptionUtility.triggerBlockChange(level, pos, blk, air);
		}
		
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
