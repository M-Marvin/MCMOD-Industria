package de.m_marvin.industria.blocks;

import java.util.function.Supplier;

import de.m_marvin.industria.registries.ModBlockStateProperties;
import de.m_marvin.industria.registries.ModBlocks;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ConduitClampBlock extends Block {
	
	public ConduitClampBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> props) {
		props.add(BlockStateProperties.FACING, ModBlockStateProperties.ORIENTATION);
	}
	
	public static enum ConduitClampType implements StringRepresentable {
		NONE("none", () -> null),IRON("iron", () -> (ConduitClampBlock) ModBlocks.IRON_CONDUIT_CLAMP);
		
		private String name;
		private Supplier<ConduitClampBlock> block;
		
		private ConduitClampType(String name, Supplier<ConduitClampBlock> blockSupplier) {
			this.name = name;
			this.block = blockSupplier;
		}
		@Override
		public String getSerializedName() {
			return name;
		}
		public ConduitClampBlock getClampBlock() {
			return block.get();
		}
	}
	
}
