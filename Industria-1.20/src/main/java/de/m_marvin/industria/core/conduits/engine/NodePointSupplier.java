package de.m_marvin.industria.core.conduits.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class NodePointSupplier {
	
	protected List<Vec3i> positions = new ArrayList<>();
	protected List<ConduitNode> nodes = new ArrayList<>();
	protected Map<Property<?>, BiFunction<Vec3i, Object, Vec3i>> modifiers = new HashMap<>();
	
	private NodePointSupplier() {
		// TODO Auto-generated constructor stub
	}
	
	public static NodePointSupplier define() {
		return new NodePointSupplier();
	}
	
	public NodePointSupplier addNode(NodeType type, int maxConnections, Vec3i position) {
		this.nodes.add(new ConduitNode(type, maxConnections, position));
		this.positions.add(position);
		return this;
	}
	
	public NodePointSupplier addNodesAround(Axis axis, NodeType type, int maxConnections, Vec3i position) {
		for (int i = 0; i < 360; i += 90) {
			Vec3i orientedPosition = MathUtility.rotatePoint(position.sub(8, 8, 8), i, true, axis).add(8, 8, 8);
			System.out.println(i + " -> " + orientedPosition);
			this.nodes.add(new ConduitNode(type, maxConnections, orientedPosition));
			this.positions.add(orientedPosition);
		}
		return this;
	}
	
	public NodePointSupplier rotateOrigin(float angle, Axis axis) {
		for (int i = 0; i < this.nodes.size(); i++) {
			Vec3i rotatedPosition = MathUtility.rotatePoint(this.positions.get(i), angle, true, axis);
			this.positions.get(i).setI(rotatedPosition);
			this.nodes.get(i).changeOffset(rotatedPosition);
		}
		return this;
	}
	
	public static final Vec3i BLOCK_CENTER = new Vec3i(8, 8, 8);
	
	public static final BiFunction<Vec3i, Object, Vec3i> FACING_HORIZONTAL_MODIFIER_DEFAULT_NORTH = (position, prop) -> MathUtility.rotatePoint(position.sub(BLOCK_CENTER), (float) MathUtility.directionHoriziontalAngleDegrees((Direction) prop), true, Axis.Y).add(BLOCK_CENTER);
	public static final BiFunction<Vec3i, Object, Vec3i> FACING_MODIFIER_DEFAULT_NORTH = (position, prop) -> MathUtility.rotatePoint(position.sub(BLOCK_CENTER), (float) MathUtility.directionHoriziontalAngleDegrees((Direction) prop), true, ((Direction) prop).getAxis() == Axis.Y ? Axis.X : Axis.Y).add(BLOCK_CENTER);
	
	public NodePointSupplier addModifier(Property<?> property, BiFunction<Vec3i, Object, Vec3i> modifier) {
		this.modifiers.put(property, modifier);
		return this;
	}
	
	protected Map<BlockState, ConduitNode[]> state2nodes = new HashMap<>();
	
	public ConduitNode[] getNodes(BlockState state) {
		if (!state2nodes.containsKey(state)) {
			List<ConduitNode> nodes = new ArrayList<>();
			for (int i = 0; i < this.nodes.size(); i++) {
				nodes.add(new ConduitNode(this.nodes.get(i).getType(),this.nodes.get(i).getMaxConnections(), this.nodes.get(i).getOffset()));
			}
			for (Property<?> property : state.getProperties()) {
				if (this.modifiers.containsKey(property)) {
					for (int i = 0; i < nodes.size(); i++) {
						ConduitNode node = nodes.get(i);
						Vec3i originalPosition = node.getOffset();
						Vec3i modifiedPosition = this.modifiers.get(property).apply(originalPosition, state.getValue(property));
						node.changeOffset(modifiedPosition);	
					}
				}
			}
			state2nodes.put(state, nodes.toArray(i -> new ConduitNode[i]));
		}
		return state2nodes.get(state);
	}
	
	public ConduitNode[] getNodes() {
		return this.nodes.toArray((l) -> new ConduitNode[l]);
	}
	
}
