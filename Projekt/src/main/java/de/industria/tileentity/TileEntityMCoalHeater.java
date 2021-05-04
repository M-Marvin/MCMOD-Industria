package de.industria.tileentity;

import de.industria.Industria;
import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.fluids.FluidDestilledWater;
import de.industria.gui.ContainerMCoalHeater;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class TileEntityMCoalHeater extends TileEntityInventoryBase implements INamedContainerProvider, ISidedInventory, ITickableTileEntity, ISimpleMachineSound {
	
	public int fuelTime;
	public float burnTime;
	public boolean isWorking;
	
	public TileEntityMCoalHeater() {
		super(ModTileEntityType.COAL_HEATER, 2);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			this.isWorking = false;
			
			Direction facing = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
			boolean powered = this.world.isBlockPowered(pos) || this.world.isBlockPowered(pos.add(facing.getOpposite().getDirectionVec())) || this.world.isBlockPowered(pos.add(facing.rotateY().getDirectionVec())) || this.world.isBlockPowered(pos.add(facing.rotateY().getDirectionVec()).add(facing.getOpposite().getDirectionVec()));
			boolean fullOfAsh = (this.getStackInSlot(1).getItem() != Item.getItemFromBlock(Industria.ash) || this.getStackInSlot(1).getCount() >= 64) && !this.getStackInSlot(1).isEmpty();
			
			if (powered && !fullOfAsh) {
				
				if (this.burnTime > 0) {
					this.burnTime -= 1;
					isWorking = true;
					if (this.burnTime % 60 == 0) {
						ItemStack ashStack = this.getStackInSlot(1);
						if (ashStack.isEmpty()) {
							this.setInventorySlotContents(1, new ItemStack(Industria.ash, 1));
						} else if (ashStack.getItem() == Item.getItemFromBlock(Industria.ash) && ashStack.getCount() < 64) {
							ashStack.grow(1);
						}
					}
				} else if (hasFuelItems()) {
					this.burnTime = ForgeHooks.getBurnTime(this.itemstacks.get(0));
					this.fuelTime = (int) this.burnTime;
					this.itemstacks.get(0).shrink(1);
					isWorking = true;
				}
				
				if (isWorking ? this.world.getGameTime() %10 == 0 : false) {
					
					int xMin = Math.min(this.pos.add(facing.getOpposite().getDirectionVec()).getX(), this.pos.getX()) - 2;
					int xMax = Math.max(this.pos.add(facing.getOpposite().getDirectionVec()).getX(), this.pos.getX()) + 2;
					int zMin = Math.min(this.pos.add(facing.rotateY().getDirectionVec()).getZ(), this.pos.getZ()) - 2;
					int zMax = Math.max(this.pos.add(facing.rotateY().getDirectionVec()).getZ(), this.pos.getZ()) + 2;
					
					for (int y = 1; y <= 6; y++) {
						for (int x = xMin; x <= xMax; x++) {
							for (int z = zMin; z <= zMax; z++) {
								
								if (this.world.rand.nextInt(6) == 0) {
									
									BlockPos pos2 = new BlockPos(x, this.getPos().getY() + y, z);
									FluidState sourceFluid = this.world.getFluidState(pos2);
									
									if (this.world.getBlockState(pos2).getBlock() instanceof FlowingFluidBlock) {
										
										if (sourceFluid.getFluid() == Fluids.WATER) {
											
											if (this.world.rand.nextInt(5) == 0) {
												
												BlockState bottomState = this.world.getBlockState(pos2.down());
												
												if (bottomState.getBlock() == Industria.limestone_sheet) {
													this.world.setBlockState(pos2, Industria.limestone_sheet.getDefaultState().with(BlockStateProperties.WATERLOGGED, true));
													this.world.setBlockState(pos2.down(), Industria.limestone.getDefaultState());
												} else if (bottomState.getFluidState().getFluid() == Fluids.EMPTY && !bottomState.isAir()) {
													this.world.setBlockState(pos2, Industria.limestone_sheet.getDefaultState().with(BlockStateProperties.WATERLOGGED, true));
												}
												
											} else {
												
												this.world.setBlockState(pos2,ModFluids.STEAM.getPreasurized().getBlockState());
												
											}
											
										} else if (sourceFluid.getFluid() == ModFluids.DESTILLED_WATER) {
											
											if (sourceFluid.get(FluidDestilledWater.HOT)) {
												
												this.world.setBlockState(pos2, ModFluids.STEAM.getPreasurized().getBlockState());
												
											} else {
												
												this.world.setBlockState(pos2, ModFluids.DESTILLED_WATER.getHot().getBlockState());
												
											}
											
										}
										
									}
									
								}
								
							}
						}
					}
					
				}
				
			}
						
		} else {
			
			if (this.isSoundRunning()) {
				
				IParticleData paricle = ParticleTypes.FLAME;
				Direction facing = getBlockState().get(BlockMultiPart.FACING);
				
				float ox = 0;
				float oz = 0;
				
				switch(facing) {
				default:
				case NORTH:
					oz = 1F;
					ox = 1F;
					break;
				case EAST:
					ox = 0F;
					oz = 1F;
					break;
				case SOUTH:
					ox = 0F;
					oz = 0F;
					break;
				case WEST:
					ox = 1F;
					oz = 0F;
					break;
				}
				;
				float width = 0.9F;
				
				float x = this.pos.getX() + ox + (world.rand.nextFloat() - 0.5F) * width;
				float y = this.pos.getY() + 1.1F;
				float z = this.pos.getZ() + oz + (world.rand.nextFloat() - 0.5F) * width;
				this.world.addParticle(paricle, x, y, z, 0, 0, 0);
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.GENERATOR_LOOP);
				
			}
			
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	public boolean canWork() {
		return this.burnTime > 0 || this.hasFuelItems();
	}
	
	public boolean hasFuelItems() {
		return this.itemstacks.get(0).isEmpty() ? false : ForgeHooks.getBurnTime(this.itemstacks.get(0)) > 0;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putFloat("burnTime", this.burnTime);
		compound.putInt("fuelTime", this.fuelTime);
		compound.putBoolean("isWorking", this.isWorking);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.burnTime = compound.getFloat("burnTime");
		this.fuelTime = compound.getInt("fuelTime");
		this.isWorking = compound.getBoolean("isWorking");
		super.read(state, compound);
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMCoalHeater(id, playerInv, this);
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0 && ForgeHooks.getBurnTime(itemStackIn) > 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 1;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.coal_heater");
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
	}
	
}
