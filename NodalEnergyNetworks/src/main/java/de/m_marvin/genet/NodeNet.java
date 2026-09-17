package de.m_marvin.genet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.genet.nodal.Node;
import de.m_marvin.genet.nodal.Node.Format;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("nodenet")
public class NodeNet {
	
	public static final String MODID = "nodenet";

	public static final Logger LOGGER = LogManager.getLogger();
	
	public static final Format BLOCK_FACE_NODE = Node.registerFormat(MODID, "block_face", Format.of(BlockPos.class, Axis.class));
	public static final Format BLOCK_ID_NODE = Node.registerFormat(MODID, "block_id", Format.of(BlockPos.class, Integer.class));
	
	public static final Format NAME_SUBNODE = Node.registerFormat(MODID, "name", BLOCK_ID_NODE.sub(String.class));
	
	public NodeNet(FMLJavaModLoadingContext modctx) {
		
	}
	
}
