package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.core.electrics.types.ElectricNetwork;

public class SPICESimulationTicker {
	
	protected final ElectricNetworkHandlerCapability handler;
	
	public SPICESimulationTicker(ElectricNetworkHandlerCapability handler) {
		this.handler = handler;
	}
	
	protected void runSimulation(ElectricNetwork network, int timeStep) {
		if (network.isExecutionActive()) {
			long timeLine = 0; //network.getTimeLine();
			String transientCommand = "tran " + timeStep + "m " + (timeLine + timeStep) + "m " + timeLine + "m uic";
			//network.getNglink().execCommand(transientCommand);
			network.setTimeLine(timeLine + timeStep);
			
		}
	}
	
	protected void run(int timeStep) {
		long currentTime = System.currentTimeMillis();
		for (ElectricNetwork network : this.handler.getCircuits()) {
			if (currentTime >= network.getSimulationStart() + network.getTimeLine()) {
				runSimulation(network, timeStep);
			}
		}
	}
	
	protected void tick() {
		int stepTime = (int) Math.floor(this.handler.getLevel().getServer().getAverageTickTime());
		run(stepTime);
	}
	
}
