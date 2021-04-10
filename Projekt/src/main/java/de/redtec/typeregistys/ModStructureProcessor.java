package de.redtec.typeregistys;

import com.mojang.serialization.Codec;

import de.redtec.RedTec;
import de.redtec.structureprocessor.JigsawTemplateProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;

public class ModStructureProcessor {
	
	public static final IStructureProcessorType<JigsawTemplateProcessor> JIGSAW_TEMPLATE = register("jigsaw_template", JigsawTemplateProcessor.CODEC);
	
	static <P extends StructureProcessor> IStructureProcessorType<P> register(String name, Codec<P> codec) {
		return Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(RedTec.MODID, name), () -> {
			return codec;
		});
	}
}
