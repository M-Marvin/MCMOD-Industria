package de.m_marvin.industria.content.magnetism.engine;

import java.util.HashMap;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.magnetism.types.MagneticField;
import de.m_marvin.industria.content.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetismHandlerCapability implements ICapabilitySerializable<ListTag> {

	/* Capability handling */
	
	private LazyOptional<MagnetismHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == de.m_marvin.industria.content.magnetism.engine.Capabilities.MAGNETISM_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	protected final Level level;
	protected final HashMap<BlockPos, MagneticFieldInfluence> pos2influenceMap = new HashMap<>();
	protected final HashMap<MagneticFieldInfluence, MagneticField> influence2fieldMap = new HashMap<>();
	protected final HashSet<MagneticField> fields = new HashSet<>();
	
	@Override
	public ListTag serializeNBT() {
		// TODO Auto-generated method stub
		return new ListTag();
	}

	@Override
	public void deserializeNBT(ListTag nbt) {
		// TODO Auto-generated method stub
		
	}
	
	public MagnetismHandlerCapability(Level level) {
		this.level = level;
	}

	/* Event handling */
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, de.m_marvin.industria.content.magnetism.engine.Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		
		
		
//		if (event.getState().getBlock() instanceof IElectricBlock) {
//			if (handler.isInNetwork(event.getPos())) {
//				if (handler.getComponentAt(event.getPos()).instance(level).equals(event.getState())) return; // No real update, ignore
//				handler.getComponentAt(event.getPos()).setChanged();
//			} else {
//				IElectricBlock block = (IElectricBlock) event.getState().getBlock();
//				handler.addComponent(event.getPos(), block, event.getState());
//			}
//		} else {
//			handler.removeComponent(event.getPos(), event.getState());
//		}
	}
	
}
