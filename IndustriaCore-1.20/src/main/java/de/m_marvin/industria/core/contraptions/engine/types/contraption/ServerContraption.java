package de.m_marvin.industria.core.contraptions.engine.types.contraption;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.contraptions.engine.types.attachment.ContraptionAttachment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class ServerContraption extends Contraption {
	
	protected ServerShip ship;
	protected MinecraftServer server;
	
	public ServerContraption(MinecraftServer server, ServerShip ship) {
		this.ship = ship;
		this.server = server;
	}
	
	@Override
	public ServerShip getShip() {
		return this.ship;
	}
	
	@Override
	public ServerLevel getLevel() {
		return this.server.getLevel(ResourceKey.create(Registries.DIMENSION, this.getPosition().getDimension()));
	}
	
	public void setTags(Set<String> tags) {
		getHandler().getContraptionTags().put(getId(), tags);
	}
	
	public void addTag(String tag) {
		getTags().add(tag);
	}
	
	public void removeTag(String tag) {
		getTags().remove(tag);
	}
	
	public void setName(String name) {
		getShip().setSlug(name);
	}
	
	public <T> T getAttachment(Class<T> attachmentClass) {
		ServerShipWorld shipWorld = VSGameUtilsKt.getShipObjectWorld(getLevel());
		Optional<LoadedServerShip> loadedShip = shipWorld.getLoadedShips().stream().filter(s -> s.getId() == getId()).findAny();
		if (loadedShip.isEmpty()) {
			IndustriaCore.LOGGER.warn("unable to get LoadedServerShip instance of contraption " + getId());
			return null;
		}
		return loadedShip.get().getAttachment(attachmentClass);
	}
	
	protected <T> void saveAttachment(Class<T> clazz, T value) {
		ServerShipWorld shipWorld = VSGameUtilsKt.getShipObjectWorld(getLevel());
		Optional<LoadedServerShip> loadedShip = shipWorld.getLoadedShips().stream().filter(s -> s.getId() == getId()).findAny();
		if (loadedShip.isEmpty()) {
			IndustriaCore.LOGGER.warn("unable to get LoadedServerShip instance of contraption " + getId());
			return;
		}
		loadedShip.get().setAttachment(value);
	}
	
	public <T extends ContraptionAttachment> void removeAttachment(Class<T> attachmentClass) {
		saveAttachment(attachmentClass, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void setAttachment(T attachment) {
		if (attachment instanceof ContraptionAttachment att) att.setContraption(this);
		saveAttachment((Class<T>) attachment.getClass(), attachment);
	}
	
	public <T> T getOrCreateAttachment(Class<T> attachmentClass) {
		T attachment = getAttachment(attachmentClass);
		if (attachment == null) {
			try {
				attachment = attachmentClass.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				IndustriaCore.LOGGER.warn("could not instantiate contraption attachment of type %s", attachmentClass.getSimpleName(), e);
				return null;
			}
			setAttachment(attachment);
		}
		return attachment;
	}

	public double getMass() {
		return getShip().getInertiaData().getMass();
	}

	public boolean isStatic() {
		return getShip().isStatic();
	}

	public void setStatic(boolean isStatic) {
		getShip().setStatic(isStatic);
	}

	public void setNameStr(String name) {
		getShip().setSlug(name);
	}

	public void getTags(String tag) {
		Set<String> tags = getHandler().getContraptionTags().get(getId());
		if (tags == null) {
			tags = new HashSet<>();
			getHandler().getContraptionTags().put(getId(), tags);
		}
		tags.add(tag);
	}

//	public boolean canSplit() {
//		return getAttachment(SplittingDisablerAttachment.class).canSplit();
//	}
//	
//	public void setCanSplit(boolean canSplit) {
//		if (canSplit) {
//			getAttachment(SplittingDisablerAttachment.class).enableSplitting();
//		} else {
//			getAttachment(SplittingDisablerAttachment.class).disableSplitting();
//		}
//		this.lastSplitState = canSplit;
//	}
//	
//	/* Helper method to temporarily disable splitting without overriding the inteded state */
//	
//	private boolean lastSplitState;
//	
//	public void tempDisableSplit() {
//		boolean split = canSplit();
//		setCanSplit(false);
//		this.lastSplitState = split;
//	}
//	
//	public void tempRestoreSplit() {
//		setCanSplit(this.lastSplitState);
//	}
	
}
