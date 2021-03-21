package de.redtec.blocks;

import java.util.Random;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityJigsaw;
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
	
	public static final IntegerProperty STAGE = BlockStateProperties.STAGE_0_1;
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	
	private ResourceLocation jigsawPool;
	private ResourceLocation jigsawConnectionName;
	
	public BlockSaplingBase(String name, ResourceLocation jigsawPool) {
		super(null, Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT));
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
		this.jigsawPool = jigsawPool;
		this.jigsawConnectionName = new ResourceLocation(RedTec.MODID, "tree_log");
		this.setDefaultState(this.stateContainer.getBaseState().with(STAGE, Integer.valueOf(0)));
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (worldIn.getLight(pos.up()) >= 9 && random.nextInt(7) == 0) {
		if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
			this.growTree(worldIn, pos, state, random);
		}

	}
	
	public void growTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
		if (state.get(STAGE) == 0) {
			world.setBlockState(pos, state.func_235896_a_(STAGE), 4);
		} else {
			if (!net.minecraftforge.event.ForgeEventFactory.saplingGrowTree(world, rand, pos)) return;
			
			BlockState jigsawReplace = world.getBlockState(pos.down());
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			world.setBlockState(pos.down(), RedTec.jigsaw.getDefaultState().with(BlockJigsaw.TYPE, BlockJigsaw.JigsawType.VERTICAL_UP));
				
			TileEntity tileEntity = world.getTileEntity(pos.down());
			
			if (tileEntity instanceof TileEntityJigsaw) {
				
				TileEntityJigsaw jigsaw = (TileEntityJigsaw) tileEntity;
				jigsaw.replaceState = jigsawReplace.getBlock().getRegistryName();
				jigsaw.targetName = this.jigsawConnectionName;
				jigsaw.poolFile = this.jigsawPool;
				jigsaw.lockOrientation = false;
				jigsaw.generateStructure(false, 1, rand);
				
			}
			
		}

	}
	
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		this.growTree(worldIn, pos, state, rand);
	}
}
