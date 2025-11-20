package de.m_marvin.industria.content.conduits;

import de.m_marvin.industria.core.electrics.types.conduits.ElectricConduit;

public class UninsulatedElectricConduit extends ElectricConduit {

	public UninsulatedElectricConduit(Properties properties, double resistance) {
		super(properties, 1, resistance);
	}
	
}
