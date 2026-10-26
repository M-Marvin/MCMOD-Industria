package de.m_marvin.genet.network.nodal;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.tvnlnna.nodal.NodalNetwork;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver_LAPACK;
import net.minecraft.server.MinecraftServer;

public class SimulationEngine<N, E> {
	
	private final MinecraftServer gameServer;
	private final Supplier<NodalNetworkSolver<N, E>> backendFactory;
	private final ForkJoinPool executor;
	private final Map<NodalNetwork<N, E>, SimulationTask> activeTasks = new IdentityHashMap<NodalNetwork<N,E>, SimulationEngine<N,E>.SimulationTask>();
	
	public SimulationEngine(MinecraftServer server) {
		this.gameServer = server;
		
		this.executor = new ForkJoinPool(
				4, // TODO config
				ForkJoinSimulationWorkerThread::new, 
				this::onThreadError, 
				true, 
				4, // TODO config
				4, // TODO config
				1, 
				null, 
				10, 
				TimeUnit.SECONDS
			);
		
		this.backendFactory = NodalNetworkSolver_LAPACK::standard; // TODO config
		
	}
	
	public boolean shutdown() {
		try {
			return this.executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			NodeNet.LOGGER.warn("Simulation Engine Shutdown: Exception during Executor Shutdown", e);
			return false;
		}
	}
	
	public int getTickTime() {
		return this.gameServer.getTickCount();
	}
	
	protected class ForkJoinSimulationWorkerThread extends ForkJoinWorkerThread {
		
		private final NodalNetworkSolver<N, E> solverBackend;
		
		protected ForkJoinSimulationWorkerThread(ForkJoinPool pool) {
			super(pool);
			this.solverBackend = SimulationEngine.this.backendFactory.get();
			
			setName("NodalSimulationWorker-" + getPoolIndex());
		}
		
		public NodalNetworkSolver<N, E> getSolverBackend() {
			return solverBackend;
		}
		
	}
	
	private void onThreadError(Thread simulationThread, Throwable error) {
		NodeNet.LOGGER.error("Exception within Simulation Engine Worker: {}", simulationThread.getName(), error);
	}
	
	public class SimulationTask extends ForkJoinTask<Void> {
		
		private static final long serialVersionUID = -5422172476180704241L;
		
		private final NodalNetwork<N, E> network;
		private volatile long expiration;
		
		public SimulationTask(NodalNetwork<N, E> network, int timeout) {
			this.network = network;
			updateTimeout(timeout);
		}
		
		public void updateTimeout(int timeout) {
			this.expiration = getTickTime() + timeout;
		}
		
		@SuppressWarnings("unchecked")
		public NodalNetworkSolver<N, E> getSolverBackend() {
			if (Thread.currentThread() instanceof SimulationEngine<?, ?>.ForkJoinSimulationWorkerThread simulationThread) {
				return (NodalNetworkSolver<N, E>) simulationThread.getSolverBackend();
			}
			throw new UnsupportedOperationException("attepmted to run simulation task in generic executor service");
		}
		
		@Override
		public Void getRawResult() {
			return null;
		}

		@Override
		protected void setRawResult(Void value) {}

		@Override
		protected boolean exec() {
			
			NodalNetworkSolver<N, E> solver = getSolverBackend();
			
			System.out.println(solver + " :: " + System.identityHashCode(this.network));
//			solver.setNetwork(this.network);
//			solver.setSimulationTime( 0.0 ); // Uh ...
//			
//			solver.step( 0.0 );
			
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
