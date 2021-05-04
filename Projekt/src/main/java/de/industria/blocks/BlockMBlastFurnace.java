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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockMBlastFurnace extends BlockMultiPart<TileEntityMBlastFurnace> implements IAdvancedBlockInfo, IElectricConnectiveBlock, ISidedInventoryProvider {

	public BlockMBlastFurnace() {
		super("blast_furnace", Material.IRON, 2F, SoundType.METAL, 3, 5, 3);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMBlastFurnace();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos partPos = getInternPartPos(state);
		return partPos.equals(new BlockPos(0, 0, 0)) || partPos.equals(new BlockPos(2, 0, 0)) || partPos.equals(new BlockPos(2, 0, 0));
	}
	
	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
		BlockPos partPos = BlockMultiPart.getInternPartPos(state);
		if (partPos.equals(new BlockPos(1, 4, 1)) || partPos.equals(new BlockPos(1, 0, 0))) {
			return (ISidedInventory) getCenterTE(pos, state, world);
		}
		return null;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
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
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}
	
}
