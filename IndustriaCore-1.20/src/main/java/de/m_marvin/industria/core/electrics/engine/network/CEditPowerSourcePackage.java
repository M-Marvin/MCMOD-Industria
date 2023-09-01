package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ServerElectricPackageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server to change a electric cable conduit's lane names
 */
public class CEditPowerSourcePackage {
	
	public final BlockPos pos;
	public final int voltage;
	public final int power;
	
	public CEditPowerSourcePackage(BlockPos pos, int voltage, int power) {
		this.pos = pos;
		this.voltage = voltage;
		this.power = power;
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public int getVoltage() {
		return voltage;
	}
	
	public int getPower() {
		return power;
	}
	
	public static void encode(CEditPowerSourcePackage msg, FriendlyByteBuf buff) {
		buff.writeBlockPos(msg.pos);
		buff.writeInt(msg.voltage);
		buff.writeInt(msg.power);
	}
	
	public static CEditPowerSourcePackage decode(FriendlyByteBuf buff) {
		BlockPos pos = buff.readBlockPos();
		int voltage = buff.readInt();
		int power = buff.readInt();
		return new CEditPowerSourcePackage(pos, voltage, power);
	}
	
	public static void handle(CEditPowerSourcePackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerElectricPackageHandler.handleEditPowerSource(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
