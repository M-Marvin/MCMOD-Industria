package de.industria.blocks;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntitySimpleBlockTicking;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.blockfeatures.IElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockTRailAdapter extends BlockContainerBase implements IAdvancedBlockInfo, IElectricConnectiveBlock {
	
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockTRailAdapter() {
		super("rail_adapter", Material.METAL, 3F, 3F, SoundType.METAL);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.HightVoltage;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0.2F;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", 0.2F * Voltage.HightVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.HightVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 0.2F));
			info.add(new TranslationTextComponent("industria.block.info.railAdapter"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		if (!worldIn.isClientSide()) {
			
			ElectricityNetworkHandler.getHandlerForWorld(worldIn).updateNetwork(worldIn, pos);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(worldIn).getNetwork(pos);
			
			boolean power = network.canMachinesRun() == Voltage.HightVoltage;
			boolean powered = state.getValue(POWERED);
			
			if (power != powered) worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, power));
			
		}
		
	}

	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.HightVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			
		}
		
	}
	
}
