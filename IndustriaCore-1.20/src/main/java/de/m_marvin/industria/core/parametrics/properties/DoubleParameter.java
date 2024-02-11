package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class DoubleParameter extends Parameter<Double> {

	public DoubleParameter(String name, double defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Double> getTypeClass() {
		return Double.class;
	}

	@Override
	public Double parseValue(JsonElement jsonElement) {
		return jsonElement.getAsDouble();
	}

	@Override
	public void writeValue(FriendlyByteBuf buff, Object value) {
		buff.writeDouble((Double) value);
	}

	@Override
	public Double readValue(FriendlyByteBuf buff) {
		return buff.readDouble();
	}
	
}
