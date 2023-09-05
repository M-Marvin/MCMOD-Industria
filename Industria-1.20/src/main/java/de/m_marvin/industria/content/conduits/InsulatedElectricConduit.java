package de.m_marvin.industria.content.conduits;

import de.m_marvin.industria.core.electrics.types.conduits.ElectricConduit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;

public class InsulatedElectricConduit extends ElectricConduit {

	public InsulatedElectricConduit(ConduitType type, double resistance, Item item, ResourceLocation texture, SoundType sound) {
		super(type, item, texture, sound, 6, resistance);
		// TODO Auto-generated constructor stub
	}
	
}
