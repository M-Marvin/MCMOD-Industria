package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.core.electrics.types.ElectricNetwork;

public class SPICESimulationTicker {
	
	public static final int INITIAL_SIMULATION_STEP = 3000;
	
	protected final ElectricNetworkHandlerCapability handler;
	
	public SPICESimulationTicker(ElectricNetworkHandlerCapability handler) {
		this.handler = handler;
	}
	
	protected void runSimulation(ElectricNetwork network, int timeStep) {
		if (network.isExecutionActive()) {
			long timeLine = network.getTimeLine();
			String transientCommand = "tran " + timeStep * 50  + "m " + (timeLine + timeStep) * 50 + "m " + timeLine * 50 + "m";
			System.out.println(transientCommand);
			network.getNglink().execCommand(transientCommand);
			network.setTimeLine(timeLine + timeStep);
			
		}
	}
	
	protected void tick() {
		long currentTime = this.handler.getLevel().getGameTime();
		for (ElectricNetwork network : this.handler.getCircuits()) {
			if (currentTime >= network.getSimulationStart() + network.getTimeLine()) {
				runSimulation(network, INITIAL_SIMULATION_STEP / 50);
			}
		}
	}
	
}
