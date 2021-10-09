package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMultipart;
import de.industria.gui.ContainerMOreWashingPlant;
import de.industria.recipetypes.WashingRecipe;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
import de.industria.util.handler.FluidBucketHelper;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMOreWashingPlant extends TileEntityInventoryBase implements ITickableTileEntity, ITEFluidConnective, ITESimpleMachineSound, ISidedInventory, INamedContainerProvider {
	
	public boolean hasPower;
	public boolean isWorking;
	public int progress;
	public int progressTotal;
	public FluidStack wasteFluid;
	public FluidStack inputFluid;
	public final int maxFluidStorage;
	
	public TileEntityMOreWashingPlant() {
		super(ModTileEntityType.ORE_WASHING_PLANT, 8);
		this.wasteFluid = FluidStack.EMPTY;
		this.inputFluid = FluidStack.EMPTY;
		this.maxFluidStorage = 2000;
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMOreWashingPlant) tileEntity).hasPower = (boolean) data[0];
			if (data[1] != null) ((TileEntityMOreWashingPlant) tileEntity).isWorking = (boolean) data[1];
			if (data[2] != null) ((TileEntityMOreWashingPlant) tileEntity).progress = (int) data[2];
			if (data[3] != null) ((TileEntityMOreWashingPlant) tileEntity).progressTotal = (int) data[3];
			if (data[4] != null) ((TileEntityMOreWashingPlant) tileEntity).wasteFluid = (FluidStack) data[4];
			if (data[5] != null) ((TileEntityMOreWashingPlant) tileEntity).inputFluid = (FluidStack) data[5];
		}, () -> hasPower, () -> isWorking, () -> progress, () -> progressTotal, () -> wasteFluid, () -> inputFluid);
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(0, 0, 0))) {
				
				TileEntityMOreWashingPlant tileEntity = (TileEntityMOreWashingPlant) BlockMultipart.getSCenterTE(worldPosition, getBlockState(), level);
				if (tileEntity != null) {
					FluidStack rest = pushFluid(tileEntity.wasteFluid, level, worldPosition);
					if (rest != tileEntity.wasteFluid) tileEntity.wasteFluid = rest;
				}

				this.inputFluid = FluidBucketHelper.transferBuckets(this, 4, this.inputFluid, this.maxFluidStorage);
				this.wasteFluid = FluidBucketHelper.transferBuckets(this, 6, this.wasteFluid, this.maxFluidStorage);
				
				ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
				this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
				this.isWorking = this.canWork() && this.hasPower;
				
				if (this.isWorking) {
					
					WashingRecipe recipe = findRecipe();
					
					if (recipe != null) {
						
						this.progressTotal = recipe.getWashingTime();
						
						if (	ItemStackHelper.canMergeRecipeStacks(this.getItem(1), recipe.getResultItem()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getItem(2), recipe.getResultItem2()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getItem(3), recipe.getResultItem3()) &&
								this.maxFluidStorage - this.wasteFluid.getAmount() >= 500) {
							
							this.progress++;
							
							if (this.progress >= this.progressTotal) {
								
								this.getItem(0).shrink(1);
								
								if (this.getItem(1).isEmpty()) {
									this.setItem(1, recipe.getResultItem().copy());
								} else {
									this.getItem(1).grow(recipe.getResultItem().getCount());
								}
								
								if (this.getItem(2).isEmpty()) {
									this.setItem(2, recipe.getResultItem2().copy());
								} else {
									this.getItem(2).grow(recipe.getResultItem2().getCount());
								}
								
								if (this.getItem(3).isEmpty()) {
									this.setItem(3, recipe.getResultItem3().copy());
								} else {
									this.getItem(3).grow(recipe.getResultItem3().getCount());
								}
								
								this.inputFluid.shrink(500);
								
								if (this.wasteFluid.isEmpty()) {
									this.wasteFluid = new FluidStack(ModFluids.CHEMICAL_WATER, 500);
								} else {
									this.wasteFluid.grow(500);
								}
								
								this.progress = 0;
								
							}
							
							return;
							
						}
						
					}
					
				}
				
				this.progress = 0;
				
			}
			
		} else {
			
			if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(0, 0, 0))) {
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.ORE_WASHING_PLANT_LOOP);
				
				if (this.isWorking) {
					
					IParticleData rawItemParticle = new ItemParticleData(ParticleTypes.ITEM, this.getItem(0));
					
					for (int i = 0; i < 5; i++) {
						float dx = (this.level.random.nextFloat() - 0.5F) + 3.3F;
						float dy = (this.level.random.nextFloat() - 0.5F) + 2;
						float dz = (this.level.random.nextFloat() - 0.5F) + 0.8F;
						createParticle(rawItemParticle, dx, dy, dz, 0, 0.1F, 0);
					}

					for (int i = 0; i < 5; i++) {
						float dx = (this.level.random.nextFloat() - 0.5F) * 0.5F + 2.8F;
						float dy = (this.level.random.nextFloat() - 0.5F) + 2.1F;
						float dz = (this.level.random.nextFloat() - 0.5F) + 1F;
						createParticle(rawItemParticle, dx, dy, dz, 0, 0.1F, 0);
					}
					
					float dx = (this.level.random.nextFloat() - 0.5F) * 0.5F + 2F;
					float dy = (this.level.random.nextFloat() - 0.5F) * 0.1F + 1.8F;
					float dz = (this.level.random.nextFloat() - 0.5F) * 0.5F + 2.5F;
					createParticle(rawItemParticle, dx, dy, dz, 0, 0.1F, 0);

					dx = (this.level.random.nextFloat() - 0.5F) * 0.5F + 1.6F;
					dy = (this.level.random.nextFloat() - 0.5F) * 0.1F + 1.8F;
					dz = (this.level.random.nextFloat() - 0.5F) * 0.5F + 2.5F;
					createParticle(rawItemParticle, dx, dy, dz, 0, 0.1F, 0);
					
					IParticleData resultItemParticle = new ItemParticleData(ParticleTypes.ITEM, canWork() ? findRecipe().getResultItem() : this.getItem(0));
					
					for (int i = 0; i < 2; i++) {

						dx = (this.level.random.nextFloat() - 0.5F) + 1F;
						dy = (this.level.random.nextFloat() - 0.5F) * 0.1F + 1.8F;
						dz = (this.level.random.nextFloat() - 0.5F) + 2.3F;
						createParticle(resultItemParticle, dx, dy, dz, 0, 0.1F, -0.02F);
						
					}
					
					IParticleData waterParticle = ParticleTypes.SPLASH;
					
					for (int i = 0; i < 4; i++) {

						dx = (this.level.random.nextFloat() - 0.5F) * 1.3F + 1.2F;
						dy = (this.level.random.nextFloat() - 0.5F) * 0.1F + 1.8F;
						dz = (this.level.random.nextFloat() - 0.5F) + 2.4F;
						createParticle(waterParticle, dx, dy, dz, 0, 0F, -0.02F);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	protected void createParticle(IParticleData particle, float x, float y, float z, float sx, float sy, float sz) {
		Vector3f cord = rotateParticleCord(x, y, z);
		Vector3f speed = rotateParticleSpeed(sx, sy, sz);
		this.level.addParticle(particle, worldPosition.getX() + cord.x(), worldPosition.getY() + cord.y(), worldPosition.getZ() + cord.z(), speed.x(), speed.y(), speed.z());
	}
	
	protected Vector3f rotateParticleCord(float x, float y, float z) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		switch (facing) {
		case NORTH:
			return new Vector3f(x, y, z);
		case SOUTH:
			return new Vector3f(-x + 1, y, -z + 1);
		case EAST:
			return new Vector3f(-z+ 1, y, x);
		case WEST:
			return new Vector3f(z, y, -x + 1);
		default:
			return new Vector3f(0, 0, 0);
		}
	}
	
	protected Vector3f rotateParticleSpeed(float x, float y, float z) {
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		switch (facing) {
		case NORTH:
			return new Vector3f(x, y, z);
		case SOUTH:
			return new Vector3f(-x, y, -z);
		case EAST:
			return new Vector3f(-z, y, x);
		case WEST:
			return new Vector3f(z, y, -x);
		default:
			return new Vector3f(0, 0, 0);
		}
	}
	
	public boolean canWork() {
		return findRecipe() != null && this.inputFluid.getAmount() >= 500 && this.inputFluid.getFluid() == Fluids.WATER;
	}
	
	public WashingRecipe findRecipe() {
		Optional<WashingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.WASHING, this, this.level);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultipart.getSCenterTE(worldPosition, getBlockState(), level);
		if (tileEntity instanceof TileEntityMOreWashingPlant) {
			if (ipos.equals(new BlockPos(2, 0, 0))) {
				int transfer = Math.min(amount, ((TileEntityMOreWashingPlant) tileEntity).wasteFluid.getAmount());
				if (transfer > 0) {
					FluidStack fluidOut = new FluidStack(((TileEntityMOreWashingPlant) tileEntity).wasteFluid.getFluid(), transfer);
					((TileEntityMOreWashingPlant) tileEntity).wasteFluid.shrink(transfer);
					return fluidOut;
				}
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultipart.getSCenterTE(worldPosition, getBlockState(), level);
		if (tileEntity instanceof TileEntityMOreWashingPlant) {
			if (ipos.equals(new BlockPos(2, 0, 0))) {
				if (((TileEntityMOreWashingPlant) tileEntity).inputFluid.getFluid().isSame(fluid.getFluid()) || ((TileEntityMOreWashingPlant) tileEntity).inputFluid.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMOreWashingPlant) tileEntity).inputFluid.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMOreWashingPlant) tileEntity).inputFluid.isEmpty()) {
							((TileEntityMOreWashingPlant) tileEntity).inputFluid = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMOreWashingPlant) tileEntity).inputFluid.grow(transfer);
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
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(0, 0, 0))) {
			return this.wasteFluid.getFluid();
		} else {
			return this.inputFluid.getFluid();
		}
	}
	
	@Override
	public FluidStack getStorage() {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(0, 0, 0))) {
			return this.wasteFluid;
		} else {
			return this.inputFluid;
		}
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().getValue(BlockMultipart.FACING);
		return	(ipos.equals(new BlockPos(0, 0, 0)) && side == facing) ||
				(ipos.equals(new BlockPos(2, 0, 0)) && side == facing);
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity palyer) {
		return new ContainerMOreWashingPlant(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.ore_washing_plant");
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3};
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index > 0;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-6, -1, -6), worldPosition.offset(6, 6, 6));
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.inputFluid.isEmpty()) compound.put("FluidIn", this.inputFluid.writeToNBT(new CompoundNBT()));
		if (!this.wasteFluid.isEmpty()) compound.put("WasteFluid", this.wasteFluid.writeToNBT(new CompoundNBT()));
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("progress", this.progress);
		compound.putInt("progressTotal", this.progressTotal);
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.inputFluid = FluidStack.EMPTY;
		this.wasteFluid = FluidStack.EMPTY;
		if (compound.contains("FluidIn")) this.inputFluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidIn"));
		if (compound.contains("WasteFluid")) this.wasteFluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("WasteFluid"));
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.progress = compound.getInt("progress");
		this.progressTotal = compound.getInt("progressTotal");
		this.buildData = MultipartBuildLocation.loadNBT(compound.getCompound("BuildData"));
		super.load(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}

	@Override
	public void setStorage(FluidStack storage) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(0, 0, 0))) {
			this.wasteFluid = storage;
		} else {
			this.inputFluid = storage;
		}
	}

	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}

	public MultipartBuildLocation getBuildData() {
		return this.buildData;
	}
	
}
