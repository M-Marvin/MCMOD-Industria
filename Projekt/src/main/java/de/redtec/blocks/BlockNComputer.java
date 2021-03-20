package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.renderer.BlockNComputerItemRenderer;
import de.redtec.tileentity.TileEntityNComputer;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import de.redtec.util.INetworkDevice;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockNComputer extends BlockMultiPart<TileEntityNComputer> implements IElectricConnective, IAdvancedBlockInfo, INetworkDevice {
	
	public BlockNComputer() {
		super("computer", Material.IRON, 2F, SoundType.METAL, 1, 2, 2);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return getInternPartPos(state).equals(BlockPos.ZERO);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityNComputer();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity instanceof TileEntityNComputer && !worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
		return ActionResultType.SUCCESS;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.LowVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityNComputer tileEntity = getCenterTE(pos, state, world);
		return tileEntity.isComputerRunning() ? 20 : 0.1F;
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
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("redtec.block.info.needEnergy", 20F * Voltage.LowVoltage.getVoltage()));
			info.add(new TranslationTextComponent("redtec.block.info.needVoltage", Voltage.LowVoltage.getVoltage()));
			info.add(new TranslationTextComponent("redtec.block.info.needCurrent", 20F));
			info.add(new TranslationTextComponent("redtec.block.info.computer"));
		};
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityNComputer tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropInventoryItems(worldIn, tileEntity.getPos(), (IInventory) tileEntity);
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockNComputerItemRenderer::new;
	}

	@Override
	public NetworkDeviceType getNetworkType() {
		return NetworkDeviceType.DEVICE;
	}

	@Override
	public NetworkDeviceIP getIP(BlockPos pos, BlockState state, World world) {
		TileEntityNComputer tileEntity = getCenterTE(pos, state, world);
		return tileEntity.deviceIP;
	}
	
	@Override
	public void setIP(NetworkDeviceIP ip, BlockPos pos, BlockState state, World world) {
		TileEntityNComputer tileEntity = getCenterTE(pos, state, world);
		tileEntity.deviceIP = ip;
	}

	@Override
	public boolean canConectNetworkWire(IWorldReader world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public void onMessageRecived(NetworkMessage message, World world, BlockPos pos, BlockState state) {
		TileEntityNComputer tileEntity = getCenterTE(pos, state, world);
		tileEntity.onMessageRecived(message);
	}
	
}
