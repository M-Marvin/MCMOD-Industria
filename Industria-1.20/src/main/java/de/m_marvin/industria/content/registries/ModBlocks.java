package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blocks.ConduitClampBlock;
import de.m_marvin.industria.content.blocks.ElectricLampBlock;
import de.m_marvin.industria.content.blocks.MotorBlock;
import de.m_marvin.industria.core.electrics.types.blocks.JunctionBoxBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Industria.MODID);
	public static void register() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Block> IRON_CONDUIT_CLAMP = BLOCKS.register("iron_conduit_clamp", () -> new ConduitClampBlock(Properties.of()));
	public static final RegistryObject<Block> MOTOR = BLOCKS.register("motor", () -> new MotorBlock(Properties.of()));
	public static final RegistryObject<Block> JUNCTION_BOX = BLOCKS.register("junction_box", () -> new JunctionBoxBlock(Properties.of()));
	public static final RegistryObject<Block> ELECTRIC_LAMP = BLOCKS.register("electric_lamp", () -> new ElectricLampBlock(Properties.of()));
	
}
