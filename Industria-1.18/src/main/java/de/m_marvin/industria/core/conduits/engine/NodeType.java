package de.m_marvin.industria.core.conduits.engine;

import java.util.List;
import java.util.function.Predicate;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;

public class NodeType {
	
	private final Predicate<Conduit> conduitPredicate;
	
	public boolean isValid(Conduit conduit) {
		return this.conduitPredicate.test(conduit);
	}
	
	protected NodeType(Predicate<Conduit> conduitPredicate) {
		this.conduitPredicate = conduitPredicate;
	}
	
	public static NodeType fromList(List<Conduit> validConduits) {
		return new NodeType(validConduits::contains);
	}
	
	public static NodeType fromPredicate(Predicate<Conduit> predicate) {
		return new NodeType(predicate);
	}
	
}
