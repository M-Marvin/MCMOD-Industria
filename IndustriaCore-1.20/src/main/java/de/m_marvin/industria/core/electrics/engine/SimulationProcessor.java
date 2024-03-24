package de.m_marvin.industria.core.electrics.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Queues;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.INGCallback;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;

public class SimulationProcessor {
	
	private static final String SPICE_INIT_LOCATION = "../share/ngspice/scripts/spinit";
	private static final String SPICE_CODEMODEL_LOC = "../share/spice/";
	
	private boolean shouldShutdown = true;
	private Queue<ElectricNetwork> tasks = Queues.newArrayDeque();
	private Processor[] processors;
	
	private class Processor extends Thread implements INGCallback {
		
		public Processor(int id) {
			this.id = id;
			this.setName("ElectricProcessor-" + id);
		}
		
		private final int id;
		private boolean errorFlag = false;
		private NativeNGLink nglink;
		private ElectricNetwork currentTask = null;
		
		private boolean init() {
			this.nglink = new NativeNGLink();
			if (!this.nglink.initNGLink(this)) {
				if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.log(Level.DEBUG, "Failed to init nglink native!");
				return false;
			}
			if (!this.nglink.initNGSpice()) {
				if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.log(Level.DEBUG, "Failed to init ngspice native!");
				return false;
			}
			return true;
		}
		
		private boolean cleanup() {
			boolean flag = true;
			if (this.nglink.isNGSpiceAttached()) {
				this.nglink.execCommand("quit"); // quit command never return "ok" for some reason, therefore unable to detect if executed successful
				if (!this.nglink.detachNGLink()) {
					if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.log(Level.DEBUG, "Failed to detache nglink!");
					flag = false;
				}
				this.nglink = null;
			}
			return flag;
		}
		
		@Override
		public void run() {
			try {
				if (!init()) {
					IndustriaCore.LOGGER.log(Level.ERROR, "EPT-" + id + ": Failed to initialize electric network processor!");
				} else {
					IndustriaCore.LOGGER.log(Level.INFO, "EPT-" + id + ": Electric netowk processor started");
					process();
				}
			} catch (Throwable e) {
				IndustriaCore.LOGGER.log(Level.ERROR, "EPT-" + id + ": Critical error on electric simulation thread!");
				this.errorFlag = true;
				e.printStackTrace();
			} finally {
				try {
					if (!cleanup()) {
						IndustriaCore.LOGGER.log(Level.WARN, "EPT-" + id + ": Clean exit on electric network processor failed!");
					}
				} catch (Throwable e) {
					IndustriaCore.LOGGER.log(Level.ERROR, "EPT-" + id + ": Critical error on electric simulation thread!");
					this.errorFlag = true;
					e.printStackTrace();
				}
			}
			IndustriaCore.LOGGER.log(Level.INFO, "EPT-" + id + ": Terminated");
		}
		
		private void process() {
			try {
				while (this.nglink.isNGSpiceAttached() && !shouldShutdown && !errorFlag) {
					synchronized (tasks) {
						if (tasks.isEmpty()) {
							tasks.wait();
						}
						this.currentTask = tasks.poll();
					}
					if (this.currentTask == null) continue;
					String netList = this.currentTask.getNetList();
					if (isNetListValid(netList)) {
						processNetList(netList);
					} else {
						this.currentTask.getNodeVoltages().clear();
					}
					this.currentTask.getComponents().forEach(c -> c.onNetworkChange(this.currentTask.getLevel()));
				}
			} catch (InterruptedException e) {}
		}
		
		@Override
		public void log(String s) {
			if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, s);
		}

		@Override
		public void detacheNGSpice() {
			IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.WARN, "SPICE-Engine detached!");
			this.errorFlag = true;
		}
		
		@Override
		public void reciveInitData(PlotDescription plotInfo) {}
		
		@Override
		public void reciveVecData(VectorValuesAll vecData, int vectorCount) {
			this.currentTask.getNodeVoltages().clear();
			for (int i = 0; i < vectorCount; i++) {
				VectorValue value = vecData.values()[i];
				this.currentTask.getNodeVoltages().put(value.name(), value.realdata());
			}
		}
		
		private boolean isNetListValid(String netList) {
			// Quick check if null or under limit 10 (net lists as short as 10 can't be valid)
			if (netList == null || netList.length() < 10) return false;
			// Filter out any comments and empty lines, check if at least two components are defined (title + component 1 + component 2 + end line)
			return netList.lines().filter(l -> !l.startsWith("*") && !l.isBlank()).toList().size() > 3;
		}
		
		private void processNetList(String netList) {
			netList = filterSingularMatrixNodes(netList);
			if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.debug("Load spice circuit:\n" + netList);
			if (!this.nglink.loadCircuit(netList)) {
				IndustriaCore.LOGGER.warn("Failed to start electric simulation! Failed to load circuit!");
				return;
			}
			if (!this.nglink.execCommand("op")) {
				IndustriaCore.LOGGER.warn("Failed to start electric simulation! Failed run op command!");
				return;
			}
		}

		private static final Pattern FILTER_NODE_PATTERN = Pattern.compile("(?:N[0-9_]{5,})|(?:node\\|[A-Za-z0-9_\\-]{1,}\\|)");
		private static final Pattern FILTER_GROUND_PATTERN = Pattern.compile("R0GND (node\\|[A-Za-z0-9_\\-]{1,}\\|) 0 1");
		
		private String filterSingularMatrixNodes(String netlist) {
			
			Optional<String> groundNode = netlist.lines().map(line -> {
				Matcher nodeMatcher = FILTER_GROUND_PATTERN.matcher(line);
				return nodeMatcher.find() ? nodeMatcher.group(1) : null;
			}).filter(s -> s != null).findAny();
			
			if (groundNode.isEmpty()) return null;
			
			List<List<String>> lineNodes = netlist.lines().map(line -> {
				Matcher nodeMatcher = FILTER_NODE_PATTERN.matcher(line);
				return nodeMatcher.results().map(MatchResult::group).toList();
			}).toList();
			
			List<String> connectedNodes = new ArrayList<>();
			findConnected(connectedNodes, groundNode.get(), lineNodes);

			StringBuilder filterList = new StringBuilder();
			List<String> lines = netlist.lines().toList();
			for (int i = 0; i < lineNodes.size(); i++) {
				boolean isSingular = lineNodes.get(i).size() > 0 && lineNodes.get(i).stream().filter(node -> connectedNodes.contains(node)).count() == 0;
				if (isSingular) continue;
				filterList.append(lines.get(i) + "\n");
			}
			
			return filterList.toString();
			
		}
		
		private void findConnected(List<String> connectedList, String current, List<List<String>> nodeGroups) {
			Set<String> foundNodes = new HashSet<>();
			nodeGroups.stream().filter(group -> group.contains(current)).forEach(group -> group.stream().filter(node -> !connectedList.contains(node)).forEach(foundNodes::add));
			if (!foundNodes.isEmpty()) {
				connectedList.addAll(foundNodes);
				for (String node : foundNodes) {
					findConnected(connectedList, node, nodeGroups);
				}
			}
		}
		
	}
	
	public SimulationProcessor(int numProcessors) {
		this.processors = new Processor[numProcessors];
	}
	
	public boolean isRunning() {
		if (this.shouldShutdown) return false;
		for (Processor proc : this.processors) {
			if (proc != null && !proc.errorFlag && proc.isAlive()) return true;
		}
		return false;
	}
	
	private void writeInitFile() {
		File workingDir = new File("").getAbsoluteFile();
		File initFile = new File(workingDir, SPICE_INIT_LOCATION).getAbsoluteFile();
		
		IndustriaCore.LOGGER.log(Level.INFO, "write SPICE init file at " + initFile.getPath());
		
		if (!initFile.getParentFile().isDirectory() && !initFile.getParentFile().mkdirs()) {
			IndustriaCore.LOGGER.log(Level.WARN, "Failed to create init file folder!");
			IndustriaCore.LOGGER.log(Level.WARN, "WARNING: This will possibly break a lot of stuff!");
			return;
		}
		
		try {


			InputStream spinitFileIn = IndustriaCore.ARCHIVE_ACCESS.openFile("spiceinit/spinit");
			OutputStream spinitFileOut = new FileOutputStream(initFile);
			spinitFileOut.write(spinitFileIn.readAllBytes());
			spinitFileIn.close();
			spinitFileOut.close();
			
		} catch (Exception e) {
			if (e instanceof FileNotFoundException && initFile.isFile()) {
				IndustriaCore.LOGGER.log(Level.INFO, "Access to init file denied, already used by another process!");
			} else {
				IndustriaCore.LOGGER.log(Level.WARN, "Failed to create init file!");
				IndustriaCore.LOGGER.log(Level.WARN, "WARNING: This will possibly break a lot of stuff!");
				e.printStackTrace();
			}
		}
		
		File modelFolder = new File(workingDir, SPICE_CODEMODEL_LOC).getAbsoluteFile();

		IndustriaCore.LOGGER.log(Level.INFO, "extract SPICE modules at " + modelFolder.getPath());
		
		if (!modelFolder.isDirectory() && !modelFolder.mkdirs()) {
			IndustriaCore.LOGGER.log(Level.WARN, "Failed to create code module folder!");
			IndustriaCore.LOGGER.log(Level.WARN, "WARNING: This will possibly break a lot of stuff!");
			return;
		}

		String[] spiceModules = IndustriaCore.ARCHIVE_ACCESS.listFiles("spiceinit/codemodels");
		
		for (String module : spiceModules) {
			
			IndustriaCore.LOGGER.log(Level.DEBUG, "-> extract module '" + module + "' ...");
			
			File modelOutFile = new File(modelFolder, module);
			
			try {

				InputStream moduleIn = IndustriaCore.ARCHIVE_ACCESS.openFile("spiceinit/codemodels/" + module);
				FileOutputStream moduleOut = new FileOutputStream(modelOutFile);
				moduleOut.write(moduleIn.readAllBytes());
				moduleIn.close();
				moduleOut.close();
				
			} catch (Exception e) {
				if (e instanceof FileNotFoundException && modelOutFile.isFile()) {
					IndustriaCore.LOGGER.log(Level.INFO, "Access to model file denied, already used by another process!");
				} else {
					IndustriaCore.LOGGER.log(Level.WARN, "Failed to extract module file " + module + "!");
					IndustriaCore.LOGGER.log(Level.WARN, "WARNING: This will possibly break a lot of stuff!");
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	public void start() {
		writeInitFile();
		this.shouldShutdown = false;
		IndustriaCore.LOGGER.log(Level.INFO, "Electric network procsssor startup");
		for (int i = 0; i < this.processors.length; i++) {
			this.processors[i] = new Processor(i);
			this.processors[i].start();
		}
	}
	
	public void shutdown() {
		IndustriaCore.LOGGER.log(Level.INFO, "Electric network procsssor shutdown triggered");
		this.shouldShutdown = true;
		this.tasks.clear();
		synchronized (this.tasks) {
			this.tasks.notifyAll();
		}
	}
	
	public void processNetwork(ElectricNetwork network) {
		if (this.shouldShutdown) return;
		synchronized (tasks) {
			this.tasks.add(network);	
			this.tasks.notify();
		}
	}
	
}
