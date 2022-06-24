package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.blocks.ConduitClampBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModBlocks {
	
	public static final Block IRON_CONDUIT_CLAMP = new ConduitClampBlock(Properties.of(Material.METAL, MaterialColor.METAL)).setRegistryName(new ResourceLocation(Industria.MODID, "iron_conduit_clamp"));
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> reg = event.getRegistry();
		reg.register(IRON_CONDUIT_CLAMP);
	}
	
}
