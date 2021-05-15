package de.industria.fluids;

import java.util.Random;

import de.industria.Industria;
import de.industria.fluids.util.GasFluid;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;

public class FluidCompressedAir extends GasFluid implements IBucketPickupHandler {
	
	@Override
	public Item getFilledBucket() {
		return Items.BUCKET;
	}
	
	@Override
	protected BlockState getBlockState(FluidState state) {
		return Industria.compressed_air.getDefaultState();
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		worldIn.removeBlock(pos, false);
		return this;
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
			new ResourceLocation(Industria.MODID, "block/compressed_air_still"), 
			new ResourceLocation(Industria.MODID, "block/compressed_air_flow"))
				.gaseous()
				.build(this);
	}
	
	@Override
	public void onMoved(World world, BlockPos pos, Direction moveDirection, FluidState state, Random random) {
		
		if (random.nextInt(3) == 0 && world.canSeeSky(pos.offset(moveDirection))) {

			world.setBlockState(pos.offset(moveDirection), Blocks.AIR.getDefaultState());
			
		}
		
	}
	
}