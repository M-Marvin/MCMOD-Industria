package de.industria.fluids;

import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCompressedAir extends BlockGasFluid {
	
	public BlockCompressedAir() {
		super("compressed_air", ModFluids.COMPRESSED_AIR, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.isLiving() && worldIn.rand.nextInt(500) == 0) {

			((LivingEntity) entityIn).heal(0.5F);
			
		}
		
	}
	
}