package de.m_marvin.industria.core.conduits.engine;

import java.util.List;
import java.util.function.Predicate;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.univec.impl.Vec4f;

public class NodeType {
	
	private final Predicate<Conduit> conduitPredicate;
	private final Vec4f color;
	
	public boolean isValid(Conduit conduit) {
		return this.conduitPredicate.test(conduit);
	}
	
	protected NodeType(Predicate<Conduit> conduitPredicate, Vec4f color) {
		this.conduitPredicate = conduitPredicate;
		this.color = color;
	}
	
	public static NodeType fromList(List<Conduit> validConduits, Vec4f color) {
		return new NodeType(validConduits::contains, color);
	}
	
	public static NodeType fromPredicate(Predicate<Conduit> predicate, Vec4f color) {
		return new NodeType(predicate, color);
	}

	public Vec4f getColor() {
		return color;
	}
	
}
