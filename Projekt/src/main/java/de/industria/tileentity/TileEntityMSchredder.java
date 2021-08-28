package de.industria.tileentity;

import java.util.List;
import java.util.Optional;

import de.industria.blocks.BlockMSchredder;
import de.industria.blocks.BlockMultipart;
import de.industria.gui.ContainerMSchredder;
import de.industria.items.ItemSchredderTool;
import de.industria.recipetypes.SchredderRecipe;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
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

public class TileEntityMSchredder extends TileEntityInventoryBase implements ITickableTileEntity, ITESimpleMachineSound, ISidedInventory, INamedContainerProvider {
	
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
		return new AxisAlignedBB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 2, 2));
	}
	
	public ItemSchredderTool getToolItem() {
		ItemStack toolStack = this.getItem(4);
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
		
		if (!this.level.isClientSide() && BlockMultipart.getInternPartPos(getBlockState()).equals(BlockPos.ZERO)) {
			
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
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
						
						entity.hurt(ModDamageSource.SCHREDDER, 1.5F);
						
					}
					
				}
						
				SchredderRecipe recipe = this.findRecipe();
				if (recipe != null) {
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getItem(1), recipe.getResultItem()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getItem(2), recipe.getResultItem2()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getItem(3), recipe.getResultItem3()) &&
						(this.getItem(4).getMaxDamage() - this.getItem(4).getDamageValue()) >= recipe.getSchredderDamage()) {
						
						this.progressTotal = recipe.getSchredderTime();
						this.progress++;
						
						if (this.progress >= this.progressTotal) {
							
							ItemStack result1 = recipe.getResultItem();
							if (this.getItem(1).isEmpty()) {
								this.setItem(1, result1);
							} else {
								this.getItem(1).grow(result1.getCount());
							}
							
							ItemStack result2 = recipe.getResultItem2();
							if (this.getItem(2).isEmpty()) {
								this.setItem(2, result2);
							} else {
								this.getItem(2).grow(result2.getCount());
							}
							
							ItemStack result3 = recipe.getResultItem3();
							if (this.getItem(3).isEmpty()) {
								this.setItem(3, result3);
							} else {
								this.getItem(3).grow(result3.getCount());
							}
							
							this.getItem(0).shrink(1);
							this.getItem(4).setDamageValue(this.getItem(4).getDamageValue() + recipe.getSchredderDamage());
							this.progress = 0;
							
						}
						
					}
										
				}
				
			}
			
		} else {
			
			if (this.isWorking) {
				
				if (!this.getItem(0).isEmpty() && findRecipe() != null) {
					
					Direction facing = this.getBlockState().getValue(BlockMultipart.FACING);
					Vector3f offset = null;
					switch(facing) {
						case NORTH: offset = new Vector3f(1, 1.8F, 1); break;
						case SOUTH: offset = new Vector3f(0, 1.8F, 0); break;
						case EAST: offset = new Vector3f(0, 1.8F, 1); break;
						case WEST: offset = new Vector3f(1, 1.8F, 0); break;
						default: offset = new Vector3f(0, 0, 0);
					}
					
					ItemParticleData particle = new ItemParticleData(ParticleTypes.ITEM, this.getItem(0));

					for (int i = 0; i < 10; i++) {
						float fX = (this.level.random.nextFloat() - 0.5F) * 1.6F;
						float fY = (this.level.random.nextFloat() - 0.5F) * 0.6F;
						float fZ = (this.level.random.nextFloat() - 0.5F) * 1.6F;
						float fX2 = (this.level.random.nextFloat() - 0.5F) * 0.2F;
						float fY2 = (this.level.random.nextFloat() - 0.5F) * 0.2F;
						float fZ2 = (this.level.random.nextFloat() - 0.5F) * 0.2F;
						
						this.level.addParticle(particle, this.worldPosition.getX() + offset.x() + fX, this.worldPosition.getY() + offset.y() + fY, this.worldPosition.getZ() + offset.z() + fZ, fX2, fY2, fZ2);
						
					}
					
				}
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.SCHREDDER_LOOP);
				
			}
			
		}
		
	}
	
	private List<Entity> getEntitysInInput() {

		Direction facing = this.getBlockState().getValue(BlockMSchredder.FACING);
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
		AxisAlignedBB inputBounds = new AxisAlignedBB(this.worldPosition.offset(directionVec1), this.worldPosition.offset(directionVec2));
		return this.level.getEntities(null, inputBounds);
		
	}
	
	public ItemStack insertItem(ItemStack stack) {
		
		if (findRecipe(stack) == null && ItemStackHelper.canMergeRecipeStacks(this.getItem(1), stack) && ItemStackHelper.canMergeRecipeStacks(this.getItem(1), stack)) {			
			if (this.getItem(1).isEmpty()) {
				this.setItem(1, stack);
				return ItemStack.EMPTY;
			} else {
				int transfer = Math.min(stack.getCount(), stack.getMaxStackSize() - this.getItem(1).getCount());
				this.getItem(1).grow(transfer);
				ItemStack rest = stack.copy();
				rest.shrink(transfer);
				return rest;
			}
		} else if (findRecipe(stack) != null && ItemStackHelper.canMergeRecipeStacks(this.getItem(0), stack)) {
			if (this.getItem(0).isEmpty()) {
				this.setItem(0, stack);
				return ItemStack.EMPTY;
			} else {
				int transfer = Math.min(stack.getCount(), stack.getMaxStackSize() - this.getItem(0).getCount());
				this.getItem(0).grow(transfer);
				ItemStack rest = stack.copy();
				rest.shrink(transfer);
				return rest;
			}
		}
		
		return stack;
		
	}
	
	public SchredderRecipe findRecipe() {
		if (this.getToolItem() == null) return null;
		Optional<SchredderRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.SCHREDDER, this, this.level);
		if (recipe.isPresent()) return recipe.get();
		return null;
	}
	
	public SchredderRecipe findRecipe(ItemStack stack) {
		ItemStack oldStack = this.getItem(0);
		this.setItem(0, stack);
		Optional<SchredderRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.SCHREDDER, this, this.level);
		this.setItem(0, oldStack);
		if (recipe.isPresent()) return recipe.get();
		return null;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putBoolean("isWorking", this.isWorking);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putInt("progress", this.progress);
		compound.putInt("progressTotal", this.progressTotal);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.isWorking = compound.getBoolean("isWorking");
		this.hasPower = compound.getBoolean("hasPower");
		this.progress = compound.getInt("progress");
		this.progressTotal = compound.getInt("progressTotal");
		super.load(state, compound);
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
		return new TranslationTextComponent("block.industria.schredder");
	}
	
}
