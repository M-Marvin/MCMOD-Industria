package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMMetalFormerItemRenderer;
import de.industria.tileentity.TileEntityMMetalFormer;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.UtilHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMMetalFormer extends BlockMultipart<TileEntityMMetalFormer> implements IBElectricConnectiveBlock, IBAdvancedBlockInfo, ISidedInventoryProvider {
	
	public BlockMMetalFormer() {
		super("metal_former", Material.METAL, 4F, SoundType.METAL, 1, 1, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return getInternPartPos(state).equals(BlockPos.ZERO);
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMMetalFormer();
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMMetalFormer tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMMetalFormer tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropContents(worldIn, tileEntity.getBlockPos(), (IInventory) tileEntity);
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return getCenterTE(pos, state, world).canWork() ? 1.2F : 0;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return true;
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
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {
			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
	
	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		return getCenterTE(pos, state, world);
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 1.2F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 1.2F));
			info.add(new TranslationTextComponent("industria.block.info.metalFormer"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMMetalFormerItemRenderer::new;
	}
	
}
