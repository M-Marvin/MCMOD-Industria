package de.industria.blocks;

import java.util.Random;

import de.industria.ModItems;
import de.industria.tileentity.TileEntityMHeaterBase;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.Block;
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
		return SoundType.GROUND;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (worldIn.getBlockState(fromPos).getBlock() != ModItems.biogas) compost(state, worldIn, pos);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		compost(state, worldIn, pos);
	}
	
	@SuppressWarnings("deprecation")
	public void compost(BlockState state, World world, BlockPos pos) {
		
		if (world.rand.nextInt(3) == 0) {
			if (isHeated(state, world, pos)) {
				BlockPos gasPos = pos.up();
				BlockState gasState = ModFluids.BIOGAS.getDefaultState().getBlockState();
				BlockState replaceState = world.getBlockState(gasPos);
				int layers = state.get(LAYERS) - 1;
				if (layers > 0 && (replaceState.isAir() || replaceState.isReplaceable(ModFluids.BIOGAS))) {
					world.setBlockState(gasPos, gasState);
					world.setBlockState(pos, this.getDefaultState().with(LAYERS, layers));
				} else {
					world.setBlockState(pos, gasState);
				}
			} else if (isIsolatedFromAir(state, world, pos) && world.rand.nextInt(5) == 0) {
				world.setBlockState(pos, ModItems.peat.getDefaultState());
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean isIsolatedFromAir(BlockState state, World worldIn, BlockPos pos) {
		for (Direction d : Direction.values()) {
			BlockPos checkPos = pos.offset(d);
			BlockState checkState = worldIn.getBlockState(checkPos);
			if (!checkState.isSolidSide(worldIn, checkPos, d) || state.isAir()) return false;
		}
		return true;
	}
	
	public boolean isHeated(BlockState state, World worldIn, BlockPos pos) {
		for (int i = 0; i <= 5; i++) {
			BlockPos checkPos = pos.down(i);
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					BlockPos testPos = checkPos.add(x, 0, z);
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
