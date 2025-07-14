package de.m_marvin.industria.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ConditionalExecutor implements Executor {
	
	private Map<Supplier<Boolean>, Runnable> taskQueue = new HashMap<>();
	private long tickcounter = 0L;
	
	public ConditionalExecutor() {}

	@Override
	public void execute(Runnable command) {
		this.taskQueue.put(() -> true, command);
	}
	
	public void executeAfterDelay(Runnable task, int delay) {
		long startTimeStamp = this.tickcounter;
		executeAsSoonAs(task, () -> { 
			return this.tickcounter - startTimeStamp >= delay;
		});
	}
	
	public void executeAsSoonAsAfterDelay(Runnable task, Supplier<Boolean> condition, int delay) {
		long startTimeStamp = this.tickcounter;
		executeAsSoonAs(task, () -> { 
			return this.tickcounter - startTimeStamp >= delay && condition.get();
		});
	}
	
	public void executeAsSoonAs(Runnable task, Supplier<Boolean> condition) {
		this.taskQueue.put(condition, task);
	}
	
	public void tryExecuteFromQueue() {
		Iterator<Supplier<Boolean>> condItr = this.taskQueue.keySet().iterator();
		while (condItr.hasNext()) {
			Supplier<Boolean> condition = condItr.next();
			try {
				if (condition.get()) {
					this.taskQueue.get(condition).run();
					condItr.remove();
				}
			} catch (Exception e) {
				IndustriaCore.LOGGER.warn("Task on conditial execution queue threw an exception: ", e);
				condItr.remove();
			}
		}
		tickcounter++;
	}
	
	public void clearQueue() {
		this.taskQueue.clear();
	}
	
	public long getTickcounter() {
		return tickcounter;
	}
	
	public static final ConditionalExecutor SERVER_TICK_EXECUTOR = new ConditionalExecutor();
	public static final ConditionalExecutor CLIENT_TICK_EXECUTOR = new ConditionalExecutor();
	
	
	
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) SERVER_TICK_EXECUTOR.tryExecuteFromQueue();
	}
	
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) CLIENT_TICK_EXECUTOR.tryExecuteFromQueue();
	}
	
}
