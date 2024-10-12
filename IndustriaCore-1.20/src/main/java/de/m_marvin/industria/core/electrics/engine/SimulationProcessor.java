package de.m_marvin.industria.core.electrics.engine;

import java.util.Queue;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Queues;

import de.m_marvin.electronflow.Solver;
import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;

// TODO optimize for new engine
public class SimulationProcessor {
	
	private boolean shouldShutdown = true;
	private Queue<ElectricNetwork> tasks = Queues.newArrayDeque();
	private Processor[] processors;
	
	private class Processor extends Thread {
		
		public Processor(int id) {
			this.id = id;
			this.setName("ElectricProcessor-" + id);
		}
		
		private final int id;
		private boolean errorFlag = false;
		private Solver solver;
		private ElectricNetwork currentTask = null;
		
		private boolean init() {
			try {
				solver = new Solver();
				solver.attachElectronFlow(Config.SPICE_DEBUG_LOGGING.get() ? (s) -> { IndustriaCore.LOGGER.debug(s); } : (s) -> {});
				return true;
			} catch (Error e) {
				solver = null;
				IndustriaCore.LOGGER.error("Error while initializing electron flow!", e);
				return false;
			}
		}
		
		private boolean cleanup() {
			try {
				if (this.solver != null) {
					this.solver.detachElectronFlow();
					this.solver = null;
				}
				return true;
			} catch (Exception e) {
				IndustriaCore.LOGGER.error(e.getMessage());
				return false;
			}
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
				IndustriaCore.LOGGER.log(Level.ERROR, "EPT-" + id + ": Critical error on electric simulation thread!", e);
				this.errorFlag = true;
			} finally {
				try {
					if (!cleanup()) {
						IndustriaCore.LOGGER.log(Level.WARN, "EPT-" + id + ": Clean exit on electric network processor failed!");
					}
				} catch (Throwable e) {
					IndustriaCore.LOGGER.log(Level.ERROR, "EPT-" + id + ": Critical error on electric simulation thread!", e);
					this.errorFlag = true;
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
//					if (isNetListValid(netList)) {
//						processNetList(netList);
//					} else {
//						this.currentTask.getNodeVoltages().clear();
//						this.currentTask.getElementCurrents().clear();
//					}
					processNetList(netList);
					this.currentTask.getComponents().forEach(c -> c.onNetworkChange(this.currentTask.getLevel()));
				}
			} catch (InterruptedException e) {}
		}
////		
////		@Override
////		public void finalData(Node[] nodes, Element[] elements, double nodecharge, double timestep) {
////			this.currentTask.getNodeVoltages().clear();
////			this.currentTask.getElementCurrents().clear();
////			for (Node node : nodes) {
////				this.currentTask.getNodeVoltages().put(node.name, node.charge / nodecharge);
////			}
////			for (Element element : elements) {
////				this.currentTask.getElementCurrents().put(element.name, element.transferCharge / timestep);
////			}
////		}
////		
//		private boolean isNetListValid(String netList) {
//			// Quick check if null or under limit 10 (net lists as short as 10 can't be valid) TODO remove outdated validation checks
//			if (netList == null || netList.length() < 10) return false;
//			// Filter out any comments and empty lines, check if at least two components are defined (title + component 1 + component 2 + end line)
//			return netList.lines().filter(l -> !l.startsWith("*") && !l.isBlank()).toList().size() > 3;
//		}
		
		private void processNetList(String netList) {
			if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.debug("Load spice circuit:\n" + netList);
			
			if (!this.solver.upload(netList)) {
				IndustriaCore.LOGGER.warn("Failed to upload network to solver!");
				return;
			}
			if (!this.solver.execute(Config.ELECTRIC_SIMULATION_COMMANDS.get())) {
				IndustriaCore.LOGGER.warn("Failed to start electric simulation!");
				return;
			}
			this.currentTask.parseDataList(this.solver.printData());
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
			if (this.tasks.contains(network)) return;
			this.tasks.add(network);	
			this.tasks.notify();
		}
	}
	
}
