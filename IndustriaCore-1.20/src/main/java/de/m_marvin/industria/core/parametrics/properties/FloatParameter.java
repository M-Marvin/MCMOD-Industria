package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class FloatParameter extends Parameter<Float> {

	public FloatParameter(String name, float defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Float> getTypeClass() {
		return Float.class;
	}

	@Override
	public Float parseValue(JsonElement jsonElement) {
		return jsonElement.getAsFloat();
	}

	@Override
	public void writeValue(FriendlyByteBuf buff, Object value) {
		buff.writeFloat((Float) value);
	}

	@Override
	public Float readValue(FriendlyByteBuf buff) {
		return buff.readFloat();
	}
	
}
