package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class IntegerParameter extends Parameter<Integer> {

	public IntegerParameter(String name, int defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Integer> getTypeClass() {
		return Integer.class;
	}

	@Override
	public Integer parseValue(JsonElement jsonElement) {
		return jsonElement.getAsInt();
	}

	@Override
	public void writeValue(FriendlyByteBuf buff, Object value) {
		buff.writeInt((Integer) value);
	}

	@Override
	public Integer readValue(FriendlyByteBuf buff) {
		return buff.readInt();
	}
	
}
