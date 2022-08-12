package de.m_marvin.industria.conduits;

import de.m_marvin.industria.util.conduit.ConduitPos;
import de.m_marvin.industria.util.conduit.IElectricConduit;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.electricity.ElectricNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class ElectricConduit extends Conduit implements IElectricConduit {
	
	public ElectricConduit(ConduitType type, Item item, ResourceLocation texture, SoundType sound) {
		super(type, item, texture, sound);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void plotCircuit(Level level, PlacedConduit instance, ConduitPos position, ElectricNetwork circuit) {
		
		ConnectionPoint[] nodes = getConnections(level, position, instance);
		
		circuit.addSerialResistance(nodes[0], nodes[1], 0);
		
	}
	
}
