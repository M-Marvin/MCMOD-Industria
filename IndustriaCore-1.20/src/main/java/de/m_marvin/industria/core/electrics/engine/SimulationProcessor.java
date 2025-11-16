package de.m_marvin.industria.core.electrics.engine;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Queues;

import de.m_marvin.electronflow.Solver;
import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;

public class SimulationProcessor {
	
	private static record SimTask(CompletableFuture<Optional<String>> completable, Supplier<String> netlist) {}
	
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
				solver.attachElectronFlow(Config.EF_DEBUG_LOGGING.get() ? (s) -> { IndustriaCore.LOGGER.info(s); } : (s) -> {});
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
					IndustriaCore.LOGGER.log(Level.INFO, "EPT-" + id + ": Electric network processor started");
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
			while (!shouldShutdown && !errorFlag) {
				synchronized (tasks) {
					if (tasks.isEmpty()) {
						try {
							tasks.wait();
						} catch (InterruptedException e) {}
					}
					this.currentTask = tasks.poll();
				}
				if (this.currentTask == null) continue;
				String netList = this.currentTask.netlist.get();
				if (netList.isEmpty()) {
					this.currentTask.completable.complete(Optional.empty());
				} else {
					this.currentTask.completable.complete(processNetList(netList));
				}
			}
		}
		
		private Optional<String> processNetList(String netList) {
			if (Config.EF_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.info("Load spice circuit:\n" + netList);
			
			if (!this.solver.upload(netList)) {
				IndustriaCore.LOGGER.warn("Failed to upload network to solver!");
				return Optional.empty();
			}
			String[] commands = Config.ELECTRIC_SIMULATION_COMMANDS.get().split("\\|");
			for (String command : commands) {
				if (!this.solver.execute(command)) {
					IndustriaCore.LOGGER.warn("Failed to start electric simulation!");
					return Optional.empty();
				}
			}
//			if (!this.currentTask.network.parseDataList(this.solver.printData())) {
//				IndustriaCore.LOGGER.warn("Failed to get simulation data, simulation probably failed!");
//				return Optional.empty();
//			}
			return Optional.of(this.solver.printData());
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
	
	public CompletableFuture<Optional<String>> processNetwork(Supplier<String> netlist) {
		synchronized (tasks) {
			CompletableFuture<Optional<String>> completable = new CompletableFuture<>();
			this.tasks.add(new SimTask(completable, netlist));
			this.tasks.notify();
			return completable;
		}
	}
	
}
