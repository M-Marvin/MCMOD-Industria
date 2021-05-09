package de.industria.tileentity;

import de.industria.Industria;
import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.fluids.FluidDestilledWater;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityMHeaterBase extends TileEntityInventoryBase implements INamedContainerProvider, ISidedInventory, ITickableTileEntity, ISimpleMachineSound {
	
	public TileEntityMHeaterBase(TileEntityType<?> tileEntityTypeIn, int slots) {
		super(tileEntityTypeIn, slots);
	}

	public boolean isWorking;
	
	abstract public void updateWorkState();
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			this.isWorking = false;
			
			Direction facing = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
			boolean powered = this.world.isBlockPowered(pos) || this.world.isBlockPowered(pos.add(facing.getOpposite().getDirectionVec())) || this.world.isBlockPowered(pos.add(facing.rotateY().getDirectionVec())) || this.world.isBlockPowered(pos.add(facing.rotateY().getDirectionVec()).add(facing.getOpposite().getDirectionVec()));
			
			if (powered) {
				
				updateWorkState();
				
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
			
			if (this.isWorking) {
				
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
	
	abstract public boolean canWork();
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("isWorking", this.isWorking);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.isWorking = compound.getBoolean("isWorking");
		super.read(state, compound);
	}
		
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
	}
	
}
