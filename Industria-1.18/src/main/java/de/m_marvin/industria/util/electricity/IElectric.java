package de.m_marvin.industria.util.electricity;

import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import net.minecraft.world.level.Level;

public interface IElectric<I, P> {
	
	public float getParalelResistance(I instance, ConnectionPoint n);
	public float getSerialResistance(I instance, ConnectionPoint n1, ConnectionPoint n2);
	public float getGeneratedVoltage(I instance, ConnectionPoint n, float networkLoad);
	
	public ConnectionPoint[] getConnections(Level level, P pos, I instance);
	
}
