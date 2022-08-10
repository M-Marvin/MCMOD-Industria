package de.m_marvin.industria.blocks;

import java.util.List;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import de.m_marvin.industria.blockentities.GeneratorBlockEntity;
import de.m_marvin.industria.blockentities.MotorBlockEntity;
import de.m_marvin.industria.registries.ConduitConnectionTypes;
import de.m_marvin.industria.registries.ModBlockEntities;
import de.m_marvin.industria.registries.ModBlockStateProperties;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IElectricConnector;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.electricity.ElectricNetwork;
import de.m_marvin.industria.util.electricity.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.util.types.MotorMode;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;

public class MotorBlock extends DirectionalKineticBlock implements EntityBlock, IElectricConnector, IHaveGoggleInformation {
	
	public static final VoxelShape BLOCK_SHAPE = Block.box(3, 3, 1, 13, 13, 16);
	public static final VoxelShape BLOCK_SHAPE_VERTICAL = Block.box(3, 0, 3, 13, 15, 13);
	
	public static final MutableConnectionPointSupplier CONDUIT_NODES = MutableConnectionPointSupplier.basedOnOrientation(FACING)
			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.SOUTH)
			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.EAST)
			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.WEST)
			.rotateBase(Direction.DOWN);
	public static final MutableConnectionPointSupplier CONDUIT_NODES_VERTICAL = MutableConnectionPointSupplier.basedOnOrientation(FACING)
			.addOnSidesOfAxis(new Vec3i(8, 10, 4), ConduitConnectionTypes.ELECTRIC, 1, Axis.Y)
			.rotateBase(Direction.DOWN);
	
	public MotorBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		if (facing.getAxis() == Axis.Y) {
			return facing.getAxisDirection() == AxisDirection.POSITIVE ? BLOCK_SHAPE_VERTICAL : UtilityHelper.rotateShape(BLOCK_SHAPE_VERTICAL, new Vec3f(8, 8, 8), Math.PI, Axis.X);
		} else {
			return UtilityHelper.rotateShape(BLOCK_SHAPE, new Vec3f(8, 8, 8), facing, Axis.Y);
		}
	}
	
	// https://github.com/mrh0/createaddition/blob/67958eb55fac0654200fe355c2bc4fc5859de3e4/src/main/java/com/mrh0/createaddition/index/CABlocks.java#L150
	// https://github.com/Creators-of-Create/Create/blob/mc1.18/dev/src/main/java/com/simibubi/create/content/contraptions/components/motor/CreativeMotorTileEntity.java
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ModBlockStateProperties.MOTOR_MODE);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level level, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		
		LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (networkHandler.isPresent() && !level.isClientSide() && pHand == InteractionHand.MAIN_HAND) {
			System.out.println("####################### DEBUG ########################");
			networkHandler.resolve().get().updateNetwork(pPos);
			System.out.println("####################### END ##########################");
		}
		
		BlockEntity be = level.getBlockEntity(pPos);
		if (be instanceof MotorBlockEntity) {
			((MotorBlockEntity) be).setGenerator(1, 1000);
		} else if (be instanceof GeneratorBlockEntity) {
			((GeneratorBlockEntity) be).tick();
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (networkHandler.isPresent() && !level.isClientSide()) {
			System.out.println("####################### DEBUG ########################");
			double voltage = networkHandler.resolve().get().getVoltageAt(getConnections(level, position, instance)[0]);
			System.out.println("Motor Voltage: " + voltage);
			System.out.println("####################### END ##########################");
			
			
			
		}
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		// TODO Auto-generated method stub
		return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		
		
		
//		final boolean added = super.addToGoggleTooltip((List) tooltip, isPlayerSneaking);
//		if (!IRotate.StressImpact.isEnabled()) {
//			return added;
//		}
//		float stressBase = this.calculateAddedStressCapacity();
//		if (Mth.equal(stressBase, 0.0f)) {
//			return added;
//		}
//		Lang.translate("gui.goggles.generator_stats", new Object[0]).forGoggles((List) tooltip);
//		Lang.translate("tooltip.capacityProvided", new Object[0]).style(ChatFormatting.GRAY).forGoggles((List) tooltip);
//		float speed = this.getTheoreticalSpeed();
//		if (speed != this.getGeneratedSpeed() && speed != 0.0f) {
//			stressBase *= this.getGeneratedSpeed() / speed;
//		}
//		speed = Math.abs(speed);
//		final float stressTotal = stressBase * speed;
//		Lang.number((double) stressTotal).translate("generic.unit.stress", new Object[0]).style(ChatFormatting.AQUA)
//				.space()
//				.add(Lang.translate("gui.goggles.at_current_speed", new Object[0]).style(ChatFormatting.DARK_GRAY))
//				.forGoggles((List) tooltip, 1);
//		return true;
		
	}
	
	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}
	
	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == getRotationAxis(state);
	}
	
	public BlockEntityType<?> getMotorTileEntityType() {
		return ModBlockEntities.MOTOR.get();
	}
	
	public BlockEntityType<?> getGeneratorTileEntityType() {
		return ModBlockEntities.GENERATOR.get();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR ? getMotorTileEntityType().create(pos, state) : getGeneratorTileEntityType().create(pos, state);
	}
	
	@Override
	public ConnectionPoint[] getConnectionPoints(BlockPos pos, BlockState state) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		if (facing.getAxis() == Axis.Y) {
			return CONDUIT_NODES_VERTICAL.getNodes(pos, state);
		} else {
			return CONDUIT_NODES.getNodes(pos, state);
		}
	}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit) {
		ConnectionPoint[] points = CONDUIT_NODES.getNodes(position, instance);
		BlockEntity blockEntity = level.getBlockEntity(position);
		if (instance.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.GENERATOR && blockEntity instanceof GeneratorBlockEntity) {
			circuit.addSource(points[0], ((GeneratorBlockEntity) blockEntity).getVoltage(), ((GeneratorBlockEntity) blockEntity).getCurrent());
		} else {
			circuit.addParalelResistance(points[0], 50);
		}
		circuit.addSerialResistance(points[0], points[1], 0);
		circuit.addSerialResistance(points[1], points[2], 0);
	}
	
	@Override
	public ConnectionPoint[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return getConnectionPoints(pos, instance);
	}
	
}
