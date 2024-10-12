//package de.m_marvin.industria.core.electrics.engine.network;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.Supplier;
//
//import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
//import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
//import de.m_marvin.industria.core.util.types.SyncRequestType;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraftforge.network.NetworkEvent.Context;
//
//public class SSyncNodeVoltagesPackage {
//	
//	public final ChunkPos chunkPos;
//	public final Map<String, Set<Component<?, ?, ?>>> components;
//	
//	public SSyncNodeVoltagesPackage(Map<String, Set<Component<?, ?, ?>>> components, ChunkPos targetChunk) {
//		this.chunkPos = targetChunk;
//		this.components = components;
//	}
//	
//	public SSyncNodeVoltagesPackage(String dataList, Component<?, ?, ?> component, ChunkPos targetChunk, SyncRequestType request) {
//		this.chunkPos = targetChunk;
//		this.components = new HashMap<>();
//		this.components.put(dataList, Collections.singleton(component));
//	}
//
//	public ChunkPos getChunkPos() {
//		return chunkPos;
//	}
//	
//	public Map<String, Set<Component<?, ?, ?>>> getComponents() {
//		return components;
//	}
//	
////	public static void encode(SSyncNodeVoltagesPackage msg, FriendlyByteBuf buff) {
////		buff.writeInt(msg.components.size());
////		for (Component<?, ?, ?> component : msg.components) {
////			CompoundTag componentTag = new CompoundTag();
////			component.serializeNbt(componentTag);
////			buff.writeNbt(componentTag);
////		}
////		buff.writeChunkPos(msg.chunkPos);
////		buff.writeEnum(msg.request);
////	}
////	
////	public static SSyncNodeVoltagesPackage decode(FriendlyByteBuf buff) {
////		int componentCount = buff.readInt();
////		Set<Component<?, ?, ?>> components = new HashSet<>();
////		for (int i = 0; i < componentCount; i++) {
////			CompoundTag componentTag = buff.readNbt();
////			Component<?, ?, ?> component = Component.deserializeNbt(componentTag);
////			components.add(component);
////		}
////		ChunkPos chunkPos = buff.readChunkPos();
////		SyncRequestType request = buff.readEnum(SyncRequestType.class);
////		return new SSyncNodeVoltagesPackage(components, chunkPos, request);
////	}
//	
//	public static void handle(SSyncNodeVoltagesPackage msg, Supplier<Context> ctx) {
//		ctx.get().enqueueWork(() -> {
//			ClientElectricPackageHandler.handleSyncNodeVoltagesServer(msg, ctx.get());
//		});
//		ctx.get().setPacketHandled(true);
//	}
//	
//}
