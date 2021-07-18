package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLeavesBase extends LeavesBlock {
	
	public BlockLeavesBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound).randomTicks().noOcclusion());
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 30;
	}
	
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
	
}
