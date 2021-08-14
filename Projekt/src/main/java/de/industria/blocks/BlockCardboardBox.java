package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityCardboardBox;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBBurnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockCardboardBox extends BlockContainerBase implements IBBurnableBlock, IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty PACKED = BooleanProperty.create("packed");
	
	public BlockCardboardBox() {
		super("cardboard_box", Material.WOOL, 0.2F, 2.5F, ModSoundEvents.CARDBOARD);
		this.registerDefaultState(this.stateDefinition.any().setValue(PACKED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, PACKED);
	}
	
	@Override
	public int getBurnTime() {
		return 60;
	}
	
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 400;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntityCardboardBox();
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace) {
		
		if (hand == Hand.MAIN_HAND) {
			if (state.getValue(PACKED)) {
				if (player.isShiftKeyDown()) {
					TileEntity te = world.getBlockEntity(pos);
					if (te instanceof IInventory) {
						InventoryHelper.dropContents(world, pos, (IInventory) te);
					}
					world.destroyBlock(pos, false);
					return ActionResultType.SUCCESS;
				}
				return ActionResultType.PASS;
			} else if (player.isShiftKeyDown()) {
				world.setBlock(pos, state.setValue(PACKED, true), 2);
				world.playSound(null, pos, ModSoundEvents.CARDBOARD_PLACE, SoundCategory.BLOCKS, 1F, 1F);
			} else {
				TileEntity tileEntity = world.getBlockEntity(pos);
				if (tileEntity instanceof INamedContainerProvider) {
					if (!world.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
					return ActionResultType.SUCCESS;
				}
			}
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof IInventory && !state.getValue(PACKED)) {
				InventoryHelper.dropContents(world, pos, (IInventory) te);
			}
			world.removeBlockEntity(pos);	
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return Block.box(1, 0, 1, 15, 14, 15);
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			boolean packed = false;
			if (stack.getTagElement("BlockStateTag") != null) {
				packed = new String("true").equals(stack.getTagElement("BlockStateTag").getString("packed"));
			}
			info.add(new TranslationTextComponent("industria.block.info.cardboardBox." + (packed ? "packed" : "unpacked")));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
}
