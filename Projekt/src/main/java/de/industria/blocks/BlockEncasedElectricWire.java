package de.industria.blocks;

import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
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
import net.minecraftforge.common.ToolType;

@SuppressWarnings("deprecation")
public class BlockEncasedElectricWire extends BlockElectricWire implements IBAdvancedStickyBlock, ITileEntityProvider {
	
	protected BlockStructureScaffold scaffoldBlock;
	
	public BlockEncasedElectricWire(String name, int maximumPower, int size, BlockStructureScaffold scaffoldBlock) {
		super(name, maximumPower, size);
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
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return this.scaffoldBlock.newBlockEntity(worldIn);
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
	public ToolType getHarvestTool(BlockState state) {
		return this.scaffoldBlock.getHarvestTool(scaffoldBlock.defaultBlockState());
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		return this.scaffoldBlock.addBlocksToMove(pistonStructureHelper, pos, state, world);
	}
	
	@Override
	public void playerDestroy(World world, PlayerEntity palyer, BlockPos pos, BlockState state, TileEntity tileEntity, ItemStack stack) {
		CompoundNBT nbt = tileEntity.serializeNBT();
		world.setBlockAndUpdate(pos, this.scaffoldBlock.defaultBlockState());
		world.getBlockEntity(pos).deserializeNBT(nbt);
		super.playerDestroy(world, palyer, pos, state, tileEntity, stack);
	}
	
	@Override
	public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
		return new ItemStack(Item.byBlock(this.scaffoldBlock));
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		if (state.getBlock() != ModItems.encased_electric_burned_cable) super.onNetworkChanges(worldIn, pos, state, network);
	}
	
}
