package de.redtec.fluids;

import java.util.Random;

import de.redtec.fluids.util.BlockModFlowingFluid;
import de.redtec.registys.ModDamageSource;
import de.redtec.registys.ModTags;
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
		super("destilled_water", ModFluids.DESTILLED_WATER, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
		this.setDefaultState(this.stateContainer.getBaseState().with(HOT, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HOT);
		super.fillStateContainer(builder);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).with(FluidDestilledWater.HOT, state.get(HOT));
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.isLiving() && state.get(HOT)) {
			
			if (((LivingEntity) entityIn).isPotionActive(Effects.FIRE_RESISTANCE)) return;
			
			boolean hasLeatherLeggings = false;
			boolean hasLeatherBoots = false;
			Iterable<ItemStack> armor = entityIn.getArmorInventoryList();
			for (ItemStack item : armor) {
				if (item.getItem() == Items.LEATHER_BOOTS) hasLeatherBoots = true;
				if (item.getItem() == Items.LEATHER_LEGGINGS) hasLeatherLeggings = true;
			}
			boolean isHeadInFluid = entityIn.areEyesInFluid(ModTags.HOT_WATER);
			
			int dammage = hasLeatherBoots && hasLeatherLeggings ? 0 : 1;
			if (isHeadInFluid) dammage += 1;
			
			if (dammage > 0 )entityIn.attackEntityFrom(ModDamageSource.HOT_FLUID, dammage);
			
		}
		
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (stateIn.get(HOT)) {
			
			float fx = rand.nextFloat() + pos.getX();
			float fy = rand.nextFloat() + pos.getY();
			float fz = rand.nextFloat() + pos.getZ();
			
			worldIn.addParticle(ParticleTypes.CLOUD, fx, fy, fz, 0, 0.1F, 0);
			
		}
		
		super.animateTick(stateIn, worldIn, pos, rand);
	}
	
}
