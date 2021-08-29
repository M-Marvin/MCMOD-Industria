package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.multipartbuilds.MultipartBuild.MultipartBuildLocation;
import de.industria.tileentity.TileEntityMThermalZentrifuge;
import de.industria.typeregistys.MultipartBuildRecipes;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.UtilHelper;
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

public class BlockMThermalZentrifuge extends BlockMultipartBuilded<TileEntityMThermalZentrifuge> implements IBElectricConnectiveBlock, IBAdvancedBlockInfo, ISidedInventoryProvider {
	
	public BlockMThermalZentrifuge() {
		super("thermal_zentrifuge", Material.METAL, 4F, SoundType.METAL, 2, 3, 2, () -> MultipartBuildRecipes.THERMAL_ZENTRIFUGE);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMThermalZentrifuge tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMThermalZentrifuge tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropContents(worldIn, tileEntity.getBlockPos(), (IInventory) tileEntity);
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntityMThermalZentrifuge();
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 1F * Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 1F));
			info.add(new TranslationTextComponent("industria.block.info.thermalZentrifuge"));
		};
	}

	@Override
	public int getStackSize() {
		return 1;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos partPos = getInternPartPos(state);
		return partPos.equals(BlockPos.ZERO);
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null; // TODO
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityMThermalZentrifuge tileEntity = getCenterTE(pos, state, world);
		return tileEntity.canWork() ? 1 : 0;
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
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.NormalVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
		
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
	}

	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		TileEntityMThermalZentrifuge tileEntity = getCenterTE(pos, state, world);
		return (ISidedInventory) tileEntity;
	}
	
	@Override
	public void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData) {
		TileEntityMThermalZentrifuge tileEntity = getCenterTE(pos, state, world);
		tileEntity.storeBuildData(buildData);
	}
	
}
