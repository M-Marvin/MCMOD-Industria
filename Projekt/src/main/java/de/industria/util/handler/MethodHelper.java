package de.industria.util.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.industria.Industria;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;

public class MethodHelper {
	
	public static void addFireSetting(Block block, int encouragement, int flammability) {
		try {
			FireBlock fireBlock = (FireBlock) Blocks.FIRE;
			Method fireInfoMethod = FireBlock.class.getDeclaredMethod("m_53444_", Block.class, int.class, int.class);
			fireInfoMethod.setAccessible(true);
			fireInfoMethod.invoke(fireBlock, block, encouragement, flammability);
		} catch (NoSuchMethodException | SecurityException e) {
			Industria.LOGGER.error("Error on call setFireInfo on FIRE block!");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Industria.LOGGER.error("Error on call setFireInfo on FIRE block!");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Industria.LOGGER.error("Error on call setFireInfo on FIRE block!");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Industria.LOGGER.error("Error on call setFireInfo on FIRE block!");
			e.printStackTrace();
		}
	}
	
}
