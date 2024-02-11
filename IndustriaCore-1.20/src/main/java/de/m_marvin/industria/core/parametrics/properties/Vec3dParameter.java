package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.network.FriendlyByteBuf;

public class Vec3dParameter extends Parameter<Vec3d> {

	public Vec3dParameter(String name, Vec3d defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Vec3d> getTypeClass() {
		return Vec3d.class;
	}

	@Override
	public Vec3d parseValue(JsonElement jsonElement) {
		JsonArray arr = jsonElement.getAsJsonArray();
		return new Vec3d(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble());
	}

	@Override
	public void writeValue(FriendlyByteBuf buff, Object value) {
		buff.writeDouble(((Vec3d) value).x());
		buff.writeDouble(((Vec3d) value).y());
		buff.writeDouble(((Vec3d) value).z());
	}

	@Override
	public Vec3d readValue(FriendlyByteBuf buff) {
		return new Vec3d(buff.readDouble(), buff.readDouble(), buff.readDouble());
	}
	
}
