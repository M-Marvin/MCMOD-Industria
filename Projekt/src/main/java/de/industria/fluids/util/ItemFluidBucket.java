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
		super(containedFluidIn, new Properties().stacksTo(1).tab(group).craftRemainder(Items.BUCKET));
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
		this.canPlaceInNether = canPlaceInNether;
	}

	@SuppressWarnings("deprecation")
	public boolean emptyBucket(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult rayTrace) {
		
		try {
			
			Field containedBlockField = BucketItem.class.getDeclaredField("content");
			containedBlockField.setAccessible(true);
			Fluid containedBlock = (Fluid) containedBlockField.get(this);
			
			if (!(containedBlock instanceof FlowingFluid)) {
				return false;
			} else {
				BlockState blockstate = worldIn.getBlockState(posIn);
				Block block = blockstate.getBlock();
				Material material = blockstate.getMaterial();
				boolean flag = blockstate.canBeReplaced(containedBlock);
				boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(worldIn, posIn, blockstate, containedBlock);
				if (!flag1) {
					return rayTrace != null && this.emptyBucket(player, worldIn, rayTrace.getBlockPos().relative(rayTrace.getDirection()), (BlockRayTraceResult)null);
				} else if (!canPlaceInNether && worldIn.dimensionType().ultraWarm()) {
					int i = posIn.getX();
					int j = posIn.getY();
					int k = posIn.getZ();
					worldIn.playSound(player, posIn, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

					for(int l = 0; l < 8; ++l) {
						worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
					}

					return true;
				} else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(worldIn,posIn,blockstate,containedBlock)) {
					((ILiquidContainer)block).placeLiquid(worldIn, posIn, blockstate, ((FlowingFluid)containedBlock).getSource(false));
					this.playEmptySound(player, worldIn, posIn);
					return true;
				} else {
					if (!worldIn.isClientSide && flag && !material.isLiquid()) {
						worldIn.destroyBlock(posIn, true);
					}

					if (!worldIn.setBlock(posIn, containedBlock.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
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
