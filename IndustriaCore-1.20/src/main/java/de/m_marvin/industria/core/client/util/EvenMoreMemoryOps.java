package de.m_marvin.industria.core.client.util;

import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4ic;
import org.lwjgl.system.MemoryUtil;

public class EvenMoreMemoryOps {
	
	public static void memPutFloatVec2Arr(long ptr, Vector2fc[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 2 + 0, vecArr[i].x());
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 2 + 4, vecArr[i].y());
		}
	}

	public static void memPutFloatVec3Arr(long ptr, Vector3fc[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 3 + 0, vecArr[i].x());
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 3 + 4, vecArr[i].y());
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 3 + 8, vecArr[i].z());
		}
	}

	public static void memPutFloatVec4Arr(long ptr, Vector4fc[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 4 + 0, vecArr[i].x());
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 4 + 4, vecArr[i].y());
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 4 + 8, vecArr[i].z());
			MemoryUtil.memPutFloat(ptr + i * Float.BYTES * 4 + 12, vecArr[i].w());
		}
	}

	public static void memPutIntVec2Arr(long ptr, Vector2ic[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 2 + 0, vecArr[i].x());
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 2 + 4, vecArr[i].y());
		}
	}

	public static void memPutIntVec3Arr(long ptr, Vector3ic[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 3 + 0, vecArr[i].x());
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 3 + 4, vecArr[i].y());
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 3 + 8, vecArr[i].z());
		}
	}

	public static void memPutIntVec4Arr(long ptr, Vector4ic[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 4 + 0, vecArr[i].x());
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 4 + 4, vecArr[i].y());
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 4 + 8, vecArr[i].z());
			MemoryUtil.memPutInt(ptr + i * Integer.BYTES * 4 + 12, vecArr[i].w());
		}
	}
	
	public static void memPutShortVec2Arr(long ptr, Vector2ic[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 2 + 0, (short) vecArr[i].x());
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 2 + 2, (short) vecArr[i].y());
		}
	}

	public static void memPutShortVec3Arr(long ptr, Vector3ic[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 3 + 0, (short) vecArr[i].x());
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 3 + 2, (short) vecArr[i].y());
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 3 + 4, (short) vecArr[i].z());
		}
	}

	public static void memPutShortVec4Arr(long ptr, Vector4ic[] vecArr) {
		for (int i = 0; i < vecArr.length; i++) {
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 4 + 0, (short) vecArr[i].x());
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 4 + 2, (short) vecArr[i].y());
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 4 + 4, (short) vecArr[i].z());
			MemoryUtil.memPutShort(ptr + i * Short.BYTES * 4 + 6, (short) vecArr[i].w());
		}
	}
	
}
