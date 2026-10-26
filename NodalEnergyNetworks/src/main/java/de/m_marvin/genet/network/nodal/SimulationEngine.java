package de.m_marvin.genet.network.nodal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import de.m_marvin.tvnlnna.nodal.NodalNetwork;
import de.m_marvin.tvnlnna.solver.NodalNetworkSolver;

public class SimulationEngine<N, E> {
	
	private ExecutorService executor; // TODO config 
	private Supplier<NodalNetworkSolver<N, E>> backendFactory;
	
	public SimulationEngine() {
		
//		this.executor = new ForkJoinPool(
//				4, // TODO config
//				null, 
//				this::onThreadError, 
//				true, 
//				4, // TODO config
//				4, // TODO config
//				1, 
//				null, 
//				10, 
//				TimeUnit.SECONDS
//			);
		
	}
	
	private void onThreadError(Thread simulationThread, Throwable error) {
		
	}
	
	public void test() {
		
		this.executor.submit(() -> {
			
			
			
		});
		
	}
	
	/**
	 * Submits a network to be simulated for a set number of (network simulation) ticks.<br>
	 * If future calls with the same network are made, the span limit is reset again, this ensures that the simulations halt quickly in case the main thread is paused.
	 * @param network The network to run the simulation for
	 * @param span The number of simulation ticks to step trough before halting the simulation
	 */
	public void submit(NodalNetwork<N, E> network, int span) {
		
		
		
	}
	
	
}
