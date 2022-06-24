package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModBlockEntities {
	
	//public static final BlockEntityType<FlexibleConduitBlockEntity> FLEXIBLE_CONDUIT_NODE = register("flexible_conduit_node", BlockEntityType.Builder.of(FlexibleConduitBlockEntity::new));
	
	public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
		//IForgeRegistry<BlockEntityType<?>> reg = event.getRegistry();
		
	}
	
}
