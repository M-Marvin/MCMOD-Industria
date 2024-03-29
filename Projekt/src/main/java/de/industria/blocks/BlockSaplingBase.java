package de.industria.blocks;

import java.util.Random;

import de.industria.Industria;
import de.industria.tileentity.TileEntityJigsaw;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class BlockSaplingBase extends SaplingBlock {
	
	public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	
	private ResourceLocation jigsawPool;
	private ResourceLocation jigsawConnectionName;
	
	public BlockSaplingBase(String name, ResourceLocation jigsawPool) {
		super(null, Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
		this.jigsawPool = jigsawPool;
		this.jigsawConnectionName = new ResourceLocation(Industria.MODID, "tree_log");
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, Integer.valueOf(0)));
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
		if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
			this.growTree(worldIn, pos, state, random);
		}

	}
	
	public void growTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
		if (state.getValue(STAGE) == 0) {
			world.setBlock(pos, state.cycle(STAGE), 4);
		} else {
			if (!net.minecraftforge.event.ForgeEventFactory.saplingGrowTree(world, rand, pos)) return;
			
			BlockState jigsawReplace = world.getBlockState(pos.below());
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			world.setBlockAndUpdate(pos.below(), ModItems.jigsaw.defaultBlockState().setValue(BlockJigsaw.TYPE, BlockJigsaw.JigsawType.VERTICAL_UP));
				
			TileEntity tileEntity = world.getBlockEntity(pos.below());
			
			if (tileEntity instanceof TileEntityJigsaw) {
				
				TileEntityJigsaw jigsaw = (TileEntityJigsaw) tileEntity;
				jigsaw.replaceState = jigsawReplace.getBlock().defaultBlockState();
				jigsaw.targetName = this.jigsawConnectionName;
				jigsaw.poolFile = this.jigsawPool;
				jigsaw.lockOrientation = false;
				jigsaw.generateStructure(false, 1, rand);
				
			}
			
		}

	}
	
	public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		this.growTree(worldIn, pos, state, rand);
	}
}
