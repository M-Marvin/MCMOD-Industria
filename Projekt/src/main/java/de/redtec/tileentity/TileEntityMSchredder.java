package de.redtec.tileentity;

import java.util.List;
import java.util.Optional;

import de.redtec.blocks.BlockMSchredder;
import de.redtec.blocks.BlockMultiPart;
import de.redtec.dynamicsounds.ISimpleMachineSound;
import de.redtec.gui.ContainerMSchredder;
import de.redtec.items.ItemSchredderTool;
import de.redtec.recipetypes.SchredderRecipe;
import de.redtec.typeregistys.ModDamageSource;
import de.redtec.typeregistys.ModRecipeTypes;
import de.redtec.typeregistys.ModSoundEvents;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.redtec.util.handler.ElectricityNetworkHandler;
import de.redtec.util.handler.ItemStackHelper;
import de.redtec.util.handler.MachineSoundHelper;
import de.redtec.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMSchredder extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, ISidedInventory, INamedContainerProvider {
	
	// Only Client Side
	public float lastPartial;
	public float rotation;
	
	public int progress;
	public int progressTotal;
	public boolean isWorking;
	public boolean hasPower;
	
	public TileEntityMSchredder() {
		super(ModTileEntityType.SCHREDDER, 5);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
	}
	
	public ItemSchredderTool getToolItem() {
		ItemStack toolStack = this.getStackInSlot(4);
		if (toolStack.getItem() instanceof ItemSchredderTool) {
			return (ItemSchredderTool) toolStack.getItem();
		}
		return null;
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote() && BlockMultiPart.getInternPartPos(getBlockState()).equals(BlockPos.ZERO)) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = this.hasPower ? this.canWork() : false;
			
			if (this.isWorking) {
				
				List<Entity> entitysInInput = getEntitysInInput();
				
				for (Entity entity : entitysInInput) {
					
					if (entity instanceof ItemEntity) {
						
						ItemStack itemStack = ((ItemEntity) entity).getItem();
						ItemStack rest = insertItem(itemStack);
						
						if (!rest.isEmpty() && rest != itemStack) {
							((ItemEntity) entity).setItem(rest);
						} else if (rest.isEmpty()) {
							entity.remove();
						}
						
						continue;
						
					} else {
						
						entity.attackEntityFrom(ModDamageSource.SCHREDDER, 1.5F);
						
					}
					
				}
						
				SchredderRecipe recipe = this.findRecipe();
				if (recipe != null) {
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), recipe.getRecipeOutput()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(2), recipe.getRecipeOutput2()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(3), recipe.getRecipeOutput3())) {
						
						this.progressTotal = recipe.getSchredderTime();
						this.progress++;
						
						if (this.progress >= this.progressTotal) {
							
							ItemStack result1 = recipe.getRecipeOutput();
							if (this.getStackInSlot(1).isEmpty()) {
								this.setInventorySlotContents(1, result1);
							} else {
								this.getStackInSlot(1).grow(result1.getCount());
							}
							
							ItemStack result2 = recipe.getRecipeOutput2();
							if (this.getStackInSlot(2).isEmpty()) {
								this.setInventorySlotContents(2, result2);
							} else {
								this.getStackInSlot(2).grow(result2.getCount());
							}
							
							ItemStack result3 = recipe.getRecipeOutput3();
							if (this.getStackInSlot(3).isEmpty()) {
								this.setInventorySlotContents(3, result3);
							} else {
								this.getStackInSlot(3).grow(result3.getCount());
							}
							
							this.getStackInSlot(0).shrink(1);
							this.getStackInSlot(4).setDamage(this.getStackInSlot(4).getDamage() + recipe.getSchredderDamage());
							this.progress = 0;
							
						}
						
					}
										
				}
				
			}
			
		} else {
			
			if (this.isWorking) {
				
				if (!this.getStackInSlot(0).isEmpty() && findRecipe() != null) {
					
					Direction facing = this.getBlockState().get(BlockMultiPart.FACING);
					Vector3f offset = null;
					switch(facing) {
						case NORTH: offset = new Vector3f(1, 1.8F, 1); break;
						case SOUTH: offset = new Vector3f(0, 1.8F, 0); break;
						case EAST: offset = new Vector3f(0, 1.8F, 1); break;
						case WEST: offset = new Vector3f(1, 1.8F, 0); break;
						default: offset = new Vector3f(0, 0, 0);
					}
					
					ItemParticleData particle = new ItemParticleData(ParticleTypes.ITEM, this.getStackInSlot(0));

					for (int i = 0; i < 10; i++) {
						float fX = (this.world.rand.nextFloat() - 0.5F) * 1.6F;
						float fY = (this.world.rand.nextFloat() - 0.5F) * 0.6F;
						float fZ = (this.world.rand.nextFloat() - 0.5F) * 1.6F;
						float fX2 = (this.world.rand.nextFloat() - 0.5F) * 0.2F;
						float fY2 = (this.world.rand.nextFloat() - 0.5F) * 0.2F;
						float fZ2 = (this.world.rand.nextFloat() - 0.5F) * 0.2F;
						
						this.world.addParticle(particle, this.pos.getX() + offset.getX() + fX, this.pos.getY() + offset.getY() + fY, this.pos.getZ() + offset.getZ() + fZ, fX2, fY2, fZ2);
						
					}
					
				}
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.SCHREDDER_LOOP);
				
			}
			
		}
		
	}
	
	private List<Entity> getEntitysInInput() {

		Direction facing = this.getBlockState().get(BlockMSchredder.FACING);
		Vector3i directionVec1 = null;
		Vector3i directionVec2 = null;
		switch(facing) {
		case NORTH: 
			directionVec1 = new Vector3i(0, 0.5F, 0);
			directionVec2 = new Vector3i(2, 1.9F, 2);
			break;
		case SOUTH: 
			directionVec1 = new Vector3i(1, 0.5F, 1);
			directionVec2 = new Vector3i(-1, 1.9F, -1);
			break;
		case EAST: 
			directionVec1 = new Vector3i(-1, 0.5F, 0);
			directionVec2 = new Vector3i(1, 1.9F, 2);
			break;
		case WEST: 
			directionVec1 = new Vector3i(0, 0.5F, -1);
			directionVec2 = new Vector3i(2, 1.9F, 1);
			break;
		default: 
			directionVec1 = new Vector3i(0, 0.5F, 0);
			directionVec2 = new Vector3i(0, 1.9F, 0);
		}
		AxisAlignedBB inputBounds = new AxisAlignedBB(this.pos.add(directionVec1), this.pos.add(directionVec2));
		return this.world.getEntitiesInAABBexcluding(null, inputBounds, null);
		
	}
	
	public ItemStack insertItem(ItemStack stack) {
		
		if (findRecipe(stack) == null && ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), stack) && ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), stack)) {			
			if (this.getStackInSlot(1).isEmpty()) {
				this.setInventorySlotContents(1, stack);
				return ItemStack.EMPTY;
			} else {
				int transfer = Math.min(stack.getCount(), stack.getMaxStackSize() - this.getStackInSlot(1).getCount());
				this.getStackInSlot(1).grow(transfer);
				ItemStack rest = stack.copy();
				rest.shrink(transfer);
				return rest;
			}
		} else if (findRecipe(stack) != null && ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(0), stack)) {
			if (this.getStackInSlot(0).isEmpty()) {
				this.setInventorySlotContents(0, stack);
				return ItemStack.EMPTY;
			} else {
				int transfer = Math.min(stack.getCount(), stack.getMaxStackSize() - this.getStackInSlot(0).getCount());
				this.getStackInSlot(0).grow(transfer);
				ItemStack rest = stack.copy();
				rest.shrink(transfer);
				return rest;
			}
		}
		
		return stack;
		
	}
	
	public SchredderRecipe findRecipe() {
		if (this.getToolItem() == null) return null;
		Optional<SchredderRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.SCHREDDER, this, this.world);
		if (recipe.isPresent()) return recipe.get();
		return null;
	}
	
	public SchredderRecipe findRecipe(ItemStack stack) {
		ItemStack oldStack = this.getStackInSlot(0);
		this.setInventorySlotContents(0, stack);
		Optional<SchredderRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.SCHREDDER, this, this.world);
		this.setInventorySlotContents(0, oldStack);
		if (recipe.isPresent()) return recipe.get();
		return null;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("isWorking", this.isWorking);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putInt("progress", this.progress);
		compound.putInt("progressTotal", this.progressTotal);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.isWorking = compound.getBoolean("isWorking");
		this.hasPower = compound.getBoolean("hasPower");
		this.progress = compound.getInt("progress");
		this.progressTotal = compound.getInt("progressTotal");
		super.read(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3};
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index > 0 && index <= 3;
	}

	public boolean canWork() {
		return (getEntitysInInput().size() > 0 && getToolItem() != null) || findRecipe() != null;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMSchredder(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.schredder");
	}
	
}
