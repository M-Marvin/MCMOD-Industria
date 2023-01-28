package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.world.phys.BlockHitResult;

public class ConduitHitResult {
	
	protected BlockHitResult blockResult;
	protected PlacedConduit conduitState;
	protected Vec3f hitPos;
	protected int node1, node2;
	
	public ConduitHitResult(BlockHitResult result) {
		this.blockResult = result;
	}

	protected ConduitHitResult(PlacedConduit conduitState, Vec3f hitPos, int node1, int node2) {
		super();
		this.conduitState = conduitState;
		this.hitPos = hitPos;
		this.node1 = node1;
		this.node2 = node2;
	}

	public static ConduitHitResult hit(PlacedConduit conduitState, Vec3f hitPos, int node1, int node2) {
		return new ConduitHitResult(conduitState, hitPos, node1, node2);
	}
	
	public static ConduitHitResult miss() {
		return new ConduitHitResult(null, null, -1, -1);
	}
	
	public static ConduitHitResult block(BlockHitResult result) {
		return new ConduitHitResult(result);
	}
	
	public BlockHitResult getBlockResult() {
		return blockResult;
	}
	
	public boolean isBlocked() {
		return this.blockResult != null;
	}
	
	public boolean isHit() {
		return this.conduitState != null && this.hitPos != null && node1 >= 0 && node2 >= 0;
	}
	
	public PlacedConduit getConduitState() {
		return conduitState;
	}

	public Vec3f getHitPos() {
		return hitPos;
	}

	public int getNode1() {
		return node1;
	}

	public int getNode2() {
		return node2;
	}
	
}
