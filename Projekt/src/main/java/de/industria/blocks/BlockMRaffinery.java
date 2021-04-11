package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMRaffineryItemRenderer;
import de.industria.tileentity.TileEntityMRaffinery;
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

public class BlockMRaffinery extends BlockMultiPart<TileEntityMRaffinery> implements IElectricConnectiveBlock, IAdvancedBlockInfo, ISidedInventoryProvider {

	public BlockMRaffinery() {
		super("raffinery", Material.IRON, 4F, SoundType.METAL, 3, 4, 2);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMRaffinery tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMRaffinery tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropInventoryItems(worldIn, tileEntity.getPos(), (IInventory) tileEntity);
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

		BlockPos ipos = getInternPartPos(state);
		Direction facing = state.get(FACING);
		
		if (ipos.equals(new BlockPos(2, 1, 1))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 0, 10, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 1))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 0, 10, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 3, 1))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 0, 10, 5, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 0))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(1, 0, 2, 15, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 0))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(1, 0, 2, 15, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 3, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(1, 0, 2, 15, 12, 16), Block.makeCuboidShape(2, 12, 3, 14, 17, 15)), facing);
		}

		return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
		
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMRaffinery();
	}
	
	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
		TileEntityMRaffinery tileEntity = getCenterTE(pos, state, world);
		return tileEntity;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", (2F * Voltage.HightVoltage.getVoltage() / 1000F) + "k"));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.HightVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 2F));
			info.add(new TranslationTextComponent("industria.block.info.raffinery"));
		};
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMRaffineryItemRenderer::new;
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.HightVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityMRaffinery tileEntity = getCenterTE(pos, state, world);
		return tileEntity.canWork() ? 2 : 0;
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
	public boolean hasTileEntity(BlockState state) {
		BlockPos pos = getInternPartPos(state);
		if (pos.equals(BlockPos.ZERO)) {
			return true;
		} else {
			return pos.equals(new BlockPos(1, 0, 1)) || pos.equals(new BlockPos(1, 3, 0));
		}
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.HightVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			
		}
		
	}
	
}
