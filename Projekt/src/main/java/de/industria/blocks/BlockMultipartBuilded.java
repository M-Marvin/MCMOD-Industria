package de.industria.blocks;

import java.util.function.Supplier;

import de.industria.multipartbuilds.MultipartBuild.MultipartBuildLocation;
import de.industria.typeregistys.MultipartBuildRecipes.MultipartBuildRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockMultipartBuilded<T extends TileEntity> extends BlockMultipart<T> {
	
	protected final Supplier<MultipartBuildRecipe> multipartRecipe;
	
	public BlockMultipartBuilded(String name, Material material, float hardnessAndResistance, SoundType sound, int sizeX, int sizeY, int sizeZ, Supplier<MultipartBuildRecipe> recipe) {
		super(name, material, hardnessAndResistance, sound, sizeX, sizeY, sizeZ);
		this.multipartRecipe = recipe;
	}
	
	public MultipartBuildRecipe getMultipartRecipe() {
		return multipartRecipe.get();
	}
	
	public void restoreBlocks(World world, BlockPos pos, BlockState state) {
		
		// TODO
		
	}
	
	public boolean canStoreBuildData(World world, BlockPos pos, BlockState state) {
		return getInternPartPos(state).equals(BlockPos.ZERO);
	}
	public abstract void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData);
	
}
