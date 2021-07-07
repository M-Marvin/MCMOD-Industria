package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMOreWashingPlantItemRenderer;
import de.industria.tileentity.TileEntityMOreWashingPlant;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.blockfeatures.IElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.VoxelHelper;
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

public class BlockMOreWashingPlant extends BlockMultiPart<TileEntityMOreWashingPlant> implements IElectricConnectiveBlock, ISidedInventoryProvider, IAdvancedBlockInfo {
	
	public BlockMOreWashingPlant() {
		super("ore_washing_plant", Material.IRON, 4F, SoundType.METAL, 4, 3, 3);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMOreWashingPlant tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMOreWashingPlant tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropInventoryItems(worldIn, tileEntity.getPos(), (IInventory) tileEntity);
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

		BlockPos ipos = getInternPartPos(state);
		Direction facing = state.get(FACING);
		
		if (ipos.equals(new BlockPos(0, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(2, 0, 3, 11, 16, 12), Block.makeCuboidShape(5, 0, 12, 11, 11, 16)), facing);
		} else if (ipos.equals(new BlockPos(0, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(2, 0, 3, 6, 15, 7), Block.makeCuboidShape(2, 11, 3, 16, 15, 7), Block.makeCuboidShape(5, 0, 0, 11, 11, 2), Block.makeCuboidShape(9, 0, 5, 16, 2, 16)), facing);
		} else if (ipos.equals(new BlockPos(0, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 7, 10, 16), Block.makeCuboidShape(7, 2, 0, 16, 9, 16)), facing);
		} else if (ipos.equals(new BlockPos(0, 2, 0))) {
			return VoxelShapes.empty();
		} else if (ipos.equals(new BlockPos(0, 2, 1))) {
			return VoxelShapes.empty();
		} else if (ipos.equals(new BlockPos(0, 2, 2))) {
			return VoxelShapes.empty();
		} else if (ipos.equals(new BlockPos(1, 2, 2))) {
			return VoxelShapes.empty();
		} else if (ipos.equals(new BlockPos(1, 1, 2))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 5, 2, 15, 13, 16), facing);
		} else if (ipos.equals(new BlockPos(1, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 11, 3, 7, 15, 7), Block.makeCuboidShape(3, 15, 3, 7, 16, 7), Block.makeCuboidShape(0, 0, 5, 7, 2, 16)), facing);
		} else if (ipos.equals(new BlockPos(1, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(3, 0, 4, 7, 16, 8), Block.makeCuboidShape(3, 0, 11, 7, 16, 15)), facing);
		} else if (ipos.equals(new BlockPos(1, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(3, 0, 3, 7, 6, 7), Block.makeCuboidShape(7, 2, 3, 16, 6, 7)), facing);
		} else if (ipos.equals(new BlockPos(1, 2, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(3, 0, 4, 7, 6, 8), Block.makeCuboidShape(7, 2, 4, 16, 6, 8), Block.makeCuboidShape(3, 0, 11, 7, 6, 15), Block.makeCuboidShape(7, 2, 11, 16, 6, 15)), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 2))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(6, 0, 0, 16, 3, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(8, 0, 8, 16, 8, 16), Block.makeCuboidShape(0, 0, 0, 8, 8, 16), Block.makeCuboidShape(8, 2, 0, 16, 4, 8)), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 8, 8, 16), Block.makeCuboidShape(8, 2, 0, 16, 6, 16)), facing);
		} else if (ipos.equals(new BlockPos(3, 2, 2))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 3, 4, 1, 8), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(6, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 10, 0, 6, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(3, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 5, 10, 6, 6), Block.makeCuboidShape(0, 0, 0, 4, 5, 5)), facing);
		} else if (ipos.equals(new BlockPos(3, 2, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 2, 10, 6, 3), Block.makeCuboidShape(0, 0, 3, 4, 5, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 8, 16, 16), Block.makeCuboidShape(8, 0, 0, 16, 6, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(8, 0, 8, 16, 16, 16), Block.makeCuboidShape(0, 0, 0, 8, 16, 16), Block.makeCuboidShape(8, 0, 0, 16, 6, 8)), facing);
		} else if (ipos.equals(new BlockPos(3, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 6, 16), Block.makeCuboidShape(1, 6, 2, 13, 16, 3), Block.makeCuboidShape(1, 6, 3, 9, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(3, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 6, 16), Block.makeCuboidShape(1, 6, 5, 13, 16, 6), Block.makeCuboidShape(1, 6, 0, 9, 16, 5)), facing);
		}  else if (ipos.equals(new BlockPos(3, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 8, 11, 15), Block.makeCuboidShape(1, 11, 3, 4, 14, 8), Block.makeCuboidShape(0, 14, 3, 4, 16, 8)), facing);
		}
		return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
		
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMOreWashingPlant();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos partPos = getInternPartPos(state);
		return	partPos.equals(BlockPos.ZERO) ||
				partPos.equals(new BlockPos(2, 0, 0));
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 3.5F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 3.5F));
			info.add(new TranslationTextComponent("industria.block.info.oreWashingPlant"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMOreWashingPlantItemRenderer::new;
	}
	
	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
		return (ISidedInventory) getCenterTE(pos, state, world);
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityMOreWashingPlant tileEntity = getCenterTE(pos, state, world);
		if (tileEntity != null) {
			return tileEntity.canWork() ? 3.5F : 0;
		}
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return getInternPartPos(state).getY() == 0;
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
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {
			
			worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			
		}
		
	}
	
}
