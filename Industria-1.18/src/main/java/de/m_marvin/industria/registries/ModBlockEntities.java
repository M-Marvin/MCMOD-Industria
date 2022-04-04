package de.m_marvin.industria.registries;

import com.mojang.datafixers.types.Type;

import de.m_marvin.industria.Industria;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModBlockEntities {
	
	//public static final BlockEntityType<FlexibleConduitBlockEntity> FLEXIBLE_CONDUIT_NODE = register("flexible_conduit_node", BlockEntityType.Builder.of(FlexibleConduitBlockEntity::new));
	
	private static <T extends BlockEntity> BlockEntityType<T> register(String key, BlockEntityType.Builder<T> builder) {
		Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, key);
		BlockEntityType<T> blockEntityType = builder.build(type);
		blockEntityType.setRegistryName(Industria.MODID, key);
		ForgeRegistries.BLOCK_ENTITIES.register(blockEntityType);
		return blockEntityType;
	}
	
}
