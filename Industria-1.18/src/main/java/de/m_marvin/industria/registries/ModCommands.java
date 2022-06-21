package de.m_marvin.industria.registries;

import com.mojang.brigadier.CommandDispatcher;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.commands.SetConduitCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		new ModCommands(event.getDispatcher());
	}
	
	public ModCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		SetConduitCommand.register(dispatcher);
	}
	
}
