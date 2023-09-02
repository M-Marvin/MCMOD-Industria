package de.m_marvin.industria.core.electrics.engine.commands;

import java.util.EnumSet;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

public class FixElectricsCommand {
	
	public static final int MAX_AREA_SIZE = 110592;
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("fixelectrics").requires((source) -> 
			source.hasPermission(2)
		)
		.then(
				Commands.argument("pos", BlockPosArgument.blockPos())
				.executes((source) -> 
						fixElectrics(source, BlockPosArgument.getLoadedBlockPos(source, "pos"))
				)
				.then(
						Commands.argument("range", IntegerArgumentType.integer(0, 48))
						.executes((source) ->
								fixElectricsRange(source, BlockPosArgument.getLoadedBlockPos(source, "pos"), IntegerArgumentType.getInteger(source, "range"))
						)
				)
				
		));
	}
	
	public static int fixElectricsRange(CommandContext<CommandSourceStack> source, BlockPos position, int range) {
		ServerLevel level = source.getSource().getLevel();
		int areaBlocks = (int) Math.pow(range, 3);
		
		if (areaBlocks > MAX_AREA_SIZE) {
			source.getSource().sendFailure(Component.translatable("industriacore.command.fixelectrics.areatolarge", areaBlocks, MAX_AREA_SIZE));
			return 0;
		}
		
		int fixedCount = 0;
		for (int x = - range / 2; x < + range / 2; x++) {
			for (int z = - range / 2; z < + range / 2; z++) {
				for (int y = - range / 2; y < + range / 2; y++) {
					BlockPos pos = new BlockPos(x, y, z).offset(position);
					BlockState state = level.getBlockState(pos);
					if (state.getBlock() instanceof IElectric<?, ?, ?>) {
						Event event = new BlockEvent.NeighborNotifyEvent(level, pos, state, EnumSet.allOf(Direction.class), false);
						MinecraftForge.EVENT_BUS.post(event);
						fixedCount++;
					}
				}
			}
		}
		int fixedCountF = fixedCount;
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.command.fixelectrics.multisuccess", fixedCountF), true);
		return Command.SINGLE_SUCCESS;
	}
	
	public static int fixElectrics(CommandContext<CommandSourceStack> source, BlockPos position) {
		ServerLevel level = source.getSource().getLevel();
		BlockState state = level.getBlockState(position);
		if (state.getBlock() instanceof IElectric<?, ?, ?>) {
			Event event = new BlockEvent.NeighborNotifyEvent(level, position, state, EnumSet.allOf(Direction.class), false);
			MinecraftForge.EVENT_BUS.post(event);
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.command.fixelectrics.success", position.getX(), position.getY(), position.getZ()), true);
			return Command.SINGLE_SUCCESS;
		}
		source.getSource().sendFailure(Component.translatable("industriacore.command.fixelectrics.noelectric", position.getX(), position.getY(), position.getZ()));
		return 0;
	}
	
}
