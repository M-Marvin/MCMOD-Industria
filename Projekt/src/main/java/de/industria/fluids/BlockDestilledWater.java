package de.industria.fluids;

import java.util.Random;

import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDestilledWater extends BlockModFlowingFluid {
	
	public static final BooleanProperty HOT = BooleanProperty.create("hot");
	
	public BlockDestilledWater() {
		super("destilled_water", ModFluids.DESTILLED_WATER, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(HOT, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(HOT);
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).setValue(FluidDestilledWater.HOT, state.getValue(HOT));
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.showVehicleHealth() && state.getValue(HOT)) {
			
			if (((LivingEntity) entityIn).hasEffect(Effects.FIRE_RESISTANCE)) return;
			
			boolean hasLeatherLeggings = false;
			boolean hasLeatherBoots = false;
			Iterable<ItemStack> armor = entityIn.getArmorSlots();
			for (ItemStack item : armor) {
				if (item.getItem() == Items.LEATHER_BOOTS) hasLeatherBoots = true;
				if (item.getItem() == Items.LEATHER_LEGGINGS) hasLeatherLeggings = true;
			}
			boolean isHeadInFluid = entityIn.isEyeInFluid(ModTags.HOT_WATER);
			
			int dammage = hasLeatherBoots && hasLeatherLeggings ? 0 : 1;
			if (isHeadInFluid) dammage += 1;
			
			if (dammage > 0 )entityIn.hurt(ModDamageSource.HOT_FLUID, dammage);
			
		}
		
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (stateIn.getValue(HOT)) {
			
			float fx = rand.nextFloat() + pos.getX();
			float fy = rand.nextFloat() + pos.getY();
			float fz = rand.nextFloat() + pos.getZ();
			
			worldIn.addParticle(ParticleTypes.CLOUD, fx, fy, fz, 0, 0.1F, 0);
			
		}
		
		super.animateTick(stateIn, worldIn, pos, rand);
	}
	
}
