package de.industria.fluids;

import java.lang.reflect.Field;

import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBiogas extends BlockGasFluid {
	
	public BlockBiogas() {
		super("biogas", ModFluids.BIOGAS, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof FallingBlockEntity) {
			if (((FallingBlockEntity) entityIn).getBlockState().getBlock().is(BlockTags.FIRE)) {
				detonate(worldIn, pos);
			}
		}
		
		if (entityIn.showVehicleHealth()) {
			
			if (((LivingEntity) entityIn).isInvertedHealAndHarm()) return;
			
			entityIn.hurt(ModDamageSource.GAS, 1F);
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() instanceof FireBlock) {
			detonate(worldIn, pos);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	public void wasExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
		detonate(worldIn, pos);
		super.wasExploded(worldIn, pos, explosionIn);
	}
	
	public void detonate(World worldIn, BlockPos pos) {
		float spreadForce = 0.2F;
		int spreadAmountMin = 0;
		int spreadAmountRnd = 2;
		float explosionForce = 1.2F;
		
		worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, explosionForce, true, Mode.BREAK);
		for (int i = worldIn.random.nextInt(spreadAmountRnd) + spreadAmountMin; i >= 0; i--) {
			worldIn.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
			FallingBlockEntity spreadFire = new FallingBlockEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE.defaultBlockState());
			float mX = (worldIn.random.nextFloat() - 0.5F) * spreadForce;
			float mY = (worldIn.random.nextFloat() * 0.5F) * spreadForce;
			float mZ = (worldIn.random.nextFloat() - 0.5F) * spreadForce;
			spreadFire.setDeltaMovement(mX, mY, mZ);
			Field timeField;
			try {
				timeField = FallingBlockEntity.class.getDeclaredField("fallTime");
				timeField.setAccessible(true);
				timeField.set(spreadFire, 1);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			worldIn.addFreshEntity(spreadFire);
		}
	}
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}
	
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 250;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 300;
	}
	
}