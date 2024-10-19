package de.m_marvin.industria.content.blocks.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.m_marvin.industria.content.blockentities.machines.ElectroMagneticCoilBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockStateProperties;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.content.registries.ModTags;
import de.m_marvin.industria.core.client.util.TooltipAdditions;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate.Plotter;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.magnetism.MagnetismUtility;
import de.m_marvin.industria.core.magnetism.types.blocks.IMagneticBlock;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.parametrics.properties.DoubleParameter;
import de.m_marvin.industria.core.registries.Circuits;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.blocks.IBaseEntityDynamicMultiBlock;
import de.m_marvin.industria.core.util.items.ITooltipAdditionsModifier;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ElectroMagneticCoilBlock extends BaseEntityBlock implements IBaseEntityDynamicMultiBlock, IElectricBlock, IMagneticBlock, ITooltipAdditionsModifier, SimpleWaterloggedBlock {
	
	public static final DoubleParameter MAGNETIC_FIELD_STRENGTH = new DoubleParameter("magneticFieldStrengthPerWatt", 0.01);
	public static final DoubleParameter MAGNET_RESISTANCE = new DoubleParameter("magnetResistance", 10);
	public static final int CONNECTION_PER_NODE = 1;
	
	public static final VoxelShape DOWN_SHAPE =VoxelShapeUtility.box(0, 0, 0, 16, 2, 16);
	public static final VoxelShape UP_SHAPE = VoxelShapeUtility.box(0, 14, 0, 16, 16, 16);
	
	public ElectroMagneticCoilBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public boolean showTooltipType(String tooltipTypeName) {
		return 	tooltipTypeName != TooltipAdditions.TOOLTIP_ELECTRICS &&
				tooltipTypeName != TooltipAdditions.TOOLTIP_MAGNETICS;
	}
	
	@Override
	public void addAdditionsTooltip(List<Component> tooltips, ItemStack item) {
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);
		TooltipAdditions.addTooltip(tooltips, Component.translatable("industria.tooltip.transformer.resistance", parametrics.getParameter(MAGNET_RESISTANCE)));
		TooltipAdditions.addTooltip(tooltips, Component.translatable("industria.tooltip.transformer.fieldstrength", parametrics.getParameter(MAGNETIC_FIELD_STRENGTH)));
	}
	
	@Override
	public Vec3d getFieldVector(Level level, BlockState state, BlockPos blockPos) {
		if (level != null && level.getBlockEntity(blockPos) instanceof ElectroMagneticCoilBlockEntity coil) {
			coil = coil.getMaster();
			if (coil.getWindingsSecundary() == 0) {
				
				BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);

				double factor = coil.getWindingsPrimary() / (double) coil.getMaxWindings();
				double voltage = ElectricUtility.getVoltageBetweenLocal(level, blockPos, "L", true, "N", true).orElseGet(() -> 0.0);
				double powerIn = voltage * (voltage / parametrics.getParameter(MAGNET_RESISTANCE));
				double fieldStrength = powerIn * parametrics.getParameter(MAGNETIC_FIELD_STRENGTH) * factor;
				
				Direction facing = state.getValue(BlockStateProperties.FACING);
				return Vec3d.fromVec(facing.getNormal()).mul(fieldStrength);
				
			}
		}
		return new Vec3d(0, 0, 0);
	}
	
	@Override
	public double getCoefficient(Level level, BlockState state, BlockPos pos) {
		return BlockParametricsManager.getInstance().getParametrics(ModBlocks.ELECTRO_MAGNETIC_COIL.get()).getMagneticCoefficient();
	}
	
	@Override
	public void onInductionNotify(Level level, BlockState state, BlockPos pos, Vec3d inductionVector) {
		if (level.getBlockEntity(pos) instanceof ElectroMagneticCoilBlockEntity coil) {
			if (coil.isMaster()) level.scheduleTick(pos, this, 1);
		}
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ElectroMagneticCoilBlockEntity(pPos, pState);
	}
	
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		if (pLevel.getBlockEntity(pPos) instanceof ElectroMagneticCoilBlockEntity coil) {
			if (coil.isMaster()) {
				coil.dropWires();
			}
		}
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.FACING);
		pBuilder.add(BlockStateProperties.NORTH);
		pBuilder.add(BlockStateProperties.SOUTH);
		pBuilder.add(BlockStateProperties.EAST);
		pBuilder.add(BlockStateProperties.WEST);
		pBuilder.add(BlockStateProperties.DOWN);
		pBuilder.add(BlockStateProperties.UP);
		pBuilder.add(ModBlockStateProperties.CONNECT);
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction attachFace = pContext.getClickedFace().getOpposite();
		Direction facing = pContext.getNearestLookingDirection();
		BlockState attachState = pContext.getLevel().getBlockState(pContext.getClickedPos().relative(attachFace));
		if (attachState.getBlock() instanceof ElectroMagneticCoilBlock) facing = attachState.getValue(BlockStateProperties.FACING);
		return super.getStateForPlacement(pContext)
				.setValue(BlockStateProperties.NORTH, false)
				.setValue(BlockStateProperties.SOUTH, false)
				.setValue(BlockStateProperties.EAST, false)
				.setValue(BlockStateProperties.WEST, false)
				.setValue(BlockStateProperties.DOWN, false)
				.setValue(BlockStateProperties.UP, false)
				.setValue(BlockStateProperties.FACING, facing)
				.setValue(ModBlockStateProperties.CONNECT, attachFace);
	}
	
	private VoxelShape makeCenterShape(boolean north, boolean south, boolean east, boolean west) {
		float f1 = 14 * 0.0625F;
		float f0 = 2 * 0.0625F;
		return Shapes.box(west ? 0 : f0, 0, north ? 0 : f0, east ? 1 : f1, 1, south ? 1 : f1);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(BlockStateProperties.FACING).getAxis()) {
		case Y:
			return Shapes.or(
					makeCenterShape(
						pState.getValue(BlockStateProperties.NORTH),
						pState.getValue(BlockStateProperties.SOUTH),
						pState.getValue(BlockStateProperties.EAST),
						pState.getValue(BlockStateProperties.WEST)
					),
					!pState.getValue(BlockStateProperties.UP) ? UP_SHAPE : Shapes.empty(),
					!pState.getValue(BlockStateProperties.DOWN) ? DOWN_SHAPE : Shapes.empty()
					);
		case X:
			return VoxelShapeUtility.transformation()
					.centered()
					.rotateZ(-90)
					.uncentered()
					.transform(Shapes.or(
						makeCenterShape(
							pState.getValue(BlockStateProperties.NORTH),
							pState.getValue(BlockStateProperties.SOUTH),
							pState.getValue(BlockStateProperties.DOWN),
							pState.getValue(BlockStateProperties.UP)
						),
						!pState.getValue(BlockStateProperties.EAST) ? UP_SHAPE : Shapes.empty(),
						!pState.getValue(BlockStateProperties.WEST) ? DOWN_SHAPE : Shapes.empty()
					));
		case Z:
			return VoxelShapeUtility.transformation()
			.centered()
			.rotateX(-90)
			.uncentered()
			.transform(Shapes.or(
				makeCenterShape(
					pState.getValue(BlockStateProperties.UP),
					pState.getValue(BlockStateProperties.DOWN),
					pState.getValue(BlockStateProperties.EAST),
					pState.getValue(BlockStateProperties.WEST)
				),
				!pState.getValue(BlockStateProperties.SOUTH) ? UP_SHAPE : Shapes.empty(),
				!pState.getValue(BlockStateProperties.NORTH) ? DOWN_SHAPE : Shapes.empty()
			));
		default: return Shapes.empty();
		}
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos position, BlockState instance) {
		if (level.getBlockEntity(position) instanceof ElectroMagneticCoilBlockEntity coil) {
			if (coil.isMaster()) return coil.getConduitNodes();
		}
		return new ConduitNode[0];
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos position, BlockState instance) {
		if (level.getBlockEntity(position) instanceof ElectroMagneticCoilBlockEntity coil) {
			if (coil.isMaster()) return coil.getMaster().getConnections();
		}
		return new NodePos[0];
	}

	@Override
	public BlockPos getConnectorMasterPos(Level level, BlockPos position, BlockState state) {
		if (level.getBlockEntity(position) instanceof ElectroMagneticCoilBlockEntity coil) {
			return coil.getMasterPos();
		}
		return position;
	}
	
	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork network, Consumer<ICircuitPlot> plotter) {
		if (level.getBlockEntity(position) instanceof ElectroMagneticCoilBlockEntity coil) {
			
			BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);
			
			NodePos[] nodes = coil.getConnections();
			NodePos[] inputNodes = Arrays.copyOfRange(nodes, 0, nodes.length / 2);
			NodePos[] outputNodes = Arrays.copyOfRange(nodes, nodes.length / 2, nodes.length);
			
			if (coil.getWindingsPrimary() > 0 && coil.getWindingsSecundary() > 0) {
				
				ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, inputNodes, true, "L1", "N1");
				ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, outputNodes, true, "L2", "N2");
				
				double windingRatio = coil.getWindingsSecundary() / (double) coil.getWindingsPrimary();
				
				Plotter templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.TRANSFORMER).plotter();
				templateSource.setProperty("winding_ratio", 1 / windingRatio);
				templateSource.setNetworkLocalNode("VDC_A", position, "L1", true);
				templateSource.setNetworkLocalNode("GND_A", position, "N1", true);
				templateSource.setNetworkLocalNode("VDC_B", position, "L2", true);
				templateSource.setNetworkLocalNode("GND_B", position, "N2", true);
				plotter.accept(templateSource);
				
			} else if (coil.getWindingsPrimary() > 0) {
				
				ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, true, "L", "N");
				
				double resistance = parametrics.getParameter(MAGNET_RESISTANCE);
				
				Plotter templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.RESISTOR).plotter();
				templateSource.setProperty("resistance", resistance);
				templateSource.setNetworkLocalNode("NET1", position, "L", true);
				templateSource.setNetworkLocalNode("NET2", position, "N", true);
				plotter.accept(templateSource);
				
			}
			
		}
	}
	
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pLevel.getBlockEntity(pPos) instanceof ElectroMagneticCoilBlockEntity) {
			MagnetismUtility.updateField(pLevel, pPos);
		}
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		level.scheduleTick(position, this, 1);
	}

	protected boolean findConnectableBlocks(Level level, BlockPos pos, BlockState state, Direction relative, boolean attached, int limit, int depth, List<BlockPos> connectedBlocks) {
		if (!state.is(ModTags.Blocks.ELECTRO_MAGNETIC_COILS)) return true;
		if (relative != null) {
			BlockState originState = level.getBlockState(pos.relative(relative.getOpposite()));
			if (
				state.getBlock() instanceof ElectroMagneticCoilBlock && 
				originState.getBlock() instanceof ElectroMagneticCoilBlock && 
				state.getValue(BlockStateProperties.FACING) != originState.getValue(BlockStateProperties.FACING)
					) return true;
		}
		if (depth >= limit || connectedBlocks.size() > limit) return false;
		Direction attachFacing = state.getValue(ModBlockStateProperties.CONNECT);
		if (attachFacing.getOpposite() != relative && !attached) return true;
		connectedBlocks.add(pos);
		for (Direction d : Direction.values()) {
			BlockPos attachedPos = pos.relative(d);
			BlockState attachState = level.getBlockState(attachedPos);
			if (connectedBlocks.contains(attachedPos)) continue;
			if (!findConnectableBlocks(level, attachedPos, attachState, d, d == attachFacing, limit, depth + 1, connectedBlocks)) return false;
		}
		return true;
	}
	
	public boolean isStructureValid(Level level, BlockState state, List<BlockPos> connectedBlocks) {
		if (connectedBlocks.isEmpty()) return false;
		
		BlockPos min = connectedBlocks.stream().reduce(MathUtility::getMinCorner).get();
		BlockPos max = connectedBlocks.stream().reduce(MathUtility::getMaxCorner).get();
		Direction facing = state.getValue(BlockStateProperties.FACING);
		
		for (int y = min.getY(); y <= max.getY(); y++) {
			for (int z = min.getZ(); z <= max.getZ(); z++) {
				for (int x = min.getX(); x <= max.getX(); x++) {
					BlockPos pos2 = new BlockPos(x, y, z);
					BlockState state2 = level.getBlockState(pos2);
					
					if (state2.getBlock() != this || !connectedBlocks.contains(pos2) || state2.getValue(BlockStateProperties.FACING) != facing) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public List<BlockPos> findMultiBlockEntityBlocks(Level level, BlockPos pos, BlockState state) {
		List<BlockPos> connectedBlocks = new ArrayList<>();
		boolean toLarge = !findConnectableBlocks(level, pos, state, null, true, 36, 0, connectedBlocks);
		if (toLarge || !isStructureValid(level, state, connectedBlocks)) {
			connectedBlocks.clear();
			connectedBlocks.add(pos);
		}
		return connectedBlocks;
	}
	
	protected void updateConnections(Level level, BlockPos pos, BlockState state) {
		
		List<BlockPos> connectedBlocks = new ArrayList<>();
		boolean toLarge = !findConnectableBlocks(level, pos, state, null, true, 36, 0, connectedBlocks);
		boolean isValid = !toLarge && isStructureValid(level, state, connectedBlocks);
		
		if (isValid) {

			BlockPos min = connectedBlocks.stream().reduce(MathUtility::getMinCorner).get();
			BlockPos max = connectedBlocks.stream().reduce(MathUtility::getMaxCorner).get();
			Axis axis = state.getValue(BlockStateProperties.FACING).getAxis();
			
			Map<BlockPos, BlockState> newStates = new HashMap<>();
			
			boolean hasChanged = false;
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					for (int x = min.getX(); x <= max.getX(); x++) {
						BlockPos pos2 = new BlockPos(x, y, z);
						BlockState state2 = level.getBlockState(pos2);
						
						if (state2.getBlock() == this && connectedBlocks.contains(pos2)) {
							
							boolean outerBlock = false;;
							switch (axis) {
							case X: outerBlock = y == min.getY() || y == max.getY() || z == min.getZ() || z == max.getZ();
							case Y: outerBlock = x == min.getX() || x == max.getX() || z == min.getZ() || z == max.getZ();
							case Z: outerBlock = x == min.getX() || x == max.getX() || y == min.getY() || y == max.getY();
							}
							
							BlockState connectedState = state2
									.setValue(BlockStateProperties.NORTH, z == min.getZ() ? false : outerBlock)
									.setValue(BlockStateProperties.SOUTH, z == max.getZ() ? false : outerBlock)
									.setValue(BlockStateProperties.EAST, x == max.getX() ? false : outerBlock)
									.setValue(BlockStateProperties.WEST, x == min.getX() ? false : outerBlock)
									.setValue(BlockStateProperties.UP, y == max.getY() ? false : outerBlock)
									.setValue(BlockStateProperties.DOWN, y == min.getY() ? false : outerBlock);
							
							if (!connectedState.equals(state2)) {
								hasChanged = true;
								newStates.put(pos2, connectedState);
							}
							
						}
						
					}
				}
			}
			
			if (hasChanged) {
				
				// Reset all masters and drop items
				for (BlockPos pos2 : connectedBlocks) {
					if (level.getBlockEntity(pos2) instanceof ElectroMagneticCoilBlockEntity transformer) {
						if (transformer.isMaster()) {
							List<ConduitEntity> conduits = ConduitUtility.getConduitsAtBlock(level, transformer.getBlockPos());
							conduits.forEach(c -> ConduitUtility.removeConduit(level, c.getPosition(), true));
						}
						transformer.setMaster(false);
						transformer.dropWires();
					}
				}

				// Set one block as master (no additional update required, the ones from above are still effective)
				if (level.getBlockEntity(min) instanceof ElectroMagneticCoilBlockEntity transformer) {
					transformer.setMaster(true);
				}
				
				// Set new states
				for (BlockPos pos1 : newStates.keySet()) {
					level.setBlockAndUpdate(pos1, newStates.get(pos1));
				}
				
			}
			
		} else {
			
			for (BlockPos pos2 : connectedBlocks) {
				
				BlockState state2 = level.getBlockState(pos2);
				BlockState unconnectedState = state2
						.setValue(BlockStateProperties.NORTH, false)
						.setValue(BlockStateProperties.SOUTH, false)
						.setValue(BlockStateProperties.EAST, false)
						.setValue(BlockStateProperties.WEST, false)
						.setValue(BlockStateProperties.UP, false)
						.setValue(BlockStateProperties.DOWN, false);

				// Set block as master (of it self)
				if (level.getBlockEntity(pos2) instanceof ElectroMagneticCoilBlockEntity transformer) {
					transformer.setMaster(true);
				}
				
				if (!unconnectedState.equals(state2)) {
					level.setBlockAndUpdate(pos2, unconnectedState);
				}
				
			}
			
		}
		
	}
	
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		updateConnections(pLevel, pPos, pState);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
		BlockState neighborState = pLevel.getBlockState(pPos);
		if (neighborState.getBlock() != this) return;
		updateConnections(pLevel, pPos, pState);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		
		InteractionResult r = GameUtility.openJunctionBlockEntityUI(pLevel, pPos, pPlayer, pHand);
		if (r != InteractionResult.PASS) return r;
		
		if (pLevel.getBlockEntity(pPos) instanceof ElectroMagneticCoilBlockEntity transformer) {
			ElectroMagneticCoilBlockEntity transformerMaster = transformer.getMaster();
			ItemStack handItem = pPlayer.getItemInHand(pHand);
			int wiresPerWinding = transformerMaster.getWiresPerWinding();
			
			boolean hitSide = pState.getValue(BlockStateProperties.FACING).getAxis() != pHit.getDirection().getAxis();
			
			if (hitSide) {
				
				if (handItem.is(IndustriaTags.Items.CUTTERS) && (transformerMaster.getWindingsPrimary() + transformerMaster.getWindingsSecundary()) > 0) {
					
					ItemStack drops = ItemStack.EMPTY;
					if (transformerMaster.getWiresSecundary().getCount() > 0) {
						ItemStack wires = transformerMaster.getWiresSecundary();
						int removedCount = Math.min(wires.getCount(), transformerMaster.getWiresPerWinding());
						drops = wires.copy();
						drops.setCount(removedCount);
						wires.shrink(removedCount);
					} else if (transformerMaster.getWiresPrimary().getCount() > 0) {
						ItemStack wires = transformerMaster.getWiresPrimary();
						int removedCount = Math.min(wires.getCount(), transformerMaster.getWiresPerWinding());
						drops = wires.copy();
						drops.setCount(removedCount);
						wires.shrink(removedCount);
					}
					
					if (!drops.isEmpty()) {
						
						GameUtility.dropItem(pLevel, drops, Vec3f.fromVec(pPos.relative(pHit.getDirection())).add(0.5F, 0.5F, 0.5F), 0.5F, 0.5F);
						
						// TODO cutter sound
//						SoundType soundType = transformerMaster.getWireConduitPrimary().getSoundType();
//						pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), soundType.getBreakSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch(), false);
						
						if (!pPlayer.isCreative()) {
							handItem.hurtAndBreak(1, pPlayer, (p) -> {});
						}
						
						ElectricUtility.updateNetwork(pLevel, transformerMaster.getBlockPos());
						GameUtility.triggerClientSync(pLevel, transformerMaster.getBlockPos());
						
						return InteractionResult.SUCCESS;
						
					}
					
				} else if (handItem.getCount() >= wiresPerWinding && (transformerMaster.getWindingsPrimary() + transformerMaster.getWindingsSecundary()) < transformer.getMaxWindings()) {
					
					boolean inserted = false;
					if (transformerMaster.isValidWireItemPrimary(handItem)) {
						ItemStack wires = transformerMaster.getWiresPrimary();
						if (wires.isEmpty()) {
							wires = handItem.copy();
							wires.setCount(wiresPerWinding);
						} else {
							wires.grow(wiresPerWinding);
						}
						transformerMaster.setWiresPrimary(wires);
						inserted = true;
					} else if (transformerMaster.isValidWireItemSecundary(handItem)) {
						ItemStack wires = transformerMaster.getWiresSecundary();
						if (wires.isEmpty()) {
							wires = handItem.copy();
							wires.setCount(wiresPerWinding);
						} else {
							wires.grow(wiresPerWinding);
						}
						transformerMaster.setWiresSecundary(wires);
						inserted = true;
					}
					
					if (inserted) {
						
						SoundType soundType = transformerMaster.getWireConduitPrimary().getSoundType();
						pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), soundType.getBreakSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch(), false);

						// TODO cutter sound
//						SoundType soundType = transformerMaster.getWireConduitPrimary().getSoundType();
//						pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), soundType.getBreakSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch(), false);
						
						if (!pPlayer.isCreative()) {
							handItem.shrink(wiresPerWinding);
						}
						
						ElectricUtility.updateNetwork(pLevel, transformerMaster.getBlockPos());
						GameUtility.triggerClientSync(pLevel, transformerMaster.getBlockPos());
						
						return InteractionResult.SUCCESS;
						
					}
					
				}
				
			}
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		switch (pRotation) {
		 case CLOCKWISE_180:
			return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.EAST))
					.setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)))
					.setValue(ModBlockStateProperties.CONNECT, pRotation.rotate(pState.getValue(ModBlockStateProperties.CONNECT)));
		 case COUNTERCLOCKWISE_90:
			return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.NORTH))
					.setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)))
					.setValue(ModBlockStateProperties.CONNECT, pRotation.rotate(pState.getValue(ModBlockStateProperties.CONNECT)));
		 case CLOCKWISE_90:
			return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.SOUTH))
					.setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)))
					.setValue(ModBlockStateProperties.CONNECT, pRotation.rotate(pState.getValue(ModBlockStateProperties.CONNECT)));
		 default:
			return pState;
		}
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		switch (pMirror) {
			case LEFT_RIGHT:
				return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.NORTH))
						.setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)))
						.setValue(ModBlockStateProperties.CONNECT, pMirror.mirror(pState.getValue(ModBlockStateProperties.CONNECT)));
			case FRONT_BACK:
				return pState.setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.EAST))
						.setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)))
						.setValue(ModBlockStateProperties.CONNECT, pMirror.mirror(pState.getValue(ModBlockStateProperties.CONNECT)));
			default:
				return pState;
		}
	}
	
}
