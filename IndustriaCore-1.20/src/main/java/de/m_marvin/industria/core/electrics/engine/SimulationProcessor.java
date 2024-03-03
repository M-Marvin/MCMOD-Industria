package de.m_marvin.industria.core.electrics.engine;

import java.util.Queue;

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
					if (this.currentTask != null && isNetListValid(this.currentTask.getNetList())) {
						processNetList(this.currentTask.getNetList());
						this.currentTask.getComponents().forEach(c -> c.onNetworkChange(this.currentTask.getLevel()));
					}
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
		
		protected boolean isNetListValid(String netList) {
			return netList != null && netList.length() > 10;
		}
		
		protected void processNetList(String netList) {
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
		
	}
	
	public SimulationProcessor(int numProcessors) {
		this.processors = new Processor[numProcessors];
		for (int i = 0; i < numProcessors; i++) {
			this.processors[i] = new Processor(i);
		}
	}
	
	public boolean isRunning() {
		if (this.shouldShutdown) return false;
		for (Processor proc : this.processors) {
			if (!proc.errorFlag && proc.isAlive()) return true;
		}
		return false;
	}
	
	public void start() {
		this.shouldShutdown = false;
		IndustriaCore.LOGGER.log(Level.INFO, "Electric network procsssor startup");
		for (int i = 0; i < this.processors.length; i++) {
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
