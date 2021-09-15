package de.industria.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.items.ItemStructureCladdingPane;
import de.industria.tileentity.TileEntityStructureScaffold;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockEnergyBarrierBorder extends BlockContainerBase implements IBAdvancedBlockInfo {
	
	public static final int MAX_BUILD_DISTANCE = 100;
	public static final float BUILD_DESTROY_FORCE = 8F;
	
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	
	public BlockEnergyBarrierBorder() {
		this("energy_barrier_border");
	}
		
	public BlockEnergyBarrierBorder(String name) {
		super(name, Material.HEAVY_METAL, 4F, 20F, SoundType.ANVIL);
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}
	
	@Override
	public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
		return state.getValue(ACTIVE) ? 1000 : super.getExplosionResistance(state, world, pos, explosion);
	}
	
	public void buildEnergyBarrier(BlockState state, World world, BlockPos pos, boolean active) {
		for (Direction d : Direction.values()) {
			BlockPos sidePos = pos.relative(d);
			BlockState sideState = world.getBlockState(sidePos);
			if (sideState.getBlock() instanceof BlockEnergyBarrierBorder) {
				if (sideState.getValue(ACTIVE) != active) {
					world.setBlockAndUpdate(sidePos, sideState.setValue(ACTIVE, active));
					world.getBlockTicks().scheduleTick(sidePos, sideState.getBlock(), 2);
				}
			} else {
				buildFieldOnSide(state, world, sidePos, d, active);
			}
		}
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		boolean active = state.getValue(ACTIVE);
		buildEnergyBarrier(state, world, pos, active);
	}
	
	@SuppressWarnings("deprecation")
	protected void buildFieldOnSide(BlockState state, World world, BlockPos pos, Direction side, boolean active) {
		HashMap<BlockPos, Axis> buildPositions = new HashMap<BlockPos, Axis>();
		for (int i = 0; i < MAX_BUILD_DISTANCE; i++) {
			BlockPos buildPos = pos.relative(side, i);
			BlockState replaceState = world.getBlockState(buildPos);
			if (!active && replaceState.getBlock() == ModItems.energy_barrier) {
				buildPositions.put(buildPos, side.getAxis());
			} else if (replaceState.getBlock() instanceof BlockEnergyBarrierBorder) {
				world.setBlockAndUpdate(buildPos, replaceState.setValue(ACTIVE, active));
				break;
			} else if (active && replaceState.getBlock().getExplosionResistance(replaceState, world, buildPos, null) <= BUILD_DESTROY_FORCE && replaceState.getBlock() != ModItems.energy_barrier) {
				buildPositions.put(buildPos, side.getAxis());
			} else if (active) {
				return;
			} else if (!active && replaceState.isAir()) {
				break;
			}
			if (i == MAX_BUILD_DISTANCE - 1) return; 
		}
		buildPositions.entrySet().forEach((entry) -> {
			BlockPos position = entry.getKey();
			Axis axis = entry.getValue();
			if (active) {
				world.destroyBlock(position, true);
				world.setBlockAndUpdate(position, ModItems.energy_barrier.createEnergyField(axis, getConnectedSides(world, pos.relative(side, -1), axis)));
			} else {
				world.destroyBlock(position, false);
			}
		});
	}
	
	protected Direction[] getConnectedSides(World world, BlockPos pos, Axis axis) {
		List<Direction> directions = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			if (d.getAxis() != axis) {
				if (world.getBlockState(pos.relative(d)).getBlock() instanceof BlockEnergyBarrierBorder) directions.add(d);
			}
		}
		return directions.toArray(new Direction[directions.size()]);
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		buildEnergyBarrier(state, world, pos, false);
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		if (handIn == Hand.MAIN_HAND) {
			
			ItemStack stack = player.getMainHandItem();
			
			if (stack.getItem() == ModItems.structure_cladding_pane) {
				
				if (!worldIn.isClientSide()) {
					
					TileEntity tileEntity = worldIn.getBlockEntity(pos);
					
					if (tileEntity instanceof TileEntityStructureScaffold) {
						
						Direction side = hit.getDirection();
						if (((TileEntityStructureScaffold) tileEntity).setCladding(side, stack)) {
							if (!player.isCreative()) stack.shrink(1);
							
							SoundEvent placeSound = ItemStructureCladdingPane.getBlockState(stack).getSoundType().getPlaceSound();
							worldIn.playSound(null, pos, placeSound, SoundCategory.BLOCKS, 1, 1);
							
						}
						
					}
					
				}
				
				return ActionResultType.CONSUME;
				
			} else if (stack.getItem().getToolTypes(stack).contains(ModToolType.WRENCH)) {

				if (!worldIn.isClientSide()) {
					
					TileEntity tileEntity = worldIn.getBlockEntity(pos);
					if (tileEntity instanceof TileEntityStructureScaffold) {
						
						Direction side = hit.getDirection();
						ItemStack removedCladding = ((TileEntityStructureScaffold) tileEntity).removeCladding(side);
						
						if (!removedCladding.isEmpty()) {

							SoundEvent breakSound = ItemStructureCladdingPane.getBlockState(removedCladding).getSoundType().getBreakSound();
							worldIn.playSound(null, pos, breakSound, SoundCategory.BLOCKS, 1, 1);
							
							if (!removedCladding.isEmpty()) player.addItem(removedCladding);
							
							if (!player.isCreative()) {
								
								stack.setDamageValue(stack.getDamageValue() + 1);
								if (stack.getDamageValue() > stack.getDamageValue()) {
									player.setItemInHand(handIn, ItemStack.EMPTY);
								}
								
							}
							
						}
						
					}
					
				}
				
				return ActionResultType.CONSUME;
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntityStructureScaffold();
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.energyBarrierBorder"));
		};
	}
	
}
