package de.m_marvin.industria.blocks;

import java.util.List;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.Lang;

import de.m_marvin.industria.blockentities.GeneratorBlockEntity;
import de.m_marvin.industria.blockentities.MotorBlockEntity;
import de.m_marvin.industria.registries.ConduitConnectionTypes;
import de.m_marvin.industria.registries.ModBlockEntities;
import de.m_marvin.industria.registries.ModBlockStateProperties;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.Formater;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IElectricConnector;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.electricity.ElectricNetwork;
import de.m_marvin.industria.util.electricity.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.util.types.MotorMode;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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

public class MotorBlock extends DirectionalKineticBlock implements EntityBlock, IElectricConnector {
	
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
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ModBlockStateProperties.MOTOR_MODE);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level level, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		
		BlockEntity be = level.getBlockEntity(pPos);
		if (be instanceof MotorBlockEntity) {
			//((MotorBlockEntity) be).setGenerator(1, 1000);
		} else if (be instanceof GeneratorBlockEntity) {
			((GeneratorBlockEntity) be).tick();
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		ElectricNetworkHandlerCapability networkHandler = UtilityHelper.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		BlockEntity entity = level.getBlockEntity(position);
		if (entity instanceof MotorBlockEntity) {
			double voltage = networkHandler.getVoltageAt(getConnectionPoints(position, instance)[0]);
			((MotorBlockEntity) entity).setVoltage(voltage);
			
			System.out.println("####################### DEBUG ########################");
			System.out.println("Motor Voltage: " + voltage);
			System.out.println("####################### END ##########################");
			
		}
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
			System.out.println(((GeneratorBlockEntity) blockEntity).getCurrent() + " " + ((GeneratorBlockEntity) blockEntity).getVoltage() + " " + level.isClientSide());
			circuit.addSource(points[0], ((GeneratorBlockEntity) blockEntity).getVoltage(), ((GeneratorBlockEntity) blockEntity).getCurrent());
		} else if (instance.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR && blockEntity instanceof MotorBlockEntity) {
			//circuit.addFixLoad(points[0], ((MotorBlockEntity) blockEntity).getCurrent());
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
