package de.industria.blocks;

import de.industria.tileentity.TileEntityEncasedFluidPipe;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockEncasedFluidPipe extends BlockFluidPipe implements IBAdvancedStickyBlock {
	
	protected BlockStructureScaffold scaffoldBlock;
	
	public BlockEncasedFluidPipe(String name, BlockStructureScaffold scaffoldBlock) {
		super(name);
		this.scaffoldBlock = scaffoldBlock;
	}
	
	public BlockStructureScaffold getScaffoldBlock() {
		return scaffoldBlock;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape wireShape = super.getShape(state, worldIn, pos, context);
		VoxelShape scaffoldShape = this.scaffoldBlock.getShape(state, worldIn, pos, context);
		return VoxelShapes.or(wireShape, scaffoldShape);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityEncasedFluidPipe();
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityEncasedFluidPipe();
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		return this.scaffoldBlock.use(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity) {
		return this.scaffoldBlock.getSoundType(state);
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		return this.scaffoldBlock.addBlocksToMove(pistonStructureHelper, pos, state, world);
	}
	
	@Override
	public void playerDestroy(World world, PlayerEntity palyer, BlockPos pos, BlockState state, TileEntity tileEntity, ItemStack stack) {
		CompoundNBT nbt = world.getBlockEntity(pos).serializeNBT();
		world.setBlockAndUpdate(pos, this.scaffoldBlock.defaultBlockState());
		world.getBlockEntity(pos).deserializeNBT(nbt);
		super.playerDestroy(world, palyer, pos, state, tileEntity, stack);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
		return new ItemStack(Item.byBlock(this.scaffoldBlock));
	}
		
}
