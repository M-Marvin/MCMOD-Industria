package de.redtec.tileentity;

import de.redtec.RedTec;
import de.redtec.blocks.BlockMCoalHeater;
import de.redtec.dynamicsounds.ISimpleMachineSound;
import de.redtec.dynamicsounds.SoundMachine;
import de.redtec.fluids.FluidDestilledWater;
import de.redtec.gui.ContainerMCoalHeater;
import de.redtec.typeregistys.ModFluids;
import de.redtec.typeregistys.ModSoundEvents;
import de.redtec.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class TileEntityMCoalHeater extends TileEntityInventoryBase implements INamedContainerProvider, ISidedInventory, ITickableTileEntity, ISimpleMachineSound {
	
	public int fuelTime;
	public float burnTime;
	
	public TileEntityMCoalHeater() {
		super(ModTileEntityType.COAL_HEATER, 2);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			boolean isWorking = false;
			
			if (this.world.isBlockPowered(pos)) {
				
				if (this.burnTime > 0) {
					this.burnTime -= 1;
					isWorking = true;
				} else if (hasFuelItems()) {
					this.burnTime = ForgeHooks.getBurnTime(this.itemstacks.get(0));
					this.fuelTime = (int) this.burnTime;
					this.itemstacks.get(0).shrink(1);
					isWorking = true;
				}
				
				if (isWorking ? this.world.getGameTime() %10 == 0 : false) {
					
					for (int y = 1; y <= 6; y++) {
						for (int x = -1; x <= 2; x++) {
							for (int z = -1; z <= 2; z++) {
								
								if (this.world.rand.nextInt(10) == 0) {
									
									BlockPos pos2 = this.pos.add(new BlockPos(x, y, z));
									FluidState sourceFluid = this.world.getFluidState(pos2);
									
									if (this.world.getBlockState(pos2).getBlock() instanceof FlowingFluidBlock) {
										
										if (sourceFluid.getFluid() == Fluids.WATER) {
											
											if (this.world.rand.nextInt(1) == 0) {
												
												BlockState bottomState = this.world.getBlockState(pos2.down());
												
												if (bottomState.getBlock() == RedTec.limestone_sheet) {
													this.world.setBlockState(pos2, RedTec.limestone_sheet.getDefaultState());
													this.world.setBlockState(pos2.down(), RedTec.limestone.getDefaultState());
												} else if (bottomState.getFluidState().getFluid() == Fluids.EMPTY && !bottomState.isAir()) {
													this.world.setBlockState(pos2, RedTec.limestone_sheet.getDefaultState());
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
			
			boolean activeState = this.getBlockState().get(BlockMCoalHeater.LIT);
			if (isWorking != activeState) this.world.setBlockState(pos, this.getBlockState().with(BlockMCoalHeater.LIT, isWorking));
			
		} else {
			
			if (this.isSoundRunning()) {
				
				SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
				
				if (this.sound == null ? true : !soundHandler.isPlaying(sound)) {
					
					this.sound = new SoundMachine(this, ModSoundEvents.GENERATOR_LOOP); // TODO
					soundHandler.play(this.sound);
					
				}
				
			}
			
		}
		
	}

	private SoundMachine sound;

	@Override
	public boolean isSoundRunning() {
		return this.getBlockState().get(BlockMCoalHeater.LIT);
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
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.burnTime = compound.getFloat("burnTime");
		this.fuelTime = compound.getInt("fuelTime");
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
		return new TranslationTextComponent("block.redtec.coal_heater");
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}
