package de.industria.util.handler;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import de.industria.ModItems;
import de.industria.blocks.BlockFallingDust;
import de.industria.blocks.BlockStairsBase;
import de.industria.fluids.util.BlockGasFluid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FlowingFluidBlock;
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
						if (isNotBurneable(t)) return t;
						return ModItems.ash.defaultBlockState().setValue(BlockFallingDust.LAYERS, world.getRandom().nextInt(4) + 2);
					}
				}).toArray(BlockState[]::new);
			}
			return Stream.of(unburnedStates).map((state) -> {
				if (isNotBurneable(state)) return state;
				return blockToBurnedMap.getOrDefault(state.getBlock(), DefaultBurnedSuppliers.NO_BEHAVIOR.getSupplier()).getBurnedState(state, world, pos);
			}).toArray(BlockState[]::new);
		}
		return unburnedStates;
	}
	
	public static boolean isNotBurneable(BlockState state) {
		return state.getBlock().is(BlockTags.FIRE) || state.getBlock() instanceof FlowingFluidBlock || state.getBlock() instanceof BlockGasFluid || state.getMaterial() == Material.PLANT || state.getMaterial() == Material.REPLACEABLE_PLANT || state.getMaterial() == Material.REPLACEABLE_WATER_PLANT || state.getMaterial() == Material.WATER_PLANT;
	}
	
	public static BlockState getBurnedVariant(BlockState unburnedState, IWorld world, BlockPos pos) {
		return getBurnedVariants(new BlockState[] {unburnedState}, world, pos)[0];
	}
	
	public static void reloadBurnBehaviors() {
		blockToBurnedMap.clear();
		ForgeRegistries.BLOCKS.forEach((registredBlock) -> {
			if (registredBlock instanceof RotatedPillarBlock && registredBlock.is(BlockTags.LOGS_THAT_BURN)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.LOGS);
			} else if (registredBlock.is(BlockTags.PLANKS) && !registredBlock.is(BlockTags.NON_FLAMMABLE_WOOD)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.PLANKS);
			} else if (registredBlock instanceof StairsBlock && registredBlock.is(BlockTags.WOODEN_STAIRS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.STAIRS);
			} else if (registredBlock instanceof SlabBlock && registredBlock.is(BlockTags.WOODEN_SLABS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.SLABS);
			} else if (registredBlock instanceof TrapDoorBlock && registredBlock.is(BlockTags.WOODEN_TRAPDOORS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.TRAPDOORS);
			} else if (registredBlock instanceof DoorBlock && registredBlock.is(BlockTags.WOODEN_DOORS)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.DOORS);
			} else if (registredBlock instanceof FenceBlock && registredBlock.is(BlockTags.WOODEN_FENCES)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.FENCES);
			} else if (registredBlock.defaultBlockState().getMaterial() == Material.WOOL) {
				setBurnBehavior(registredBlock, DefaultBurnedSuppliers.NO_BEHAVIOR);
			} else if (registredBlock.defaultBlockState().isSolidRender(null, null) && registredBlock.isFlammable(registredBlock.defaultBlockState(), null, null, Direction.UP)) {
				setBurnBehavior(registredBlock.getBlock(), DefaultBurnedSuppliers.MISC_BLOCKS);
			} else if (registredBlock.isFlammable(registredBlock.defaultBlockState(), null, null, Direction.UP)) {
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
			return ModItems.burned_log.getNotPersistant().setValue(BlockStateProperties.AXIS, state.getValue(BlockStateProperties.AXIS));
		}),
		PLANKS((state, world, pos) -> {
			return ModItems.burned_planks.getNotPersistant();
		}),
		MISC_BLOCKS((state, world, pos) -> {
			return ModItems.burned_block.getNotPersistant();
		}),
		STAIRS((state, world, pos) -> {
			return ModItems.burned_wood_stairs.getNotPersistant().setValue(BlockStairsBase.SHAPE, state.getValue(BlockStairsBase.SHAPE)).setValue(BlockStairsBase.HALF, state.getValue(BlockStairsBase.HALF)).setValue(BlockStairsBase.FACING, state.getValue(BlockStairsBase.FACING));
		}),
		SLABS((state, world, pos) -> {
			return ModItems.burned_wood_slab.getNotPersistant().setValue(BlockStateProperties.SLAB_TYPE, state.getValue(BlockStateProperties.SLAB_TYPE));
		}),
		FENCES((state, world, pos) -> {
			return ModItems.burned_fence.getNotPersistant().setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
		}),
		@SuppressWarnings("deprecation")
		MISC_OBJECTS((state, world, pos) -> {
			BlockState bottomState = world.getBlockState(pos.below());
			if (bottomState.isSolidRender(world, pos.below()) && !bottomState.isAir()) {
				return ModItems.burned_scrap.getNotPersistant();
			}
			return ModItems.ash.defaultBlockState();
		}),
		TRAPDOORS((state, world, pos) -> {
			return ModItems.burned_trapdoor.getNotPersistant().setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)).setValue(BlockStateProperties.HALF, state.getValue(BlockStateProperties.HALF)).setValue(BlockStateProperties.OPEN, state.getValue(BlockStateProperties.OPEN)).setValue(BlockStateProperties.POWERED, state.getValue(BlockStateProperties.POWERED));
		}),
		DOORS((state, world, pos) -> {
			return ModItems.burned_door.getNotPersistant().setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)).setValue(BlockStateProperties.OPEN, state.getValue(BlockStateProperties.OPEN)).setValue(BlockStateProperties.POWERED, state.getValue(BlockStateProperties.POWERED));
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
