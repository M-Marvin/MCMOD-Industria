package de.redtec.packet;

import java.util.function.Supplier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityJigsaw;
import de.redtec.util.handler.ItemStackHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CEditJigsawTileEntityPacket {
	
	public BlockPos pos;
	public ResourceLocation poolFile;
	public ResourceLocation name;
	public ResourceLocation targetName;
	public BlockState replaceState;
	public boolean lockOrientation;
	
	public CEditJigsawTileEntityPacket(BlockPos pos, ResourceLocation poolFile, ResourceLocation name, ResourceLocation targetName, BlockState replaceState, boolean lockOrientation) {
		this.pos = pos;
		this.poolFile = poolFile;
		this.name = name;
		this.targetName = targetName;
		this.replaceState = replaceState;
		this.lockOrientation = lockOrientation;
	}
	
	public CEditJigsawTileEntityPacket(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.poolFile = buf.readResourceLocation();
		this.name = buf.readResourceLocation();
		this.targetName = buf.readResourceLocation();
		try {
			BlockStateParser parser = new BlockStateParser(new StringReader(buf.readString()), true);
			parser.parse(false);
			this.replaceState = parser.getState();
		} catch (CommandSyntaxException e) {
			this.replaceState = Blocks.AIR.getDefaultState();
			RedTec.LOGGER.error("Cant parse BlockState!");
			e.printStackTrace();
		}
		this.lockOrientation = buf.readBoolean();
	}
		
	public static void encode(CEditJigsawTileEntityPacket packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeResourceLocation(packet.poolFile);
		buf.writeResourceLocation(packet.name);
		buf.writeResourceLocation(packet.targetName);
		buf.writeString(ItemStackHelper.getBlockStateString(packet.replaceState));
		buf.writeBoolean(packet.lockOrientation);
	}
	
	public static void handle(final CEditJigsawTileEntityPacket packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().world;
			TileEntity contactTileEntity = world.getTileEntity(packet.pos);
			
			if (contactTileEntity instanceof TileEntityJigsaw) {
				
				((TileEntityJigsaw) contactTileEntity).poolFile = packet.poolFile;
				((TileEntityJigsaw) contactTileEntity).name = packet.name;
				((TileEntityJigsaw) contactTileEntity).targetName = packet.targetName;
				((TileEntityJigsaw) contactTileEntity).replaceState = packet.replaceState;
				((TileEntityJigsaw) contactTileEntity).lockOrientation = packet.lockOrientation;
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}
