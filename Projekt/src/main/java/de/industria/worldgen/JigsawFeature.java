package de.industria.worldgen;

import java.util.Random;

import com.mojang.serialization.Codec;

import de.industria.tileentity.TileEntityJigsaw;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class JigsawFeature extends Feature<JigsawFeatureConfig> {
	
	public JigsawFeature(Codec<JigsawFeatureConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, JigsawFeatureConfig config) {
		
		BlockState state = reader.getBlockState(pos);
		
		if (config.target.test(state, rand)) {
			
			reader.setBlock(pos, config.jigsawState, 2);
			TileEntity tileEntity = reader.getBlockEntity(pos);
			
			if (tileEntity instanceof TileEntityJigsaw) {
				
				TileEntityJigsaw jigsaw = (TileEntityJigsaw) tileEntity;
				jigsaw.deserializeNBT(config.jigsawData);
				jigsaw.setLevelAndPosition(reader.getLevel(), pos);
				
				int generationLevels = config.levelsMin == config.levelsMax ? config.levelsMin : rand.nextInt(config.levelsMax - config.levelsMin) + config.levelsMin;
				
				jigsaw.setWaitForGenerate(generationLevels, rand);
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
}
