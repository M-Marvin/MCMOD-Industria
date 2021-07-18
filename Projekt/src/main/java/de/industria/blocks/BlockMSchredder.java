package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMSchredderItemRenderer;
import de.industria.tileentity.TileEntityMSchredder;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.blockfeatures.IElectricConnectiveBlock;
import de.industria.util.handler.VoxelHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMSchredder extends BlockMultiPart<TileEntityMSchredder> implements IElectricConnectiveBlock, IAdvancedBlockInfo, ISidedInventoryProvider {
	
	public static final VoxelShape BASE = Block.box(0, 0, 0, 16, 14, 16);
	public static final VoxelShape CORNER_1 = VoxelShapes.or(Block.box(0, -2, 0, 16, 14, 1), Block.box(0, -2, 0, 2, 16, 16));
	public static final VoxelShape CORNER_2 = VoxelShapes.or(Block.box(0, -2, 0, 16, 14, 1), Block.box(14, -2, 0, 16, 16, 16));
	public static final VoxelShape CORNER_3 = VoxelShapes.or(Block.box(0, -2, 15, 16, 14, 16), Block.box(0, -2, 0, 2, 16, 16));
	public static final VoxelShape CORNER_4 = VoxelShapes.or(Block.box(0, -2, 15, 16, 14, 16), Block.box(14, -2, 0, 16, 16, 16));
	
	public BlockMSchredder() {
		super("schredder", Material.METAL, 3.5F, SoundType.METAL, 2, 2, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos partPos = getInternPartPos(state);
		return partPos.equals(BlockPos.ZERO);
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMSchredder();
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 0.8F * Voltage.HightVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.HightVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 0.8F));
			info.add(new TranslationTextComponent("industria.block.info.schredder"));
		};
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		BlockPos partPos = getInternPartPos(state);
		Direction facing = state.getValue(FACING);
		if (partPos.getY() == 0) {
			return Block.box(0, 0, 0, 16, 14, 16);
		} else if (partPos.getX() == 0 && partPos.getZ() == 0) {
			return VoxelHelper.rotateShape(CORNER_1, facing);
		} else if (partPos.getX() == 1 && partPos.getZ() == 0) {
			return VoxelHelper.rotateShape(CORNER_2, facing);
		} else if (partPos.getX() == 0 && partPos.getZ() == 1) {
			return VoxelHelper.rotateShape(CORNER_3, facing);
		} else {
			return VoxelHelper.rotateShape(CORNER_4, facing);
		}
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMSchredderItemRenderer::new;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMSchredder tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropContents(worldIn, tileEntity.getBlockPos(), (IInventory) tileEntity);
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.HightVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return getCenterTE(pos, state, world).canWork() ? 0.8F : 0F;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side != Direction.UP;
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}
	
	@Override
	public List<BlockPos> getMultiBlockParts(World world, BlockPos pos, BlockState state) {
		List<BlockPos> multiParts = new ArrayList<BlockPos>();
		Direction facing = state.getValue(FACING);
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				for (int z = 0; z < this.sizeZ; z++) {
					BlockPos internPos = new BlockPos(x, y, z);
					BlockPos offset = rotateOffset(internPos, facing);
					BlockPos partPos = getCenterTE(pos, state, world).getBlockPos().offset(offset);
					multiParts.add(partPos);
				}
			}
		}
		return multiParts;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMSchredder tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		TileEntityMSchredder centerTileEntity = getCenterTE(pos, state, world);
		return centerTileEntity;
	}

	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.HightVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
		
	}
	
}
