package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMElectricHeaterItemRenderer;
import de.industria.tileentity.TileEntityMElectricHeater;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMElectricHeater extends BlockMultipart<TileEntityMElectricHeater> implements IBAdvancedBlockInfo, IBElectricConnectiveBlock {
	
	public BlockMElectricHeater() {
		super("electric_heater", Material.METAL, 4F, SoundType.METAL, 2, 1, 2);
	}
		
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMElectricHeater();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", (1F * Voltage.HightVoltage.getVoltage() / 1000F) + "k"));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.HightVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 1F));
			info.add(new TranslationTextComponent("industria.block.info.electricHeater"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMElectricHeaterItemRenderer::new;
	}
		
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos pos = getInternPartPos(state);
		return pos.equals(BlockPos.ZERO);
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.HightVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return getCenterTE(pos, state, world).canWork() ? 1 : 0;
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
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.HightVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
		
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
	
}
