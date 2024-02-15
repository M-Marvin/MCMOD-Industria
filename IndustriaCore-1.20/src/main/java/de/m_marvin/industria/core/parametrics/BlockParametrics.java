package de.m_marvin.industria.core.parametrics;

import java.util.HashMap;
import java.util.Map;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.parametrics.properties.DoubleParameter;
import de.m_marvin.industria.core.parametrics.properties.IntegerParameter;
import de.m_marvin.industria.core.parametrics.properties.Parameter;
import de.m_marvin.industria.core.parametrics.properties.Vec3dParameter;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class BlockParametrics {
	
	private final ResourceLocation block;
	private final Map<Parameter<?>, Object> parameters;
	
	public BlockParametrics(ResourceLocation block) {
		this.block = block;
		this.parameters = new HashMap<>();
	}
	
	public void writeToBuffer(FriendlyByteBuf buff) {
		buff.writeResourceLocation(this.block);
		buff.writeInt(this.parameters.size());
		for (Parameter<?> parameter : parameters.keySet()) {
			buff.writeUtf(parameter.getName());
			parameter.writeValue(buff, parameters.get(parameter));
		}
	}
	
	public static BlockParametrics readFromBuffer(FriendlyByteBuf buff) {
		BlockParametrics parametrics = new BlockParametrics(buff.readResourceLocation());
		int parameterCount = buff.readInt();
		for (int i = 0; i < parameterCount; i++) {
			Parameter<?> parameter = Parameter.getParameterByName(buff.readUtf());
			Object value = parameter.readValue(buff);
			parametrics.setParameter(parameter, value);
		}
		return parametrics;
	}
	
	public ResourceLocation getBlock() {
		return block;
	}
	
	public boolean hasParameter(Parameter<?> parameter) {
		return this.parameters.containsKey(parameter);
	}
	
	public <T> T getParameter(Parameter<T> parameter) {
		Object comparable = this.parameters.get(parameter);
		if (comparable == null) {
			IndustriaCore.LOGGER.warn("Parameter " + parameter.toString() + " not set for block " + this.block.toString() + "!");
			return parameter.defaultValue();
		} else {
			try {
				return parameter.getTypeClass().cast(comparable);
			} catch (ClassCastException e) {
				IndustriaCore.LOGGER.error("Paramter " + parameter.toString() + " could not be casted to type " + parameter.getTypeClass().getName() + ", wrong type assigned in file!");
				return parameter.defaultValue();
			}
		}
	}

	public void setParameter(Parameter<?> parameter, Object value) {
		this.parameters.put(parameter, parameter.getTypeClass().cast(value));
	}
	
	/* Default paramters used by most blocks */
	
	public static final IntegerParameter PARAMETER_NOMINAL_POWER = new IntegerParameter("nominalPower", 400);
	public static final IntegerParameter PARAMETER_POWER_MAX = new IntegerParameter("powerMax", 390);
	public static final IntegerParameter PARAMETER_POWER_MIN = new IntegerParameter("powerMin", 410);
	public static final IntegerParameter PARAMETER_NOMINAL_VOLTAGE = new IntegerParameter("nominalVoltage", 230);
	public static final IntegerParameter PARAMETER_VOLTAGE_MIN = new IntegerParameter("voltageMin", 300);
	public static final IntegerParameter PARAMETER_VOLTAGE_MAX = new IntegerParameter("voltageMax", 200);
	public static final DoubleParameter PARAMETER_MAGNETIC_COEFFICIENT = new DoubleParameter("magneticCoefficient", 1.0);
	public static final Vec3dParameter PARAMETER_MAGNETIC_VECTOR = new Vec3dParameter("magneticVector", new Vec3d(0, 1.0, 0));
	
	public int getNominalPower() {
		return getParameter(PARAMETER_NOMINAL_POWER);
	}
	
	public int getPowerMax() {
		return getParameter(PARAMETER_POWER_MAX);
	}
	
	public int getPowerMin() {
		return getParameter(PARAMETER_POWER_MIN);
	}
	
	public int getNominalVoltage() {
		return getParameter(PARAMETER_NOMINAL_VOLTAGE);
	}
	
	public int getVoltageMax() {
		return getParameter(PARAMETER_VOLTAGE_MAX);
	}
	
	public int getVoltageMin() {
		return getParameter(PARAMETER_VOLTAGE_MIN);
	}
	
	public double getMagneticCoefficient() {
		return getParameter(PARAMETER_MAGNETIC_COEFFICIENT);
	}

	public Vec3d getMagneticVector() {
		return getParameter(PARAMETER_MAGNETIC_VECTOR);
	}

	public double getPowerPercentageP(double power) {
		int powerMin = getPowerMin();
		int powerMax = getPowerMax();
		return Math.min(power / powerMin, 1) + Math.max((power - powerMin) / (powerMax - powerMin), 0);
	}

	public double getPowerPercentageV(double voltage) {
		int nominalVoltage = getNominalVoltage();
		int nominalPower = getNominalPower();
		int powerMin = getPowerMin();
		int powerMax = getPowerMax();
		double power = voltage * (nominalPower / (double) nominalVoltage);
		return Math.min(power / powerMin, 1) + Math.max((power - powerMin) / (powerMax - powerMin), 0);
	}

	public double getVoltageOvershoot(double voltage) {
		int voltageMin = getVoltageMin();
		int voltageMax = getVoltageMax();
		return Math.min((voltage / (double) voltageMin), 1) + Math.max((voltage - voltageMin) / (double) (voltageMax - voltageMin), 0);
	}
	
	public double getExplodeChance(double voltagePercentage, double powerPercentage) {
		return Math.max(0, voltagePercentage - 2) + Math.max(0, powerPercentage - 2);
	}
	
}
