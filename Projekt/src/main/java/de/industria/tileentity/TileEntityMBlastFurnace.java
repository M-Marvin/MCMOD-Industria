package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMBlastFurnace;
import de.industria.blocks.BlockMultipart;
import de.industria.gui.ContainerMBlastFurnace;
import de.industria.multipartbuilds.MultipartBuild.MultipartBuildLocation;
import de.industria.recipetypes.BlastFurnaceRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
import de.industria.util.handler.UtilHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMBlastFurnace extends TileEntityInventoryBase implements ITickableTileEntity, ITESimpleMachineSound, INamedContainerProvider, ITEFluidConnective, ISidedInventory {
	
	public int maxFluidStorage;
	public FluidStack gasStorage;
	
	public boolean hasPower;
	public boolean hasHeater;
	public boolean hasHeat;
	public boolean isWorking;
	public int progress;
	public int progressTotal;
	
	public TileEntityMBlastFurnace() {
		super(ModTileEntityType.BLAST_FURNACE, 5);
		this.maxFluidStorage = 3000;
		this.gasStorage = FluidStack.EMPTY;
	}
	
	@Override
	public void tick() {
		
		if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
			
			if (!this.level.isClientSide()) {
				
				this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
				ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
				
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
				this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
				
				BlockPos heaterPos = UtilHelper.rotateBlockPos(new BlockPos(1, 0, 1), getBlockState().getValue(BlockMultipart.FACING)).offset(this.worldPosition).below();
				BlockState heaterState = this.level.getBlockState(heaterPos);
				TileEntity heaterTile = heaterState.getBlock() instanceof BlockMultipart ? BlockMultipart.getSCenterTE(heaterPos, heaterState, this.level) : this.level.getBlockEntity(heaterPos);
				this.hasHeater = heaterTile instanceof TileEntityMHeaterBase;
				this.hasHeat = this.hasHeater ? ((TileEntityMHeaterBase) heaterTile).isWorking : false;
				this.isWorking = this.hasPower && this.hasHeat && this.canWork();
				
				if (this.isWorking) {
					
					BlastFurnaceRecipe recipe = findRecipe();
					
					if (recipe != null) {
						
						this.progressTotal = recipe.getSmeltingTime();
						
						if (ItemStackHelper.canMergeRecipeStacks(this.getItem(3), recipe.getResultItem()) &&
							ItemStackHelper.canMergeRecipeStacks(this.getItem(4), recipe.getWasteOut())) {
							
							this.progress++;
							
							if (this.progress > this.progressTotal) {
								
								this.progress = 0;
								
								for (ItemStack item : recipe.getItemsIn()) {
									for (int i = 0; i < 3; i++) {
										if (this.getItem(i).getItem() == item.getItem() && this.getItem(i).getCount() >= item.getCount()) {
											this.removeItem(i, item.getCount());
										}
									}
								}
								
								this.gasStorage.shrink(recipe.getConsumtionFluid().getAmount());
								
								if (this.getItem(3).isEmpty()) {
									this.setItem(3, recipe.assemble(this));
								} else {
									this.getItem(3).grow(recipe.assemble(this).getCount());
								}

								if (this.getItem(4).isEmpty()) {
									this.setItem(4, recipe.getWasteOut());
								} else {
									this.getItem(4).grow(recipe.getWasteOut().getCount());
								}
								
							}
							
						}
						
					}
					
				} else {
					
					if (this.progress > 0) this.progress -= 2;
					
				}
				
			} else {
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.BLENDER_LOOP); // TODO
				
			}
			
		}
		
	}
	
	public boolean canWork() {
		return findRecipe() != null && this.hasHeat;
	}
	
	public BlastFurnaceRecipe findRecipe() {
		Optional<BlastFurnaceRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.BLAST_FURNACE, this, this.level);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMBlastFurnace(id, playerInv, this);
	}

	@Override
	public FluidStack getFluid(int amount) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos = BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(worldPosition, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 0, 0))) {
				if (((TileEntityMBlastFurnace) tileEntity).gasStorage.getFluid().isSame(fluid.getFluid()) || ((TileEntityMBlastFurnace) tileEntity).gasStorage.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMBlastFurnace) tileEntity).gasStorage.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMBlastFurnace) tileEntity).gasStorage.isEmpty()) {
							((TileEntityMBlastFurnace) tileEntity).gasStorage = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMBlastFurnace) tileEntity).gasStorage.grow(transfer);
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
		return this.gasStorage.getFluid();
	}
	
	@Override
	public FluidStack getStorage() {
		BlockPos ipos = BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(ipos, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				return ((TileEntityMBlastFurnace) tileEntity).gasStorage;
			}
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().getValue(BlockMultipart.FACING);
		return ipos.equals(new BlockPos(2, 0, 0)) && side == facing;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.blast_furnace");
	}

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3, 4};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index >= 0 && index <= 2;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index >= 3 && index <= 4;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("hasHeater", this.hasHeater);
		compound.putBoolean("hasHeat", this.hasHeat);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putInt("Progress", this.progress);
		compound.put("Gas", this.gasStorage.writeToNBT(new CompoundNBT()));
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.hasPower = compound.getBoolean("hasPower");
		this.hasHeater = compound.getBoolean("hasHeater");
		this.hasHeat = compound.getBoolean("hasHeat");
		this.isWorking = compound.getBoolean("isWorking");
		this.progressTotal = compound.getInt("progressTotal");
		this.progress = compound.getInt("Progress");
		this.gasStorage = FluidStack.loadFluidStackFromNBT(compound.getCompound("Gas"));
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
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-3, -1, -3), worldPosition.offset(4, 6, 4));
	}

	@Override
	public void setStorage(FluidStack storage) {
		BlockPos ipos = BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(ipos, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				((TileEntityMBlastFurnace) tileEntity).gasStorage = storage;
			}
		}
	}

	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}
	
}
