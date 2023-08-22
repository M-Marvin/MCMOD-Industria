package de.m_marvin.industria.core.scrollinput.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.scrollinput.engine.ServerScrollPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a screw driver was used to adjust a block
 */
public class CScrollInputPackage {
	
	public InteractionHand hand;
	public BlockHitResult hitResult;
	public double scrollDelta;
		
	public CScrollInputPackage(BlockHitResult hitResult, double scrollDelta, InteractionHand hand) {
		this.scrollDelta = scrollDelta;
		this.hitResult = hitResult;
		this.hand = hand;
	}
		
	public double getScrollDelta() {
		return scrollDelta;
	}
	
	public BlockHitResult getHitResult() {
		return hitResult;
	}
	
	public InteractionHand getHand() {
		return hand;
	}
	
	public static void encode(CScrollInputPackage msg, FriendlyByteBuf buff) {
		buff.writeDouble(msg.scrollDelta);
		buff.writeBlockHitResult(msg.hitResult);
		buff.writeBoolean(msg.hand == InteractionHand.MAIN_HAND);
	}
	
	public static CScrollInputPackage decode(FriendlyByteBuf buff) {
		double scrollDelta = buff.readDouble();
		BlockHitResult hitResult = buff.readBlockHitResult();
		InteractionHand hand = buff.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		return new CScrollInputPackage(hitResult, scrollDelta, hand);
	}
	
	public static void handle(CScrollInputPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerScrollPackageHandler.handleScrollPackage(msg, null);
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
