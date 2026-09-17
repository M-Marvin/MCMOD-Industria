package de.m_marvin.genet;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import de.m_marvin.genet.nodal.Node;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;

public class TestNoGame {
	
	public static void main(String... args) {
		
		System.out.println("Testing without starting game ...");
		
		BlockPos block = new BlockPos(123, 40, -23);
		int id = 400;
		
		Node node = NodeNet.BLOCK_ID_NODE.node(block, id);
		Node subnode = NodeNet.NAME_SUBNODE.subnode(node, "test");
		
		System.out.println(Node.Format.CODEC.encodeStart(JsonOps.INSTANCE, NodeNet.BLOCK_ID_NODE).getOrThrow());
		System.out.println(NodeNet.BLOCK_ID_NODE.encodeStart(JsonOps.INSTANCE, node.npos()).getOrThrow());
		
		JsonElement jsonNode = Node.CODEC.encodeStart(JsonOps.INSTANCE, node).getOrThrow();
		System.out.println(jsonNode);
		
		JsonElement jsonSubnode = Node.CODEC.encodeStart(JsonOps.INSTANCE, subnode).getOrThrow();
		System.out.println(jsonSubnode);
		
//		JsonElement e1 = NodeNet.BLOCK_ID_NODE.encodeStart(JsonOps.INSTANCE, node).getOrThrow();
//		System.out.println(e1);
//		
//		BlockPos block2 = new BlockPos(123, 40, -23);
//		Axis face2 = Axis.Z;
//		
//		Node node2 = NodeNet.BLOCK_FACE_NODE.node(block2, face2);
//		
//		System.out.println(node2.equals(node));
//		
//		var j =  Node.CODEC.encodeStart(JsonOps.INSTANCE, node);
//		if (!j.isSuccess()) {
//			System.err.println(j.error().get().message());
//			System.exit(-1);
//		}
//		
//		JsonElement json =  j.result().get();
//		
//		System.out.println(json);
		
	}
	
}
