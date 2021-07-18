package de.industria.fluids;

import java.util.Random;

import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModFluids;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSteam extends BlockGasFluid {
	
	public static final BooleanProperty PRESSURIZED = BooleanProperty.create("pressurized");
	
	public BlockSteam() {
		super("steam", ModFluids.STEAM, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(PRESSURIZED, false));
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(PRESSURIZED);
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).setValue(FluidSteam.PRESSURIZED, state.getValue(PRESSURIZED));
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.showVehicleHealth()) {

			if (((LivingEntity) entityIn).hasEffect(Effects.FIRE_RESISTANCE)) return;
			
			entityIn.hurt(ModDamageSource.HOT_STEAM, 1F);
			
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
