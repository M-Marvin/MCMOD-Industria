package de.m_marvin.industria.conduits;

import de.m_marvin.industria.util.conduit.IElectricConduit;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;

public class ElectricConduit extends Conduit implements IElectricConduit {
	
	public ElectricConduit(ConduitType type, Item item, ResourceLocation texture, SoundType sound) {
		super(type, item, texture, sound);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public float getParalelResistance(PlacedConduit instance, ConnectionPoint n) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getSerialResistance(PlacedConduit instance, ConnectionPoint n1, ConnectionPoint n2) {
		return 1; // TODO Leitungswiederstand
	}

	@Override
	public float getGeneratedVoltage(PlacedConduit instance, ConnectionPoint n) {
		return 0;
	}
	
}
