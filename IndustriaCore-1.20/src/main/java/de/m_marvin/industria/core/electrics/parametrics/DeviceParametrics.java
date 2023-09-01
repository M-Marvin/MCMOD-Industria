package de.m_marvin.industria.core.electrics.parametrics;

public class DeviceParametrics {
	
	private final int nominalVoltage;
	private final int voltageMax;
	private final int voltageMin;
	private final int nominalPower;
	private final int powerMax;
	private final int powerMin;
	
	public DeviceParametrics(int nominalVoltage, int voltageMax, int voltageMin, int nominalPower, int powerMax, int powerMin) {
		this.nominalVoltage = nominalVoltage;
		this.voltageMax = voltageMax;
		this.voltageMin = voltageMin;
		this.nominalPower = nominalPower;
		this.powerMax = powerMax;
		this.powerMin = powerMin;
	}
	
	public int getNominalPower() {
		return nominalPower;
	}
	
	public int getPowerMax() {
		return powerMax;
	}
	
	public int getPowerMin() {
		return powerMin;
	}
	
	public int getNominalVoltage() {
		return nominalVoltage;
	}
	
	public int getVoltageMax() {
		return voltageMax;
	}
	
	public int getVoltageMin() {
		return voltageMin;
	}

	public double getPowerPercentageP(double power) {
		return Math.min(power / powerMin, 1) + Math.max((power - powerMin) / (powerMax - powerMin), 0);
	}

	public double getPowerPercentageV(double voltage) {
		double power = voltage * (nominalPower / (double) nominalVoltage);
		return Math.min(power / powerMin, 1) + Math.max((power - powerMin) / (powerMax - powerMin), 0);
	}

	public double getVoltageOvershoot(double voltage) {
		return Math.min((voltage / (double) voltageMin), 1) + Math.max((voltage - voltageMin) / (double) (voltageMax - voltageMin), 0);
	}
	
	public double getExplodeChance(double voltagePercentage, double powerPercentage) {
		return Math.max(0, voltagePercentage - 2) + Math.max(0, powerPercentage - 2);
	}
	
}
