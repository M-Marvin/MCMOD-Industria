package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.renderer.BlockMSteamGeneratorItemRenderer;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.tileentity.TileEntityMSteamGenerator.TEPart;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMSteamGenerator extends BlockMultiPart implements IElectricConnective, IAdvancedBlockInfo {
	
	public BlockMSteamGenerator() {
		super("steam_generator", Material.IRON, 8F, SoundType.METAL, 3, 3, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return TEPart.hasTileEntity(getInternPartPos(state));
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMSteamGenerator();
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityMSteamGenerator(TEPart.fromPosition(getInternPartPos(state)));
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityMSteamGenerator) {
			return ((TileEntityMSteamGenerator) te).getVoltage();
		}
		return Voltage.NoLimit;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityMSteamGenerator) {
			return -((TileEntityMSteamGenerator) te).getGenerateCurrent();
		}
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityMSteamGenerator) {
			return ((TileEntityMSteamGenerator) te).getPart() == TEPart.ELECTRICITY && side == Direction.UP;
		}
		return false;
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.create(0.01F, 0.01F, 0.01F, 0.99F, 0.99F, 0.99F);
	}

	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.needEnergy", "3.68k"));
		info.add(new TranslationTextComponent("redtec.block.info.needVoltage", Voltage.NormalVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.needCurrent", 3680 / Voltage.NormalVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.steamGenerator.maxMB", 50));
		info.add(new TranslationTextComponent("redtec.block.info.steamGenerator"));
		return info;
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMSteamGeneratorItemRenderer::new;
	}
	
}
