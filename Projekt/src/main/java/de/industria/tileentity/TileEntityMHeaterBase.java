package de.industria.tileentity;

import de.industria.blocks.BlockMultipart;
import de.industria.fluids.FluidDestilledWater;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityMHeaterBase extends TileEntityInventoryBase implements ISidedInventory, ITickableTileEntity, ITESimpleMachineSound {
	
	public TileEntityMHeaterBase(TileEntityType<?> tileEntityTypeIn, int slots) {
		super(tileEntityTypeIn, slots);
	}

	public boolean isWorking;
	public boolean powered;
	
	abstract public void updateWorkState();
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			this.isWorking = false;
			
			Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
			powered = this.level.hasNeighborSignal(worldPosition) || this.level.hasNeighborSignal(worldPosition.offset(facing.getOpposite().getNormal())) || this.level.hasNeighborSignal(worldPosition.offset(facing.getClockWise().getNormal())) || this.level.hasNeighborSignal(worldPosition.offset(facing.getClockWise().getNormal()).offset(facing.getOpposite().getNormal()));
			
			if (powered) {
				
				updateWorkState();
				
				if (isWorking ? this.level.getGameTime() %10 == 0 : false) {
					
					int xMin = Math.min(this.worldPosition.offset(facing.getOpposite().getNormal()).getX(), this.worldPosition.getX()) - 2;
					int xMax = Math.max(this.worldPosition.offset(facing.getOpposite().getNormal()).getX(), this.worldPosition.getX()) + 2;
					int zMin = Math.min(this.worldPosition.offset(facing.getClockWise().getNormal()).getZ(), this.worldPosition.getZ()) - 2;
					int zMax = Math.max(this.worldPosition.offset(facing.getClockWise().getNormal()).getZ(), this.worldPosition.getZ()) + 2;
					
					for (int y = 1; y <= 6; y++) {
						for (int x = xMin; x <= xMax; x++) {
							for (int z = zMin; z <= zMax; z++) {
								
								if (this.level.random.nextInt(6) == 0) {
									
									BlockPos pos2 = new BlockPos(x, this.getBlockPos().getY() + y, z);
									FluidState sourceFluid = this.level.getFluidState(pos2);
									
									if (this.level.getBlockState(pos2).getBlock() instanceof FlowingFluidBlock) {
										
										if (sourceFluid.getType() == Fluids.WATER) {
											
											if (this.level.random.nextInt(5) == 0) {
												
												BlockState bottomState = this.level.getBlockState(pos2.below());
												
												if (bottomState.getBlock() == ModItems.limestone_sheet) {
													this.level.setBlockAndUpdate(pos2, ModItems.limestone_sheet.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
													this.level.setBlockAndUpdate(pos2.below(), ModItems.limestone.defaultBlockState());
												} else if (bottomState.getFluidState().getType() == Fluids.EMPTY && !bottomState.isAir()) {
													this.level.setBlockAndUpdate(pos2, ModItems.limestone_sheet.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
												}
												
											} else {
												
												this.level.setBlockAndUpdate(pos2,ModFluids.STEAM.getPreasurized().createLegacyBlock());
												
											}
											
										} else if (sourceFluid.getType() == ModFluids.DESTILLED_WATER) {
											
											if (sourceFluid.getValue(FluidDestilledWater.HOT)) {
												
												this.level.setBlockAndUpdate(pos2, ModFluids.STEAM.getPreasurized().createLegacyBlock());
												
											} else {
												
												this.level.setBlockAndUpdate(pos2, ModFluids.DESTILLED_WATER.getHot().createLegacyBlock());
												
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
			
			if (this.isWorking) {
				
				IParticleData paricle = ParticleTypes.FLAME;
				Direction facing = getBlockState().getValue(BlockMultipart.FACING);
				
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
				
				float x = this.worldPosition.getX() + ox + (level.random.nextFloat() - 0.5F) * width;
				float y = this.worldPosition.getY() + 1.1F;
				float z = this.worldPosition.getZ() + oz + (level.random.nextFloat() - 0.5F) * width;
				this.level.addParticle(paricle, x, y, z, 0, 0, 0);
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.GENERATOR_LOOP);
				
			}
			
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	abstract public boolean canWork();
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putBoolean("isWorking", this.isWorking);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.isWorking = compound.getBoolean("isWorking");
		super.load(state, compound);
	}
		
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 2, 2));
	}
	
}
