package de.industria.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockRubberLog extends BlockLogBase {
	
	public final static EnumProperty<RipeState> RIPE_STATE = EnumProperty.create("ripe_state", RipeState.class);
	
	public BlockRubberLog(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(name, material, hardnessAndResistance, sound);
		this.registerDefaultState(this.defaultBlockState().setValue(RIPE_STATE, RipeState.CANT_BE_RIPE));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(RIPE_STATE);
		super.createBlockStateDefinition(builder);
	}
	
	public static enum RipeState implements IStringSerializable {
		CANT_BE_RIPE("cant_be_ripe"),CAN_BE_RIPE("can_be_ripe"),IS_RIPE("ripe");
		private String name;
		private RipeState(String name) {
			this.name = name;
		}
		@Override
		public String getSerializedName() {
			return name;
		}
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (state.getValue(RIPE_STATE) == RipeState.CAN_BE_RIPE) {
			
			if (random.nextInt(30) == 0) {
				
				worldIn.setBlockAndUpdate(pos, state.setValue(RIPE_STATE, RipeState.IS_RIPE));
				
			} else if (random.nextInt(120) == 0) {
				
				worldIn.setBlockAndUpdate(pos, state.setValue(RIPE_STATE, RipeState.CANT_BE_RIPE));
				
			}
			
		}
		
	}
	
}
