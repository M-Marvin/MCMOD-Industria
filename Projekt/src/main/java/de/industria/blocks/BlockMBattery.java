package de.industria.blocks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMBatteryItemRenderer;
import de.industria.tileentity.TileEntityMBattery;
import de.industria.typeregistys.ModTabs;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMBattery extends BlockContainerBase implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<BatteryMode> MODE = EnumProperty.create("mode", BatteryMode.class);
	
	public BlockMBattery() {
		super("battery", Material.STONE, 4F, 7F, SoundType.STONE);
		this.registerDefaultState(this.stateDefinition.any().setValue(MODE, BatteryMode.IDLE));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, MODE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntityMBattery();
	}
	
	public static enum BatteryMode implements IStringSerializable {
		IDLE("idle"),CHARGING("charging"),DISCHARGING("discharching");
		
		protected String name;
		BatteryMode(String name) {
			this.name = name;
		}
		public static BatteryMode byName(String name) {
			switch(name) {
			case "idle": return IDLE;
			case "charging": return CHARGING;
			case "discharching": return DISCHARGING;
			default: return IDLE;
			}
		}
		@Override
		public String getSerializedName() {
			return name;
		}
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityMBattery) {
			return ((TileEntityMBattery) tileEntity).voltage;
		}
		return Voltage.NoLimit;
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityMBattery) {
			TileEntityMBattery battery = (TileEntityMBattery) tileEntity;
			
			if (state.getValue(MODE) == BatteryMode.CHARGING) {
				return battery.getChargeCurrent();
			} else if (state.getValue(MODE) == BatteryMode.DISCHARGING) {
				return -battery.getDischargeCurrent();
			}
			
		}
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side.getAxis() == state.getValue(FACING).getCounterClockWise().getAxis();
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Stream.of(
				Block.box(0, 0, 2, 16, 13, 14),
				Block.box(0, 13, 6, 16, 14, 14),
				Block.box(1, 13, 3, 4, 15, 6),
				Block.box(12, 13, 3, 15, 15, 6),
				Block.box(1, 14, 8, 7, 15, 13),
				Block.box(9, 14, 8, 15, 15, 13)
				).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
		return VoxelHelper.rotateShape(shape, state.getValue(FACING));
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			
			NumberFormat format = new DecimalFormat("0.00");
			String storage = format.format(getStorage(stack) / 50F / 60F / 1000000F);
			String capacity = format.format(getCapacity() / 50F / 60F / 1000000F);
			
			info.add(new TranslationTextComponent("industria.block.info.energy", (storage + "/" + capacity + "M"), (int) (getStorage(stack) / (float) getCapacity() * 100) + "%"));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Math.max(0, getVoltage(stack).getVoltage())));
			info.add(new TranslationTextComponent("industria.block.info.battery"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMBatteryItemRenderer::new;
	}
	
	public long getStorage(ItemStack batteryStack) {
		if (batteryStack.hasTag()) {
			if (batteryStack.getTag().contains("BlockEntityTag")) {
				if (batteryStack.getTag().getCompound("BlockEntityTag").contains("Storage")) {
					return batteryStack.getTag().getCompound("BlockEntityTag").getLong("Storage");
				}
			}
		}
		return 0;
	}

	public long getCapacity() {
		return TileEntityMBattery.MAX_STORAGE;
	}
	
	public Voltage getVoltage(ItemStack batteryStack) {
		if (batteryStack.hasTag()) {
			if (batteryStack.getTag().contains("BlockEntityTag")) {
				if (batteryStack.getTag().getCompound("BlockEntityTag").contains("Voltage")) {
					return Voltage.byName(batteryStack.getTag().getCompound("BlockEntityTag").getString("Voltage"));
				}
			}
		}
		return Voltage.NoLimit;
	}
	
	public ItemStack getChargedBattery(Voltage voltage) {
		CompoundNBT stackTag = new CompoundNBT();
		CompoundNBT teTag = new CompoundNBT();
		teTag.putString("Voltage", voltage.getSerializedName());
		teTag.putLong("Storage", TileEntityMBattery.MAX_STORAGE);
		stackTag.put("BlockEntityTag", teTag);
		CompoundNBT stateTag = new CompoundNBT();
		stateTag.putString("mode", BatteryMode.DISCHARGING.getSerializedName());
		stackTag.put("BlockStateTag", stateTag);
		ItemStack batteryStack = new ItemStack(this);
		batteryStack.setTag(stackTag);
		return batteryStack;
	}
	
	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {
		if (group == ModTabs.MACHINES) {
			list.add(getChargedBattery(Voltage.LowVoltage));
			list.add(getChargedBattery(Voltage.NormalVoltage));
			list.add(getChargedBattery(Voltage.HightVoltage));
			list.add(getChargedBattery(Voltage.ExtremVoltage));
		}
		super.fillItemCategory(group, list);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
}
