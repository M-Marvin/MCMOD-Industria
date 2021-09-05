package de.industria.blocks;

import java.util.function.Supplier;

import de.industria.typeregistys.MultipartBuildRecipes.MultipartBuildRecipe;
import de.industria.util.handler.UtilHelper;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockMultipartBuilded<T extends TileEntity> extends BlockMultipart<T> {
	
	protected final Supplier<MultipartBuildRecipe> multipartRecipe;
	
	public BlockMultipartBuilded(String name, Material material, float hardnessAndResistance, SoundType sound, int sizeX, int sizeY, int sizeZ, Supplier<MultipartBuildRecipe> recipe) {
		super(name, material, hardnessAndResistance, sound, sizeX, sizeY, sizeZ);
		this.multipartRecipe = recipe;
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		if (!player.isCreative()) {
			BlockPos partPos = BlockMultipart.getInternPartPos(state);
			BlockPos partOffset = UtilHelper.rotateBlockPos(partPos, state.getValue(BlockMultipart.FACING));
			BlockPos originPos = pos.subtract(partOffset);
			boolean success = restoreBlocks(world, originPos, world.getBlockState(originPos));
			if (success) {
				world.destroyBlock(pos, willHarvest);
				return false;
			}
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
	
	public MultipartBuildRecipe getMultipartRecipe() {
		return multipartRecipe.get();
	}
	
	@SuppressWarnings("deprecation")
	public boolean restoreBlocks(World world, BlockPos pos, BlockState state) {
		try {
			MultipartBuildLocation buildData = getBuildData(world, pos, state);
			if (buildData.isEmpty()) buildData = getMultipartRecipe().getStructureBuilder().getDefaultBuildData();
			
			for (int y = 0; y < getMultipartRecipe().getStructureBuilder().getSizeY(); y++) {
				for (int z = 0; z < getMultipartRecipe().getStructureBuilder().getSizeZ(); z++) {
					for (int x = 0; x < getMultipartRecipe().getStructureBuilder().getSizeX(); x++) {
						BlockPos offset = UtilHelper.rotateBlockPos(new BlockPos(x, y, z), state.getValue(FACING)).offset(pos);
						BlockState oldState = buildData.blockStates.get(new BlockPos(x, y, z));
						Rotation rotation = UtilHelper.directionToRotation(UtilHelper.directionToRotation(state.getValue(FACING)).rotate(buildData.orientation));
						world.setBlock(offset, oldState.rotate(rotation), 2);
					}
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean canStoreBuildData(World world, BlockPos pos, BlockState state) {
		return getInternPartPos(state).equals(BlockPos.ZERO);
	}
	public abstract void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData);
	public abstract MultipartBuildLocation getBuildData(World world, BlockPos pos, BlockState state);
	
}
