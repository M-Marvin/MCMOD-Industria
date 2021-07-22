package de.industria.tileentity;

import java.util.List;
import java.util.Optional;

import de.industria.blocks.BlockMBlender;
import de.industria.blocks.BlockMSchredder;
import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMBlender;
import de.industria.recipetypes.BlendingRecipe;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.FluidBucketHelper;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMBlender extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, ISidedInventory, INamedContainerProvider, IFluidConnective {
	
	// Client only
	public float rotation;
	public float lastPartial;
	
	public FluidStack fluidIn1;
	public FluidStack fluidIn2;
	public FluidStack fluidOut;
	public int progress;
	public int progressTotal;
	public float tankFillState;
	
	public boolean isWorking;
	public boolean hasPower;
	public final int maxFluidStorage;
	
	public TileEntityMBlender() {
		super(ModTileEntityType.BLENDER, 9);
		this.fluidIn1 = FluidStack.EMPTY;
		this.fluidIn2 = FluidStack.EMPTY;
		this.fluidOut = FluidStack.EMPTY;
		this.maxFluidStorage = 3000;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-4, -1, -4), worldPosition.offset(4, 4, 4));
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMBlender(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.blender");
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		if (ipos.getY() == 0) return new int[] {0, 1, 2};
		return new int[] {};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		if (getItem(index).getItem() == itemStackIn.getItem()) {
			return true;
		} else {
			for (int i = 0; i < 3; i++) {
				if (getItem(i).getItem() == itemStackIn.getItem()) return false;
			}
			return true;
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}

	@Override
	public void tick() {
		
		if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
			
			if (!this.level.isClientSide()) {
				
				this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
				ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
				
				this.fluidIn1 = FluidBucketHelper.transferBuckets(this, 3, this.fluidIn1, this.maxFluidStorage);
				this.fluidIn2 = FluidBucketHelper.transferBuckets(this, 5, this.fluidIn2, this.maxFluidStorage);
				this.fluidOut = FluidBucketHelper.transferBuckets(this, 7, this.fluidOut, this.maxFluidStorage);
				
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
				
				this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
				this.isWorking = this.hasPower ? this.canWork() : false;
				
				if (this.isWorking) {
					
					List<Entity> entitysInInput = getEntitysInInput();
					for (Entity entity : entitysInInput) {
						if (entity instanceof ItemEntity) {
							ItemStack item = ((ItemEntity) entity).getItem();
							for (int i = 0; i < 3; i++) {
								if (ItemStackHelper.canMergeRecipeStacks(this.getItem(i), item)) {
									if (this.getItem(i).isEmpty()) {
										this.setItem(i, item.copy());
									} else {
										this.getItem(i).grow(item.getCount());
									}
									entity.remove();
									break;
								}
							}
						} else if (entity instanceof LivingEntity) {
							((LivingEntity) entity).hurt(ModDamageSource.SCHREDDER, 0.5F);
						}
					}
					
					BlendingRecipe recipe = findRecipe();
					
					if (recipe != null) {

						if (this.tankFillState < 1F) {
							this.tankFillState += 0.02F;
						} else {
								
							if (ItemStackHelper.canMergeRecipeFluidStacks(this.fluidOut, recipe.getResultItemFluid(), this.maxFluidStorage)) {
								
								this.progressTotal = recipe.getMixingTime();
								this.progress++;
	
								if (this.progress > this.progressTotal) {
									
									this.progress = 0;
									
									for (ItemStack item : recipe.itemsIn) {
										for (int i = 0; i < 3; i++) {
											if (this.getItem(i).getItem() == item.getItem() && this.getItem(i).getCount() >= item.getCount()) {
												this.removeItem(i, item.getCount());
											}
										}
									}

									for (FluidStack fluid : recipe.fluidsIn) {
										for (int i = 0; i < 2; i++) {
											FluidStack fluidStorage = i == 0 ? this.fluidIn1 : this.fluidIn2;
											if (fluidStorage.getFluid() == fluid.getFluid() && fluidStorage.getAmount() >= fluid.getAmount()) {
												fluidStorage.shrink(fluid.getAmount());
											}
										}
									}
									
									if (this.fluidOut.isEmpty()) {
										this.fluidOut = recipe.getResultItemFluid();
									} else {
										this.fluidOut.grow(recipe.getResultItemFluid().getAmount());
									}
									
								}
								
							}
							
						}
						
					}
					
					
				} else {

					if (this.tankFillState > 0) this.tankFillState -= 0.02F;
					this.progress = 0;
					this.progressTotal = 0;
					
				}
				
			} else {
				
				if (this.isWorking) {
					
					if (this.tankFillState > 0) {
						
						IParticleData paricle = ParticleTypes.POOF;
						Direction facing = getBlockState().getValue(BlockMultiPart.FACING);
						
						int ox = 0;
						int oz = 0;
						
						switch(facing) {
						default:
						case NORTH:
							oz = 2;
							ox = 2;
							break;
						case EAST:
							ox = -1;
							oz = 2;
							break;
						case SOUTH:
							ox = -1;
							oz = -1;
							break;
						case WEST:
							ox = 2;
							oz = -1;
							break;
						}
						
						float oy = 2.5F;
						float width = 1.5F;
						float height = 0.4F;

						float x = this.worldPosition.getX() + ox + (level.random.nextFloat() - 0.5F) * width;
						float y = this.worldPosition.getY() + oy + (level.random.nextFloat() - 0.5F) * height;
						float z = this.worldPosition.getZ() + oz + (level.random.nextFloat() - 0.5F) * width;
						this.level.addParticle(paricle, x, y, z, 0, 0, 0);
						
					}
					
					MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.BLENDER_LOOP);
					
				}
				
			}
			
		} else if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(new BlockPos(2, 1, 0))) {
			TileEntityMBlender tileEntity = (TileEntityMBlender) BlockMultiPart.getSCenterTE(worldPosition, getBlockState(), level);
			if (tileEntity != null) {
				FluidStack rest = pushFluid(tileEntity.fluidOut, level, worldPosition);
				if (rest != tileEntity.fluidOut) tileEntity.fluidOut = rest;
			}
		}
		
	}
	
	public boolean canWork() {
		return findRecipe() != null || getEntitysInInput().size() > 0;
	}
	
	public BlendingRecipe findRecipe() {
		Optional<BlendingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.BLENDING, this, this.level);
		if (recipe.isPresent()) {
			return recipe.get();
		} else {
			return null;
		}
	}
	
	private List<Entity> getEntitysInInput() {

		Direction facing = this.getBlockState().getValue(BlockMSchredder.FACING);
		Vector3i directionVec1 = null;
		Vector3i directionVec2 = null;
		switch(facing) {
		case NORTH: 
			directionVec1 = new Vector3i(1, 1F, 1);
			directionVec2 = new Vector3i(3, 2F, 3);
			break;
		case SOUTH: 
			directionVec1 = new Vector3i(0, 1F, 0);
			directionVec2 = new Vector3i(-2, 2F, -2);
			break;
		case EAST: 
			directionVec1 = new Vector3i(-2, 1F, 1);
			directionVec2 = new Vector3i(0, 2F, 3);
			break;
		case WEST: 
			directionVec1 = new Vector3i(1, 1F, -2);
			directionVec2 = new Vector3i(3, 2F, 0);
			break;
		default: 
			directionVec1 = new Vector3i(0, 1F, 0);
			directionVec2 = new Vector3i(0, 2F, 0);
		}
		AxisAlignedBB inputBounds = new AxisAlignedBB(this.worldPosition.offset(directionVec1), this.worldPosition.offset(directionVec2));
		return this.level.getEntities(null, inputBounds);
		
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlender.getSCenterTE(ipos, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlender) {
			if (ipos.equals(new BlockPos(2, 1, 0))) {
				int transfer = Math.min(amount, ((TileEntityMBlender) tileEntity).fluidOut.getAmount());
				if (transfer > 0) {
					FluidStack fluidOut = new FluidStack(((TileEntityMBlender) tileEntity).fluidOut.getFluid(), transfer);
					((TileEntityMBlender) tileEntity).fluidOut.shrink(transfer);
					return fluidOut;
				}
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlender.getSCenterTE(worldPosition, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlender) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				if (((TileEntityMBlender) tileEntity).fluidIn1.getFluid().isSame(fluid.getFluid()) || ((TileEntityMBlender) tileEntity).fluidIn1.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMBlender) tileEntity).fluidIn1.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMBlender) tileEntity).fluidIn1.isEmpty()) {
							((TileEntityMBlender) tileEntity).fluidIn1 = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMBlender) tileEntity).fluidIn1.grow(transfer);
						}
						return fluidRest;
					}
				}
			} else if (ipos.equals(new BlockPos(1, 1, 2))) {
				if (((TileEntityMBlender) tileEntity).fluidIn2.getFluid().isSame(fluid.getFluid()) || ((TileEntityMBlender) tileEntity).fluidIn2.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMBlender) tileEntity).fluidIn2.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMBlender) tileEntity).fluidIn2.isEmpty()) {
							((TileEntityMBlender) tileEntity).fluidIn2 = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMBlender) tileEntity).fluidIn2.grow(transfer);
						}
						return fluidRest;
					}
				}
			}
		}
		return fluid;
	}

	@Override
	public Fluid getFluidType() {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlender.getSCenterTE(ipos, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlender) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				return ((TileEntityMBlender) tileEntity).fluidIn1.getFluid();
			} else if (ipos.equals(new BlockPos(1, 1, 2))) {
				return ((TileEntityMBlender) tileEntity).fluidIn2.getFluid();
			} else if (ipos.equals(new BlockPos(2, 1, 0))) {
				return ((TileEntityMBlender) tileEntity).fluidOut.getFluid();
			}
		}
		return Fluids.EMPTY;
	}

	@Override
	public FluidStack getStorage() {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlender.getSCenterTE(ipos, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlender) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				return ((TileEntityMBlender) tileEntity).fluidIn1;
			} else if (ipos.equals(new BlockPos(1, 1, 2))) {
				return ((TileEntityMBlender) tileEntity).fluidIn2;
			} else if (ipos.equals(new BlockPos(2, 1, 0))) {
				return ((TileEntityMBlender) tileEntity).fluidOut;
			}
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().getValue(BlockMultiPart.FACING);
		return	((ipos.equals(new BlockPos(2, 2, 2)) || ipos.equals(new BlockPos(1, 1, 2))) && side == facing.getOpposite()) ||
				((ipos.equals(new BlockPos(2, 1, 0))) && side == facing);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
			if (!this.fluidIn1.isEmpty()) compound.put("FluidIn1", this.fluidIn1.writeToNBT(new CompoundNBT()));
			if (!this.fluidIn2.isEmpty()) compound.put("FluidIn2", this.fluidIn2.writeToNBT(new CompoundNBT()));
			if (!this.fluidOut.isEmpty()) compound.put("FluidOut", this.fluidOut.writeToNBT(new CompoundNBT()));
			compound.putInt("Progress", this.progress);
			compound.putInt("ProgressTotal", this.progressTotal);
			compound.putBoolean("hasPower", this.hasPower);
			compound.putBoolean("isWorking", this.isWorking);
			compound.putFloat("tankFillState", this.tankFillState);
		}
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		if (state == null ? true : BlockMultiPart.getInternPartPos(state).equals(BlockPos.ZERO)) {
			this.fluidIn1 = FluidStack.EMPTY;
			this.fluidIn2 = FluidStack.EMPTY;
			this.fluidOut = FluidStack.EMPTY;
			if (compound.contains("FluidIn1")) this.fluidIn1 = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidIn1"));
			if (compound.contains("FluidIn2")) this.fluidIn2 = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidIn2"));
			if (compound.contains("FluidOut")) this.fluidOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidOut"));
			this.progress = compound.getInt("Progress");
			this.progressTotal = compound.getInt("ProgressTotal");
			this.hasPower = compound.getBoolean("hasPower");
			this.isWorking = compound.getBoolean("isWorking");
			this.tankFillState = compound.getFloat("tankFillState");
		}
		super.load(state, compound);
	}
	
}
