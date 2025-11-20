package de.m_marvin.industria.content.conduits;

import de.m_marvin.industria.core.electrics.types.conduits.ElectricConduit;

public class InsulatedElectricConduit extends ElectricConduit {

	public InsulatedElectricConduit(Properties properties, double resistance) {
		super(properties, 6, resistance);
	}
	
}
