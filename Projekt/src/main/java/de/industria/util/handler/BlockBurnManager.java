package de.industria.util.handler;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import de.industria.ModItems;
import de.industria.blocks.BlockFallingDust;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockBurnManager {
	
	protected static HashMap<Block, IBurnedSupplier> blockToBurnedMap = new HashMap<Block, IBurnedSupplier>();
	
	public static BlockState[] getBurnedVariants(BlockState[] unburnedStates, IWorld world, BlockPos pos) {
		if (world.getRandom().nextFloat() <= 40F / 100F) {
			if (world.getRandom().nextFloat() <= 40F / 100F) {
				return Stream.of(unburnedStates).map(new Function<BlockState, BlockState>() {
					@Override
					public BlockState apply(BlockState t) {
						return ModItems.ash.getDefaultState().with(BlockFallingDust.LAYERS, world.getRandom().nextInt(4) + 2);
					}
				}).toArray(BlockState[]::new);
			}
			return Stream.of(unburnedStates).map((state) -> {
				return blockToBurnedMap.getOrDefault(state.getBlock(), DefaultBurnedSuppliers.NO_BEHAVIOR.getSupplier()).getBurnedState(state, world, pos);
			}).toArray(BlockState[]::new);
		}
		return unburnedStates;
	}
	
	public static BlockState getBurnedVariant(BlockState unburnedState, IWorld world, BlockPos pos) {
		return getBurnedVariants(new BlockState[] {unburnedState}, world, pos)[0];
	}
	
	public static void reloadBurnBehaviors() {
		blockToBurnedMap.clear();
		ForgeRegistries.BLOCKS.forEach((registredBlock) -> {
			if (registredBlock instanceof RotatedPillarBlock && registredBlock.isIn(BlockTags.LOGS_THAT_BURN)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.LOGS);
			} else if (registredBlock.isIn(BlockTags.PLANKS) && !registredBlock.isIn(BlockTags.NON_FLAMMABLE_WOOD)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.PLANKS);
			} else if (registredBlock instanceof StairsBlock && registredBlock.isIn(BlockTags.WOODEN_STAIRS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.STAIRS);
			} else if (registredBlock instanceof SlabBlock && registredBlock.isIn(BlockTags.WOODEN_SLABS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.SLABS);
			} else if (registredBlock instanceof TrapDoorBlock && registredBlock.isIn(BlockTags.WOODEN_TRAPDOORS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.TRAPDOORS);
			} else if (registredBlock instanceof DoorBlock && registredBlock.isIn(BlockTags.WOODEN_DOORS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.DOORS);
			} else if (registredBlock instanceof FenceBlock && registredBlock.isIn(BlockTags.WOODEN_FENCES)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.FENCES);
			} else if (registredBlock.getDefaultState().getMaterial() == Material.WOOL) {
				setBurnBehavior(registredBlock, DefaultBurnedSuppliers.NO_BEHAVIOR);
			} else if (registredBlock.getDefaultState().isOpaqueCube(null, null) && registredBlock.isFlammable(registredBlock.getDefaultState(), null, null, Direction.UP)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.MISC_BLOCKS);
			} else if (registredBlock.isFlammable(registredBlock.getDefaultState(), null, null, Direction.UP)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.MISC_OBJECTS);
			}
		});
		setBurnBehavior(Blocks.COAL_BLOCK, DefaultBurnedSuppliers.NO_BEHAVIOR);
		setBurnBehavior(Blocks.TNT, DefaultBurnedSuppliers.NO_BEHAVIOR);
	}
	
	public static void setBurnBehavior(Block block, IBurnedSupplier supplier) {
		blockToBurnedMap.put(block, supplier);
	}
	public static void setBurnBehavior(Block block, DefaultBurnedSuppliers supplier) {
		blockToBurnedMap.put(block, supplier.getSupplier());
	}
	
	@FunctionalInterface
	public static interface IBurnedSupplier {
		public BlockState getBurnedState(BlockState state, IWorld world, BlockPos pos);
	}
	
	public static enum DefaultBurnedSuppliers {
		
		LOGS((state, world, pos) -> {
			return ModItems.burned_log.getNotPersistant().with(BlockStateProperties.AXIS, state.get(BlockStateProperties.AXIS));
		}),
		PLANKS((state, world, pos) -> {
			return ModItems.burned_planks.getNotPersistant();
		}),
		MISC_BLOCKS((state, world, pos) -> {
			return ModItems.burned_block.getNotPersistant();
		}),
		STAIRS((state, world, pos) -> {
			return ModItems.burned_stairs.getNotPersistant(); // TODO
		}),
		SLABS((state, world, pos) -> {
			return ModItems.burned_wood_slab.getNotPersistant().with(BlockStateProperties.SLAB_TYPE, state.get(BlockStateProperties.SLAB_TYPE));
		}),
		FENCES((state, world, pos) -> {
			return ModItems.burned_fence.getNotPersistant().with(BlockStateProperties.NORTH, state.get(BlockStateProperties.NORTH)).with(BlockStateProperties.SOUTH, state.get(BlockStateProperties.SOUTH)).with(BlockStateProperties.EAST, state.get(BlockStateProperties.EAST)).with(BlockStateProperties.WEST, state.get(BlockStateProperties.WEST)).with(BlockStateProperties.WATERLOGGED, state.get(BlockStateProperties.WATERLOGGED));
		}),
		@SuppressWarnings("deprecation")
		MISC_OBJECTS((state, world, pos) -> {
			BlockState bottomState = world.getBlockState(pos.down());
			if (bottomState.isOpaqueCube(world, pos.down()) && !bottomState.isAir()) {
				return ModItems.burned_scrap.getNotPersistant();
			}
			return ModItems.ash.getDefaultState();
		}),
		TRAPDOORS((state, world, pos) -> {
			return ModItems.burned_trapdoor.getNotPersistant().with(BlockStateProperties.HORIZONTAL_FACING, state.get(BlockStateProperties.HORIZONTAL_FACING)).with(BlockStateProperties.HALF, state.get(BlockStateProperties.HALF)).with(BlockStateProperties.OPEN, state.get(BlockStateProperties.OPEN)).with(BlockStateProperties.POWERED, state.get(BlockStateProperties.POWERED));
		}),
		DOORS((state, world, pos) -> {
			return ModItems.burned_door.getNotPersistant().with(BlockStateProperties.HORIZONTAL_FACING, state.get(BlockStateProperties.HORIZONTAL_FACING)).with(BlockStateProperties.DOUBLE_BLOCK_HALF, state.get(BlockStateProperties.DOUBLE_BLOCK_HALF)).with(BlockStateProperties.OPEN, state.get(BlockStateProperties.OPEN)).with(BlockStateProperties.POWERED, state.get(BlockStateProperties.POWERED));
		}),
		NO_BEHAVIOR((state, world, pos) -> {
			return state;
		});
		
		protected IBurnedSupplier supplier;
		
		private DefaultBurnedSuppliers(IBurnedSupplier supplier) {
			this.supplier = supplier;
		}
		
		public IBurnedSupplier getSupplier() {
			return supplier;
		}
		
	}
	
}
