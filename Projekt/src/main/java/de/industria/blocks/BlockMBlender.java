package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMBlenderItemRenderer;
import de.industria.tileentity.TileEntityMBlender;
import de.industria.typeregistys.MultipartBuildRecipes;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
import de.industria.util.handler.UtilHelper;
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

public class BlockMBlender extends BlockMultipartBuilded<TileEntityMBlender> implements IBElectricConnectiveBlock, IBAdvancedBlockInfo, ISidedInventoryProvider {
	
	public BlockMBlender() {
		super("blender", Material.METAL, 4F, SoundType.METAL, 3, 3, 3, () -> MultipartBuildRecipes.BLENDER);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		BlockPos ipos = getInternPartPos(state);
		Direction facing = state.getValue(FACING);
		
		if (ipos.equals(new BlockPos(1, 1, 0))) {
			return Block.box(0, 0, 0, 16, 8, 16);
		} else if (ipos.equals(new BlockPos(0, 1, 0))) {
			return VoxelHelper.rotateShape(Block.box(8, 0, 0, 16, 8, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 0))) {
			return Block.box(0, 0, 0, 16, 8, 16);
		} else if (ipos.equals(new BlockPos(1, 2, 0))) {
			return VoxelHelper.rotateShape(Block.box(0, 0, 14, 16, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(0, 2, 0))) {
			return VoxelHelper.rotateShape(Block.box(4, 0, 14, 16, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(1, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 0, 0, 16, 16, 1), Block.box(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(15, 0, 0, 16, 16, 16), Block.box(0, 0, 0, 15, 16, 1)), facing);
		} else if (ipos.equals(new BlockPos(1, 2, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 0, 15, 16, 16, 16), Block.box(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(15, 0, 0, 16, 16, 16), Block.box(0, 0, 15, 15, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(1, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 0, 0, 16, 16, 1), Block.box(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(15, 0, 0, 16, 16, 16), Block.box(0, 0, 0, 15, 16, 1)), facing);
		} else if (ipos.equals(new BlockPos(1, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 0, 15, 16, 16, 16), Block.box(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(15, 0, 0, 16, 16, 16), Block.box(0, 0, 15, 15, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(0, 2, 2))) {
			return VoxelHelper.rotateShape(Block.box(0, 0, 0, 16, 16, 5), facing);
		}
		
		return Block.box(0, 0, 0, 16, 16, 16);
		
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMBlender tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMBlender tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropContents(worldIn, tileEntity.getBlockPos(), (IInventory) tileEntity);
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMBlender();
	}
	
	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		TileEntityMBlender tileEntity = getCenterTE(pos, state, world);
		return tileEntity;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos pos = getInternPartPos(state);
		if (pos.equals(BlockPos.ZERO)) {
			return true;
		} else {
			return pos.equals(new BlockPos(2, 1, 0)) || pos.equals(new BlockPos(2, 2, 2)) || pos.equals(new BlockPos(1, 1, 2));
		}
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 1.2F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 1.2F));
			info.add(new TranslationTextComponent("industria.block.info.blender"));
		};
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMBlenderItemRenderer::new;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return getCenterTE(pos, state, world).canWork() ? 1.2F : 0F;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return getInternPartPos(state).getY() == 0;
	}
	
	@Override
	public List<BlockPos> getMultiBlockParts(World world, BlockPos pos, BlockState state) {
		List<BlockPos> multiParts = new ArrayList<BlockPos>();
		Direction facing = state.getValue(FACING);
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				for (int z = 0; z < this.sizeZ; z++) {
					BlockPos internPos = new BlockPos(x, y, z);
					BlockPos offset = UtilHelper.rotateBlockPos(internPos, facing);
					BlockPos partPos = getCenterTE(pos, state, world).getBlockPos().offset(offset);
					multiParts.add(partPos);
				}
			}
		}
		return multiParts;
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {
			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
	
	@Override
	public void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData) {
		TileEntityMBlender tileEntity = getCenterTE(pos, state, world);
		tileEntity.storeBuildData(buildData);
	}

	@Override
	public MultipartBuildLocation getBuildData(World world, BlockPos pos, BlockState state) {
		TileEntityMBlender tileEntity = getCenterTE(pos, state, world);
		return tileEntity.getBuildData();
	}
	
}