package de.industria.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import de.industria.ModItems;
import de.industria.blocks.BlockJigsaw;
import de.industria.tileentity.TileEntityJigsaw;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

public class JigsawFeatureConfig implements IFeatureConfig {

	public static final Codec<JigsawFeatureConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(RuleTest.field_237127_c_.fieldOf("target").forGetter((config) -> {
			return config.target;
		}), BlockState.CODEC.fieldOf("jigsawState").forGetter((config) ->  {
			return config.jigsawState;
		}), CompoundNBT.CODEC.fieldOf("jigsawData").forGetter((config) -> {
			return config.jigsawData;
		}), Codec.INT.fieldOf("levelsMin").forGetter((config) -> {
			return config.levelsMin;
		}), Codec.INT.fieldOf("levelsMax").forGetter((config) -> {
			return config.levelsMax;
		})).apply(codec, JigsawFeatureConfig::new);
	});
	
	public final RuleTest target;
	public final CompoundNBT jigsawData;
	public final BlockState jigsawState;
	public int levelsMin;
	public int levelsMax;
	
	public JigsawFeatureConfig(RuleTest target, BlockState jigsawState, CompoundNBT jigsawData, int levelsMin, int levelsMax) {
		this.target = target;
		this.jigsawData = jigsawData;
		this.jigsawState = jigsawState;
		this.levelsMin = levelsMin;
		this.levelsMax = levelsMax;
	}
	
	public JigsawFeatureConfig(RuleTest target, Direction horizontalDirection, BlockJigsaw.JigsawType verticalDirection, ResourceLocation structurePool, ResourceLocation connectionName, BlockState replaceState, boolean horizontalLocked, int generationLevelsMin, int generationLevelsMax) {
		
		TileEntityJigsaw tileEntity = new TileEntityJigsaw();
		tileEntity.replaceState = replaceState;
		tileEntity.lockOrientation = horizontalLocked;
		tileEntity.poolFile = structurePool;
		tileEntity.targetName = connectionName;
		this.jigsawData = tileEntity.serializeNBT();
		this.jigsawState = ModItems.jigsaw.getDefaultState().with(BlockJigsaw.FACING, horizontalDirection).with(BlockJigsaw.TYPE, verticalDirection);
		this.levelsMax = generationLevelsMax;
		this.levelsMin = generationLevelsMin;
		this.target = target;
		
	}
	
}
