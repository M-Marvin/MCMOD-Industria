package de.industria.fluids;

import java.util.Random;

import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNatronLye extends BlockModFlowingFluid {
	
	public BlockNatronLye() {
		super("natron_lye", ModFluids.NATRON_LYE, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		entityIn.hurt(ModDamageSource.ACID, 0.5F);
		if (entityIn instanceof ItemEntity) {
			entityIn.remove();
			worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

		if (worldIn.getBlockState(pos.above()).isAir()) {
			float fx = rand.nextFloat() + pos.getX();
			float fy = rand.nextFloat() + pos.getY();
			float fz = rand.nextFloat() + pos.getZ();
			
			worldIn.addParticle(ParticleTypes.CLOUD, fx, fy, fz, 0, 0.1F, 0);
		}
		
		super.animateTick(stateIn, worldIn, pos, rand);
		
	}
	
}