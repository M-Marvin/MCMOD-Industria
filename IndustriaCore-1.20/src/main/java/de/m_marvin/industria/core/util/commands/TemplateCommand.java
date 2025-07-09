package de.m_marvin.industria.core.util.commands;

import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.ssdplugins.engine.IStructureTemplateExtended;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.StructureFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class TemplateCommand {
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("template").requires((source) -> 
			source.hasPermission(2)
		)
		.then(
				Commands.literal("save")
				.then(
						Commands.argument("name", ResourceLocationArgument.id())
						.then(
								Commands.argument("pos1", BlockPosArgument.blockPos())
								.executes(source -> 
										saveConnected(source, ResourceLocationArgument.getId(source, "name"), BlockPosArgument.getLoadedBlockPos(source, "pos1")))
								.then(
										Commands.argument("pos2", BlockPosArgument.blockPos())
										.executes(source -> 
												save(source, ResourceLocationArgument.getId(source, "name"), BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos2"), true)
										)
										.then(
												Commands.argument("withEntities", BoolArgumentType.bool())
												.executes(source -> 
														save(source, ResourceLocationArgument.getId(source, "name"), BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos2"), BoolArgumentType.getBool(source, "withEntities"))
												)
										)
								)
						)
				)
		)
		.then(
				Commands.literal("load")
				.then(
						Commands.argument("name", ResourceLocationArgument.id())
						.then(
								Commands.argument("pos", BlockPosArgument.blockPos())
								.executes(source ->
										load(source, ResourceLocationArgument.getId(source, "name"), BlockPosArgument.getLoadedBlockPos(source, "pos"), Rotation.NONE, Mirror.NONE)
								)
								.then(
										Commands.argument("rotation", RotationArgument.rotation())
										.executes(source ->
												load(source, ResourceLocationArgument.getId(source, "name"), BlockPosArgument.getLoadedBlockPos(source, "pos"), RotationArgument.getRotation(source, "rotation"), Mirror.NONE)
										)
										.then(
												Commands.argument("mirror", MirroringArgument.mirroring())
												.executes(source ->
														load(source, ResourceLocationArgument.getId(source, "name"), BlockPosArgument.getLoadedBlockPos(source, "pos"), RotationArgument.getRotation(source, "rotation"), MirroringArgument.getMirror(source, "mirror"))
												)
										)
								)
						)
				)
		));
	}
	
	public static int load(CommandContext<CommandSourceStack> source, ResourceLocation name, BlockPos pos, Rotation rotation, Mirror mirror) {
		StructureTemplateManager manager = source.getSource().getLevel().getStructureManager();
		Optional<StructureTemplate> template = manager.get(name);
		if (template.isPresent()) {
			
			StructurePlaceSettings settings = new StructurePlaceSettings();
			settings.setRotation(rotation);
			settings.setMirror(mirror);
			if (!template.get().placeInWorld(source.getSource().getLevel(), pos, pos, settings, source.getSource().getLevel().getRandom(), 2)) {
				source.getSource().sendFailure(Component.translatable("industriacore.commands.template.loadfailed", name.toLanguageKey()));
				return 0;
			} else {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.template.loaded", name.toString()), true);
				return 1;
			}
			
		}
		
		source.getSource().sendFailure(Component.translatable("industriacore.commands.template.notfound", name.toString()));
		return 0;
	}
	
	public static int saveConnected(CommandContext<CommandSourceStack> source, ResourceLocation name, BlockPos pos) {
		
		int maxBlocks = Config.MAX_SELECTION_BLOCKS.get();
		Optional<List<BlockPos>> blocks = StructureFinder.findStructure(source.getSource().getLevel(), pos, maxBlocks, s -> !s.isAir());
		
		if (blocks.isEmpty() || blocks.get().isEmpty() || blocks.get().size() > maxBlocks) {
			source.getSource().sendFailure(Component.translatable("industriacore.commands.template.save.connected.structureinvalid", maxBlocks));
			return 0;
		}
		
		StructureTemplateManager manager = source.getSource().getLevel().getStructureManager();
		StructureTemplate template = manager.getOrCreate(name);
		((IStructureTemplateExtended) template).fillFromLevelPosIterable(source.getSource().getLevel(), blocks.get(), null);
		manager.save(name);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.template.saved", name.toString()), false);
		return 1;
	}
	
	public static int save(CommandContext<CommandSourceStack> source, ResourceLocation name, BlockPos pos1, BlockPos pos2, boolean withEntities) {

		int maxBlocks = Config.MAX_SELECTION_BLOCKS.get();
		int blockCount = 
				(Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) *
				(Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) *
				(Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
		
		if (blockCount > maxBlocks) {
			source.getSource().sendFailure(Component.translatable("industriacore.commands.template.save.selection.structureinvalid", blockCount, maxBlocks));
			return 0;
		}
		
		StructureTemplateManager manager = source.getSource().getLevel().getStructureManager();
		StructureTemplate template = manager.getOrCreate(name);
		BlockPos min = MathUtility.getMinCorner(pos1, pos2);
		BlockPos max = MathUtility.getMaxCorner(pos1, pos2);
		template.fillFromWorld(source.getSource().getLevel(), max, pos2.subtract(min).offset(1, 1, 1), withEntities, null);
		manager.save(name);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.template.saved", name.toString()), false);
		return 1;
	}
	
}
