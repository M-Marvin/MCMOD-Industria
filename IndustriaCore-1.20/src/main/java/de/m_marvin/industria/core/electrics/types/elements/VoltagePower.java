package de.m_marvin.industria.core.electrics.types.elements;

import java.util.Optional;

import de.m_marvin.electronflow.nltisolver.DecimalPrefixFormater;
import de.m_marvin.electronflow.nltisolver.elements.Element;
import de.m_marvin.electronflow.nltisolver.elements.TwoPort;
import de.m_marvin.electronflow.nltisolver.network.IndexedNetwork.StampingContext;
import de.m_marvin.unimat.impl.MatrixNd;

public class VoltagePower extends TwoPort {

	protected double factor;
	protected int vidMeter;
	protected int vidSource;
	
	public VoltagePower(String name, String nodeA, String nodeB, double factor) {
		super(name, nodeA, nodeB);
		this.factor = factor;
	}
	
//	public static Optional<Element> tryParse(String[] args) {
//		if (args[0].startsWith("VIU") && args.length == 6) {
//			double value = DecimalPrefixFormater.parseDouble(args[5]);
//			return Optional.of(new VoltagePower(args[0], args[1], args[2], args[3], args[4], value));
//		}
//		return Optional.empty();
//	}
	
	@Override
	public String type() {
		return "VIU";
	}
	
	@Override
	public String configInfo() {
		return DecimalPrefixFormater.format("%s V/A", this.factor);
	}
	
	public void setFactor(double factor) {
		this.factor = factor;
	}
	
	public double factor() {
		return factor;
	}
	
	@Override
	public int[] vsourceIds() {
		return new int[] {
				this.vidSource,
				this.vidMeter
		};
	}
	
	@Override
	public double[] currents() {
		return new double[] {
				this.network.getVSourceCurrent(this.vidMeter),
				this.network.getVSourceCurrent(this.vidSource)
		};
	}

	@Override
	public void index(StampingContext ctx) {
		super.index(ctx);
		this.vidSource = ctx.nextVoltageSourceId();
		this.vidMeter = ctx.nextVoltageSourceId();
	}
	
	@Override
	public void stampMatricies(StampingContext ctx, MatrixNd A, MatrixNd z) {
		
//		if (ctx.stampsA()) {
//			int midSource = this.vidSource + ctx.nodeCount();
//			int midMeter = this.vidMeter + ctx.nodeCount();
//			if (this.nodeAid != 0) {
//				A.addM(midMeter, this.nodeAid - 1, +1.0);
//				A.addM(this.nodeAid - 1, midMeter, +1.0);
//			}
//			if (this.nodeBid != 0) {
//				A.addM(midMeter, this.nodeBid - 1, -1.0);
//				A.addM(this.nodeBid - 1, midMeter, -1.0);
//			}
//			if (this.nodeCid != 0) {
//				A.addM(midSource, this.nodeCid - 1, +1.0);
//				A.addM(this.nodeCid - 1, midSource, +1.0);
//			}
//			if (this.nodeDid != 0) {
//				A.addM(midSource, this.nodeDid - 1, -1.0);
//				A.addM(this.nodeDid - 1, midSource, -1.0);
//			}
//			A.addM(midMeter, midSource, -this.factor);
//		}
		
	}
	
}
