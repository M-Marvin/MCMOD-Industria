package de.industria.fluids.util;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import de.industria.Industria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ItemFluidBucket extends BucketItem {

	public boolean canPlaceInNether;
	
	@SuppressWarnings("deprecation")
	public ItemFluidBucket(Fluid containedFluidIn, String name, ItemGroup group, boolean canPlaceInNether) {
		super(containedFluidIn, new Properties().maxStackSize(1).group(group).containerItem(Items.BUCKET));
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
		this.canPlaceInNether = canPlaceInNether;
	}

	@SuppressWarnings("deprecation")
	public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult rayTrace) {
		
		try {
			
			Field containedBlockField = BucketItem.class.getDeclaredField("containedBlock");
			containedBlockField.setAccessible(true);
			Fluid containedBlock = (Fluid) containedBlockField.get(this);
			
			if (!(containedBlock instanceof FlowingFluid)) {
				return false;
			} else {
				BlockState blockstate = worldIn.getBlockState(posIn);
				Block block = blockstate.getBlock();
				Material material = blockstate.getMaterial();
				boolean flag = blockstate.isReplaceable(containedBlock);
				boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, posIn, blockstate, containedBlock);
				if (!flag1) {
					return rayTrace != null && this.tryPlaceContainedLiquid(player, worldIn, rayTrace.getPos().offset(rayTrace.getFace()), (BlockRayTraceResult)null);
				} else if (!canPlaceInNether) {
					int i = posIn.getX();
					int j = posIn.getY();
					int k = posIn.getZ();
					worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

					for(int l = 0; l < 8; ++l) {
						worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
					}

					return true;
				} else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn,posIn,blockstate,containedBlock)) {
					((ILiquidContainer)block).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid)containedBlock).getStillFluidState(false));
					this.playEmptySound(player, worldIn, posIn);
					return true;
				} else {
					if (!worldIn.isRemote && flag && !material.isLiquid()) {
						worldIn.destroyBlock(posIn, true);
					}

					if (!worldIn.setBlockState(posIn, containedBlock.getDefaultState().getBlockState(), 11) && !blockstate.getFluidState().isSource()) {
						return false;
					} else {
						this.playEmptySound(player, worldIn, posIn);
						return true;
					}
				}
			}
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
}
