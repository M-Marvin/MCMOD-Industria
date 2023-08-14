package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import de.m_marvin.univec.impl.Vec4f;

public class NodeTypes {
	
	public static final NodeType ALL = NodeType.fromPredicate((conduit) -> true, new Vec4f(1, 0, 1, 1));
	
}
