package de.m_marvin.industria.core.electrics.engine;

import java.util.Queue;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Queues;

import de.m_marvin.electronflow.ElectronFlow;
import de.m_marvin.electronflow.NativeElectronFlow;
import de.m_marvin.electronflow.NativeElectronFlow.Node;
import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;

// TODO optimize for new engine
public class SimulationProcessor {
	
	private boolean shouldShutdown = true;
	private Queue<ElectricNetwork> tasks = Queues.newArrayDeque();
	private Processor[] processors;
	
	private class Processor extends Thread implements NativeElectronFlow.FinalCallback {
		
		public Processor(int id) {
			this.id = id;
			this.setName("ElectricProcessor-" + id);
		}
		
		private final int id;
		private boolean errorFlag = false;
		private ElectronFlow engine;
		private ElectricNetwork currentTask = null;
		
		private boolean init() {
			engine = new ElectronFlow();
			engine.printVersionInfo();
			engine.setCallbacks(null, this);
			return true;
		}
		
		private boolean cleanup() {
			if (this.engine != null) {
				this.engine.destroy();
				this.engine = null;
			}
			return true;
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
				while (!shouldShutdown && !errorFlag) {
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
		public void finalData(Node[] nodes, double nodecharge) {
			this.currentTask.getNodeVoltages().clear();
			for (Node node : nodes) {
				this.currentTask.getNodeVoltages().put(node.name, node.charge / nodecharge);
			}
		}
		
		private boolean isNetListValid(String netList) {
			// Quick check if null or under limit 10 (net lists as short as 10 can't be valid)
			if (netList == null || netList.length() < 10) return false;
			// Filter out any comments and empty lines, check if at least two components are defined (title + component 1 + component 2 + end line)
			return netList.lines().filter(l -> !l.startsWith("*") && !l.isBlank()).toList().size() > 3;
		}
		
		private void processNetList(String netList) {
			if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.debug("Load spice circuit:\n" + netList);
			
			if (!this.engine.loadNetList(netList)) {
				IndustriaCore.LOGGER.warn("Failed to start electric simulation! Failed to load circuit!");
				return;
			}
			this.engine.controllCommand("step", "2m", "600", "30"); // TODO electron flow
			this.engine.controllCommand("printv", "0");
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
	
	public void start() {
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
