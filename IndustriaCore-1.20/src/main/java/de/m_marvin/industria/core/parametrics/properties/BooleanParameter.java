package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class BooleanParameter extends Parameter<Boolean> {

	public BooleanParameter(String name, boolean defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Boolean> getTypeClass() {
		return Boolean.class;
	}

	@Override
	public Boolean parseValue(JsonElement jsonElement) {
		return jsonElement.getAsBoolean();
	}

	@Override
	public void writeValue(FriendlyByteBuf buff, Object value) {
		buff.writeBoolean((Boolean) value);
	}

	@Override
	public Boolean readValue(FriendlyByteBuf buff) {
		return buff.readBoolean();
	}
	
}
