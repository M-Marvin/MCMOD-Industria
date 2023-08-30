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
	
}
