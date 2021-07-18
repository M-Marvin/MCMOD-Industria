package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMBlastFurnace;
import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMBlastFurnace;
import de.industria.recipetypes.BlastFurnaceRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
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

public class TileEntityMBlastFurnace extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, INamedContainerProvider, IFluidConnective, ISidedInventory {
	
	public int maxFluidStorage;
	public FluidStack oxygenStorage;
	
	public boolean hasPower;
	public boolean hasHeater;
	public boolean hasHeat;
	public boolean isWorking;
	public int progress;
	public int progressTotal;
	
	public TileEntityMBlastFurnace() {
		super(ModTileEntityType.BLAST_FURNACE, 5);
		this.maxFluidStorage = 3000;
		this.oxygenStorage = FluidStack.EMPTY;
	}
	
	@Override
	public void tick() {
		
		if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
			
			if (!this.level.isClientSide()) {
				
				this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
				ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
				
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
				this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
				
				BlockPos heaterPos = BlockMultiPart.rotateOffset(new BlockPos(1, 0, 1), getBlockState().getValue(BlockMultiPart.FACING)).offset(this.worldPosition).below();
				BlockState heaterState = this.level.getBlockState(heaterPos);
				TileEntity heaterTile = heaterState.getBlock() instanceof BlockMultiPart ? BlockMultiPart.getSCenterTE(heaterPos, heaterState, this.level) : this.level.getBlockEntity(heaterPos);
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
								
								this.oxygenStorage.shrink(recipe.getConsumtionFluid().getAmount());
								
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
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(worldPosition, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 0, 0))) {
				if (((TileEntityMBlastFurnace) tileEntity).oxygenStorage.getFluid().isSame(fluid.getFluid()) || ((TileEntityMBlastFurnace) tileEntity).oxygenStorage.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMBlastFurnace) tileEntity).oxygenStorage.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMBlastFurnace) tileEntity).oxygenStorage.isEmpty()) {
							((TileEntityMBlastFurnace) tileEntity).oxygenStorage = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMBlastFurnace) tileEntity).oxygenStorage.grow(transfer);
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
		return this.oxygenStorage.getFluid();
	}
	
	@Override
	public FluidStack getStorage() {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(ipos, this.getBlockState(), level);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				return ((TileEntityMBlastFurnace) tileEntity).oxygenStorage;
			}
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().getValue(BlockMultiPart.FACING);
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
		compound.put("Oxygen", this.oxygenStorage.writeToNBT(new CompoundNBT()));
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
		this.oxygenStorage = FluidStack.loadFluidStackFromNBT(compound.getCompound("Oxygen"));
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
		return new AxisAlignedBB(worldPosition.offset(-3, -1, -3), worldPosition.offset(4, 5, 4));
	}
	
}
