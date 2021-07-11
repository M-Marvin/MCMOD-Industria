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

public class BlockFuelGas extends BlockGasFluid {
	
	public BlockFuelGas() {
		super("fuel_gas", ModFluids.FUEL_GAS, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof FallingBlockEntity) {
			if (((FallingBlockEntity) entityIn).getBlockState().getBlock().isIn(BlockTags.FIRE)) {
				detonate(worldIn, pos);
			}
		}
		
		if (entityIn.isLiving()) {
			
			if (((LivingEntity) entityIn).isEntityUndead()) return;
			
			entityIn.attackEntityFrom(ModDamageSource.GAS, 1F);
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() instanceof FireBlock) {
			detonate(worldIn, pos);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		detonate(worldIn, pos);
		super.onExplosionDestroy(worldIn, pos, explosionIn);
	}
	
	public void detonate(World worldIn, BlockPos pos) {
		float spreadForce = 1;
		int spreadAmountMin = 1;
		int spreadAmountRnd = 2;
		float explosionForce = 3;
		
		worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, explosionForce, true, Mode.BREAK);
		for (int i = worldIn.rand.nextInt(spreadAmountRnd) + spreadAmountMin; i >= 0; i--) {
			worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
			FallingBlockEntity spreadFire = new FallingBlockEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE.getDefaultState());
			float mX = (worldIn.rand.nextFloat() - 0.5F) * spreadForce;
			float mY = (worldIn.rand.nextFloat() * 0.5F) * spreadForce;
			float mZ = (worldIn.rand.nextFloat() - 0.5F) * spreadForce;
			spreadFire.setMotion(mX, mY, mZ);
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
			worldIn.addEntity(spreadFire);
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