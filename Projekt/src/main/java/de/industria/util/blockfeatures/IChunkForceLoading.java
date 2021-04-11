package de.industria.util.blockfeatures;

import java.util.List;

import net.minecraft.util.math.ChunkPos;

public interface IChunkForceLoading {
	
	public List<ChunkPos> getLoadHoldChunks();
	
}
