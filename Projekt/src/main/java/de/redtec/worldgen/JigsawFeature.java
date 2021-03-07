package de.redtec.worldgen;

import java.util.Random;

import com.mojang.serialization.Codec;

import de.redtec.tileentity.TileEntityJigsaw;
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
	
	public boolean func_241855_a(ISeedReader seedReader, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, JigsawFeatureConfig config) {
		
		BlockState state = seedReader.getBlockState(pos);
		
		if (config.target.test(state, rand)) {
			
			seedReader.setBlockState(pos, config.jigsawState, 2);
			TileEntity tileEntity = seedReader.getTileEntity(pos);
			
			if (tileEntity instanceof TileEntityJigsaw) {
				
				TileEntityJigsaw jigsaw = (TileEntityJigsaw) tileEntity;
				jigsaw.deserializeNBT(config.jigsawData);
				jigsaw.setWorldAndPos(seedReader.getWorld(), pos);
				
				int generationLevels = config.levelsMin == config.levelsMax ? config.levelsMin : rand.nextInt(config.levelsMax - config.levelsMin) + config.levelsMin;
				
				jigsaw.setWaitForGenerate(generationLevels, rand);
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
}
