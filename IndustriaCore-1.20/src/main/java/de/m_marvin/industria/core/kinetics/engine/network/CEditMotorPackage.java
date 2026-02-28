//package de.m_marvin.industria.core.kinetics.engine.network;
//
//import java.util.function.Supplier;
//
//import de.m_marvin.industria.core.kinetics.engine.ServerKineticPackageHandler;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.network.NetworkEvent;
//
///*
// * Tells the server if the player changed an motor blocks settings
// */
//public class CEditMotorPackage {
//	
//	public final BlockPos pos;
//	public final double rpm;
//	public final double torque;
//	
//	public CEditMotorPackage(BlockPos pos, double rpm, double torque) {
//		this.pos = pos;
//		this.rpm = rpm;
//		this.torque = torque;
//	}
//	
//	public BlockPos getPos() {
//		return pos;
//	}
//	
//	public double getRPM() {
//		return rpm;
//	}
//	
//	public double getTorque() {
//		return torque;
//	}
//	
//	public static void encode(CEditMotorPackage msg, FriendlyByteBuf buff) {
//		buff.writeBlockPos(msg.pos);
//		buff.writeDouble(msg.rpm);
//		buff.writeDouble(msg.torque);
//	}
//	
//	public static CEditMotorPackage decode(FriendlyByteBuf buff) {
//		BlockPos pos = buff.readBlockPos();
//		double rpm = buff.readDouble();
//		double torque = buff.readDouble();
//		return new CEditMotorPackage(pos, rpm, torque);
//	}
//	
//	public static void handle(CEditMotorPackage msg, Supplier<NetworkEvent.Context> ctx) {
//		
//		ctx.get().enqueueWork(() -> {
//			ServerKineticPackageHandler.handleEditMotor(msg, ctx.get());
//		});
//		ctx.get().setPacketHandled(true);
//		
//	}
//	
//}
