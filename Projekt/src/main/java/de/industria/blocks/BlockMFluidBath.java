package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMFluidBathItemRenderer;
import de.industria.tileentity.TileEntityMFluidBath;
import de.industria.typeregistys.MultipartBuildRecipes;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.UtilHelper;
import de.industria.util.handler.VoxelHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
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

public class BlockMFluidBath extends BlockMultipartBuilded<TileEntityMFluidBath> implements IBElectricConnectiveBlock, IBAdvancedBlockInfo, ISidedInventoryProvider {

	public BlockMFluidBath() {
		super("fluid_bath", Material.METAL, 3F, SoundType.METAL, 2, 2, 3, () -> MultipartBuildRecipes.FLUID_BATH);
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		BlockPos ipos = BlockMultipart.getInternPartPos(state);
		if (ipos.equals(new BlockPos(1, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(13, 14, 0, 15, 16, 16), Block.box(0, 0, 0, 16, 8, 12)), state.getValue(BlockMultipart.FACING));
		} else if (ipos.equals(new BlockPos(0, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 14, 0, 3, 16, 16), Block.box(0, 0, 0, 16, 8, 12)), state.getValue(BlockMultipart.FACING));
		} else if (ipos.equals(new BlockPos(0, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 0, 1, 3, 14, 3), Block.box(1, 14, 1, 3, 16, 16), Block.box(0, 0, 8, 16, 8, 16)), state.getValue(BlockMultipart.FACING));
		} else if (ipos.equals(new BlockPos(1, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(13, 0, 1, 15, 14, 3), Block.box(13, 14, 1, 15, 16, 16), Block.box(0, 0, 8, 16, 8, 16)), state.getValue(BlockMultipart.FACING));
		} else if (ipos.equals(new BlockPos(0, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(1, 0, 13, 3, 14, 15), Block.box(1, 14, 0, 3, 16, 15)), state.getValue(BlockMultipart.FACING));
		} else if (ipos.equals(new BlockPos(1, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.box(13, 0, 13, 15, 14, 15), Block.box(13, 14, 0, 15, 16, 15)), state.getValue(BlockMultipart.FACING));
		}

		return Block.box(0, 0, 0, 16, 16, 16);
		
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropContents(worldIn, tileEntity.getBlockPos(), (IInventory) tileEntity);
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMFluidBath();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 2F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 2F));
			info.add(new TranslationTextComponent("industria.block.info.fluidBath"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMFluidBathItemRenderer::new;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity tileEntity = getCenterTE(pos, state, world);
		if (tileEntity instanceof TileEntityMFluidBath) {
			return ((TileEntityMFluidBath) tileEntity).canWork() ? 2F : 0;
		}
		return 0F;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return BlockMultipart.getInternPartPos(state).getY() == 0;
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
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
	public boolean hasTileEntity(BlockState state) {
		BlockPos pos = getInternPartPos(state);
		if (pos.equals(BlockPos.ZERO)) {
			return true;
		} else {
			return pos.equals(new BlockPos(0, 0, 2));
		}
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
		
	}

	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		if (getInternPartPos(state).getY() == 0) {
			TileEntityMFluidBath tileEntity = getCenterTE(pos, state, world);
			return tileEntity;
		}
		return null;
	}

	@Override
	public void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, world);
		tileEntity.storeBuildData(buildData);
	}

	@Override
	public MultipartBuildLocation getBuildData(World world, BlockPos pos, BlockState state) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, world);
		return tileEntity.getBuildData();
	}
	
}
