package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMBlastFurnaceItemRenderer;
import de.industria.tileentity.TileEntityMBlastFurnace;
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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
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

public class BlockMBlastFurnace extends BlockMultiPart<TileEntityMBlastFurnace> implements IAdvancedBlockInfo, IElectricConnectiveBlock, ISidedInventoryProvider {

	public BlockMBlastFurnace() {
		super("blast_furnace", Material.METAL, 2F, SoundType.METAL, 3, 5, 3);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMBlastFurnace();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		BlockPos internPart = getInternPartPos(state);
		Direction facing = state.getValue(FACING);
		
		if (internPart.equals(new BlockPos(0, 0, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 8, 16), Block.box(8, 8, 8, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 0, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 8, 16), Block.box(8, 8, 0, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 0, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 8, 16), Block.box(8, 8, 0, 16, 16, 8)), facing);
		if (internPart.equals(new BlockPos(1, 0, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 8, 16), Block.box(8, 8, 0, 16, 16, 8)), facing);
		if (internPart.equals(new BlockPos(2, 0, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 8, 16), Block.box(0, 8, 0, 8, 16, 8)), facing);
		if (internPart.equals(new BlockPos(2, 0, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 8, 16), Block.box(0, 8, 0, 8, 16, 16)), facing);
		
		if (internPart.equals(new BlockPos(2, 1, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(3, 0, 3, 7, 16, 7), Block.box(8, 0, 3, 12, 16, 7), Block.box(0, 0, 8, 8, 16, 16)), facing);
		if (internPart.equals(new BlockPos(1, 1, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 8, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 1, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 0, 8, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 1, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 0, 0, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 1, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 0, 0, 16, 16, 8)), facing);
		if (internPart.equals(new BlockPos(1, 1, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 0, 16, 16, 8)), facing);
		if (internPart.equals(new BlockPos(2, 1, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 6, 0, 11, 16, 6), Block.box(0, 0, 0, 8, 16, 8)), facing);
		if (internPart.equals(new BlockPos(2, 1, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 6, 6, 11, 16, 16), Block.box(0, 0, 0, 8, 16, 16)), facing);
		
		if (internPart.equals(new BlockPos(2, 2, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(6, 8, 8, 8, 16, 10), Block.box(0, 8, 14, 2, 16, 16), Block.box(3, 0, 3, 7, 14, 7), Block.box(8, 0, 3, 12, 16, 7), Block.box(0, 0, 8, 8, 8, 16), Block.box(0, 10, 3, 3, 14, 7)), facing);
		if (internPart.equals(new BlockPos(1, 2, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 8, 14, 16, 16, 16), Block.box(0, 0, 8, 16, 8, 16), Block.box(4, 10, 7, 8, 14, 14), Block.box(4, 10, 3, 16, 14, 7)), facing);
		if (internPart.equals(new BlockPos(0, 2, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 8, 8, 10, 16, 10), Block.box(14, 8, 14, 16, 16, 16), Block.box(8, 0, 8, 16, 8, 16)), facing);
		if (internPart.equals(new BlockPos(0, 2, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(16, 8, 0, 14, 16, 16), Block.box(8, 0, 0, 16, 8, 16)), facing);
		if (internPart.equals(new BlockPos(0, 2, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 8, 6, 10, 16, 8), Block.box(16, 8, 0, 14, 16, 2), Block.box(8, 0, 0, 16, 8, 8)), facing);
		if (internPart.equals(new BlockPos(1, 2, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 8, 0, 16, 16, 2), Block.box(0, 0, 0, 16, 8, 8)), facing);
		if (internPart.equals(new BlockPos(2, 2, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(6, 8, 6, 8, 16, 8), Block.box(0, 8, 0, 2, 16, 2), Block.box(8, 0, 0, 11, 6, 6), Block.box(0, 0, 0, 8, 8, 8)), facing);
		if (internPart.equals(new BlockPos(2, 2, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 8, 0, 2, 16, 16), Block.box(8, 0, 6, 11, 6, 16), Block.box(0, 0, 0, 8, 8, 16)), facing);
		
		if (internPart.equals(new BlockPos(2, 3, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 8, 7, 12, 12, 16), Block.box(6, 0, 8, 8, 16, 10), Block.box(0, 0, 14, 2, 16, 16), Block.box(8, 0, 3, 12, 12, 7)), facing);
		if (internPart.equals(new BlockPos(1, 3, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 14, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 3, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 0, 8, 10, 16, 10), Block.box(14, 0, 14, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 3, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(14, 0, 0, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 3, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 0, 6, 10, 16, 8), Block.box(14, 0, 0, 16, 16, 2)), facing);
		if (internPart.equals(new BlockPos(1, 3, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 8, 8, 16, 12, 12), Block.box(8, 8, 0, 12, 12, 8), Block.box(0, 0, 0, 16, 16, 2)), facing);
		if (internPart.equals(new BlockPos(2, 3, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 8, 8, 12, 12, 12), Block.box(8, 8, 0, 12, 12, 8), Block.box(6, 0, 6, 8, 16, 8), Block.box(0, 0, 0, 2, 16, 2)), facing);
		if (internPart.equals(new BlockPos(2, 3, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 8, 0, 12, 12, 16), Block.box(0, 0, 0, 2, 16, 16)), facing);

		if (internPart.equals(new BlockPos(2, 4, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 13, 8, 8, 15, 10), Block.box(6, 13, 8, 8, 15, 16), Block.box(6, 0, 8, 8, 15, 10), Block.box(0, 0, 14, 2, 16, 16)), facing);
		if (internPart.equals(new BlockPos(1, 4, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 0, 14, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 4, 0))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(16, 13, 8, 8, 15, 10), Block.box(8, 13, 8, 10, 15, 16), Block.box(8, 0, 8, 10, 15, 10), Block.box(14, 0, 14, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 4, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(8, 13, 0, 10, 15, 16), Block.box(14, 0, 0, 16, 16, 16)), facing);
		if (internPart.equals(new BlockPos(0, 4, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(16, 13, 6, 8, 15, 8), Block.box(8, 13, 0, 10, 15, 8), Block.box(8, 0, 6, 10, 15, 8), Block.box(14, 0, 0, 16, 16, 2)), facing);
		if (internPart.equals(new BlockPos(1, 4, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 13, 6, 16, 15, 8), Block.box(0, 0, 0, 16, 16, 2)), facing);
		if (internPart.equals(new BlockPos(2, 4, 2))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(0, 13, 6, 8, 15, 8), Block.box(6, 13, 0, 8, 15, 8), Block.box(6, 0, 6, 8, 15, 8), Block.box(0, 0, 0, 2, 16, 2)), facing);
		if (internPart.equals(new BlockPos(2, 4, 1))) return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(6, 13, 0, 8, 15, 16), Block.box(0, 0, 0, 2, 16, 16)), facing);
		
		return Block.box( 0, 0, 0, 16, 16, 16);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos partPos = getInternPartPos(state);
		return partPos.equals(new BlockPos(0, 0, 0)) || partPos.equals(new BlockPos(2, 0, 0)) || partPos.equals(new BlockPos(2, 0, 0));
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMBlastFurnace tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		BlockPos partPos = BlockMultiPart.getInternPartPos(state);
		if (partPos.equals(new BlockPos(1, 4, 1)) || partPos.equals(new BlockPos(1, 0, 0))) {
			return (ISidedInventory) getCenterTE(pos, state, world);
		}
		return null;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 1.2F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 1.2F));
			info.add(new TranslationTextComponent("industria.block.info.blastFurnace"));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMBlastFurnaceItemRenderer::new;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityMBlastFurnace tileEntity = getCenterTE(pos, state, world);
		return tileEntity.canWork() ? 1.2F : 0F;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return BlockMultiPart.getInternPartPos(state).getY() == 0;
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
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {
			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
	}
	
}
