package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.renderer.BlockMSchredderItemRenderer;
import de.redtec.tileentity.TileEntityMSchredder;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMSchredder extends BlockMultiPart implements IElectricConnective, IAdvancedBlockInfo, ISidedInventoryProvider {
	
	public static final VoxelShape BASE = Block.makeCuboidShape(0, 0, 0, 16, 14, 16);
	public static final VoxelShape X_CORNER_1 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 0, 16, 16, 2), Block.makeCuboidShape(0, -2, 0, 1, 14, 16));
	public static final VoxelShape X_CORNER_2 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 0, 16, 16, 2), Block.makeCuboidShape(15, -2, 0, 16, 14, 16));
	public static final VoxelShape X_CORNER_3 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 14, 16, 16, 16), Block.makeCuboidShape(0, -2, 0, 1, 14, 16));
	public static final VoxelShape X_CORNER_4 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 14, 16, 16, 16), Block.makeCuboidShape(15, -2, 0, 16, 14, 16));
	public static final VoxelShape Z_CORNER_1 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 0, 16, 14, 1), Block.makeCuboidShape(0, -2, 0, 2, 16, 16));
	public static final VoxelShape Z_CORNER_2 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 0, 16, 14, 1), Block.makeCuboidShape(14, -2, 0, 16, 16, 16));
	public static final VoxelShape Z_CORNER_3 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 15, 16, 14, 16), Block.makeCuboidShape(0, -2, 0, 2, 16, 16));
	public static final VoxelShape Z_CORNER_4 = VoxelShapes.or(Block.makeCuboidShape(0, -2, 15, 16, 14, 16), Block.makeCuboidShape(14, -2, 0, 16, 16, 16));
	
	public BlockMSchredder() {
		super("schredder", Material.IRON, 3.5F, SoundType.METAL, 2, 2, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos partPos = getInternPartPos(state);
		return partPos.equals(BlockPos.ZERO);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMSchredder();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.needEnergy", 0.8F * Voltage.HightVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.needVoltage", Voltage.HightVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.needCurrent", 0.8F));
		info.add(new TranslationTextComponent("redtec.block.info.schredder"));
		return info;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		BlockPos partPos = getInternPartPos(state);
		if (partPos.getY() == 0) {
			return Block.makeCuboidShape(0, 0, 0, 16, 14, 16);
		} else {
			Direction facing = state.get(FACING);
			switch(facing) {
			case NORTH:
				if (partPos.getX() == 0 && partPos.getZ() == 0) {
					return Z_CORNER_1;
				} else if (partPos.getX() == 1 && partPos.getZ() == 0) {
					return Z_CORNER_2;
				} else if (partPos.getX() == 0 && partPos.getZ() == 1) {
					return Z_CORNER_3;
				} else {
					return Z_CORNER_4;
				}
			case SOUTH:
				if (partPos.getX() == 0 && partPos.getZ() == 0) {
					return Z_CORNER_4;
				} else if (partPos.getX() == 1 && partPos.getZ() == 0) {
					return Z_CORNER_3;
				} else if (partPos.getX() == 0 && partPos.getZ() == 1) {
					return Z_CORNER_2;
				} else {
					return Z_CORNER_1;
				}
			case EAST:
				if (partPos.getX() == 0 && partPos.getZ() == 0) {
					return X_CORNER_2;
				} else if (partPos.getX() == 1 && partPos.getZ() == 0) {
					return X_CORNER_4;
				} else if (partPos.getX() == 0 && partPos.getZ() == 1) {
					return X_CORNER_1;
				} else {
					return X_CORNER_3;
				}
			case WEST:
				if (partPos.getX() == 0 && partPos.getZ() == 0) {
					return X_CORNER_3;
				} else if (partPos.getX() == 1 && partPos.getZ() == 0) {
					return X_CORNER_1;
				} else if (partPos.getX() == 0 && partPos.getZ() == 1) {
					return X_CORNER_4;
				} else {
					return X_CORNER_2;
				}
			default:
				return BASE;
			}
		}
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMSchredderItemRenderer::new;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, tileEntity.getPos(), (IInventory) tileEntity);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
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
		Direction facing = state.get(FACING);
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				for (int z = 0; z < this.sizeZ; z++) {
					BlockPos internPos = new BlockPos(x, y, z);
					BlockPos offset = rotateOffset(internPos, facing);
					BlockPos partPos = getCenterTE(pos, state, world).getPos().add(offset);
					multiParts.add(partPos);
				}
			}
		}
		return multiParts;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		TileEntity tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity instanceof TileEntityMSchredder) {
			if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
		TileEntityMSchredder centerTileEntity = getCenterTE(pos, state, world);
		return centerTileEntity;
	}
	
	public TileEntityMSchredder getCenterTE(BlockPos pos, BlockState state, IWorld world) {
		BlockPos partPos = BlockMultiPart.getInternPartPos(state);
		BlockPos partOffset = BlockMultiPart.rotateOffset(partPos, state.get(BlockMultiPart.FACING));
		BlockPos centerTEPos = pos.subtract(partOffset);
		TileEntity tileEntity = world.getTileEntity(centerTEPos);
		if (tileEntity instanceof TileEntityMSchredder) {
			return (TileEntityMSchredder) tileEntity;
		}
		return null;
	}
	
}
