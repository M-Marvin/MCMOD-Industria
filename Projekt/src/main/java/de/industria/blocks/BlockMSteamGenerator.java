package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMSteamGeneratorItemRenderer;
import de.industria.tileentity.TileEntityMSteamGenerator;
import de.industria.typeregistys.MultipartBuildRecipes;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.UtilHelper;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
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

public class BlockMSteamGenerator extends BlockMultipartBuilded<TileEntityMSteamGenerator> implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public BlockMSteamGenerator() {
		super("steam_generator", Material.METAL, 8F, SoundType.METAL, 3, 3, 2, () -> MultipartBuildRecipes.STEAM_GENERATOR);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos internPos = getInternPartPos(state);
		return internPos.equals(new BlockPos(1, 2, 1)) || internPos.equals(new BlockPos(1, 2, 0)) || internPos.equals(new BlockPos(0, 0, 0));
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMSteamGenerator();
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityMSteamGenerator te = getCenterTE(pos, state, world);
		return te.getVoltage();
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntityMSteamGenerator te = getCenterTE(pos, state, world);
		return -te.getGenerateCurrent();
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side.getAxis() != state.getValue(FACING).getAxis();
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
	
	@Override
	public void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData) {
		TileEntityMSteamGenerator tileEntity = getCenterTE(pos, state, world);
		if (tileEntity != null) tileEntity.storeBuildData(buildData);
	}

	@Override
	public MultipartBuildLocation getBuildData(World world, BlockPos pos, BlockState state) {
		TileEntityMSteamGenerator tileEntity = getCenterTE(pos, state, world);
		return tileEntity.getBuildData();
	}
	
}
