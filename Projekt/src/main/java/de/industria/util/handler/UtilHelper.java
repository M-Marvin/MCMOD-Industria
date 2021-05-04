package de.industria.util.handler;

import java.lang.reflect.Array;
import java.util.Random;

public class UtilHelper {
	
	@SuppressWarnings("unchecked")
	public static <E> E[] sortRandom(Class<E> clazz, E[] array, Random rand) {
		E[] randArr = (E[]) Array.newInstance(clazz, array.length);
		
		for (E entry : array) {
			int randOffset = rand.nextInt(array.length - 1);
			
			boolean flag = false;
			while (randArr[randOffset] != null) {
				if (flag && randOffset == array.length - 1) {
					throw new RuntimeException("Error on randomize genric Array!");
				}
				if (randOffset == array.length - 1) flag = true;
				randOffset++;
				randOffset %= array.length;
			}
			
			randArr[randOffset] = entry;
		}
		
		return randArr;
	}
	
}
