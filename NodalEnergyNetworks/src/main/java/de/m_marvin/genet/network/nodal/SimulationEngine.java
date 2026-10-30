package de.m_marvin.genet.network.nodal;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.stdextras.periodic.PeriodicExecutor.PeriodicTask;
import de.m_marvin.stdextras.periodic.PeriodicThreadPool;
import de.m_marvin.tvnlnna.NetworkSolverException;
import de.m_marvin.tvnlnna.nodal.NodalNetwork;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver_LAPACK;
import net.minecraft.server.MinecraftServer;

public class SimulationEngine<N, E> {

	public static final double NOMINAL_BASE_TICK_TIME = 0.05;
	
	private final MinecraftServer gameServer;
	private final Supplier<NodalNetworkSolver<N, E>> backendFactory;
	private final Map<NodalNetwork<N, E>, SimulationTask> activeTasks = new IdentityHashMap<NodalNetwork<N,E>, SimulationEngine<N,E>.SimulationTask>();
	private final PeriodicThreadPool simulationService;
	private int simulationStepTime = 50000;
	
	public SimulationEngine(MinecraftServer server) {
		this.gameServer = server;
		
		this.backendFactory = NodalNetworkSolver_LAPACK::standard; // TODO config
		
		this.simulationService = new PeriodicThreadPool(
				this.simulationStepTime, 
				4, // TODO config
				"NodeNet-Worker-"
		);
		
	}
	
	// TODO adjust when server tick time is changed
	public void setSimulationStepTime(int stepTime) {
		this.simulationStepTime = stepTime;
		this.simulationService.setPeriodeTime(this.simulationStepTime);
	}
	
	public int getSimulationStepTime() {
		return simulationStepTime;
	}
	
	public boolean shutdown() {
		try {
			this.simulationService.shutdown();
			if (!this.simulationService.awaitTermination(10, TimeUnit.SECONDS)) {
				this.simulationService.shutdownNow();
				NodeNet.LOGGER.warn("Simulation Engine Shutdown: Termination Abnormal");
			}
			return true;
		} catch (InterruptedException e) {
			NodeNet.LOGGER.warn("Simulation Engine Shutdown: Exception during Executor Shutdown", e);
			return false;
		}
	}
	
	public int getTickTime() {
		return this.gameServer.getTickCount();
	}
	
	public double getSimulationTime() {
		return getTickTime() * NOMINAL_BASE_TICK_TIME;
	}
	
	public class SimulationTask extends PeriodicTask {
		
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
		
		@Override
		public boolean exec(long lastPeriod, long lastExecution, BooleanSupplier hasTime) {
			
			if (this.exception == null) {
				try {
					this.solver.step(SimulationEngine.this.simulationStepTime);
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
			this.simulationService.submit(task);
			this.activeTasks.put(network, task);
		} else {
			task.updateTimeout(timeout);
		}
	}
	
}
