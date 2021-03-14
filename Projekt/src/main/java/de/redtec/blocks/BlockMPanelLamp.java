package de.redtec.blocks;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockMPanelLamp extends BlockContainerBase implements IElectricConnective, IAdvancedBlockInfo {
	
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public BlockMPanelLamp() {
		super("panel_lamp", Material.IRON, 1F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return state.get(LIT) ? 15 : 0;
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.LowVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0.1F;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side == state.get(FACING).getOpposite();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getFace().getOpposite()).with(LIT, false);
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		boolean active = state.get(LIT);
		boolean powered = network.canMachinesRun() == Voltage.LowVoltage;
		
		if (network.getVoltage().getVoltage() > Voltage.LowVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			
		}
		
		if (active != powered) worldIn.setBlockState(pos, state.with(LIT, powered));
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		this.updateNetwork(worldIn, pos);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

		switch (state.get(FACING)) {
		case SOUTH: return NORTH_OPEN_AABB;
		case NORTH: return SOUTH_OPEN_AABB;
		case WEST: return EAST_OPEN_AABB;
		case EAST: return WEST_OPEN_AABB;
		case UP: return TOP_AABB;
		case DOWN: return BOTTOM_AABB;
		default: return NORTH_OPEN_AABB;
		}
		
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
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
	public IBlockToolType getBlockInfo() {
		return (stack, info, flag) -> {
			info.add(new TranslationTextComponent("redtec.block.info.needEnergy", 0.1F * Voltage.LowVoltage.getVoltage()));
			info.add(new TranslationTextComponent("redtec.block.info.needVoltage", Voltage.LowVoltage.getVoltage()));
			info.add(new TranslationTextComponent("redtec.block.info.needCurrent", 0.1F));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
}
