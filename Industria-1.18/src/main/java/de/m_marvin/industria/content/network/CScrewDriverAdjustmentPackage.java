package de.m_marvin.industria.content.network;

import java.util.function.Supplier;

import de.m_marvin.industria.content.ServerPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a screw driver was used to adjust a block
 */
public class CScrewDriverAdjustmentPackage {
	
	public InteractionHand hand;
	public BlockHitResult hitResult;
	public double scrollDelta;
		
	public CScrewDriverAdjustmentPackage(BlockHitResult hitResult, double scrollDelta, InteractionHand hand) {
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
	
	public static void encode(CScrewDriverAdjustmentPackage msg, FriendlyByteBuf buff) {
		buff.writeDouble(msg.scrollDelta);
		buff.writeBlockHitResult(msg.hitResult);
		buff.writeBoolean(msg.hand == InteractionHand.MAIN_HAND);
	}
	
	public static CScrewDriverAdjustmentPackage decode(FriendlyByteBuf buff) {
		double scrollDelta = buff.readDouble();
		BlockHitResult hitResult = buff.readBlockHitResult();
		InteractionHand hand = buff.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		return new CScrewDriverAdjustmentPackage(hitResult, scrollDelta, hand);
	}
	
	public static void handle(CScrewDriverAdjustmentPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerPackageHandler.handleScrewDriverAdjustment(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
