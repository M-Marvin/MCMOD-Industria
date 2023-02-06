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
	
	public static NodePointSupplier define() {
		return new NodePointSupplier();
	}
	
	public NodePointSupplier addNode(NodeType type, int maxConnections, Vec3i position) {
		this.nodes.add(new ConduitNode(type, maxConnections, position));
		this.positions.add(position);
		return this;
	}
	
	public NodePointSupplier addNodesAround(Axis axis, NodeType type, int maxConnections, Vec3i position) {
		for (Direction d : Direction.values()) {
			if (d.getAxis() != axis) {
				Vec3i orientedPosition = MathUtility.rotatePoint(position, (float) MathUtility.directionAngleDegrees(d), true, axis);
				this.nodes.add(new ConduitNode(type, maxConnections, orientedPosition));
			}
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
	
	public static final BiFunction<Vec3i, Object, Vec3i> FACING_HORIZONTAL_MODIFIER_DEFAULT_NORTH = (position, prop) -> MathUtility.rotatePoint(position.sub(BLOCK_CENTER), (float) MathUtility.directionAngleDegrees((Direction) prop), true, Axis.Y).add(BLOCK_CENTER);
	public static final BiFunction<Vec3i, Object, Vec3i> FACING_MODIFIER_DEFAULT_NORTH = (position, prop) -> MathUtility.rotatePoint(position.sub(BLOCK_CENTER), (float) MathUtility.directionAngleDegrees((Direction) prop), true, ((Direction) prop).getAxis() == Axis.Y ? Axis.X : Axis.Y).add(BLOCK_CENTER);
	
	public NodePointSupplier addModifier(Property<?> property, BiFunction<Vec3i, Object, Vec3i> modifier) {
		this.modifiers.put(property, modifier);
		return this;
	}
	
	public NodePointSupplier updateNodes(BlockState state) {
		for (Property<?> property : state.getProperties()) {
			if (this.modifiers.containsKey(property)) {
				for (int i = 0; i < this.nodes.size(); i++) {
					Vec3i originalPosition = this.positions.get(i);
					ConduitNode node = this.nodes.get(i);
					Vec3i modifiedPosition = this.modifiers.get(property).apply(originalPosition, state);
					node.changeOffset(modifiedPosition);	
				}
			}
		}
		return this;
	}
	
	public ConduitNode[] getNodes() {
		return this.nodes.toArray((l) -> new ConduitNode[l]);
	}
	
}
