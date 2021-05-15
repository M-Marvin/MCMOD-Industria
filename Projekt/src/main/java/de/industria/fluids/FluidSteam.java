package de.industria.fluids;

import java.util.Random;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.fluids.util.GasFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;

public class FluidSteam extends GasFluid implements IBucketPickupHandler {
	
	public static final BooleanProperty PRESSURIZED = BooleanProperty.create("pressurized");
	
	public FluidSteam() {
		this.setDefaultState(this.stateContainer.getBaseState().with(PRESSURIZED, false));
	}
	
	@Override
	public Item getFilledBucket() {
		return ModItems.steam_bucket;
	}
	
	@Override
	protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
		builder.add(PRESSURIZED);
		super.fillStateContainer(builder);
	}
	
	@Override
	protected BlockState getBlockState(FluidState state) {
		return ModItems.steam.getDefaultState().with(BlockSteam.PRESSURIZED, state.get(PRESSURIZED));
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		worldIn.removeBlock(pos, false);
		return this;
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
			new ResourceLocation(Industria.MODID, "block/steam_still"), 
			new ResourceLocation(Industria.MODID, "block/steam_flow"))
				.gaseous()
				.build(this);
	}
	
	@Override
	public void onMoved(World world, BlockPos pos, Direction moveDirection, FluidState state, Random random) {
		
		if (random.nextInt(20) == 0 && world.canSeeSky(pos)) {
					
			if (world.getBlockState(pos.offset(moveDirection)).getFluidState().getFluid() == this) world.setBlockState(pos.offset(moveDirection), Blocks.AIR.getDefaultState());
			
			if (pos.getY() < 150) {

				FallingBlockEntity condensetWater = new FallingBlockEntity(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, ModFluids.DESTILLED_WATER.getDefaultState().with(FluidDestilledWater.HOT, true).getBlockState());
				condensetWater.fallTime = -1000;
				world.addEntity(condensetWater);
				
			}
			
		}
		
	}

	public FluidState getPreasurized() {
		return this.getDefaultState().with(PRESSURIZED, true);
	}
	
	public FluidState getNormal() {
		return this.getDefaultState().with(PRESSURIZED, false);
	}
	
}