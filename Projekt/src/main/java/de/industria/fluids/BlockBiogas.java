package de.industria.fluids;

import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBiogas extends BlockGasFluid {
	
	public BlockBiogas() {
		super("biogas", ModFluids.BIOGAS, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
		
	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state);
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.isLiving()) {
			
			if (((LivingEntity) entityIn).isEntityUndead()) return;
			
			entityIn.attackEntityFrom(ModDamageSource.GAS, 1F);
			
		}
		
	}
	
}