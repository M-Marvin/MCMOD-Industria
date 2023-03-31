package de.m_marvin.industria.content.blocks;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.client.registries.NodeTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class JunctionBlock extends Block implements IConduitConnector, IElectric<BlockState, BlockPos, Block> {

	public JunctionBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void serializeNBT(BlockState instance, BlockPos position, CompoundTag nbt) {
		nbt.put("State", NbtUtils.writeBlockState(instance));
		nbt.put("Position", NbtUtils.writeBlockPos(position));
	}

	@Override
	public BlockState deserializeNBTInstance(CompoundTag nbt) {
		return NbtUtils.readBlockState(nbt.getCompound("State"));
	}

	@Override
	public BlockPos deserializeNBTPosition(CompoundTag nbt) {
		return NbtUtils.readBlockPos(nbt.getCompound("Position"));
	}
	
	public static final NodePointSupplier NODES = new NodePointSupplier().addNodesAround(Axis.Y, NodeTypes.ALL, 1, NodePointSupplier.BLOCK_CENTER);
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes();
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, getConduitNodes(level, pos, instance).length).mapToObj(i -> new NodePos(pos, i)).toArray(i -> new NodePos[i]);
	}
	
	@Override
	public CircuitTemplate plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit) {
		// TODO Auto-generated method stub
		
		CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(Industria.MODID, "source"));
		
		template.setProperty("nominal_current", 12);
		template.setProperty("nominal_voltage", 230);
		template.setNetworkNode("NET1", getConnections(level, position, instance)[0], "");
		template.setNetworkNode("NET2", getConnections(level, position, instance)[1], "");
		
		return template;
		
	}

}
