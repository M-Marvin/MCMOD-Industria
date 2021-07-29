package de.industria.entity;

import java.util.Optional;

import de.industria.typeregistys.ModEntityType;
import de.industria.util.handler.FluidTankHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityFallingFluid extends Entity {
	
	private FluidState fluidState = Fluids.WATER.defaultFluidState();
	public int time;
	protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(EntityFallingFluid.class, DataSerializers.BLOCK_POS);
	protected static final DataParameter<Optional<BlockState>> FALLING_STATE = EntityDataManager.defineId(EntityFallingFluid.class, DataSerializers.BLOCK_STATE);
	
	public EntityFallingFluid(World world, double posX, double posY, double posZ, FluidState fluidState) {
		this(ModEntityType.FALLING_FLUID, world);
		setFluidState(fluidState);
		this.blocksBuilding = true;
		this.setPos(posX, posY + (double)((1.0F - this.getBbHeight()) / 2.0F), posZ);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = posX;
		this.yo = posY;
		this.zo = posZ;
		this.setStartPos(this.blockPosition());
	}
	
	/**
	 * Prevents the entity for checking valid spawn position by checking the replaced block.
	 */
	public void setAirSpawned() {
		this.time = 1;
	}
	
	public EntityFallingFluid(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public boolean isAttackable() {
		return false;
	}
	
	public void setFluidState(FluidState fluidState) {
		this.fluidState = fluidState;
		this.entityData.set(FALLING_STATE, Optional.of(fluidState.createLegacyBlock()));
	}
	
	public void setStartPos(BlockPos p_184530_1_) {
		this.entityData.set(DATA_START_POS, p_184530_1_);
	}
	
	@OnlyIn(Dist.CLIENT)
	public BlockPos getStartPos() {
		return this.entityData.get(DATA_START_POS);
	}
	
	protected boolean isMovementNoisy() {
		return false;
	}
	
	protected void defineSynchedData() {
		this.entityData.define(DATA_START_POS, BlockPos.ZERO);
		this.entityData.define(FALLING_STATE, Optional.of(Blocks.LAVA.defaultBlockState()));
	}
	
	@SuppressWarnings("deprecation")
	public boolean isPickable() {
		return !this.removed;
	}
	
	@Override
	public void tick() {
		
		if (this.fluidState.isEmpty()) {
			this.remove();
		} else {
			
			Fluid fluid = this.fluidState.getType();
			if (this.time++ == 0) {
				BlockPos blockpos = this.blockPosition();
				if (this.level.getFluidState(blockpos).getType() == fluid) {
					this.level.removeBlock(blockpos, false);
				} else if (!this.level.isClientSide) {
					this.remove();
					return;
				}
			}

			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
			}
			
			this.move(MoverType.SELF, this.getDeltaMovement());
			if (!this.level.isClientSide) {
				BlockPos blockpos1 = this.blockPosition();
				BlockState blockstate1 = this.level.getBlockState(blockpos1);
				
				if (!this.onGround && blockstate1.getFluidState().isEmpty()) {
					if (!this.level.isClientSide && (this.time > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.time > 600)) {
						this.remove();
					}
				} else {
					
					BlockPos blockpos2 = blockstate1.getFluidState().isEmpty() ? blockpos1 : blockpos1.above();
					
					@SuppressWarnings("unused")
					BlockState blockstate = this.level.getBlockState(blockpos2);
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
					
					FluidTankHelper tankHelper = new FluidTankHelper(this.level, blockpos2);
					BlockPos insertPos = tankHelper.insertFluidInTank(this.fluidState.getType());
					
					if (insertPos != null) {
						
						this.level.setBlockAndUpdate(insertPos, this.fluidState.createLegacyBlock());
						
					}
					
					this.remove();
					
				}
			} else {
				
				FluidState fluidState = this.getFluidState();
				IParticleData particles = new BlockParticleData(ParticleTypes.BLOCK, fluidState.createLegacyBlock());
				
				for (int i = 0; i < 10; i++) {

					float ox = this.level.getRandom().nextFloat() - 0.5F;
					float oy = this.level.getRandom().nextFloat() - 0.5F + 1;
					float oz = this.level.getRandom().nextFloat() - 0.5F;
					
					this.level.addParticle(particles, this.position().x + ox, this.position().y + oy, this.position().z + oz, 0, -2.1F, 0);
					
				}
				
			}

			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
			
		}
		
	}
	
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		nbt.put("FluidState", NBTUtil.writeBlockState(this.fluidState.createLegacyBlock()));
		nbt.putInt("Time", this.time);
	}
	
	protected void readAdditionalSaveData(CompoundNBT nbt) {
		this.setFluidState(NBTUtil.readBlockState(nbt.getCompound("FluidState")).getFluidState());;
		this.time = nbt.getInt("Time");
		if (this.fluidState.isEmpty()) {
			this.setFluidState(Fluids.WATER.defaultFluidState());;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public World getLevel() {
		return this.level;
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean displayFireAnimation() {
		return false;
	}

	public void fillCrashReportCategory(CrashReportCategory p_85029_1_) {
		super.fillCrashReportCategory(p_85029_1_);
		p_85029_1_.setDetail("Immitating FluidState", this.fluidState.toString());
	}
	
	public FluidState getFluidState() {
		if (this.level.isClientSide()) {
			Optional<BlockState> dataState = this.entityData.get(FALLING_STATE);
			if (dataState.isPresent()) {
				return dataState.get().getFluidState();
			}
		}
		return this.fluidState;
	}
	
	public boolean onlyOpCanSetNbt() {
		return true;
	}

	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
}
