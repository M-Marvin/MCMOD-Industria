package de.m_marvin.industria.core.physics.engine.commands;

import java.util.List;
import java.util.Optional;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.StructureFinder;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PhysicsCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("physics").requires((source) -> {
			return source.hasPermission(2);
		})
		.then(
				Commands.literal("reset")
				.executes((source) -> 
					resetPhysics(source)
				)
		));
	}
	
	public static int resetPhysics(CommandContext<CommandSourceStack> source) {
		if (PhysicUtility.resetFrameQueue(source.getSource().getLevel())) {

			return Command.SINGLE_SUCCESS;
		} else {
			
			return 0;
		}
	}
	
}

