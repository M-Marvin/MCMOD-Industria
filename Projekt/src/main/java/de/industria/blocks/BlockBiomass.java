package de.industria.blocks;

import java.util.Random;

import de.industria.tileentity.TileEntityMHeaterBase;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockBiomass extends BlockFallingDust {
	
	public BlockBiomass() {
		super("biomass");
	}
	
	@Override
	public SoundType getSoundType(BlockState state) {
		return SoundType.GRAVEL;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		compost(state, worldIn, pos);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void compost(BlockState state, World world, BlockPos pos) {
		if (isHeated(state, world, pos)) {
			BlockPos gasPos = pos.above();
			BlockState gasState = ModFluids.BIOGAS.defaultFluidState().createLegacyBlock();
			BlockState replaceState = world.getBlockState(gasPos);
			int layers = state.getValue(LAYERS) - 1;
			if (layers > 0 && replaceState.isAir()) {
				world.setBlockAndUpdate(gasPos, gasState);
				world.setBlockAndUpdate(pos, this.defaultBlockState().setValue(LAYERS, layers));
			} else if (layers == 0){
				world.setBlockAndUpdate(pos, gasState);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean isIsolatedFromAir(BlockState state, World worldIn, BlockPos pos) {
		for (Direction d : Direction.values()) {
			BlockPos checkPos = pos.relative(d);
			BlockState checkState = worldIn.getBlockState(checkPos);
			if (!checkState.isFaceSturdy(worldIn, checkPos, d) || state.isAir()) return false;
		}
		return true;
	}
	
	public boolean isHeated(BlockState state, World worldIn, BlockPos pos) {
		for (int i = 0; i <= 5; i++) {
			BlockPos checkPos = pos.below(i);
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					BlockPos testPos = checkPos.offset(x, 0, z);
					BlockState checkState = worldIn.getBlockState(testPos);
					if (checkState.getBlock() == this) continue;
					if (checkState.getBlock() instanceof BlockMultiPart) {
						TileEntity heaterEntity = BlockMultiPart.getSCenterTE(testPos, checkState, worldIn);
						if (heaterEntity instanceof TileEntityMHeaterBase) {
							return ((TileEntityMHeaterBase) heaterEntity).isWorking;
						}
					}
				}
			}
		}
		return false;
	}
	
}
