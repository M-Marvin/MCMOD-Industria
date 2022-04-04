package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.blocks.ConduitClampBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModBlocks {
	
	public static final Block IRON_CONDUIT_CLAMP = registerBlock("iron_conduit_clamp", new ConduitClampBlock(Properties.of(Material.METAL, MaterialColor.METAL)));
	//public static final FlexibleConduitNodeBlock CONDUIT_NODE = registerBlock("conduit_node", new FlexibleConduitNodeBlock(Properties.of(Material.AIR)));
	
	public static <T extends Block> T registerBlock(String name, T block) {
		block.setRegistryName(Industria.MODID, name);
		ForgeRegistries.BLOCKS.register(block);
		return block;
	}
	
}
