package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;

import de.m_marvin.industria.content.blockentities.machines.ElectroMagneticCoilBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.TransformerCoilBlockEntity;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class TransformerCoilBlock extends ElectroMagneticCoilBlock implements IElectricBlock {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 4, new Vec3i(8, 16, 8))
			.addNode(NodeTypes.ELECTRIC, 4, new Vec3i(8, 0, 8))
			.addModifier(BlockStateProperties.AXIS, NodePointSupplier.AXIS_MODIFIER_DEFAULT_Y);
	
	public TransformerCoilBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new TransformerCoilBlockEntity(pPos, pState);
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return NODES.getNodePositions(pos);
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof TransformerCoilBlockEntity transformer) {
			return transformer.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof TransformerCoilBlockEntity transformer) {
			transformer.setNodeLanes(laneLabels);
		}
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		if (level.getBlockEntity(position) instanceof TransformerCoilBlockEntity transformer) {
			
			String[] nodeLanes = transformer.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, 0, nodeLanes[0], 1, nodeLanes[1]);
			
			
			
		}
	}
	
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pLevel.getBlockEntity(pPos) instanceof ElectroMagneticCoilBlockEntity coil) {
			coil.getMaster().updateElectromagnetism();
		}
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		level.scheduleTick(position, this, 1);
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pPlayer.getItemInHand(pHand).is(IndustriaTags.Items.CONDUITS) && pHit.getDirection().getAxis() == pState.getValue(BlockStateProperties.AXIS)) return InteractionResult.PASS;
		InteractionResult result = GameUtility.openJunctionBlockEntityUI(pLevel, pPos, pPlayer, pHand);
		if (result == InteractionResult.SUCCESS) return InteractionResult.SUCCESS;
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
	
}
