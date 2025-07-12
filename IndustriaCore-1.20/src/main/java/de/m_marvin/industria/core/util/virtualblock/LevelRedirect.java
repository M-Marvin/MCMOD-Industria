package de.m_marvin.industria.core.util.virtualblock;

import net.minecraft.world.level.Level;

public class LevelRedirect {
	
	private LevelRedirect() {}
	
	public static Level newLevelRedirect(VirtualBlock block, Level level) {
		if (level.isClientSide()) {
			return ClientLevelRedirectBaundary.newLevelRedirect(block, level);
		} else {
			return ServerLevelRedirect.newRedirect(block, level);
		}
	}
	
}
