package de.industria.structureprocessor;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import de.industria.typeregistys.ModStructureProcessor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class JigsawTemplateProcessor extends StructureProcessor {
	
	public static final Codec<JigsawTemplateProcessor> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(JigsawReplacement.CODEC.fieldOf("replacement").forGetter((processor) -> {
			return processor.replacement;
		}), BlockState.CODEC.listOf().fieldOf("blockList").forGetter((processor) -> {
			return processor.blockList;
		})).apply(codec, JigsawTemplateProcessor::new);
	});
	
	public final JigsawReplacement replacement;
	public final List<BlockState> blockList;
	
	public JigsawTemplateProcessor(JigsawReplacement replacement, List<BlockState> blockList) {
		this.replacement = replacement;
		this.blockList = blockList;
	}
	
	@Override
	protected IStructureProcessorType<?> getType() {
		return ModStructureProcessor.JIGSAW_TEMPLATE;
	}
	
	@Override
	public Template.BlockInfo process(IWorldReader world, BlockPos seedPos, BlockPos pos, Template.BlockInfo rawBlockInfo, Template.BlockInfo blockInfo, PlacementSettings placement, @Nullable Template template) {
		
		BlockState replaceState = world.getBlockState(blockInfo.pos);
		boolean canReplace = this.replacement.getRuleCheck().test(this.blockList, replaceState);
		
		return canReplace ? blockInfo : null;
		
	}
	
	public static enum JigsawReplacement {
		
		@SuppressWarnings("deprecation")
		NO("no", (blocks, state) -> {
			return state.isAir();
		}),
		ONLY("only", (blocks, state) -> {
			return blocks.contains(state);
		}),
		EXCEPT("except", (blocks, state) -> {
			return !blocks.contains(state);
		}),
		ALL("all", (blocks, state) -> {
			return true;
		});
		
		public static final Codec<JigsawReplacement> CODEC = RecordCodecBuilder.create((codec) -> {
			return codec.group(Codec.STRING.fieldOf("name").forGetter((replacement) -> {
				return replacement.name;
			})).apply(codec, JigsawReplacement::fromName);
		});
		
		protected String name;
		protected RuleCheck ruleCheck;
		
		private JigsawReplacement(String name, RuleCheck ruleCheck) {
			this.name = name;
			this.ruleCheck = ruleCheck;
		}
		
		public String getName() {
			return this.name;
		}
		
		public RuleCheck getRuleCheck() {
			return ruleCheck;
		}
		
		public static JigsawReplacement fromName(String name) {
			switch(name) {
			case "no": return NO;
			case "only": return ONLY;
			case "except": return EXCEPT;
			case "all": return ALL;
			}
			return ALL;
		}
		
		@FunctionalInterface
		public static interface RuleCheck {
			public boolean test(List<BlockState> blocks, BlockState state);
		}
		
	}
	
}