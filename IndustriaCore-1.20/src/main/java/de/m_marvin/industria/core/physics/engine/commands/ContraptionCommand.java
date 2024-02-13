package de.m_marvin.industria.core.physics.engine.commands;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.engine.commands.arguments.ContraptionArgument;
import de.m_marvin.industria.core.physics.engine.commands.arguments.Vec3Relative;
import de.m_marvin.industria.core.physics.engine.commands.arguments.Vec3RelativeArgument;
import de.m_marvin.industria.core.physics.types.Contraption;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
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
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ContraptionCommand {
	
	public static void offsetSourcePosAndRotationFromCommandSource(CommandSourceStack source, Vec3d sourcePosition, Vec3d sourceRotation) {
		
		Vec3d sourcePosition1 = Vec3d.fromVec(source.getPosition());
		if (source.getEntity() != null) {
			// TODO entity source totation
			
			//source.getEntity().get
			
		} else {
			Vec3d sourceExecutorPos = Vec3d.fromVec(source.getPosition());
			Ship sourceContraption = PhysicUtility.getContraptionOfBlock(source.getLevel(), MathUtility.toBlockPos(sourceExecutorPos));
			if (sourceContraption != null) {
				ContraptionPosition sourceContraptionPosition = new ContraptionPosition(sourceContraption);
				sourceRotation.setI(sourceContraptionPosition.getOrientation().euler(EulerOrder.XYZ, true));
				sourcePosition1 = PhysicUtility.toWorldPos(sourceContraption.getTransform(), sourceExecutorPos);
			}
		}
		sourcePosition.addI(sourcePosition1);
		
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
		)
		.then(
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
		).then(
				Commands.literal("test")
				.then(
						Commands.argument("vec", Vec3Argument.vec3())
						.executes((source) -> 
								test(source, Vec3Argument.getVec3(source, "vec"))
						)
				)
		));
	}
	
	public static int test(CommandContext<CommandSourceStack> source, Vec3 vec) {
		
		
		
		return 1;
	}
	
	public static int setVelocity(CommandContext<CommandSourceStack> source, Collection<Contraption> contraptions, Vec3Relative velocity, Vec3Relative omega) {

		Vec3d sourcePosition = new Vec3d();
		Vec3d sourceRotation = new Vec3d();
		offsetSourcePosAndRotationFromCommandSource(source.getSource(), sourcePosition, sourceRotation);
		
		for (Contraption contraption : contraptions) {

			Vec3d newVelocity = velocity.getPosition(contraption.getVelocityVec(), sourceRotation);
			Vec3d newOmega = omega != null ? omega.getPosition(contraption.getOmegaVec().mul(MathUtility.ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND), sourceRotation) : null; // The angular velocity is like an vector, so we use the position getter here too
			
			ContraptionPosition transform = contraption.getPosition();
			transform.setVelocity(newVelocity);
			if (omega != null) transform.setOmega(newOmega.mul(MathUtility.ROTATIONS_PER_SECOND_TO_ANGULAR_VELOCITY));
			PhysicUtility.teleportContraption(source.getSource().getLevel(), (ServerShip) contraption.getContraption(), transform);
		}
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.velocity.set.success", contraptions.size()), false);
		return 1;
		
	}

	public static int setScale(CommandContext<CommandSourceStack> source, Collection<Contraption> contraptions, double scale) {
		
		for (Contraption contraption : contraptions) {
			ContraptionPosition transform = contraption.getPosition();
			transform.setScale(scale);
			PhysicUtility.teleportContraption(source.getSource().getLevel(), (ServerShip) contraption.getContraption(), transform);
		}
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.scale.set.success", contraptions.size(), scale), false);
		return 1;
		
	}
	
	public static int setStatic(CommandContext<CommandSourceStack> source, Collection<Contraption> contraptions, boolean state) {
		
		for (Contraption contaption : contraptions) {
			((ServerShip) contaption.getContraption()).setStatic(state);
		}
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.static.set." + (state ? "static" : "dynamic"), contraptions.size()), false);
		return 1;
		
	}
	
	public static int setName(CommandContext<CommandSourceStack> source, Contraption contraption, String name) {
		
		((ServerShip) contraption.getContraption()).setSlug(name);
		
		source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.name.set", name), true);
		return Command.SINGLE_SUCCESS;
		
	}
	
	public static int editTags(CommandContext<CommandSourceStack> source, Collection<Contraption> contraptions, int add_remove_list, String tag) {
		
		if (add_remove_list == 0) {
			
			if (tag == null || tag.isEmpty()) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.tagempty"), true);
				return 0;
			}
			
			for (Contraption contraption : contraptions) {
				PhysicUtility.addContraptionTag(contraption.getLevel(), contraption.getContraption(), tag);
			}
			
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.set.success", tag, contraptions.size()), true);
			return contraptions.size();
			
		} else if (add_remove_list == 1) {

			if (tag == null || tag.isEmpty()) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.tagempty"), true);
				return 0;
			}
			
			for (Contraption contraption : contraptions) {
				PhysicUtility.removeContraptionTag(contraption.getLevel(), contraption.getContraption(), tag);
			}
			
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.remove.success", tag, contraptions.size()), true);
			return contraptions.size();
			
		} else {
			
			if (contraptions.size() != 1) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.listmultiple"), true);
				return 0;
			}
			
			Contraption contraption = contraptions.iterator().next();
			
			Set<String> tags = PhysicUtility.getContraptionTags(contraption.getLevel(), contraption.getContraption());
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.listhead", contraption.getDisplayString(), tags.size()), true);
			
			for (String tagstring : tags) {
				source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.tags.listentry", tagstring), false);
			}
			
			return Command.SINGLE_SUCCESS;
			
		}
		
	}
	
	public static int findContraption(CommandContext<CommandSourceStack> source, Contraption contraption, Entity entityToTeleport) {
		
		Vec3d contraptionPosition = contraption.getPosition().getPosition();
		
		if (entityToTeleport != null) {
						
			if (source.getSource().getEntity() instanceof Player player) {
				
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
	
	public static int teleportContraption(CommandContext<CommandSourceStack> source, Collection<Contraption> contraptions, Vec3Relative position, Vec3Relative rotation) {
		
		Vec3d sourcePosition = new Vec3d();
		Vec3d sourceRotation = new Vec3d();
		offsetSourcePosAndRotationFromCommandSource(source.getSource(), sourcePosition, sourceRotation);
		
		ServerLevel level = source.getSource().getLevel();
		String dimension = PhysicUtility.getDimensionId(level);
		
		Vec3d newPosition = position.getPosition(sourcePosition, sourceRotation);
		Vec3d newRotation = rotation != null ? rotation.getRotation(sourceRotation, sourceRotation) : null;
		
		for (Contraption contraption : contraptions) {
			
			ContraptionPosition contraptionPos = contraption.getPosition();
			
			contraptionPos.setPosition(newPosition);
			contraptionPos.setDimension(dimension);
			if (rotation != null) contraptionPos.setOrientation(new Quaterniond(newRotation, EulerOrder.XYZ, true));
			
			PhysicUtility.teleportContraption(source.getSource().getLevel(), (ServerShip) contraption.getContraption(), contraptionPos);
			
		}
		
		if (contraptions.size() == 1) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.teleport.success", newPosition.x(), newPosition.y(), newPosition.z()), true);
		} else {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.teleport.success_multiple", contraptions.size(), newPosition.x(), newPosition.y(), newPosition.z()), true);
		}
		return Command.SINGLE_SUCCESS;
		
	}
	
	public static int createContraption(CommandContext<CommandSourceStack> source, BlockPos pos1, BlockPos pos2, float scale) {
		
		AABB bounds = new AABB(MathUtility.getMinCorner(pos1, pos2), MathUtility.getMaxCorner(pos1, pos2));
		
		if (bounds.getXsize() > 32 || bounds.getYsize() > 32 || bounds.getZsize() > 32) {
			source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.create.tolarge", (int) bounds.getXsize(), (int) bounds.getYsize(), (int) bounds.getZsize(), 32, 32, 32));
			return 0;
		}
		
		boolean success = PhysicUtility.convertToContraption(source.getSource().getLevel(), bounds, true, scale);
		
		BlockPos minCorner = MathUtility.getMinCorner(pos1, pos2);
		
		if (success) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.create.success", (int) minCorner.getX(), (int) minCorner.getY(), (int) minCorner.getZ()), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.create.failed"));
		return 0;
		
	}
	
	public static int assembleContraption(CommandContext<CommandSourceStack> source, BlockPos startPos, float scale) {
		
		Optional<List<BlockPos>> structureBlocks = StructureFinder.findStructure(source.getSource().getLevel(), startPos, 16 * 16 * 16, PhysicUtility::isValidContraptionBlock);
		
		if (structureBlocks.isEmpty()) {
			
			source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.assemble.toLarge", 16 * 16 * 16));
			return 0;
			
		}
		
		boolean success = PhysicUtility.assembleToContraption(source.getSource().getLevel(), structureBlocks.get(), true, scale);
		
		if (success) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.assemble.success", startPos.getX(), startPos.getY(), startPos.getZ(), startPos), true);
			return Command.SINGLE_SUCCESS;
		}
		
		source.getSource().sendFailure(Component.translatable("industriacore.commands.contraption.assemble.failed"));
		return 0;
		
	}
	
	public static int removeContraption(CommandContext<CommandSourceStack> source, Collection<Contraption> contraptions) {
		int removed = 0;
		for (Contraption contraption : contraptions) {
			if (PhysicUtility.removeContraption(source.getSource().getLevel(), contraption.getContraption())) removed++;
		}
		
		if (contraptions.size() == 1) {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.remove.success", contraptions.iterator().next().getDisplayString()), true);
		} else {
			source.getSource().sendSuccess(() -> Component.translatable("industriacore.commands.contraption.remove.success_multiple", contraptions.size()), true);
		}
		
		return removed;
	}
	
}

