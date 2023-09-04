package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.List;
import java.util.function.Consumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.util.Formater;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public abstract class ElectricConduit extends Conduit implements IElectricConduit {
	
	public final int wireCount;
	public final double resistance;
	
	public ElectricConduit(ConduitType type, Item item, ResourceLocation texture, SoundType sound, int wireCount, double resistance) {
		super(type, item, texture, sound);
		this.wireCount = wireCount;
		this.resistance = resistance;
	}
	
	@Override
	public void appendHoverText(Level level, List<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(level, tooltip, flags);
		tooltip.add(Formater.build().appand(Component.translatable("industriacore.tooltip.electricconduit.resistance", this.getResistancePerBlock())).withStyle(ChatFormatting.GRAY).component());
	}
	
	public double getResistancePerBlock() {
		return resistance;
	}
	
	@Override
	public ConduitEntity newConduitEntity(ConduitPos position, Conduit conduit, double length) {
		return new ElectricConduitEntity(position, conduit, length, this.wireCount);
	}
	
	protected int searchForLabel(String[] lables, String label) {
		int freeSpot = -(lables.length + 1);
		for (int i = 0; i < lables.length; i++) {
			if (lables[i].equals(label)) return i;
			if (freeSpot < 0 && lables[i].isEmpty()) freeSpot = i;
		}
		return -(freeSpot + 1);
	}
	
	@Override
	public int getWireCount() {
		return this.wireCount;
	}
	
	@Override
	public String[] getWireLanes(Level level, ConduitPos pos, ConduitEntity instance, NodePos node) {
		if (instance instanceof ElectricConduitEntity entity) {
			return entity.getWireLanes();
		}
		return new String[] {};
	}
	
	@Override
	public void setWireLanes(Level level, ConduitPos pos, ConduitEntity instance, NodePos node, String[] laneLabels) {
		if (instance instanceof ElectricConduitEntity entity) {
			entity.setWireLanes(laneLabels);
		}
	}
	
	@Override
	public boolean isWire() {
		return true;
	}
	
	@Override
	public void plotCircuit(Level level, ConduitEntity instance, ConduitPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "resistor"));
		template.setProperty("resistance", this.resistance * instance.length);
		
		NodePos[] connections = getConnections(level, position, instance);
		String[] wireLabels = this.getWireLanes(level, position, instance, null);
		for (int i = 0; i < wireLabels.length; i++) {
			template.setNetworkNode("NET1", connections[0], i, wireLabels[i]);
			template.setNetworkNode("NET2", connections[1], i, wireLabels[i]);
			plotter.accept(template);
		}
	}
	
}
