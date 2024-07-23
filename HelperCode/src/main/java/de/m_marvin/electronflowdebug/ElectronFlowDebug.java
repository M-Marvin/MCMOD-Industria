package de.m_marvin.electronflowdebug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.m_marvin.electronflow.ElectronFlow;
import de.m_marvin.electronflow.NativeElectronFlow.Element;
import de.m_marvin.electronflow.NativeElectronFlow.Node;

public class ElectronFlowDebug {
	
	public static void main(String... args) {
		
		File f = new File(args[0]);
		
		loadNetlist(f);
		parseForLoads();
		simulateNetwork();
		printResults();
		
	}
	
	public record Component(String name, String pos, String element, String n1, String n2) {};
	
	public static String netlist;
	public static List<Component> sources = new ArrayList<>();
	public static List<Component> loads = new ArrayList<>();
	
	public static void loadNetlist(File file) {
		try {
			FileInputStream fi = new FileInputStream(file);
			netlist = new String(fi.readAllBytes());
			fi.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final Pattern COMPONENT_PATTERN = Pattern.compile("\\* Component (\\w+)\\{([\\w:]+)\\} ([\\w\\{\\}=, -\\[\\]]+)");
	public static final Pattern SOURCE_PATTERN = Pattern.compile("\\* Voltage source \\[ v_nominal=([\\w.]+), i_max=([\\w.]+) \\]");
	public static final Pattern SOURCE_NODES = Pattern.compile("(V1_\\d+_[\\w|~]+) ([\\w|~]+) ([\\w|~]+)");
	public static final Pattern LOAD_PATTERN = Pattern.compile("\\* Load-Resistor with fixed power consumption \\[ power_nominal=([\\w.]+), initial_impedance=([\\w.]+) \\]");
	public static final Pattern LOAD_NODES = Pattern.compile("(R1_\\d+) ([\\w|~]+) ([\\w|~]+)");
	
	public static void parseForLoads() {
		
		try {
			BufferedReader reader = new BufferedReader(new StringReader(netlist));
			
			String line;
			String componentType = null;
			String componentPos = null;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				
				if (SOURCE_PATTERN.matcher(line).find()) {
					String Vsource = reader.readLine();
					
					Matcher m = SOURCE_NODES.matcher(Vsource);
					if (m.find()) {
						String element = m.group(1);
						String n1 = m.group(2);
						String n2 = m.group(3);
						
						System.out.println("is voltage source: " + element + " " + n1 + " " + n2);
						sources.add(new Component(componentType, componentPos, element, n1, n2));
					}
				}
				
				if (LOAD_PATTERN.matcher(line).find()) {
					String Vload = reader.readLine();

					Matcher m = LOAD_NODES.matcher(Vload);
					if (m.find()) {
						String element = m.group(1);
						String n1 = m.group(2);
						String n2 = m.group(3);
						
						System.out.println("is load: " + element + " " + n1 + " " + n2);
						loads.add(new Component(componentType, componentPos, element, n1, n2));
					}
				}
				
				Matcher m = COMPONENT_PATTERN.matcher(line);
				if (m.find()) {
					
					String clazz = m.group(1);
					String type = m.group(2);
					String pos = m.group(3);
					
					if (clazz.equals("Conduit")) continue;
					
					System.out.println("found " + type + " at " + pos);
					componentType = type;
					componentPos = pos;
					continue;
					
				}
			}
		} catch (IOException e) {}
		
	}
	
	public static Map<String, Double> currents = new HashMap<>();
	public static Map<String, Double> voltages = new HashMap<>();
	
	public static void finalCallback(Node[] nodes, Element[] elements, double nodecharge, double timestep) {
		for (Node node : nodes) voltages.put(node.name, node.charge / nodecharge);
		for (Element element : elements) currents.put(element.name, element.transferCharge / timestep);
	}
	
	public static void simulateNetwork() {
		
		ElectronFlow ef = new ElectronFlow();
		
		ef.setCallbacks(null, ElectronFlowDebug::finalCallback);
		ef.loadAndRunNetList(netlist);
		
	}
	
	public static void printResults() {
		
		System.out.println("\n\nSources:");
		
		double pts = 0;
		for (Component source : sources) {
			
			double v1 = voltages.getOrDefault(source.n1, 0.0);
			double v2 = voltages.getOrDefault(source.n2, 0.0);
			double v = v1 - v2;
			double i = currents.getOrDefault(source.element, 0.0);
			double p = v * i;
			
			System.out.println(source.name + " at " + source.pos);
			System.out.println("V " + v);
			System.out.println("I " + i);
			System.out.println("P " + p);
			
			pts += p;
			
		}
		
		System.out.println("== total ==");
		System.out.println("Ptotal = " + pts);
		
		System.out.println("\n\nLoads:");

		double ptl = 0;
		for (Component load : loads) {
			
			double v1 = voltages.getOrDefault(load.n1, 0.0);
			double v2 = voltages.getOrDefault(load.n2, 0.0);
			double v = v1 - v2;
			double i = currents.getOrDefault(load.element, 0.0);
			double p = v * i;
			
			System.out.println(load.name + " at " + load.pos);
			System.out.println("V " + v);
			System.out.println("I " + i);
			System.out.println("P " + p);
			
			ptl += p;
		}

		System.out.println("== total ==");
		System.out.println("Ptotal = " + ptl);
		
	}
	
}
