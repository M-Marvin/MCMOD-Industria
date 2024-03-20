package de.m_marvin.industria.core.electrics.engine.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class SSyncCircuitTemplatesPackage {
	
	public final Map<ResourceLocation, CircuitTemplate> circuitTemplates;
	
	public SSyncCircuitTemplatesPackage(Map<ResourceLocation, CircuitTemplate> circuitTemplates) {
		this.circuitTemplates = circuitTemplates;
	}
	
	public Map<ResourceLocation, CircuitTemplate> getCircuitTemplates() {
		return circuitTemplates;
	}
	
	public static void encode(SSyncCircuitTemplatesPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.circuitTemplates.size());
		for (Entry<ResourceLocation, CircuitTemplate> template : msg.circuitTemplates.entrySet()) {
			buff.writeResourceLocation(template.getKey());
			buff.writeUtf(template.getValue().getTemplate());
			buff.writeUtf(template.getValue().getIdProperty());
			buff.writeInt(template.getValue().getNetworks().length);
			for (String s : template.getValue().getNetworks()) buff.writeUtf(s);
			buff.writeInt(template.getValue().getProperties().length);
			for (String s : template.getValue().getProperties()) buff.writeUtf(s);
		}
	}
	
	public static SSyncCircuitTemplatesPackage decode(FriendlyByteBuf buff) {
		Map<ResourceLocation, CircuitTemplate> circuitTemplates = new HashMap<>();
		int entryCount = buff.readInt();
		for (int i = 0; i < entryCount; i++) {
			ResourceLocation resloc = buff.readResourceLocation();
			String template = buff.readUtf();
			String idProperty = buff.readUtf();
			String[] networks = new String[buff.readInt()];
			for (int i1 = 0; i1 < networks.length; i1++) networks[i1] = buff.readUtf();
			String[] properties = new String[buff.readInt()];
			for (int i1 = 0; i1 < properties.length; i1++) properties[i1] = buff.readUtf();
			circuitTemplates.put(resloc, new CircuitTemplate(networks, properties, template, idProperty));
		}
		return new SSyncCircuitTemplatesPackage(circuitTemplates);
	}
	
	public static void handle(SSyncCircuitTemplatesPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleSyncCircuitTemplates(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}