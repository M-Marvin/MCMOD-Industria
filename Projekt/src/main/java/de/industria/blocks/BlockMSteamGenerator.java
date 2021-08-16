package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMSteamGeneratorItemRenderer;
import de.industria.tileentity.TileEntityMSteamGenerator;
import de.industria.tileentity.TileEntityMSteamGenerator.TEPart;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMSteamGenerator extends BlockMultiPart<TileEntityMSteamGenerator> implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public BlockMSteamGenerator() {
		super("steam_generator", Material.METAL, 8F, SoundType.METAL, 3, 3, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return TEPart.hasTileEntity(getInternPartPos(state));
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMSteamGenerator();
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityMSteamGenerator(TEPart.fromPosition(getInternPartPos(state)));
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof TileEntityMSteamGenerator) {
			return ((TileEntityMSteamGenerator) te).getVoltage();
		}
		return Voltage.NoLimit;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof TileEntityMSteamGenerator) {
			return -((TileEntityMSteamGenerator) te).getGenerateCurrent();
		}
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof TileEntityMSteamGenerator) {
			return ((TileEntityMSteamGenerator) te).getPart() == TEPart.ELECTRICITY && side == Direction.UP;
		}
		return false;
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.box(0.01F, 0.01F, 0.01F, 0.99F, 0.99F, 0.99F);
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", (5000F / 1000F) + "k"));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 5000 / Voltage.NormalVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.maxMB", 25));
			info.add(new TranslationTextComponent("industria.block.info.steamGenerator"));
		};
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMSteamGeneratorItemRenderer::new;
	}
	
}
