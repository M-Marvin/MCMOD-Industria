package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMCapacitorBank extends BlockBase implements IBAdvancedBlockInfo, IBElectricConnectiveBlock {
	
	public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 100);
	public static final EnumProperty<Voltage> VOLTAGE = EnumProperty.create("voltage", Voltage.class);
	public static final BooleanProperty DISCHRAGE = BooleanProperty.create("discharge");
	
	public BlockMCapacitorBank() {
		super("capacitor_bank", Material.METAL, 1.5F, 1.5F, SoundType.METAL);
		this.registerDefaultState(this.stateDefinition.any().setValue(VOLTAGE, Voltage.NoLimit).setValue(CHARGE, 0).setValue(DISCHRAGE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(CHARGE, VOLTAGE, DISCHRAGE);
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return state.getValue(VOLTAGE);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return Stream.of(
				Block.box(11, 8, 1, 15, 16, 5),
				Block.box(0, 0, 0, 16, 8, 16),
				Block.box(1, 8, 1, 5, 16, 5),
				Block.box(11, 8, 11, 15, 16, 15),
				Block.box(1, 8, 6, 5, 16, 10),
				Block.box(6, 8, 6, 10, 16, 10),
				Block.box(11, 8, 6, 15, 16, 10),
				Block.box(6, 8, 11, 10, 16, 15),
				Block.box(1, 8, 11, 5, 16, 15),
				Block.box(6, 8, 1, 10, 16, 5)
				).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		if (state.getValue(CHARGE) != 100 && !state.getValue(DISCHRAGE)) {
			return 4;
		} else if (state.getValue(CHARGE) > 0 && state.getValue(VOLTAGE) != Voltage.NoLimit && state.getValue(DISCHRAGE)) {
			return -4;
		}
		return 0;
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
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.capacitorBank.chargeCurrent", 4F));
			info.add(new TranslationTextComponent("industria.block.info.capacitorBank"));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
		
	@Override
	public NetworkChangeResult beforNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network, int lap) {
		if (!state.getValue(DISCHRAGE) && state.getValue(CHARGE) < 100 && network.canMachinesRun() != Voltage.NoLimit) {
			worldIn.setBlockAndUpdate(pos, state.setValue(VOLTAGE, network.getVoltage()).setValue(CHARGE, state.getValue(CHARGE) + 1));
		} else if (!state.getValue(DISCHRAGE) && network.canMachinesRun() == Voltage.NoLimit && state.getValue(CHARGE) == 100) {
			worldIn.setBlockAndUpdate(pos, state.setValue(DISCHRAGE, true));
			return NetworkChangeResult.SKIPTICK;
		} else if (state.getValue(DISCHRAGE) && state.getValue(CHARGE) > 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(CHARGE, state.getValue(CHARGE) - 1));
		} else if (state.getValue(DISCHRAGE) && state.getValue(CHARGE) == 0) {
			worldIn.setBlockAndUpdate(pos, state.setValue(CHARGE, 0).setValue(DISCHRAGE, false).setValue(VOLTAGE, Voltage.NoLimit));
		}
		return NetworkChangeResult.CONTINUE;
	}
	
}
