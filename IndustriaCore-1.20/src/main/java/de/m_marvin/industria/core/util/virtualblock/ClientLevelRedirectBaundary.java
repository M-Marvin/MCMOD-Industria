package de.m_marvin.industria.core.util.virtualblock;

import net.minecraft.world.level.Level;

// This class just acts as class loading boundary on the server
public class ClientLevelRedirectBaundary {

	private ClientLevelRedirectBaundary() {}
	
	public static Level newLevelRedirect(VirtualBlock block, Level level) {
		return ClientLevelRedirect.newRedirect(block, level);
	}
	
}
