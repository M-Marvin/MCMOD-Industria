package de.m_marvin.industria.core.electrics.circuits;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;

public class CircuitTemplate {

	private static final Pattern PROPERTY_PATTERN = Pattern.compile("P\\{([\\w]+)\\}");
	private static final Pattern NETWORK_PATTERN = Pattern.compile("N\\{([\\w]+)\\}");
	
	private Map<String, String> networks = new HashMap<>();
	private Map<String, String> properties = new HashMap<>();
	private String idProperty;
	private String template;
		
	public CircuitTemplate(String[] networks, String[] properties, String template, String idProperty) {
		this.template = template;
		for (String network : networks) this.networks.put(network, "NONE");
		for (String property : properties) this.properties.put(property, "NONE");
		this.idProperty = idProperty;
	}
	
	public void setProperty(String name, double value) {
		if (this.properties.containsKey(name)) this.properties.put(name, Double.toString(value));
	}
	
	public void setNetworkNode(String name, NodePos node, String lane) {
		setNetwork(name, node.getKeyString(lane));
	}
	
	public void setNetwork(String name, String net) {
		if (this.networks.containsKey(name)) this.networks.put(name, net);
	}

	public void prepare(int templateId) {
		setProperty(idProperty, templateId);
	}
	
	public void setDefault() {
		this.networks.keySet().forEach(key -> this.networks.put(key, "NONE"));
		this.properties.keySet().forEach(key -> this.properties.put(key, "0"));
	}
	
	public String plot() {
		Matcher matcher1 = PROPERTY_PATTERN.matcher(template);
		String plot = matcher1.replaceAll(match -> this.properties.get(match.group(1)));
		Matcher matcher2 = NETWORK_PATTERN.matcher(plot);
		return matcher2.replaceAll(match -> this.networks.get(match.group(1)));
	}
	
}