package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.tileentity.TileEntityMThermalZentrifuge;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMThermalZentrifuge extends BlockContainerBase implements IElectricConnective, ISidedInventoryProvider, IAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	
	public BlockMThermalZentrifuge() {
		super("thermal_zentrifuge", Material.IRON, 3F, SoundType.METAL);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ACTIVE, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, ACTIVE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMThermalZentrifuge();
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.HightVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntityMThermalZentrifuge) {
			return ((TileEntityMThermalZentrifuge) tileEntity).canWork() ? 1.5F : 0;
		}
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side != state.get(FACING).getOpposite();
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}

	@Override
	public ISidedInventory createInventory(BlockState sate, IWorld world, BlockPos pos) {
		return (ISidedInventory) world.getTileEntity(pos);
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {

		if (network.getVoltage().getVoltage() > Voltage.HightVoltage.getVoltage() && network.getCurrent() > 0) {

			worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			
		}
		
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof INamedContainerProvider) {
			if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}

	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.needEnergy", (1.5F * Voltage.HightVoltage.getVoltage() / 1000F) + "k"));
		info.add(new TranslationTextComponent("redtec.block.info.needVoltage", Voltage.HightVoltage.getVoltage()));
		info.add(new TranslationTextComponent("redtec.block.info.needCurrent", 1.5F));
		info.add(new TranslationTextComponent("redtec.block.info.thermalZentrifuge"));
		return info;
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
}
