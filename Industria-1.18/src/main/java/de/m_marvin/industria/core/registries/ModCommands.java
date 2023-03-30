package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.engine.command.SetConduitCommand;
import de.m_marvin.industria.core.physics.engine.commands.ContraptionCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		SetConduitCommand.register(event.getDispatcher());
		ContraptionCommand.register(event.getDispatcher());
	}
	
}
