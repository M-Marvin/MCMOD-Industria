package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.command.ConduitArgument;
import de.m_marvin.industria.core.physics.engine.commands.arguments.ContraptionArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CommandArguments {

	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, IndustriaCore.MODID);
	public static void register() {
		COMMAND_ARGUMENT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<SingletonArgumentInfo<ContraptionArgument>> CONTRAPTION_ID_ARGUMENT_SINGLE = COMMAND_ARGUMENT_TYPES.register("contraption_single", () ->
		ArgumentTypeInfos.registerByClass(ContraptionArgument.class, SingletonArgumentInfo.contextFree(ContraptionArgument::contraption)));

	public static final RegistryObject<SingletonArgumentInfo<ContraptionArgument>> CONTRAPTION_ID_ARGUMENT_MULTIPLE = COMMAND_ARGUMENT_TYPES.register("contraption_multiple", () ->
		ArgumentTypeInfos.registerByClass(ContraptionArgument.class, SingletonArgumentInfo.contextFree(ContraptionArgument::contraptions)));
	
	public static final RegistryObject<SingletonArgumentInfo<ConduitArgument>> CONDUIT_ARGUMENT = COMMAND_ARGUMENT_TYPES.register("conduit", () ->
		ArgumentTypeInfos.registerByClass(ConduitArgument.class, SingletonArgumentInfo.contextFree(ConduitArgument::conduit)));
	
}
