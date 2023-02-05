package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.core.conduits.engine.NodeType;
import de.m_marvin.univec.impl.Vec4f;

public class NodeTypes {
	
	public static final NodeType ALL = NodeType.fromPredicate((conduit) -> true, new Vec4f(1, 0, 1, 1));
	
}
