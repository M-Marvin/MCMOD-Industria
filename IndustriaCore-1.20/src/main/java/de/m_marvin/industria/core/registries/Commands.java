package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.command.SetConduitCommand;
import de.m_marvin.industria.core.electrics.engine.commands.FixElectricsCommand;
import de.m_marvin.industria.core.physics.engine.commands.ContraptionCommand;
import de.m_marvin.industria.core.physics.engine.commands.arguments.contraption.ContraptionSelectorOptions;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Commands {
	
	static {
		ContraptionSelectorOptions.bootStrap();
	}
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		SetConduitCommand.register(event.getDispatcher());
		ContraptionCommand.register(event.getDispatcher());
		FixElectricsCommand.register(event.getDispatcher());
	}
	
}
