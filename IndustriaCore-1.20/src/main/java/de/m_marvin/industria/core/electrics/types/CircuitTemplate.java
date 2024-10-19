package de.m_marvin.industria.core.electrics.types;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import net.minecraft.core.BlockPos;

public class CircuitTemplate {

	public static class Plotter implements ICircuitPlot {

		private static final Pattern PROPERTY_PATTERN = Pattern.compile("P\\{([\\w]+)\\}");
		private static final Pattern NETWORK_PATTERN = Pattern.compile("N\\{([\\w]+)\\}");
		
		private final Map<String, String> networks = new HashMap<>();
		private final Map<String, String> properties = new HashMap<>();
		private final CircuitTemplate template;
		
		public Plotter(CircuitTemplate template) {
			this.template = template;
			for (String s : template.networks) this.networks.put(s, "NONE");
			for (String s : template.properties) this.properties.put(s, "0");
		}
		
		public void setProperty(String name, String value) {
			if (this.properties.containsKey(name)) this.properties.put(name, value);
		}

		public void setProperty(String name, double value) {
			setProperty(name, Double.toString(value));
		}

		public void setProperty(String name, long value) {
			setProperty(name, Long.toString(value));
		}

		public void setProperty(String name, int value) {
			setProperty(name, Integer.toString(value));
		}
		
		public void setNetworkNode(String name, NodePos node, int laneId, String lane) {
			setNetwork(name, ElectricNetwork.getNodeKeyString(node, laneId, lane));
		}

		public void setNetworkLocalNode(String name, BlockPos position, String lane, boolean prot) {
			setNetwork(name, ElectricNetwork.getLocalNodeKeyString(position, lane, prot));
		}

		public void setNetwork(String name, String net) {
			if (this.networks.containsKey(name)) this.networks.put(name, net);
		}

		@Override
		public void prepare(long templateId) {
			setProperty(this.template.idProperty, templateId);
		}
		
		@Override
		public String getAnyNode() {
			return this.networks.values().stream().findAny().orElseGet(() -> null);
		}

		@Override
		public String plot() {
			try {
				Matcher matcher1 = PROPERTY_PATTERN.matcher(this.template.template);
				String plot = matcher1.replaceAll(match -> this.properties.getOrDefault(match.group(1), "N/A"));
				Matcher matcher2 = NETWORK_PATTERN.matcher(plot);
				return matcher2.replaceAll(match -> this.networks.getOrDefault(match.group(1), "NA"));
			} catch (Exception e) {
				IndustriaCore.LOGGER.error("Could't plot circuit template!");
				e.printStackTrace();
			}
			return "";
		}
		
		@Override
		public String toString() {
			return "PLOTTER:\n" + this.template.toString() + "\nEND PLOTTER";
		}
		
	}
	
	private final String[] networks;
	private final String[] properties;
	private final String idProperty;
	private final String template;
		
	public CircuitTemplate(String[] networks, String[] properties, String template, String idProperty) {
		this.template = template;
		this.networks = networks;
		this.properties = properties;
		this.idProperty = idProperty;
	}
	
	public Plotter plotter() {
		return new Plotter(this);
	}
	
	public String[] getNetworks() {
		return networks;
	}
	
	public String[] getProperties() {
		return properties;
	}
	
	public String getIdProperty() {
		return idProperty;
	}
	
	public String getTemplate() {
		return template;
	}
	
	@Override
	public String toString() {
		return this.template;
	}
	
}
