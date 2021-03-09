package de.redtec.blocks;

import de.redtec.tileentity.TileEntityJigsaw;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockJigsaw extends BlockContainerBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<JigsawType> TYPE = EnumProperty.create("type", JigsawType.class);
	
	public BlockJigsaw() {
		super("jigsaw", Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(-1.0F, 3600000.0F).noDrops());
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, TYPE);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityJigsaw();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		if (context.getFace() == Direction.DOWN) {
			return this.getDefaultState().with(TYPE, JigsawType.VERTICAL_DOWN).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
		} else if (context.getFace() == Direction.UP) {
			return this.getDefaultState().with(TYPE, JigsawType.VERTICAL_UP).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
		} else {
			return this.getDefaultState().with(TYPE, JigsawType.HORIZONTAL).with(FACING, context.getFace());
		}
		
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof TileEntityJigsaw) {
			
			if (!worldIn.isRemote()) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, (buf) -> {
					buf.writeBlockPos(pos);
					buf.writeCompoundTag(tileEntity.serializeNBT());
				});
			}
			return ActionResultType.CONSUME;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof TileEntityJigsaw) {
			
			((TileEntityJigsaw) tileEntity).onNeighborChange();
			
		}
		
		
	}
	
	public static enum JigsawType implements IStringSerializable {
		
		HORIZONTAL("horizontal"),VERTICAL_UP("vertical_up"),VERTICAL_DOWN("vertical_down");
		
		private String name;
		
		private JigsawType(String name) {
			this.name = name;
		}
		
		@Override
		public String getString() {
			return name;
		}
		
		public JigsawType getOppesite() {
			if (this == HORIZONTAL) {
				return HORIZONTAL;
			} else if (this == VERTICAL_DOWN) {
				return VERTICAL_UP;
			} else {
				return VERTICAL_DOWN;
			}
		}
		
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}
	
}
