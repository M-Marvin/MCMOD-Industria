package de.m_marvin.industria.core.scrollinput.type.items;

import de.m_marvin.industria.core.scrollinput.engine.ScrollInputListener.ScrollContext;

public interface IScrollOverride {
	
	public boolean overridesScroll(ScrollContext context);
	public void onScroll(ScrollContext context);
	
}
