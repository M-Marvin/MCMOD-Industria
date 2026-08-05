package de.m_marvin.industria.core.electrics.types.blocks;

import de.m_marvin.industria.core.client.util.TooltipAdditions;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ComponentCircuitContext;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ElectricElement;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ElectricNode;
import de.m_marvin.industria.core.electrics.types.blockentities.VoltageSourceBlockEntity;
import de.m_marvin.industria.core.registries.ElectricElements;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.items.ITooltipAdditionsModifier;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import tvnlnna.nodal.NodalElementState;

public class VoltageSourceBlock extends BaseEntityBlock implements IElectricBlock, ITooltipAdditionsModifier {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 8, new Vec3i(8, 8, 0))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	
	public VoltageSourceBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean showTooltipType(String tooltipTypeName) {
		return tooltipTypeName != TooltipAdditions.TOOLTIP_ELECTRICS;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, MathUtility.getFacingDirection(pContext.getPlayer()).getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}
	
	@Override
	public void updateElectricElements(Level level, ElectricReference reference, BlockState instance, ComponentCircuitContext context, boolean initialInstall) {
		
		if ( level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity source) {
			
			String[] sourceLanes = source.getNodeLanes();
			NodePos node = new NodePos(reference.block(), 0);
			ElectricNode cnodeGround = ElectricNode.node(node, sourceLanes[1]);
			ElectricNode cnodeLine = ElectricNode.node(node, sourceLanes[0]);
			NodalElementState sourceState = context.getOrInstall(
					ElectricElements.VOLTAGE_FIXED.get(), ElectricElement.element(reference, "source"), 
					cnodeLine, cnodeGround);
			sourceState.setParameter("U", source.getVoltage());
			sourceState.setParameter("P", source.getPower());
			
		}
		
	}
	
	@Override
	public NodePos[] getElectricConnections(Level level, ElectricReference reference, BlockState instance) {
		return NODES.getNodePositions(reference.block());
	}
	
	@Override
	public String[] getWireLanes(Level level, ElectricReference reference, BlockState instance, NodePos node) {
		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity powerSource) {
			return powerSource.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, ElectricReference reference, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity powerSource) {
			powerSource.getNodeLanes(laneLabels);
		}
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new VoltageSourceBlockEntity(pPos, pState);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)));
	}
	
}
