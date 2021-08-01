package de.industria.items;

import javax.annotation.Nullable;

import de.industria.Industria;
import de.industria.fluids.util.ItemFluidBucket;
import de.industria.fluids.util.ItemGasBucket;
import de.industria.renderer.BlockFluidCannisterItemRenderer;
import de.industria.tileentity.TileEntityFluidCannister;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class ItemFluidCannister extends BlockItem {
	
	public ItemFluidCannister(Block block) {
		super(block, new Properties().stacksTo(1).tab(Industria.TOOLS).setISTER(() -> BlockFluidCannisterItemRenderer::new));
		this.setRegistryName(new ResourceLocation(Industria.MODID, "cannister"));
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		if (context.getPlayer().isShiftKeyDown()) {
			return super.useOn(context);
		} else {
			return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
		}
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		
		ItemStack cannisterStack = player.getItemInHand(hand);
		FluidStack content = getContent(cannisterStack);
		RayTraceResult raytraceResultFill = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
		RayTraceResult raytraceResultEmpty = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
		
		// Check for interacting with blocks
		ActionResult<ItemStack> blockInteractReturn = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, cannisterStack, raytraceResultEmpty); // EmptyRaytrace -> Blocks have priority for filling cannister with sourceblocks
		if (blockInteractReturn != null) return blockInteractReturn;
		
		// Try to fill the cannister
		// Check if the click has blocked or missed and the cannister can contain more fluid
		if (raytraceResultFill.getType() == Type.MISS) {
			return ActionResult.pass(cannisterStack);
		} else if (raytraceResultFill.getType() != Type.BLOCK) {
			return ActionResult.pass(cannisterStack);
		} else if (TileEntityFluidCannister.MAX_CONTENT - content.getAmount() >= 1000) {
			
			BlockRayTraceResult blockRaytraceResult = (BlockRayTraceResult) raytraceResultFill;
			BlockPos blockpos = blockRaytraceResult.getBlockPos();
			Direction direction = blockRaytraceResult.getDirection();
			BlockPos blockpos1 = blockpos.relative(direction);
			
			if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, cannisterStack)) {
				BlockState state1 = world.getBlockState(blockpos);
				if (state1.getBlock() instanceof IBucketPickupHandler) {
					
					Fluid fluid = state1.getFluidState().getType();
					if (fluid != Fluids.EMPTY && (fluid == content.getFluid() || content.isEmpty())) {
						
						fluid = ((IBucketPickupHandler)state1.getBlock()).takeLiquid(world, blockpos, state1);
						
						if (content.isEmpty()) {
							content = new FluidStack(fluid, 1000);
						} else {
							content.grow(1000);
						}
						setContent(cannisterStack, content);
						
						SoundEvent fillSound = content.getFluid().getAttributes().getFillSound();
						if (fillSound == null) fillSound = SoundEvents.BUCKET_FILL;
						player.playSound(fillSound, 1.0F, 1.0F);
						
						return ActionResult.sidedSuccess(cannisterStack, world.isClientSide);
						
					}
				}
			}
			
		}
		
		// Try to empty cannister
		// Check if the raytrace has blocked or missed and the cannister contains fluid
		if (raytraceResultEmpty.getType() == Type.MISS) {
			return ActionResult.pass(cannisterStack);
		} else if (raytraceResultEmpty.getType() != Type.BLOCK) {
			return ActionResult.pass(cannisterStack);
		} else if (content.getAmount() >= 1000) {

			BlockRayTraceResult blockRaytraceResult = (BlockRayTraceResult) raytraceResultEmpty;
			BlockPos blockpos = blockRaytraceResult.getBlockPos();
			Direction direction = blockRaytraceResult.getDirection();
			BlockPos blockpos1 = blockpos.relative(direction);
			
			BlockState blockstate = world.getBlockState(blockpos);
			boolean canBlockContain = blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)blockstate.getBlock()).canPlaceLiquid(world, blockpos, blockstate, content.getFluid());
			BlockPos blockpos2 = canBlockContain ? blockpos : blockpos1;
			
			if (this.emptyBucket(player, world, blockpos2, blockRaytraceResult, content.getFluid())) {
				
				content.shrink(1000);
				setContent(cannisterStack, content);
				
				SoundEvent soundevent = content.getFluid().getAttributes().getEmptySound();
				if(soundevent == null) soundevent = SoundEvents.BUCKET_EMPTY;
				world.playSound(player, blockpos2, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
				
				return ActionResult.sidedSuccess(cannisterStack, world.isClientSide);
				
			}
				
		}
		
		return ActionResult.fail(cannisterStack);
		
	}
	
	/**
	 * Modified vanilla bucket behavior
	 */
	@SuppressWarnings("deprecation")
	public boolean emptyBucket(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockRayTraceResult raytraceResult, Fluid content) {
		
		BlockState blockstate = world.getBlockState(pos);
		Block block = blockstate.getBlock();
		Material material = blockstate.getMaterial();
		boolean flag = blockstate.canBeReplaced(content);
		boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(world, pos, blockstate, content);
		
		if (!flag1) {
			return raytraceResult != null && this.emptyBucket(player, world, raytraceResult.getBlockPos().relative(raytraceResult.getDirection()), (BlockRayTraceResult)null, content);
		} else if (world.dimensionType().ultraWarm() && !canPlaceInNether(content)) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
	
			for(int l = 0; l < 8; ++l) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
			}
	
			return true;
		} else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(world,pos,blockstate,content)) {
			((ILiquidContainer)block).placeLiquid(world, pos, blockstate, ((FlowingFluid)content).getSource(false));
			return true;
		} else {
			if (!world.isClientSide && flag && !material.isLiquid()) {
					world.destroyBlock(pos, true);
			}
	
			if (!world.setBlock(pos, content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	protected boolean canPlaceInNether(Fluid content) {
		Item bucketItem = content.getBucket();
		if (bucketItem instanceof ItemFluidBucket) {
			return ((ItemFluidBucket) bucketItem).canPlaceInNether;
		} else if (bucketItem instanceof ItemGasBucket) {
			return true;
		} else {
			return !content.is(FluidTags.WATER);
		}
	}
	
	public FluidStack getContent(ItemStack cannisterStack) {
		if (cannisterStack.getItem() == this && cannisterStack.hasTag()) {
			CompoundNBT tag = cannisterStack.getTag();
			if (tag.contains("BlockEntityTag")) {
				CompoundNBT nbt = tag.getCompound("BlockEntityTag");
				if (nbt.contains("Fluid")) {
					return FluidStack.loadFluidStackFromNBT(nbt.getCompound("Fluid"));
				}
			}
		}
		return FluidStack.EMPTY;
	}
	
	public void setContent(ItemStack cannisterStack, FluidStack content) {
		if (cannisterStack.getItem() == this) {
			CompoundNBT tag = cannisterStack.getOrCreateTag();
			CompoundNBT nbt = tag.contains("BlockEntityTag") ? tag.getCompound("BlockEntityTag") : new CompoundNBT();
			nbt.put("Fluid", content.writeToNBT(new CompoundNBT()));
			tag.put("BlockEntityTag", nbt);
			cannisterStack.setTag(tag);
		}
	}
		
}
