package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.renderer.BlockMFluidBathItemRenderer;
import de.redtec.tileentity.TileEntityMFluidBath;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import de.redtec.util.VoxelHelper;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMFluidBath extends BlockMultiPart<TileEntityMFluidBath> implements IElectricConnective, IAdvancedBlockInfo, ISidedInventoryProvider {

	public BlockMFluidBath() {
		super("fluid_bath", Material.IRON, 3F, SoundType.METAL, 2, 2, 3);
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(state);
		if (ipos.equals(new BlockPos(1, 1, 1))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(15, 0, 0, 16, 16, 16), state.get(BlockMultiPart.FACING));
		} else if (ipos.equals(new BlockPos(0, 1, 1))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 0, 1, 16, 16), state.get(BlockMultiPart.FACING));
		} else if (ipos.equals(new BlockPos(0, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 16, 12), Block.makeCuboidShape(0, 0, 12, 1, 16, 16)), state.get(BlockMultiPart.FACING));
		} else if (ipos.equals(new BlockPos(1, 1, 0))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 16, 12), Block.makeCuboidShape(15, 0, 12, 16, 16, 16)), state.get(BlockMultiPart.FACING));
		} else if (ipos.equals(new BlockPos(0, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 4, 16, 16, 16), Block.makeCuboidShape(0, 0, 0, 1, 16, 4)), state.get(BlockMultiPart.FACING));
		} else if (ipos.equals(new BlockPos(1, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(0, 0, 4, 16, 16, 16), Block.makeCuboidShape(15, 0, 0, 16, 16, 4)), state.get(BlockMultiPart.FACING));
		}

		return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
		
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, worldIn);
		InventoryHelper.dropInventoryItems(worldIn, tileEntity.getPos(), (IInventory) tileEntity);
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMFluidBath();
	}

	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.needEnergy", 2F * Voltage.NormalVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.needCurrent", 2F));
		info.add(new TranslationTextComponent("redtec.block.info.fluidBath"));
		return info;
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
		return BlockMultiPart.getInternPartPos(state).getY() == 0;
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
			return pos.equals(new BlockPos(0, 1, 0)) || pos.equals(new BlockPos(1, 1, 2));
		}
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			
		}
		
	}

	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
		TileEntityMFluidBath tileEntity = getCenterTE(pos, state, world);
		return tileEntity;
	}
	
}
