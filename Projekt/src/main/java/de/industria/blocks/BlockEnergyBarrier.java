package de.industria.blocks;

import java.awt.Color;

import de.industria.typeregistys.ModItems;
import de.industria.util.handler.UtilHelper;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;

public class BlockEnergyBarrier extends BlockBase {
	
	public static final EnumProperty<SideState> NORTH = EnumProperty.create("north", SideState.class);
	public static final EnumProperty<SideState> SOUTH = EnumProperty.create("south", SideState.class);
	public static final EnumProperty<SideState> EAST = EnumProperty.create("east", SideState.class);
	public static final EnumProperty<SideState> WEST = EnumProperty.create("west", SideState.class);
	public static final EnumProperty<SideState> UP = EnumProperty.create("up", SideState.class);
	public static final EnumProperty<SideState> DOWN = EnumProperty.create("down", SideState.class);
	
	public static final Integer[] FIELD_COLORS = new Integer[] {new Color(0, 0, 255).getRGB(), new Color(255, 0, 255).getRGB(), new Color(255, 0, 0).getRGB(), new Color(255, 255, 0).getRGB(), new Color(0, 255, 0).getRGB(), new Color(0, 255, 255).getRGB()};
	
	public BlockEnergyBarrier() {
		super("energy_barrier", Material.BARRIER, -1.0F, 3600000.0F, SoundType.STONE);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, SideState.NONE).setValue(SOUTH, SideState.NONE).setValue(EAST, SideState.VERTICAL).setValue(WEST, SideState.VERTICAL).setValue(UP, SideState.NONE).setValue(DOWN, SideState.NONE));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = VoxelShapes.empty();
		
		VoxelShape planeShapeNSWEV = Block.box(7.8F, 0, 0, 8.2F, 16, 8);
		if (state.getValue(NORTH) == SideState.VERTICAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEV, Direction.NORTH));
		if (state.getValue(SOUTH) == SideState.VERTICAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEV, Direction.SOUTH));
		if (state.getValue(EAST) == SideState.VERTICAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEV, Direction.EAST));
		if (state.getValue(WEST) == SideState.VERTICAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEV, Direction.WEST));
		
		VoxelShape planeShapeNSWEH = Block.box(0, 7.8F, 0, 16, 8.2F, 8);
		if (state.getValue(NORTH) == SideState.HORIZONTAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEH, Direction.NORTH));
		if (state.getValue(SOUTH) == SideState.HORIZONTAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEH, Direction.SOUTH));
		if (state.getValue(EAST) == SideState.HORIZONTAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEH, Direction.EAST));
		if (state.getValue(WEST) == SideState.HORIZONTAL) shape = VoxelShapes.or(shape, VoxelHelper.rotateShape(planeShapeNSWEH, Direction.WEST));
		
		if (state.getValue(UP) == SideState.HORIZONTAL) shape = VoxelShapes.or(shape, Block.box(0, 8, 7.8F, 16, 16, 8.2F));
		if (state.getValue(DOWN) == SideState.HORIZONTAL) shape = VoxelShapes.or(shape, Block.box(0, 0, 7.8F, 16, 8, 8.2F));
		if (state.getValue(UP) == SideState.VERTICAL) shape = VoxelShapes.or(shape, Block.box(7.8F, 8, 0, 8.2F, 16, 16F));
		if (state.getValue(DOWN) == SideState.VERTICAL) shape = VoxelShapes.or(shape, Block.box(7.8F, 0, 0, 8.2F, 8, 16F));
		
		return shape;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
		return true;
	}
	
	public BlockState createEnergyField(Axis axis, Direction... connectedSides) {
		BlockState state = this.defaultBlockState().setValue(EAST, SideState.NONE).setValue(WEST, SideState.NONE);
		switch(axis) {
		case X:
			if (UtilHelper.containsArray(connectedSides, Direction.NORTH)) state = state.setValue(NORTH, SideState.HORIZONTAL);
			if (UtilHelper.containsArray(connectedSides, Direction.SOUTH)) state = state.setValue(SOUTH, SideState.HORIZONTAL);
			if (UtilHelper.containsArray(connectedSides, Direction.UP)) state = state.setValue(UP, SideState.HORIZONTAL);
			if (UtilHelper.containsArray(connectedSides, Direction.DOWN)) state = state.setValue(DOWN, SideState.HORIZONTAL);
			break;
		case Z:
			if (UtilHelper.containsArray(connectedSides, Direction.EAST)) state = state.setValue(EAST, SideState.HORIZONTAL);
			if (UtilHelper.containsArray(connectedSides, Direction.WEST)) state = state.setValue(WEST, SideState.HORIZONTAL);
			if (UtilHelper.containsArray(connectedSides, Direction.UP)) state = state.setValue(UP, SideState.VERTICAL);
			if (UtilHelper.containsArray(connectedSides, Direction.DOWN)) state = state.setValue(DOWN, SideState.VERTICAL);
			break;
		case Y:
			if (UtilHelper.containsArray(connectedSides, Direction.NORTH)) state = state.setValue(NORTH, SideState.VERTICAL);
			if (UtilHelper.containsArray(connectedSides, Direction.SOUTH)) state = state.setValue(SOUTH, SideState.VERTICAL);
			if (UtilHelper.containsArray(connectedSides, Direction.EAST)) state = state.setValue(EAST, SideState.VERTICAL);
			if (UtilHelper.containsArray(connectedSides, Direction.WEST)) state = state.setValue(WEST, SideState.VERTICAL);
			break;
		}
		return state;
	}
	
	public boolean canConnect(BlockState state) {
		return state.getBlock() == ModItems.energy_barrier || state.getBlock() instanceof BlockEnergyBarrierBorder;
	}
	
	public static enum SideState implements IStringSerializable {
		
		NONE("none"),
		HORIZONTAL("horizontal"),
		VERTICAL("vertical");
		
		protected String name;
		
		SideState(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return name;
		}
		
	}
	
	public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
		switch(p_185499_2_) {
		case CLOCKWISE_180:
			return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(SOUTH)).setValue(EAST, p_185499_1_.getValue(WEST)).setValue(SOUTH, p_185499_1_.getValue(NORTH)).setValue(WEST, p_185499_1_.getValue(EAST));
		case COUNTERCLOCKWISE_90:
			return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(EAST)).setValue(EAST, p_185499_1_.getValue(SOUTH)).setValue(SOUTH, p_185499_1_.getValue(WEST)).setValue(WEST, p_185499_1_.getValue(NORTH));
		case CLOCKWISE_90:
			return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(WEST)).setValue(EAST, p_185499_1_.getValue(NORTH)).setValue(SOUTH, p_185499_1_.getValue(EAST)).setValue(WEST, p_185499_1_.getValue(SOUTH));
		default:
			return p_185499_1_;
		}
	}
	
	public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
		switch(p_185471_2_) {
		case LEFT_RIGHT:
			return p_185471_1_.setValue(NORTH, p_185471_1_.getValue(SOUTH)).setValue(SOUTH, p_185471_1_.getValue(NORTH));
		case FRONT_BACK:
			return p_185471_1_.setValue(EAST, p_185471_1_.getValue(WEST)).setValue(WEST, p_185471_1_.getValue(EAST));
		default:
			return p_185471_1_;
		}
	}
	
	public int getColor(IBlockDisplayReader world, BlockPos pos, BlockState state, int tint) {
		return tint < FIELD_COLORS.length ? FIELD_COLORS[tint] : FIELD_COLORS[0];
	}
	
	@Override
	public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
		return 1;
	}
	
}
