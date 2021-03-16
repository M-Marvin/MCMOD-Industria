package de.redtec.blocks;

import java.util.HashMap;

import de.redtec.tileentity.TileEntityAdvancedMovingBlock;
import de.redtec.util.AdvancedPistonBlockStructureHelper;
import de.redtec.util.IAdvancedStickyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class BlockMultiPart<T extends TileEntity> extends BlockContainerBase implements IAdvancedStickyBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty POS_X = IntegerProperty.create("pos_x", 0, 6);
	public static final IntegerProperty POS_Y = IntegerProperty.create("pos_y", 0, 6);
	public static final IntegerProperty POS_Z = IntegerProperty.create("pos_z", 0, 6);
	
	protected final int sizeX;
	protected final int sizeY;
	protected final int sizeZ;
	
	public BlockMultiPart(String name, Material material, float hardnessAndResistance, SoundType sound, int sizeX, int sizeY, int sizeZ) {
		super(name, Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)).notSolid());
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POS_X, POS_Y, POS_Z, FACING);
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		
		Direction facing = state.get(FACING);
		BlockPos internOffset = rotateOffset(getInternPartPos(state), facing);
		BlockPos internCenter = rotateOffset(getCenter(), facing);
		BlockPos centerPos = pos.subtract(internOffset).add(internCenter);
		BlockState centerState = pos.equals(centerPos) ? state : worldIn.getBlockState(centerPos);
		
		HashMap<BlockPos, BlockState> parts = makeParts(facing, centerPos);
		
		for (BlockPos partPos : parts.keySet()) {
			
			BlockState removeSate = worldIn.getBlockState(partPos);
			
			if (removeSate.equals(parts.get(partPos)) && !partPos.equals(pos)) worldIn.removeBlock(partPos, false);
			
		}
		
		worldIn.removeBlock(centerPos, false);
		
		super.onBlockHarvested(worldIn, centerPos, centerState, player);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		Direction facing = context.getPlacementHorizontalFacing().getOpposite();
		BlockPos center = getCenter();
		BlockState centerState = this.getDefaultState().with(POS_X, center.getX()).with(POS_Y, center.getY()).with(POS_Z, center.getZ()).with(FACING, facing);
		HashMap<BlockPos, BlockState> parts = makeParts(centerState.get(FACING), context.getPos());
		
		for (BlockPos pos : parts.keySet()) {
			
			if (!context.getWorld().getBlockState(pos).isAir()) return context.getWorld().getBlockState(context.getPos());
			
		}
		
		return centerState;
		
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

		if (isCenter(state)) {
			
			HashMap<BlockPos, BlockState> parts = makeParts(state.get(FACING), pos);
			
			for (BlockPos posPart : parts.keySet()) {
				
				worldIn.setBlockState(posPart, parts.get(posPart));
				
			}
			
		}
		
	}
	
	public static BlockPos getInternPartPos(BlockState state) {
		return new BlockPos(state.get(POS_X), state.get(POS_Y), state.get(POS_Z));
	}
	
	public BlockPos getCenter() {
		return new BlockPos(this.sizeX / 2, 0, this.sizeZ / 2);
	}
	
	public boolean isCenter(BlockState state) {
		BlockPos pos = getInternPartPos(state);
		return pos.equals(getCenter());
	}
		
	public HashMap<BlockPos, BlockState> makeParts(Direction facing, BlockPos centerPos) {
		
		HashMap<BlockPos, BlockState> parts = new HashMap<BlockPos, BlockState>();
		
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				for (int z = 0; z < this.sizeZ; z++) {
					
					BlockPos internPos = new BlockPos(x, y, z);
					
					if (!internPos.equals(getCenter())) {
						
						BlockPos internCenterOffser = internPos.subtract(getCenter());
						BlockPos offset = rotateOffset(internCenterOffser, facing);
						BlockPos position = centerPos.add(offset);
						
						BlockState partState = this.getDefaultState().with(POS_X, internPos.getX()).with(POS_Y, internPos.getY()).with(POS_Z, internPos.getZ()).with(FACING, facing);
						
						parts.put(position, partState);
						
					}
					
				}	
			}
		}
		
		return parts;
		
	}
	
	public static BlockPos rotateOffset(BlockPos internOffset, Direction facing) {
		
		switch (facing) {
		case NORTH: return internOffset;
		case SOUTH: return new BlockPos(-internOffset.getX(), internOffset.getY(), -internOffset.getZ());
		case EAST: return new BlockPos(-internOffset.getZ(), internOffset.getY(), internOffset.getX());
		case WEST: return new BlockPos(internOffset.getZ(), internOffset.getY(), -internOffset.getX());
		default: return internOffset;
		}
		
	}

	@SuppressWarnings("unchecked")
	public T getCenterTE(BlockPos pos, BlockState state, IBlockReader world) {
		BlockPos partPos = BlockMultiPart.getInternPartPos(state);
		BlockPos partOffset = BlockMultiPart.rotateOffset(partPos, state.get(BlockMultiPart.FACING));
		BlockPos centerTEPos = pos.subtract(partOffset);
		TileEntity tileEntity = world.getTileEntity(centerTEPos);
		try {
			return (T) tileEntity;
		} catch (ClassCastException e) {
			return null;
		}
		
	}
	
	public static TileEntity getSCenterTE(BlockPos pos, BlockState state, IWorld world) {
		BlockPos partPos = BlockMultiPart.getInternPartPos(state);
		BlockPos partOffset = BlockMultiPart.rotateOffset(partPos, state.get(BlockMultiPart.FACING));
		BlockPos centerTEPos = pos.subtract(partOffset);
		TileEntity tileEntity = world.getTileEntity(centerTEPos);
		return tileEntity instanceof TileEntityAdvancedMovingBlock ? null : tileEntity; // Prevent crash when moving MultiBlocks
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		BlockPos partPos = BlockMultiPart.getInternPartPos(state);
		BlockPos partOffset = BlockMultiPart.rotateOffset(partPos, state.get(BlockMultiPart.FACING));
		BlockPos centerOffset = BlockMultiPart.rotateOffset(this.getCenter(), state.get(BlockMultiPart.FACING));
		BlockPos centerTEPos = pos.subtract(partOffset).add(centerOffset);
		
		for (BlockPos pos2 : this.makeParts(state.get(FACING), centerTEPos).keySet()) {
			pistonStructureHelper.addBlockLine(pos2, pistonStructureHelper.getMoveDirection());
		}
		return true;
	}
	
}
