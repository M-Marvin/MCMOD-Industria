package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class EnumParameter<E extends Enum<E>> extends Parameter<E> {
	
	protected final Class<E> enumClass;
	
	public EnumParameter(String name, Class<E> enumClass, E defaultValue) {
		super(name, defaultValue);
		this.enumClass = enumClass;
	}
	
	@Override
	public Class<E> getTypeClass() {
		return this.enumClass;
	}

	@Override
	public E parseValue(JsonElement jsonElement) {
		return Enum.valueOf(getTypeClass(), jsonElement.getAsString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeValue(FriendlyByteBuf buff, Object value) {
		buff.writeEnum((E) value);
	}

	@Override
	public E readValue(FriendlyByteBuf buff) {
		return buff.readEnum(this.enumClass);
	}
	
}
