package de.m_marvin.industria.core.compound.types.blocks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock;
import de.m_marvin.industria.core.magnetism.MagnetismUtility;
import de.m_marvin.industria.core.magnetism.types.blocks.IMagneticBlock;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.types.StateTransform;
import de.m_marvin.industria.core.util.virtualblock.VirtualBlock;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

public class CompoundBlock extends BaseEntityBlock implements IKineticBlock, IMagneticBlock {
	
	public static final EnumProperty<StateTransform> TRANSFORM = Blocks.PROP_TRANSFORM;
	
	public CompoundBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(TRANSFORM);
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(TRANSFORM, StateTransform.of(pRotation));
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(TRANSFORM, StateTransform.of(pMirror));
	}
	
	@Override
	public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity blockEntity) {
			blockEntity.applyTransform();
		}
		super.onBlockStateChange(level, pos, oldState, newState);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return Shapes.empty(); 
		// This makes the block act transparent even if it has a full block hit-box
		// This is purely visual
	}

	@Override
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity blockEntity) {
			return blockEntity.getTransmissionNodes();
		}
		return new TransmissionNode[0];
	}

	@Override
	public BlockState getPartState(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity blockEntity) {
			return blockEntity.getPartState(partId);
		}
		return state;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new CompoundBlockEntity(pPos, pState);
	}
	
	/* Helper functions for block redirects */
	
	public static <T> T performOnTargetedAndReturn(BlockGetter level, BlockPos pos, Player player, Supplier<T> fallback, BiFunction<CompoundBlockEntity, VirtualBlock, T> action) {
		if (level == null) return fallback.get();
		ClipContext clip = MathUtility.getPlayerPOVClipContext(level, player, Fluid.ANY, player.getBlockReach());
		BlockHitResult hit = level.clip(clip);
		if (hit.getType() == Type.MISS) return fallback.get();
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
			for (VirtualBlock part : compound.getParts().values()) {
				BlockHitResult hit2 = part.getLevel().clip(clip);
				if (hit2 != null && hit2.getType() != Type.MISS && hit.getLocation().distanceTo(hit2.getLocation()) < 0.01) {
					return action.apply(compound, part);
				}
			}
		}
		return fallback.get();
	}

	public static boolean performOnTargeted(BlockGetter level, BlockPos pos, Player player, BiConsumer<CompoundBlockEntity, VirtualBlock> action) {
		if (level == null) return false;
		ClipContext clip = MathUtility.getPlayerPOVClipContext(level, player, Fluid.ANY, player.getBlockReach());
		BlockHitResult hit = level.clip(clip);
		if (hit.getType() == Type.MISS) return false;
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
			for (VirtualBlock part : compound.getParts().values()) {
				BlockHitResult hit2 = part.getLevel().clip(clip);
				if (hit2 != null && hit2.getType() != Type.MISS && hit.getLocation().distanceTo(hit2.getLocation()) < 0.01) {
					action.accept(compound, part);
					return true;
				}
			}
		}
		return false;
	}
	
	public static <T> T performOnAllAndCombine(BlockGetter level, BlockPos pos, Supplier<T> fallback, BiFunction<CompoundBlockEntity, VirtualBlock, T> action, BinaryOperator<T> accumulator) {
		if (level == null) return fallback.get();
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
			Optional<T> result = compound.getParts().values().stream()
				.map(p -> action.apply(compound, p))
				.reduce(accumulator);
			return result.orElseGet(fallback);
		}
		return fallback.get();
	}

	public static <T> T performOnAllAndChoseCommon(BlockGetter level, BlockPos pos, Supplier<T> fallback, BiFunction<CompoundBlockEntity, VirtualBlock, T> action) {
		if (level == null) return fallback.get();
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
			Optional<Entry<T, Long>> result = compound.getParts().values().stream()
				.map(p -> action.apply(compound, p))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.max(Map.Entry.comparingByValue());
			return result.isPresent() ? result.get().getKey() : fallback.get();
		}
		return fallback.get();
	}
	
	public static void performOnAll(BlockGetter level, BlockPos pos, BiConsumer<CompoundBlockEntity, VirtualBlock> action) {
		if (level == null) return;
		if (level.getBlockEntity(pos) instanceof CompoundBlockEntity compound) {
			compound.getParts().values()
				.forEach(p -> action.accept(compound, p));
		}
	}
	
	public static boolean trueIfAny(boolean a, boolean b) {
		return a || b;
	}
	
	public static boolean trueIfAll(boolean a, boolean b) {
		return a && b;
	}
	
	public static int sumInt(int a, int b) {
		return a + b;
	}
	
	public static float sumFloat(float a, float b) {
		return a + b;
	}

	public static float maxFloat(float a, float b) {
		return a > b ? a : b;
	}
	
	public static double sumDouble(double a, double b) {
		return a + b;
	}
	
	/* Block Function Redirects  */

	@SuppressWarnings("resource")
	@Override
	public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		if (!performOnTargeted(pLevel, pPos, pPlayer, 
				(compound, part) -> part.getBlock().playerWillDestroy(part.getLevel(), part.getPos(), part.getState(), pPlayer)))
		super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}
	
	@SuppressWarnings("resource")
	@Override
	public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity,ItemStack pTool) {
		if (!performOnTargeted(pLevel, pPos, pPlayer, 
				(compound, part) -> part.getBlock().playerDestroy(part.getLevel(), pPlayer, part.getPos(), part.getState(), part.getBlockEntity(), pTool)))
		super.playerDestroy(pLevel, pPlayer, pPos, pState, pBlockEntity, pTool);
	}

	@SuppressWarnings("resource")
	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getState().getBlock().animateTick(p.getState(), p.getLevel(), p.getPos(), pRandom));
	}
	
	@SuppressWarnings("resource")
	@Override
	public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getState().entityInside(p.getLevel(), p.getPos(), pEntity));
	}

	@SuppressWarnings("resource")
	@Override
	public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
		performOnAll(level, pos, 
				(compound, p) -> p.getBlock().onBlockExploded(p.getState(), p.getLevel(), p.getPos(), explosion));
	}
	
	@SuppressWarnings("resource")
	@Override
	public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
		performOnAll(level, pos, 
				(compound, p) -> p.getBlock().onCaughtFire(p.getState(), p.getLevel(), p.getPos(), direction, igniter));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateIndirectNeighbourShapes(BlockState pState, LevelAccessor pLevel, BlockPos pPos, int pFlags, int pRecursionLeft) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getBlock().updateIndirectNeighbourShapes(p.getState(), p.getLevel(), p.getPos(), pFlags, pRecursionLeft));
	}
	
	@SuppressWarnings("resource")
	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getBlock().stepOn(p.getLevel(), p.getPos(), p.getState(), pEntity));
	}
	
	@SuppressWarnings("resource")
	@Override
	public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getBlock().wasExploded(p.getLevel(), p.getPos(), pExplosion));
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		performOnAll(level, pos, 
				(compound, p) -> p.getBlock().onNeighborChange(p.getState(), p.getLevel(), p.getPos(), neighbor));
	}

	@Override
	public void onInductionNotify(Level level, BlockState state, BlockPos pos, Vec3d inductionVector) {
		performOnAll(level, pos, 
				(compound, p) -> {
					if (p.getBlock() instanceof IMagneticBlock magnetic)
						magnetic.onInductionNotify(p.getLevel(), p.getState(), p.getPos(), inductionVector);
				});
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult hit, Projectile pProjectile) {
		performOnAll(pLevel, hit.getBlockPos(), 
				(compound, p) -> p.getState().onProjectileHit(p.getLevel(), p.getState(), hit, pProjectile));
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		performOnTargeted(pLevel, pPos, pPlayer, 
				(compound, part) -> part.getBlock().attack(part.getState(), part.getLevel(), part.getPos(), pPlayer));
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		performOnAll(pLevel, pPos, 
				(compound, part) -> part.getBlock().onRemove(part.getState(), part.getLevel(), part.getPos(), pNewState.is(this) ? part.getState() : pNewState, pMovedByPiston));
		super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getBlock().neighborChanged(p.getState(), p.getLevel(), p.getPos(), pNeighborBlock, pNeighborPos, pMovedByPiston));
	}

	@SuppressWarnings("resource")
	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getState().randomTick((ServerLevel) p.getLevel(), p.getPos(), pRandom));
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pState.getValue(TRANSFORM) != StateTransform.NONE)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(TRANSFORM, StateTransform.NONE));
		performOnAll(pLevel, pPos, 
				(compound, p) -> p.getBlock().tick(p.getState(), (ServerLevel) p.getLevel(), p.getPos(), pRandom));
	}

	@SuppressWarnings("resource")
	@Override
	public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return performOnAllAndCombine(level, pos, 
				() -> super.addLandingEffects(state1, level, pos, state2, entity, numberOfParticles), 
				(compound, p) -> p.getState().addLandingEffects((ServerLevel) p.getLevel(), p.getPos(), state2, entity, numberOfParticles), 
				CompoundBlock::trueIfAny);
	}
	
	@SuppressWarnings("resource")
	@Override
	public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
		return performOnAllAndCombine(level, pos, 
				() -> super.addRunningEffects(state, level, pos, entity),
				(compound, p) -> p.getState().addRunningEffects(p.getLevel(), p.getPos(), entity), 
				CompoundBlock::trueIfAny);
	}

	@Override
	public boolean canBeHydrated(BlockState state, BlockGetter getter, BlockPos pos, FluidState fluid, BlockPos fluidPos) {
		return performOnAllAndCombine(getter, pos, 
				() -> super.canBeHydrated(state, getter, pos, fluid, fluidPos), 
				(compound, p) -> p.getState().canBeHydrated(p.getLevel(), p.getPos(), fluid, fluidPos), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return performOnAllAndCombine(level, pos, 
				() -> super.canConnectRedstone(state, level, pos, direction), 
				(compound, p) -> p.getState().canRedstoneConnectTo(p.getLevel(), p.getPos(), direction), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		return performOnAllAndCombine(level, pos, 
				() -> super.canDropFromExplosion(state, level, pos, explosion), 
				(compound, p) -> p.getState().canDropFromExplosion(p.getLevel(), p.getPos(), explosion), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		return performOnAllAndCombine(level, pos, 
				() -> super.canEntityDestroy(state, level, pos, entity), 
				(compound, p) -> p.getState().canEntityDestroy(p.getLevel(), p.getPos(), entity), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return performOnAllAndCombine(level, pos, 
				() -> super.collisionExtendsVertically(state, level, pos, collidingEntity), 
				(compound, p) -> p.getState().collisionExtendsVertically(p.getLevel(), p.getPos(), collidingEntity), 
				CompoundBlock::trueIfAny);
	}

	@Override
	public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
		return performOnAllAndCombine(level, pos, 
				() -> super.getWeakChanges(state, level, pos), 
				(compound, p) -> p.getState().getWeakChanges(p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		return performOnAllAndCombine(level, pos, 
				() -> super.hidesNeighborFace(level, pos, state, neighborState, dir), 
				(compound, p) -> p.getState().hidesNeighborFace(p.getLevel(), p.getPos(), neighborState, dir), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isBurning(state, level, pos), 
				(compound, p) -> p.getState().isBurning(p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isConduitFrame(state, level, pos, conduit), 
				(compound, p) -> p.getState().isConduitFrame(p.getLevel(), p.getPos(), conduit), 
				CompoundBlock::trueIfAny);
	}
	@Override
	public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isFertile(state, level, pos), 
				(compound, p) -> p.getState().isFertile(p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}
	@Override
	public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isFireSource(state, level, pos, direction), 
				(compound, p) -> p.getState().isFireSource(p.getLevel(), p.getPos(), direction), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isFlammable(state, level, pos, direction), 
				(compound, p) -> p.getState().isFlammable(p.getLevel(), p.getPos(), direction), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isLadder(state, level, pos, entity), 
				(compound, p) -> p.getState().isLadder(p.getLevel(), p.getPos(), entity), 
				CompoundBlock::trueIfAny);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isOcclusionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.isOcclusionShapeFullBlock(pState, pLevel, pPos), 
				(compound, p) -> p.getBlock().isOcclusionShapeFullBlock(p.getState(), p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.isPathfindable(pState, pLevel, pPos, pType), 
				(compound, p) -> p.getState().isPathfindable(p.getLevel(), p.getPos(), pType), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isPortalFrame(state, level, pos), 
				(compound, p) -> p.getState().isPortalFrame(p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}
	
	@Override
	public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return performOnAllAndCombine(level, pos, 
				() -> super.isScaffolding(state, level, pos, entity), 
				(compound, p) -> p.getBlock().isScaffolding(p.getState(), p.getLevel(), p.getPos(), entity), 
				CompoundBlock::trueIfAny);

	}
	
	@Override
	public boolean isRandomlyTicking(BlockState pState) {
		return true; // There is now way to check the child blocks, so we assume some need random ticks
	}

	@Override
	public boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return performOnAllAndCombine(level, pos, 
				() -> super.makesOpenTrapdoorAboveClimbable(state, level, pos, trapdoorState), 
				(compound, p) -> p.getBlock().makesOpenTrapdoorAboveClimbable(p.getState(), p.getLevel(), p.getPos(), trapdoorState), 
				CompoundBlock::trueIfAny);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.canSurvive(pState, pLevel, pPos), 
				(compound, p) -> p.getBlock().canSurvive(p.getState(), p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAll);
	}
	
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return performOnAllAndCombine(world, pos, 
				() -> super.canSustainPlant(state, world, pos, facing, plantable), 
				(compound, p) -> p.getBlock().canSustainPlant(p.getState(), p.getLevel(), p.getPos(), facing, plantable), 
				CompoundBlock::trueIfAll);
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.triggerEvent(pState, pLevel, pPos, pId, pParam), 
				(compound, p) -> p.getBlock().triggerEvent(p.getState(), p.getLevel(), p.getPos(), pId, pParam), 
				CompoundBlock::trueIfAny);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> true, 
				(compound, p) -> p.getBlock().propagatesSkylightDown(p.getState(), p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.isCollisionShapeFullBlock(pState, pLevel, pPos), 
				(compound, p) -> p.getBlock().isCollisionShapeFullBlock(p.getState(), p.getLevel(), p.getPos()), 
				CompoundBlock::trueIfAny);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		return performOnTargetedAndReturn(level, pos, player, 
				() -> super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid), 
				(compound, part) -> {
					/* Perform the removal and drop spawning of the block and its block entity manually here
					 * Otherwise it would cause the compound block to convert into an normal
					 * Block before the drops have been spawned.
					 */
					Block bblock = part.getBlock();
					BlockState bstate = part.getState();
					BlockEntity bentity = part.getBlockEntity();
					if (part.getBlock().onDestroyedByPlayer(part.getState(), part.getLevel(), part.getPos(), player, willHarvest, fluid) && !player.isCreative())
						bblock.playerDestroy(part.getLevel(), player, part.getPos(), bstate, bentity, player.getMainHandItem());
					if (compound.isEmpty()) {
						level.removeBlock(pos, false);
					}
					return false;
				});
	}
	
	@Override
	public boolean isSignalSource(BlockState pState) {
		return true;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return performOnTargetedAndReturn(level, pos, player, 
				() -> super.canHarvestBlock(state, level, pos, player), 
				(compound, part) -> part.getBlock().canHarvestBlock(part.getState(), part.getLevel(), part.getPos(), player));
	}
	
	@Override
	public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return performOnAllAndCombine(level, pos, 
				() -> super.getExpDrop(state, level, randomSource, pos, fortuneLevel, silkTouchLevel), 
				(compound, p) -> p.getState().getExpDrop(p.getLevel(), randomSource, p.getPos(), fortuneLevel, silkTouchLevel), 
				CompoundBlock::sumInt);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return Math.min(15, performOnAllAndCombine(level, pos, 
				() -> super.getLightEmission(state, level, pos), 
				(compound, p) -> p.getState().getLightBlock(p.getLevel(), p.getPos()), 
				CompoundBlock::sumInt));
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
		return Math.min(15, performOnAllAndCombine(pLevel, pPos, 
				() -> super.getSignal(pState, pLevel, pPos, pDirection), 
				(compound, p) -> p.getBlock().getSignal(p.getState(), p.getLevel(), p.getPos(), pDirection), 
				CompoundBlock::sumInt));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int getDirectSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
		return Math.min(15, performOnAllAndCombine(pLevel, pPos, 
				() -> super.getDirectSignal(pState, pLevel, pPos, pDirection), 
				(compound, p) -> p.getBlock().getDirectSignal(p.getState(), p.getLevel(), p.getPos(), pDirection), 
				CompoundBlock::sumInt));
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return Math.min(15, performOnAllAndCombine(pLevel, pPos, 
				() -> super.getAnalogOutputSignal(pState, pLevel, pPos), 
				(compound, p) -> p.getBlock().getAnalogOutputSignal(p.getState(), p.getLevel(), p.getPos()), 
				CompoundBlock::sumInt));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
		return performOnTargetedAndReturn(pLevel, pPos, pPlayer, 
				() -> super.getDestroyProgress(pState, pPlayer, pLevel, pPos), 
				(compound, part) -> part.getBlock().getDestroyProgress(part.getState(), pPlayer, part.getLevel(), part.getPos()));
	}

	@Override
	public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		return performOnAllAndCombine(level, pos, 
				() -> super.getExplosionResistance(state, level, pos, explosion),
				(compound, p) -> p.getBlock().getExplosionResistance(state, level, pos, explosion), 
				CompoundBlock::maxFloat);
	}
	
	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
		return performOnAllAndCombine(level, pos, 
				() -> super.getEnchantPowerBonus(state, level, pos),
				(compound, p) -> p.getState().getEnchantPowerBonus(p.getLevel(), p.getPos()), 
				CompoundBlock::sumFloat);
	}
	
	@Override
	public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return performOnAllAndCombine(level, pos, 
				() -> super.getFriction(state, level, pos, entity),
				(compound, p) -> p.getState().getFriction(p.getLevel(), p.getPos(), entity) / (float) compound.getParts().size(), 
				CompoundBlock::sumFloat);
	}

	@SuppressWarnings("resource")
	@Override
	public double getCoefficient(Level level, BlockState state, BlockPos pos) {
		return performOnAllAndCombine(level, pos, 
				() -> 0.0, 
				(compound, p) -> MagnetismUtility.getBlockCoefficient(p.getLevel(), p.getState(), p.getPos()) / compound.getParts().size(), 
				CompoundBlock::sumDouble);
	}

	@Override
	public @Nullable float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return performOnAllAndCombine(level, beaconPos, 
				() -> super.getBeaconColorMultiplier(state, level, pos, beaconPos), 
				(compound, p) -> p.getState().getBeaconColorMultiplier(level, pos, beaconPos), 
				(a, b) -> {
					if (a == null) return b;
					if (b == null) return a;
					for (int i = 0; i < a.length; i++)
						a[i] *= b[i];
					return a;
				});
	}

	@SuppressWarnings("resource")
	@Override
	public Vec3d getFieldVector(Level level, BlockState state, BlockPos blockPos) {
		return performOnAllAndCombine(level, blockPos, 
				() -> new Vec3d(0, 0, 0), 
				(compound, p) -> MagnetismUtility.getBlockField(p.getLevel(), p.getState(), p.getPos()), 
				(a, b) -> a.add(b));
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return performOnAllAndChoseCommon(level, pos, 
				() -> super.getSoundType(state, level, pos, entity), 
				(compound, p) -> p.getState().getSoundType(p.getLevel(), p.getPos(), entity));
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return performOnTargetedAndReturn(level, pos, player, 
				() -> super.getCloneItemStack(state, target, level, pos, player), 
				(compound, part) -> part.getBlock().getCloneItemStack(part.getState(), target, part.getLevel(), part.getPos(), player));
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return performOnTargetedAndReturn(pLevel, pPos, pPlayer, 
				() -> super.use(pState, pLevel, pPos, pPlayer, pHand, pHit), 
				(compound, part) -> part.getBlock().use(part.getState(), part.getLevel(), part.getPos(), pPlayer, pHand, pHit));
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.getBlockSupportShape(pState, pLevel, pPos), 
				(compound, p) -> p.getBlock().getBlockSupportShape(p.getState(), p.getLevel(), p.getPos()), 
				Shapes::or);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.getInteractionShape(pState, pLevel, pPos), 
				(compound, p) -> p.getBlock().getInteractionShape(p.getState(), p.getLevel(), p.getPos()), 
				Shapes::or);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.getShape(pState, pLevel, pPos, pContext), 
				(compound, p) -> p.getBlock().getShape(p.getState(), p.getLevel(), p.getPos(), pContext), 
				Shapes::or);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return performOnAllAndCombine(pLevel, pPos, 
				() -> super.getCollisionShape(pState, pLevel, pPos, pContext), 
				(compound, p) -> p.getBlock().getCollisionShape(p.getState(), p.getLevel(), p.getPos(), pContext), 
				Shapes::or);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {		return performOnAllAndCombine(pLevel, pPos, 
			() -> super.getVisualShape(pState, pLevel, pPos, pContext), 
			(compound, p) -> p.getBlock().getVisualShape(p.getState(), p.getLevel(), p.getPos(), pContext), 
			Shapes::or);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState pState, net.minecraft.world.level.storage.loot.LootParams.Builder pParams) {
		BlockEntity blockEntity = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof CompoundBlockEntity compound) {
			return compound.getParts().values().stream()
					.flatMap(part -> {
						LootParams.Builder lootparams$builder = (new LootParams.Builder(pParams.getLevel()))
								.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(compound.getBlockPos()))
								.withParameter(LootContextParams.BLOCK_STATE, part.getState())
								.withOptionalParameter(LootContextParams.BLOCK_ENTITY, part.getBlockEntity())
								.withOptionalParameter(LootContextParams.THIS_ENTITY, pParams.getOptionalParameter(LootContextParams.THIS_ENTITY))
								.withParameter(LootContextParams.TOOL, pParams.getParameter(LootContextParams.TOOL));
						return part.getState().getDrops(lootparams$builder).stream();
					})
					.toList();
		}
		return super.getDrops(pState, pParams);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		performOnAll(pLevel, pPos, 
				(compound, p) -> {
					BlockState state = p.getState().updateShape(pDirection, pNeighborState, p.getLevel(), p.getPos(), pNeighborPos);
					if (state.equals(p.getState())) return;
					if (state.isAir()) {
						p.getLevel().destroyBlock(p.getPos(), true);
					} else {
						p.getLevel().setBlock(p.getPos(), state, 3);
					}
				});
		return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
	}
	
}
