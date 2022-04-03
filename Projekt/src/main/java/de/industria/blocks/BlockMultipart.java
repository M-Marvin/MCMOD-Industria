package de.industria.blocks;

import java.util.HashMap;

import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import de.industria.util.handler.UtilHelper;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
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

public abstract class BlockMultipart<T extends TileEntity> extends BlockContainerBase implements IBAdvancedStickyBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty POS_X = IntegerProperty.create("pos_x", 0, 6);
	public static final IntegerProperty POS_Y = IntegerProperty.create("pos_y", 0, 6);
	public static final IntegerProperty POS_Z = IntegerProperty.create("pos_z", 0, 6);
	
	protected final int sizeX;
	protected final int sizeY;
	protected final int sizeZ;
	
	public BlockMultipart(String name, Material material, float hardnessAndResistance, SoundType sound, int sizeX, int sizeY, int sizeZ) {
		super(name, Properties.of(material).strength(hardnessAndResistance).sound(sound).noOcclusion());
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}
	
	public int getMultipartSizeX() {
		return sizeX;
	}
	
	public int getMultipartSizeY() {
		return sizeY;
	}
	
	public int getMultipartSizeZ() {
		return sizeZ;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POS_X, POS_Y, POS_Z, FACING);
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		
		Direction facing = state.getValue(FACING);
		BlockPos internOffset = UtilHelper.rotateBlockPos(getInternPartPos(state), facing);
		BlockPos internCenter = UtilHelper.rotateBlockPos(getCenter(), facing);
		BlockPos centerPos = pos.subtract(internOffset).offset(internCenter);
		BlockState centerState = pos.equals(centerPos) ? state : worldIn.getBlockState(centerPos);
		
		HashMap<BlockPos, BlockState> parts = makeParts(facing, centerPos);
		
		for (BlockPos partPos : parts.keySet()) {
			
			BlockState removeSate = worldIn.getBlockState(partPos);
			
			if (removeSate.equals(parts.get(partPos)) && !partPos.equals(pos)) worldIn.removeBlock(partPos, false);
			
		}
		
		if (!isCenter(state)) worldIn.removeBlock(centerPos, false);
		
		super.playerWillDestroy(worldIn, centerPos, centerState, player);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		Direction facing = context.getHorizontalDirection().getOpposite();
		BlockPos center = getCenter();
		BlockState centerState = this.defaultBlockState().setValue(POS_X, center.getX()).setValue(POS_Y, center.getY()).setValue(POS_Z, center.getZ()).setValue(FACING, facing);
		HashMap<BlockPos, BlockState> parts = makeParts(centerState.getValue(FACING), context.getClickedPos());
		
		for (BlockPos pos : parts.keySet()) {
			
			if (!context.getLevel().getBlockState(pos).isAir()) return context.getLevel().getBlockState(context.getClickedPos());
			
		}
		
		return centerState;
		
	}
	
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

		if (isCenter(state)) {
			
			HashMap<BlockPos, BlockState> parts = makeParts(state.getValue(FACING), pos);
			
			for (BlockPos posPart : parts.keySet()) {
				
				worldIn.setBlockAndUpdate(posPart, parts.get(posPart));
				
			}
			
		}
		
	}
	
	public static BlockPos getInternPartPos(BlockState state) {
		return new BlockPos(state.getValue(POS_X), state.getValue(POS_Y), state.getValue(POS_Z));
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
						BlockPos offset = UtilHelper.rotateBlockPos(internCenterOffser, facing);
						BlockPos position = centerPos.offset(offset);
						
						BlockState partState = this.defaultBlockState().setValue(POS_X, internPos.getX()).setValue(POS_Y, internPos.getY()).setValue(POS_Z, internPos.getZ()).setValue(FACING, facing);
						
						parts.put(position, partState);
						
					}
					
				}	
			}
		}
		
		return parts;
		
	}
	
	@SuppressWarnings("unchecked")
	public T getCenterTE(BlockPos pos, BlockState state, IBlockReader world) {
		try {
			BlockPos partPos = BlockMultipart.getInternPartPos(state);
			BlockPos partOffset = UtilHelper.rotateBlockPos(partPos, state.getValue(BlockMultipart.FACING));
			BlockPos centerTEPos = pos.subtract(partOffset);
			TileEntity tileEntity = world.getBlockEntity(centerTEPos);
			try {
				return (T) tileEntity;
			} catch (ClassCastException e) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public static TileEntity getSCenterTE(BlockPos pos, BlockState state, IWorld world) {
		BlockPos partPos = BlockMultipart.getInternPartPos(state);
		BlockPos partOffset = UtilHelper.rotateBlockPos(partPos, state.getValue(BlockMultipart.FACING));
		BlockPos centerTEPos = pos.subtract(partOffset);
		TileEntity tileEntity = world.getBlockEntity(centerTEPos);
		return tileEntity instanceof TileEntityAdvancedMovingBlock ? null : tileEntity; // Prevent crash when moving MultiBlocks
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		BlockPos partPos = BlockMultipart.getInternPartPos(state);
		BlockPos partOffset = UtilHelper.rotateBlockPos(partPos, state.getValue(BlockMultipart.FACING));
		BlockPos centerOffset = UtilHelper.rotateBlockPos(this.getCenter(), state.getValue(BlockMultipart.FACING));
		BlockPos centerTEPos = pos.subtract(partOffset).offset(centerOffset);
		
		for (BlockPos pos2 : this.makeParts(state.getValue(FACING), centerTEPos).keySet()) {
			pistonStructureHelper.addBlockLine(pos2, pistonStructureHelper.getMoveDirection());
		}
		return true;
	}
	
}
