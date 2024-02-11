package de.m_marvin.industria.core.physics.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.valkyrienskies.mod.common.BlockStateInfo;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

/*
 * This is an patch to VS2 to sync the mass configurations of blocks to the client on dedicated servers
 * Might be removed as soon as VS2 fixes this in their code
 */

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class VS2MassSyncPatch {
	
	protected static Map<ResourceLocation, Double> clientMassInfoCache;
	
	public static double getPatchedBlockMass(BlockState state) {
		if (clientMassInfoCache == null) {
			return BlockStateInfo.INSTANCE.get(state).getFirst();
		} else {
			return clientMassInfoCache.getOrDefault(ForgeRegistries.BLOCKS.getKey(state.getBlock()), 100.0);
		}
	}
	
	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		
		Map<ResourceLocation, Double> massInfo = new HashMap<>();
		for (ResourceLocation blockKey : ForgeRegistries.BLOCKS.getKeys()) {
			
			BlockState state = ForgeRegistries.BLOCKS.getValue(blockKey).defaultBlockState();
			double mass = BlockStateInfo.INSTANCE.get(state).getFirst();
			massInfo.put(blockKey, mass);
			
		}
		
		ServerPlayer player = event.getPlayer();
		
		if (player == null) {
			IndustriaCore.NETWORK.send(PacketDistributor.ALL.noArg(), new SSyncVS2BlockInfoPackage(massInfo));
		} else {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SSyncVS2BlockInfoPackage(massInfo));
		}
		
	}
	
	public static class SSyncVS2BlockInfoPackage {
		
		public final Map<ResourceLocation, Double> massInfo;
		
		public SSyncVS2BlockInfoPackage(Map<ResourceLocation, Double> massInfo) {
			this.massInfo = massInfo;
		}
		
		public Map<ResourceLocation, Double> getMassInfo() {
			return massInfo;
		}
		
		public static void encode(SSyncVS2BlockInfoPackage msg, FriendlyByteBuf buff) {
			buff.writeInt(msg.massInfo.size());
			for (ResourceLocation block : msg.massInfo.keySet()) {
				buff.writeResourceLocation(block);
				buff.writeDouble(msg.massInfo.get(block));
			}
		}
		
		public static SSyncVS2BlockInfoPackage decode(FriendlyByteBuf buff) {
			Map<ResourceLocation, Double> massInfo = new HashMap<>();
			int entryCount = buff.readInt();
			for (int i = 0; i < entryCount; i++) {
				ResourceLocation block = buff.readResourceLocation();
				double mass = buff.readDouble();
				massInfo.put(block, mass);
			}
			return new SSyncVS2BlockInfoPackage(massInfo);
		}
		
		public static void handle(SSyncVS2BlockInfoPackage msg, Supplier<NetworkEvent.Context> ctx) {
			
			ctx.get().enqueueWork(() -> {
				clientMassInfoCache = msg.getMassInfo();
			});
			ctx.get().setPacketHandled(true);
			
		}
		
	}
	
}
