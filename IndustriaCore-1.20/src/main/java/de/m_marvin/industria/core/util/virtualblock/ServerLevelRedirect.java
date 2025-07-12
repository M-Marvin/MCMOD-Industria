package de.m_marvin.industria.core.util.virtualblock;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.TheUnsafeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

class ServerLevelRedirect extends ServerLevel {
	
	private ServerLevelRedirect() {
		/* This makes it impossible to construct the level using the normal constructor, 
		 * as this would break the game by calling the ServerLevel constructor! */
		super(null, null, null, null, null, null, null, false, 0, null, false, null);
		this.level = null;
		this.block = null;
	}
	
	public static ServerLevelRedirect newRedirect(VirtualBlock virtualBlock, Level level) {
		try {
			/* WARNING unsafe code section, this code operates directly on the native memory, 
			 * bypassing any java restrictions */
			ServerLevelRedirect redirect = TheUnsafeUtility.securedAllocateNewInstance(ServerLevelRedirect.class);
			TheUnsafeUtility.securedCopyClassFields(ServerLevel.class, level, redirect);
			TheUnsafeUtility.securedWriteField(ServerLevelRedirect.class.getDeclaredField("block"), redirect, virtualBlock);
			TheUnsafeUtility.securedWriteField(ServerLevelRedirect.class.getDeclaredField("level"), redirect, level);
			return redirect;
		} catch (InstantiationException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Failed to construct ServerLevelRedirect using Unsafe!", e);
		}
	}
	
	private VirtualBlock block;
	private ServerLevel level;
	
	@Override
	public String toString() {
		return "Virtual" + this.level.toString();
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return level.getCapability(cap, side);
	}
	
	@Override
	public BlockEntity getBlockEntity(BlockPos pPos) {
		if (pPos.equals(block.getPos())) 
			return block.blockEntity;
		return level.getBlockEntity(pPos);
	}

	@Override
	public BlockState getBlockState(BlockPos p_45571_) {
		if (p_45571_.equals(block.getPos()))
			return block.state;
		return level.getBlockState(p_45571_);
	}

	@Override
	public boolean isStateAtPosition(BlockPos pPos, Predicate<BlockState> pState) {
		if (pPos.equals(block.getPos()))
			return pState.test(block.state);
		return level.isStateAtPosition(pPos, pState);
	}

	@Override
	public boolean setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft) {
		if (pPos.equals(block.getPos())) {
			BlockState oldState = block.getState();
			block.setBlock(pState);
			oldState.onRemove(this, pPos, pState, false);
			if ((pFlags & 1) > 0) GameUtility.triggerUpdate(level, pPos);
			return true;
		}
		return level.setBlock(pPos, pState, pFlags);
	}
	
	@Override
	public void removeBlockEntity(BlockPos pPos) {
		if (pPos.equals(block.getPos())) {
			block.setBlockEntity(null);
			return;
		}
		level.removeBlockEntity(pPos);
	}

	@Override
	public void setBlockEntity(BlockEntity pBlockEntity) {
		if (pBlockEntity.getBlockPos().equals(block.getPos())) {
			block.setBlockEntity(pBlockEntity);
			return;
		}
		level.setBlockEntity(pBlockEntity);
	}
	
	@Override
	public void addBlockEntityTicker(TickingBlockEntity pTicker) {}
	
	@Override
	public void addFreshBlockEntities(Collection<BlockEntity> beList) {
		level.addFreshBlockEntities(beList);
	}
	
	@Override
	public boolean addFreshEntity(Entity pEntity) {
		return level.addFreshEntity(pEntity);
	}
	
	@Override
	public ProfilerFiller getProfiler() {
		return level.getProfiler();
	}
	
	@Override
	public Supplier<ProfilerFiller> getProfilerSupplier() {
		return level.getProfilerSupplier();
	}
	
	@Override
	public int getMaxBuildHeight() {
		return level.getMaxBuildHeight();
	}
	
	@Override
	public double getMaxEntityRadius() {
		return level.getMaxEntityRadius();
	}
	
	@Override
	public int getMaxLightLevel() {
		return level.getMaxLightLevel();
	}

	@Override
	public DamageSources damageSources() {
		return level.damageSources();
	}
	
	@Override
	public boolean destroyBlock(BlockPos pPos, boolean pDropBlock, Entity pEntity, int pRecursionLeft) {
		
		if (pPos.equals(block.getPos())) {
			/* copied from Level class to call redirected methods */
			BlockState blockstate = this.getBlockState(pPos);
			if (blockstate.isAir()) {
				return false;
			} else {
				FluidState fluidstate = this.getFluidState(pPos);
				if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
					this.levelEvent(2001, pPos, Block.getId(blockstate));
				}

				if (pDropBlock) {
					BlockEntity blockentity = blockstate.hasBlockEntity() ? this.getBlockEntity(pPos) : null;
					Block.dropResources(blockstate, level, pPos, blockentity, pEntity, ItemStack.EMPTY);
				}

				boolean flag = this.setBlock(pPos, fluidstate.createLegacyBlock(), 3, pRecursionLeft);
				if (flag) {
					this.gameEvent(GameEvent.BLOCK_DESTROY, pPos, GameEvent.Context.of(pEntity, blockstate));
				}

				return flag;
			}
		}
		
		return level.destroyBlock(pPos, pDropBlock, pEntity, pRecursionLeft);
	}

	@Override
	public FluidState getFluidState(BlockPos pPos) {
		return level.getFluidState(pPos);
	}

	@Override
	public boolean isFluidAtPosition(BlockPos pPos, Predicate<FluidState> pPredicate) {
		return level.isFluidAtPosition(pPos, pPredicate);
	}

	@Override
	public List<Entity> getEntities(Entity pEntity, AABB pArea, Predicate<? super Entity> pPredicate) {
		return level.getEntities(pEntity, pArea, pPredicate);
	}

	@Override
	public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> pEntityTypeTest, AABB pBounds,
			Predicate<? super T> pPredicate) {
		return level.getEntities(pEntityTypeTest, pBounds, pPredicate);
	}

	@Override
	public List<ServerPlayer> players() {
		return level.players();
	}

	@Override
	public ChunkAccess getChunk(int pX, int pZ, ChunkStatus pRequiredStatus, boolean pNonnull) {
		return level.getChunk(pX, pZ, pRequiredStatus, pNonnull);
	}

	@Override
	public int getHeight(Types pHeightmapType, int pX, int pZ) {
		return level.getHeight(pHeightmapType, pX, pZ);
	}

	@Override
	public int getSkyDarken() {
		return level.getSkyDarken();
	}

	@Override
	public BiomeManager getBiomeManager() {
		return level.getBiomeManager();
	}

	@Override
	public Holder<Biome> getUncachedNoiseBiome(int pX, int pY, int pZ) {
		return level.getUncachedNoiseBiome(pX, pY, pZ);
	}

	@Override
	public boolean isClientSide() {
		return level.isClientSide();
	}

	@Override
	public int getSeaLevel() {
		return level.getSeaLevel();
	}

	@Override
	public DimensionType dimensionType() {
		return level.dimensionType();
	}
	
	@Override
	public ResourceKey<DimensionType> dimensionTypeId() {
		return level.dimensionTypeId();
	}

	@Override
	public Holder<DimensionType> dimensionTypeRegistration() {
		return level.dimensionTypeRegistration();
	}
	
	@Override
	public ResourceKey<Level> dimension() {
		return level.dimension();
	}
	
	@Override
	public RegistryAccess registryAccess() {
		return level.registryAccess();
	}

	@Override
	public FeatureFlagSet enabledFeatures() {
		return level.enabledFeatures();
	}

	@Override
	public float getShade(Direction pDirection, boolean pShade) {
		return level.getShade(pDirection, pShade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return level.getLightEngine();
	}

	@Override
	public WorldBorder getWorldBorder() {
		return level.getWorldBorder();
	}

	@Override
	public long nextSubTickCount() {
		return level.nextSubTickCount();
	}

	@Override
	public LevelTicks<Block> getBlockTicks() {
		return level.getBlockTicks();
	}

	@Override
	public LevelTicks<Fluid> getFluidTicks() {
		return level.getFluidTicks();
	}

	private <T> ScheduledTick<T> createTick(BlockPos pPos, T pType, int pDelay, TickPriority pPriority) {
		return new ScheduledTick<>(pType, pPos, this.getLevelData().getGameTime() + (long)pDelay, pPriority, this.nextSubTickCount());
	}
	
	private <T> ScheduledTick<T> createTick(BlockPos pPos, T pType, int pDelay) {
		return new ScheduledTick<>(pType, pPos, this.getLevelData().getGameTime() + (long)pDelay, this.nextSubTickCount());
	}

	@Override
	public void scheduleTick(BlockPos pPos, Block pBlock, int pDelay, TickPriority pPriority) {
		this.getBlockTicks().schedule(createTick(pPos, pPos.equals(block.getPos()) ? level.getBlockState(pPos).getBlock() : pBlock, pDelay, pPriority));
	}

	@Override
	public void scheduleTick(BlockPos pPos, Block pBlock, int pDelay) {
		this.getBlockTicks().schedule(createTick(pPos, pPos.equals(block.getPos()) ? level.getBlockState(pPos).getBlock() : pBlock, pDelay));
	}
	
	@Override
	public void sendBlockUpdated(BlockPos pPos, BlockState pOldState, BlockState pNewState, int pFlags) {
		level.sendBlockUpdated(pPos, pOldState, pNewState, pFlags);
	}

	@Override
	public void updateNeighborsAt(BlockPos pPos, Block pBlock) {
		level.updateNeighborsAt(pPos, pBlock);
	}
	
	@Override
	public void updateNeighborsAtExceptFromFacing(BlockPos pPos, Block pBlockType, Direction pSkipSide) {
		level.updateNeighborsAtExceptFromFacing(pPos, pBlockType, pSkipSide);
	}
	
	@Override
	public void neighborChanged(BlockPos pPos, Block pBlock, BlockPos pFromPos) {
		level.neighborChanged(pPos, pBlock, pFromPos);
	}

	@Override
	public void neighborShapeChanged(Direction pDirection, BlockState pQueried, BlockPos pPos, BlockPos pOffsetPos, int pFlags, int pRecursionLevel) {
		level.neighborShapeChanged(pDirection, pQueried, pPos, pOffsetPos, pFlags, pRecursionLevel);
	}
	
	@Override
	public void neighborChanged(BlockState pState, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
		level.neighborChanged(pState, pPos, pBlock, pFromPos, pIsMoving);
	}
	
	@Override
	public LevelData getLevelData() {
		return level.getLevelData();
	}

	@Override
	public GameRules getGameRules() {
		return level.getGameRules();
	}
	
	@Override
	public DifficultyInstance getCurrentDifficultyAt(BlockPos pPos) {
		return level.getCurrentDifficultyAt(pPos);
	}

	@Override
	public MinecraftServer getServer() {
		return level.getServer();
	}

	@Override
	public ServerChunkCache getChunkSource() {
		return level.getChunkSource();
	}

	@Override
	public RandomSource getRandom() {
		return level.getRandom();
	}
	
	@Override
	public void playSound(Player pPlayer, BlockPos pPos, SoundEvent pSound, SoundSource pSource, float pVolume,
			float pPitch) {
		level.playSound(pPlayer, pPos, pSound, pSource, pVolume, pPitch);
	}

	@Override
	public void addParticle(ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed,
			double pYSpeed, double pZSpeed) {
		level.addParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
	}

	@Override
	public void levelEvent(Player pPlayer, int pType, BlockPos pPos, int pData) {
		level.levelEvent(pPlayer, pType, pPos, pData);
	}

	@Override
	public void gameEvent(GameEvent pEvent, Vec3 pPosition, Context pContext) {
		level.gameEvent(pEvent, pPosition, pContext);
	}

	@Override
	public void blockEvent(BlockPos pPos, Block pBlock, int pEventID, int pEventParam) {
		level.blockEvent(pPos, pBlock, pEventID, pEventParam);
	}
	
	@Override
	public void playSeededSound(Player pPlayer, double pX, double pY, double pZ, Holder<SoundEvent> pSound,
			SoundSource pSource, float pVolume, float pPitch, long pSeed) {
		level.playSeededSound(pPlayer, pX, pY, pZ, pSound, pSource, pVolume, pPitch, pSeed);
	}

	@Override
	public void playSeededSound(Player pPlayer, Entity pEntity, Holder<SoundEvent> pSound, SoundSource pCategory,
			float pVolume, float pPitch, long pSeed) {
		level.playSeededSound(pPlayer, pEntity, pSound, pCategory, pVolume, pPitch, pSeed);
	}

	@Override
	public String gatherChunkSourceStats() {
		return level.gatherChunkSourceStats();
	}

	@Override
	public Entity getEntity(int pId) {
		return level.getEntity(pId);
	}

	@Override
	public MapItemSavedData getMapData(String pMapName) {
		return level.getMapData(pMapName);
	}

	@Override
	public void setMapData(String pMapName, MapItemSavedData pData) {
		level.setMapData(pMapName, pData);
	}

	@Override
	public int getFreeMapId() {
		return level.getFreeMapId();
	}

	@Override
	public void destroyBlockProgress(int pBreakerId, BlockPos pPos, int pProgress) {
		level.destroyBlockProgress(pBreakerId, pPos, pProgress);
	}

	@Override
	public ServerScoreboard getScoreboard() {
		return level.getScoreboard();
	}

	@Override
	public RecipeManager getRecipeManager() {
		return level.getRecipeManager();
	}

	@Override
	public LevelEntityGetter<Entity> getEntities() {
		return level.getEntities();
	}
	
}
