package de.m_marvin.industria.core.electrics.engine;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Queues;

import de.m_marvin.electronflow.Solver;
import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;

public class SimulationProcessor {
	
	private static record SimTask(CompletableFuture<Boolean> completable, ElectricNetwork network) {}
	
	private boolean shouldShutdown = true;
	private Queue<SimTask> tasks = Queues.newArrayDeque();
	private Processor[] processors;
	
	private class Processor extends Thread {
		
		public Processor(int id) {
			this.id = id;
			this.setName("ElectricProcessor-" + id);
		}
		
		private final int id;
		private boolean errorFlag = false;
		private Solver solver;
		private SimTask currentTask = null;
		
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
					String netList = this.currentTask.network.getNetList();
					if (!processNetList(netList)) {
						// TODO make fuse blow/ingame message
					}
					this.currentTask.network.getComponents().forEach(c -> c.onNetworkChange(this.currentTask.network.getLevel()));
					this.currentTask.completable.complete(true);
				}
			} catch (InterruptedException e) {}
		}
		
		private boolean processNetList(String netList) {
			if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.debug("Load spice circuit:\n" + netList);
			
			if (!this.solver.upload(netList)) {
				IndustriaCore.LOGGER.warn("Failed to upload network to solver!");
				return false;
			}
			if (!this.solver.execute(Config.ELECTRIC_SIMULATION_COMMANDS.get())) {
				IndustriaCore.LOGGER.warn("Failed to start electric simulation!");
				return false;
			}
			if (!this.currentTask.network.parseDataList(this.solver.printData())) {
				IndustriaCore.LOGGER.warn("Failed to get simulation data, simulation probably failed!");
				return false;
			}
			return true;
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
	
	public CompletableFuture<Boolean> processNetwork(ElectricNetwork network) {
		if (this.shouldShutdown) return CompletableFuture.completedFuture(false);
		synchronized (tasks) {
			for (SimTask task : this.tasks) {
				if (task.network == network) {
					return task.completable;
				}
			}
			CompletableFuture<Boolean> completable = new CompletableFuture<>();
			this.tasks.add(new SimTask(completable, network));
			this.tasks.notify();	
			return completable;
		}
	}
	
}
