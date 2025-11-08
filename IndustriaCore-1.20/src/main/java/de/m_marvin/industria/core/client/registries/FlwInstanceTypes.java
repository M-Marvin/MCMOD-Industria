package de.m_marvin.industria.core.client.registries;

import org.lwjgl.system.MemoryUtil;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.flyinstances.ShiftedTextureInstance;
import de.m_marvin.industria.core.client.util.EvenMoreMemoryOps;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.api.layout.FloatRepr;
import dev.engine_room.flywheel.api.layout.IntegerRepr;
import dev.engine_room.flywheel.api.layout.LayoutBuilder;
import dev.engine_room.flywheel.lib.instance.SimpleInstanceType;
import dev.engine_room.flywheel.lib.util.ExtraMemoryOps;
import net.minecraft.resources.ResourceLocation;

public class FlwInstanceTypes {
	
	public static final InstanceType<ShiftedTextureInstance> SHIFTED_TEXTURE = SimpleInstanceType.builder(ShiftedTextureInstance::new)
			.layout(LayoutBuilder.create()
					.vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
					.vector("overlay", IntegerRepr.SHORT, 2)
					.vector("light", FloatRepr.UNSIGNED_SHORT, 2)
					.vector("position", FloatRepr.FLOAT, 3)
					.vectorArray("shifts", FloatRepr.FLOAT, 2, ShiftedTextureInstance.MAX_ANIMATED_QUADS)
					.build())
			.writer((ptr, instance) -> {
				MemoryUtil.memPutByte(ptr, instance.red);
				MemoryUtil.memPutByte(ptr + 1, instance.green);
				MemoryUtil.memPutByte(ptr + 2, instance.blue);
				MemoryUtil.memPutByte(ptr + 3, instance.alpha);
				ExtraMemoryOps.put2x16(ptr + 4, instance.overlay);
				ExtraMemoryOps.put2x16(ptr + 8, instance.light);
				MemoryUtil.memPutFloat(ptr + 12, instance.posX);
				MemoryUtil.memPutFloat(ptr + 16, instance.posY);
				MemoryUtil.memPutFloat(ptr + 20, instance.posZ);
				EvenMoreMemoryOps.memPutFloatVec2Arr(ptr + 24, instance.shifts);
			})
			.vertexShader(ResourceLocation.tryBuild(IndustriaCore.MODID, "instance/shifted_texture.vert"))
			.cullShader(ResourceLocation.tryBuild(IndustriaCore.MODID, "instance/shifted_texture_cull.glsl"))
			.build();
	
}
