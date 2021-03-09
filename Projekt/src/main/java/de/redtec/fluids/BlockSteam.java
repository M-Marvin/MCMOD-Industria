package de.redtec.fluids;

import java.util.Random;

import de.redtec.fluids.util.BlockGasFluid;
import de.redtec.typeregistys.ModDamageSource;
import de.redtec.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSteam extends BlockGasFluid {
	
	public static final BooleanProperty PREASURIZED = BooleanProperty.create("preasurized");
	
	public BlockSteam() {
		super("steam", ModFluids.STEAM, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
		this.setDefaultState(this.stateContainer.getBaseState().with(PREASURIZED, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(PREASURIZED);
		super.fillStateContainer(builder);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).with(FluidSteam.PREASURIZED, state.get(PREASURIZED));
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.isLiving()) {

			if (((LivingEntity) entityIn).isPotionActive(Effects.FIRE_RESISTANCE)) return;
			
			entityIn.attackEntityFrom(ModDamageSource.HOT_STEAM, 1F);
			
		}
		
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (rand.nextInt(3) == 0) {

			float fx = rand.nextFloat() + pos.getX();
			float fy = rand.nextFloat() + pos.getY();
			float fz = rand.nextFloat() + pos.getZ();
			
			worldIn.addParticle(ParticleTypes.CLOUD, fx, fy, fz, 0, 0.1F, 0);
			
		}
		
		super.animateTick(stateIn, worldIn, pos, rand);
	}
	
}
