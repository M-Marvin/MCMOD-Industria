package de.redtec.fluids.util;

import javax.annotation.Nullable;

import de.redtec.RedTec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ItemGasBucket extends BucketItem {
	
	@SuppressWarnings("deprecation")
	public ItemGasBucket(Fluid containedFluidIn, String name, ItemGroup group) {
		super(containedFluidIn, new Properties().group(group).maxStackSize(1));
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
	}
	
	@SuppressWarnings("deprecation")
	public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult p_180616_4_) {
		
		BlockState blockstate = worldIn.getBlockState(posIn);
		Block block = blockstate.getBlock();
		Material material = blockstate.getMaterial();
		boolean flag = blockstate.isReplaceable(this.getFluid());
		boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, posIn, blockstate, this.getFluid());
		if (!flag1) {
			return p_180616_4_ != null && this.tryPlaceContainedLiquid(player, worldIn, p_180616_4_.getPos().offset(p_180616_4_.getFace()), (BlockRayTraceResult)null);
		} else if (worldIn.func_230315_m_().func_236040_e_() && this.getFluid().isIn(FluidTags.WATER)) {
			int i = posIn.getX();
			int j = posIn.getY();
			int k = posIn.getZ();
			worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

			for(int l = 0; l < 8; ++l) {
				worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
			}

			return true;
		} else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn,posIn,blockstate,getFluid())) {
			((ILiquidContainer)block).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid)this.getFluid()).getStillFluidState(false));
			this.playEmptySound(player, worldIn, posIn);
			return true;
		} else {
			if (!worldIn.isRemote && flag && !material.isLiquid()) {
				worldIn.destroyBlock(posIn, true);
			}

			if (!worldIn.setBlockState(posIn, this.getFluid().getDefaultState().getBlockState(), 11) && !blockstate.getFluidState().isSource()) {
				return false;
			} else {
				this.playEmptySound(player, worldIn, posIn);
				return true;
			}
		}
		
	}
	
}
