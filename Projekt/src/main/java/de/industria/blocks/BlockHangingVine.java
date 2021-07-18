package de.industria.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockHangingVine extends BlockBase {
	
	public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
	public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
	
	public BlockHangingVine() {
		super("hanging_vine", Properties.of(Material.PLANT).strength(0).sound(SoundType.VINE).noCollission().randomTicks());
		this.registerDefaultState(stateDefinition.any().setValue(PART, Part.END).setValue(AGE, 0));
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(PART, AGE);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return state.getValue(PART) == Part.END ? Block.box(2, 8, 2, 14, 16, 14) : Block.box(2, 0, 2, 14, 16, 14);
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (worldIn.getBlockState(pos.above()).getBlock() == this) {
			return true;
		} else {
			return isValidGround(worldIn.getBlockState(pos.above()), worldIn, pos.above());
		}
	}
	
	public boolean isValidGround(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return state.canOcclude() || state.getBlock() instanceof LeavesBlock;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (fromPos.equals(pos.above())) {
			if (!this.canSurvive(state, worldIn, pos)) {
				worldIn.getBlockTicks().scheduleTick(pos, this, 1);
			}
		} else if (fromPos.equals(pos.below())) {
			worldIn.setBlockAndUpdate(pos, state.setValue(PART, worldIn.getBlockState(fromPos).getBlock() == this ? Part.PLANT : Part.END).setValue(AGE, 0));
		}
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (!this.canSurvive(state, worldIn, pos)) {
			worldIn.destroyBlock(pos, true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (state.getValue(AGE) < 25 && random.nextInt(6) == 0 && worldIn.getBlockState(pos.below()).isAir()) {
			int age = Math.min(state.getValue(AGE) + random.nextInt(5) + 1, 25);
			worldIn.setBlockAndUpdate(pos.below(), this.defaultBlockState().setValue(PART, Part.END).setValue(AGE, age));
		}
	}
	
	public enum Part implements IStringSerializable {
		
		END("end"),PLANT("plant");
		
		protected String name;
		
		private Part(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
				
	}
	
}
