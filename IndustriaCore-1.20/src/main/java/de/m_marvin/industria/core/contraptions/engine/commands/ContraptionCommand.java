package de.m_marvin.industria.core.contraptions.engine.commands;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.contraptions.engine.commands.arguments.contraption.ContraptionArgument;
import de.m_marvin.industria.core.contraptions.engine.commands.arguments.vec3relative.Vec3Relative;
import de.m_marvin.industria.core.contraptions.engine.commands.arguments.vec3relative.Vec3RelativeArgument;
import de.m_marvin.industria.core.contraptions.engine.types.ContraptionPosition;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.StructureFinder;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ContraptionCommand {
	
	public static void offsetSourcePosAndRotationFromCommandSource(CommandSourceStack source, Vec3d sourcePosition, Vec3d sourceRotation) {
		
		Vec3d sourceExecutorPos = Vec3d.fromVec(source.getPosition());
		Ship sourceContraption = ContraptionUtility.getContraptionOfBlock(source.getLevel(), MathUtility.toBlockPos(sourceExecutorPos));
		if (sourceContraption != null) {
			ContraptionPosition sourceContraptionPosition = new ContraptionPosition(sourceContraption);
			sourceRotation.setI(sourceContraptionPosition.getOrientation().euler(EulerOrder.XYZ, true));
			sourceExecutorPos = ContraptionUtility.toWorldPos(sourceContraption.getTransform(), sourceExecutorPos);
		}
		sourcePosition.addI(sourceExecutorPos);
		
	}
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("contraption").requires((source) -> {
			return source.hasPermission(2);
		})
		.then(
				Commands.literal("create")
				.then(
						Commands.argument("pos1", BlockPosArgument.blockPos())
						.executes((source) -> 
								createContraption(source, BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos1"), 1F)
						)
						.then(
								Commands.argument("pos2", BlockPosArgument.blockPos())
								.executes((source) -> 
										createContraption(source, BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos2"), 1F)
								)
								.then(
										Commands.argument("scale", FloatArgumentType.floatArg(0.0625F, 8F))
										.executes((source) ->
												createContraption(source, BlockPosArgument.getLoadedBlockPos(source, "pos1"), BlockPosArgument.getLoadedBlockPos(source, "pos2"), FloatArgumentType.getFloat(source, "scale"))
										)
								)
						)
				)
		)
		.then(
				Commands.literal("assemble")
				.then(
						Commands.argument("startPos", BlockPosArgument.blockPos())
						.executes((source) -> 
								assembleContraption(source, BlockPosArgument.getLoadedBlockPos(source, "startPos"), 1F)
						)
						.then(
								Commands.argument("scale", FloatArgumentType.floatArg(0.0625F, 8F))
								.executes((source) -> 
										assembleContraption(source, BlockPosArgument.getLoadedBlockPos(source, "startPos"), FloatArgumentType.getFloat(source, "scale"))
								)
						)
				)
		).then(
				Commands.literal("remove")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraptions())
						.executes((source) ->
								removeContraption(source, ContraptionArgument.getContraptions(source, "contraption"))
						)
				)
		)
		.then(
				Commands.literal("teleport")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraptions())
						.then(
								Commands.argument("position", Vec3RelativeArgument.vec3position())
								.executes((source) -> 
										teleportContraption(source, ContraptionArgument.getContraptions(source, "contraption"), Vec3RelativeArgument.getVec3Relative(source, "position"), null)
								)
								.then(
										Commands.argument("rotation", Vec3RelativeArgument.vec3())
										.executes((source) -> 
											teleportContraption(source, ContraptionArgument.getContraptions(source, "contraption"), Vec3RelativeArgument.getVec3Relative(source, "position"), Vec3RelativeArgument.getVec3Relative(source, "rotation"))
										)
								)
						)
				)
		)
		.then(
				Commands.literal("find")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraption())
						.executes((source) ->
							findContraption(source, ContraptionArgument.getContraption(source, "contraption"), null)
						)
						.then(
								Commands.literal("teleport")
								.executes((source) -> 
										findContraption(source, ContraptionArgument.getContraption(source, "contraption"), source.getSource().getEntity())
								)
								.then(
										Commands.argument("entity", EntityArgument.entity())
										.executes((source) ->
												findContraption(source, ContraptionArgument.getContraption(source, "contraption"), EntityArgument.getEntity(source, "entity"))
										)
								)
						)
				)
		)
		.then(
				Commands.literal("name")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraption())
						.then(
								Commands.argument("name", StringArgumentType.string())
								.executes((source) -> 
										setName(source, ContraptionArgument.getContraption(source, "contraption"), StringArgumentType.getString(source, "name"))
								)
						)
				)
		).then(
				Commands.literal("tag")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraptions())
						.then(
								Commands.literal("add")
								.then(
										Commands.argument("tag", StringArgumentType.string())
										.executes((source) ->
												editTags(source, ContraptionArgument.getContraptions(source, "contraption"), 0, StringArgumentType.getString(source, "tag"))
										)
								)
						).then(
								Commands.literal("remove")
								.then(
										Commands.argument("tag", StringArgumentType.string())
										.executes((source) ->
												editTags(source, ContraptionArgument.getContraptions(source, "contraption"), 1, StringArgumentType.getString(source, "tag"))
										)
								)
						).then(
								Commands.literal("list")
								.executes((source) ->
										editTags(source, ContraptionArgument.getContraptions(source, "contraption"), 2, null)
								)
						)
				)
		).then(
				Commands.literal("static")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraptions())
						.then(
								Commands.argument("state", BoolArgumentType.bool())
								.executes((source) ->
										setStatic(source, ContraptionArgument.getContraptions(source, "contraption"), BoolArgumentType.getBool(source, "state"))
								)
						)
				)
//		).then(
//				Commands.literal("splitting")
//				.then(
//						Commands.argument("contraption", ContraptionArgument.contraptions())
//						.then(
//								Commands.argument("state", BoolArgumentType.bool())
//								.executes((source) ->
//										setSplitting(source, ContraptionArgument.getContraptions(source, "contraption"), BoolArgumentType.getBool(source, "state"))
//								)
//						)
//				)
		).then(
				Commands.literal("scale")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraptions())
						.then(
								Commands.argument("scale", DoubleArgumentType.doubleArg(0.1, 10.0))
								.executes((source) ->
										setScale(source, ContraptionArgument.getContraptions(source, "contraption"), DoubleArgumentType.getDouble(source, "scale"))
								)
						)
				)
		).then(
				Commands.literal("velocity")
				.then(
						Commands.argument("contraption", ContraptionArgument.contraptions())
						.then(
								Commands.argument("velocity", Vec3RelativeArgument.vec3())
								.executes((source) ->
										setVelocity(source, ContraptionArgument.getContraptions(source, "contraption"), Vec3RelativeArgument.getVec3Relative(source, "velocity"), null)
								)
								.then(
										Commands.argument("omega", Vec3RelativeArgument.vec3())
										.executes((source) -> 
												setVelocity(source, ContraptionArgument.getContraptions(source, "contraption"), Vec3RelativeArgument.getVec3Relative(source, "velocity"), Vec3RelativeArgument.getVec3Relative(source, "omega"))
										)
								)
						)
				)
		));
	}
	
	public static int setVelocity(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions, Vec3Relative velocity, Vec3Relative omega) {

		Vec3d sourcePosition = new Vec3d();
		Vec3d sourceRotation = new Vec3d();
		offsetSourcePosAndRotationFromCommandSource(source.getSource(), sourcePosition, sourceRotation);
		
		for (ServerContraption contraption : contraptions) {

			Vec3d newVelocity = velocity.getPosition(contraption.getVelocityVec(), sourceRotation);
			Vec3d newOmega = omega != null ? omega.getPosition(contraption.getOmegaVec().mul(MathUtility.ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND), sourceRotation) : null; // The angular velocity is like an vector, so we use the position getter here too
			
			ContraptionPosition transform = contraption.getPosition();
			transform.setVelocity(newVelocity);
			if (omega != null) transform.setOmega(newOmega.mul(MathUtility.ROTATIONS_PER_SECOND_TO_ANGULAR_VELOCITY));
			ContraptionUtility.teleportContraption(contraption, transform);
		}
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.velocity.set.success", contraptions.size()), false);
		return 1;
		
	}

	public static int setScale(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions, double scale) {
		
		for (ServerContraption contraption : contraptions) {
			ContraptionPosition position = contraption.getPosition();
			position.setScale(scale);
			ContraptionUtility.teleportContraption(contraption, position);
		}
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.scale.set.success", contraptions.size(), scale), false);
		return 1;
		
	}
	
	public static int setStatic(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions, boolean state) {
		
		for (ServerContraption contaption : contraptions) {
			contaption.setStatic(state);
		}
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.static.set." + (state ? "static" : "dynamic"), contraptions.size()), false);
		return 1;
		
	}
	
//	public static int setSplitting(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions, boolean state) {
//
//		for (ServerContraption contaption : contraptions) {
//			contaption.setCanSplit(state);
//		}
//		
//		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.splitting.set." + (state ? "on" : "off"), contraptions.size()), false);
//		return 1;
//		
//	}
	
	public static int setName(CommandContext<CommandSourceStack> source, ServerContraption contraption, String name) {
		
		contraption.setName(name);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.name.set", name), true);
		return Command.SINGLE_SUCCESS;
		
	}
	
	public static int editTags(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions, int add_remove_list, String tag) {
		
		if (add_remove_list == 0) {
			
			if (tag == null || tag.isEmpty()) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.tagempty"), true);
				return 0;
			}
			
			for (ServerContraption contraption : contraptions) {
				contraption.addTag(tag);
			}
			
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.set.success", tag, contraptions.size()), true);
			return contraptions.size();
			
		} else if (add_remove_list == 1) {

			if (tag == null || tag.isEmpty()) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.tagempty"), true);
				return 0;
			}
			
			for (ServerContraption contraption : contraptions) {
				contraption.addTag(tag);
			}
			
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.remove.success", tag, contraptions.size()), true);
			return contraptions.size();
			
		} else {
			
			if (contraptions.size() != 1) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.listmultiple"), true);
				return 0;
			}
			
			ServerContraption contraption = contraptions.iterator().next();
			
			Set<String> tags = contraption.getTags();
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.listhead", contraption.getDisplayString(), tags.size()), true);
			
			for (String tagstring : tags) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.listentry", tagstring), false);
			}
			
			return Command.SINGLE_SUCCESS;
			
		}
		
	}
	
	public static int findContraption(CommandContext<CommandSourceStack> source, ServerContraption contraption, Entity entityToTeleport) {
		
		Vec3d contraptionPosition = contraption.getPosition().getPosition();
		
		if (entityToTeleport != null) {
						
			if (source.getSource().getEntity() instanceof Player) {
				
				entityToTeleport.teleportTo(contraptionPosition.x, contraptionPosition.y, contraptionPosition.z);
				
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.find.teleported", contraptionPosition.x().intValue(), contraptionPosition.y().intValue(), contraptionPosition.z().intValue()), true);
				return Command.SINGLE_SUCCESS;
				
			} else {

				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.find.noplayer"), true);
				return 0;
				
			}
			
		} else {
			
			Component coordMsgComp = Component.translatable("industriacore.commands.coordinates", contraptionPosition.x().intValue(), contraptionPosition.y().intValue(), contraptionPosition.z().intValue()).withStyle((style) -> {
				return style
						.withColor(ChatFormatting.GREEN)
						.withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/tp @s " + contraptionPosition.x() + " " + contraptionPosition.y() + " " + contraptionPosition.z()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")));
			});
			
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.find.found", coordMsgComp), true);
			return Command.SINGLE_SUCCESS;
			
		}
		
	}
	
	public static int teleportContraption(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions, Vec3Relative position, Vec3Relative rotation) {
		
		Vec3d sourcePosition = new Vec3d();
		Vec3d sourceRotation = new Vec3d();
		offsetSourcePosAndRotationFromCommandSource(source.getSource(), sourcePosition, sourceRotation);
		
		ServerLevel level = source.getSource().getLevel();
		ResourceLocation dimension = level.dimension().location();
		
		Vec3d newPosition = position.getPosition(sourcePosition, sourceRotation);
		Vec3d newRotation = rotation != null ? rotation.getRotation(sourceRotation, sourceRotation) : null;
		
		int teleported = 0;
		for (ServerContraption contraption : contraptions) {
			
			ContraptionPosition contraptionPos = contraption.getPosition();
			
			contraptionPos.setPosition(newPosition);
			contraptionPos.setDimension(dimension);
			if (rotation != null) contraptionPos.setOrientation(new Quaterniond().setEulerI(newRotation, EulerOrder.XYZ, true));
			
			if (ContraptionUtility.teleportContraption(contraption, contraptionPos)) teleported++;
			
		}
		
		int teleportedF = teleported;
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.teleport.success", teleportedF, String.format("%.2f", newPosition.x()), String.format("%.2f", newPosition.y()), String.format("%.2f", newPosition.z())), true);
		return teleported;
		
	}
	
	public static int createContraption(CommandContext<CommandSourceStack> source, BlockPos pos1, BlockPos pos2, float scale) {
		
		int maxBlocks = Config.MAX_SELECTION_BLOCKS.get();
		int blockCount = 
				(Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) *
				(Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) *
				(Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
		
		if (blockCount > maxBlocks) {
			source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.create.invalidstructure", blockCount, maxBlocks));
			return 0;
		}
		
		if (ContraptionUtility.convertToContraption(source.getSource().getLevel(), pos1, pos2, true, scale)) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.create.success", blockCount), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.create.failed"));
		return 0;
		
	}
	
	public static int assembleContraption(CommandContext<CommandSourceStack> source, BlockPos startPos, float scale) {
		
		int maxBlocks = Config.MAX_SELECTION_BLOCKS.get();
		Optional<List<BlockPos>> structureBlocks = StructureFinder.findStructure(source.getSource().getLevel(), startPos, maxBlocks, ContraptionUtility::isValidContraptionBlock);
		
		if (structureBlocks.isEmpty() || structureBlocks.get().isEmpty()) {
			
			source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.assemble.invalidstructure", maxBlocks));
			return 0;
			
		}
		
		if (ContraptionUtility.assembleToContraption(source.getSource().getLevel(), structureBlocks.get(), true, scale)) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.assemble.success", structureBlocks.get().size()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.assemble.failed"));
		return 0;
		
	}
	
	public static int removeContraption(CommandContext<CommandSourceStack> source, Collection<ServerContraption> contraptions) {
		int removed = 0;
		
		for (ServerContraption contraption : contraptions) {
			if (ContraptionUtility.removeContraption(contraption)) removed++;
		}
		
		int removedF = removed;
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.remove.success", removedF), true);
		
		return removed;
	}

}

