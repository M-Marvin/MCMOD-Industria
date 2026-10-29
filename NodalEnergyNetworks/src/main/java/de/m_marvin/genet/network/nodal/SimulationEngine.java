package de.m_marvin.genet.network.nodal;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.tvnlnna.NetworkSolverException;
import de.m_marvin.tvnlnna.nodal.NodalNetwork;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver_LAPACK;
import net.minecraft.server.MinecraftServer;

public class SimulationEngine<N, E> {
	
	private final MinecraftServer gameServer;
	private final Supplier<NodalNetworkSolver<N, E>> backendFactory;
	private final Map<NodalNetwork<N, E>, SimulationTask> activeTasks = new IdentityHashMap<NodalNetwork<N,E>, SimulationEngine<N,E>.SimulationTask>();
	
	public static final double NOMINAL_BASE_TICK_TIME = 0.05;
	public static final double NOMINAL_SIM_TICK_TIME = 0.05;
	
	public SimulationEngine(MinecraftServer server) {
		this.gameServer = server;
		
		this.backendFactory = NodalNetworkSolver_LAPACK::standard; // TODO config
		
		
		
	}
	
	public boolean shutdown() {
//		try {
//			return this.executor.awaitTermination(10, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			NodeNet.LOGGER.warn("Simulation Engine Shutdown: Exception during Executor Shutdown", e);
//			return false;
//		}
	}
	
	public int getTickTime() {
		return this.gameServer.getTickCount();
	}
	
	public double getSimulationTime() {
		return getTickTime() * NOMINAL_BASE_TICK_TIME;
	}
	
	
	
//	private void onThreadError(Thread simulationThread, Throwable error) {
//		NodeNet.LOGGER.error("Exception within Simulation Engine Worker: {}", simulationThread.getName(), error);
//	}
	
	public static class SimulationWorkerThread extends Thread {
		
		private static int simulationWorkerIndex = 0;
		
		public SimulationWorkerThread(ThreadGroup group) {
			super(group, "NodelSimulationWorker-" + simulationWorkerIndex++);
		}
		
		@Override
		public void run() {
			
			
			
		}
		
	}
	
	public class SimulationTask {
		
		private final NodalNetworkSolver<N, E> solver;
		private final NodalNetwork<N, E> network;
		private NetworkSolverException exception = null;
		private volatile long expiration;
		
		public SimulationTask(NodalNetwork<N, E> network, int timeout) {
			this.network = network;
			updateTimeout(timeout);
			
			this.solver = SimulationEngine.this.backendFactory.get();
			this.solver.setNetwork(this.network);
			this.solver.setSimulationTime(SimulationEngine.this.getSimulationTime());
			this.solver.resetAndInitSimulation();
		}
		
		public void updateTimeout(int timeout) {
			this.expiration = getTickTime() + timeout;
		}
		
		public NetworkSolverException getException() {
			return exception;
		}
		
		public boolean runStep() {
			
			if (this.exception == null) {
				try {
					this.solver.step(NOMINAL_BASE_TICK_TIME);
				} catch (NetworkSolverException e) {
					this.exception = e;
					NodeNet.LOGGER.warn("simulation halted, solver exception during simulation of network:\n{}", this.network, e);
				}
			}
			
			return getTickTime() > this.expiration;
			
		}
		
	}
	
	/**
	 * Submits a network to be simulated for a set number of (network simulation) ticks.<br>
	 * If future calls with the same network are made, the span limit is reset again, this ensures that the simulations halt quickly in case the main thread is paused.
	 * @param network The network to run the simulation for
	 * @param timeout The number of simulation ticks to step trough before halting the simulation
	 */
	public void submit(NodalNetwork<N, E> network, int timeout) {
		SimulationTask task = this.activeTasks.get(network);
		if (task == null) {
			task = new SimulationTask(network, timeout);
			this.executor.submit(task);
			this.activeTasks.put(network, task);
		} else {
			task.updateTimeout(timeout);
		}
	}
	
}
