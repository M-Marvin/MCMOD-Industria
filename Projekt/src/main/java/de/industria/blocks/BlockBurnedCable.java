package de.industria.blocks;

import java.util.Random;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityMBurnedCable;
import de.industria.typeregistys.ModDamageSource;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockBurnedCable extends BlockElectricWire {
	
	public BlockBurnedCable() {
		super("burned_cable", 10, 4);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityMBurnedCable();
	}
	
	public static void crateBurnedCable(BlockState cableState, BlockPos pos, World worldIn) {
		
		BlockState burnedState = (cableState.getBlock() instanceof BlockEncasedElectricWire ? ModItems.encased_electric_burned_cable : ModItems.burned_cable).defaultBlockState()
				.setValue(NORTH, cableState.getValue(NORTH))
				.setValue(SOUTH, cableState.getValue(SOUTH))
				.setValue(EAST, cableState.getValue(EAST))
				.setValue(WEST, cableState.getValue(WEST))
				.setValue(UP, cableState.getValue(UP))
				.setValue(DOWN, cableState.getValue(DOWN))
				.setValue(WATERLOGGED, cableState.getValue(WATERLOGGED));
		

		if (burnedState.getBlock() != ModItems.burned_cable) {
			CompoundNBT nbt = worldIn.getBlockEntity(pos).serializeNBT();
			worldIn.setBlockAndUpdate(pos, burnedState);
			worldIn.getBlockEntity(pos).deserializeNBT(nbt);
		} else {
			worldIn.setBlockAndUpdate(pos, burnedState);
			TileEntityMBurnedCable tileEntity = (TileEntityMBurnedCable) worldIn.getBlockEntity(pos);
			tileEntity.setCableBlock(cableState.getBlock());
		}
		
	}
	
	public static Block getBurnedCableFromStack(ItemStack burnedCableStack) {
		Item item = burnedCableStack.getItem();
		if (item instanceof BlockItem ? ((BlockItem) item).getBlock() == ModItems.burned_cable : false) {
			CompoundNBT tag = burnedCableStack.getTagElement("BlockEntityTag");
			if (tag != null ? tag.contains("CableBlock") : false) {
				ResourceLocation cableName = new ResourceLocation(tag.getString("CableBlock"));
				return ForgeRegistries.BLOCKS.getValue(cableName);
			}
		}
		return null;
	}
	
	public static ItemStack getBurnedCable(Block cable) {
		ItemStack stack = new ItemStack(ModItems.burned_cable);
		CompoundNBT tag = new CompoundNBT();
		CompoundNBT betag = new CompoundNBT();
		betag.putString("CableBlock", cable.getRegistryName().toString());
		tag.put("BlockEntityTag", betag);
		stack.setTag(tag);
		return stack;
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (rand.nextInt(10) == 0) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
			ElectricityNetwork network = handler.getNetwork(pos);
			Voltage voltage = network.getVoltage();
			
			if (network.getCurrent() > 0) {
				
				int particleCount = voltage.getVoltage() / 10;

				if (particleCount > 0) {
					
					for (int i = 0; i < particleCount; i++) {
						
						
						IParticleData particle = ParticleTypes.CRIT;
						IParticleData particle1 = ParticleTypes.FLAME;
						IParticleData particle2 = ParticleTypes.LARGE_SMOKE;
						
						double x = pos.getX() + 0.5d;
						double y = pos.getY() + 0.2d;
						double z = pos.getZ() + 0.5d;
						
						double x2 = x + rand.nextFloat() - 0.5F;
						double y2 = y + rand.nextFloat() - 0.5F;
						double z2 = z + rand.nextFloat() - 0.5F;
						
						double xSpeed = rand.nextFloat() - 0.5F;
						double ySpeed = rand.nextFloat() - 0.5F;
						double zSpeed = rand.nextFloat() - 0.5F;
						
						double range = 2d;
						
						worldIn.addParticle(particle, x, y, z, xSpeed * range, ySpeed * range, zSpeed * range);
						worldIn.addParticle(particle1, x2, y2 + 0.2, z2, 0, 0, 0);
						worldIn.addParticle(particle2, x2, y2 + 0.2F, z2, 0, 0, 0);
						
					}
					
					if (rand.nextInt(6) == 0) worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.SPARKING_CABLE, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.2F + 0.8F, false);
					
				}
				
			}
			
		}
		
	}

	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof LivingEntity) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
			ElectricityNetwork network = handler.getNetwork(pos);
			Voltage voltage = network.getVoltage();
			
			int dammge = voltage.getVoltage() / 100;

			if (dammge > 0) entityIn.hurt(ModDamageSource.ELCTRIC_SHOCK, dammge);
			
		}
		
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			Block cableType = getBurnedCableFromStack(stack);
			info.add(new TranslationTextComponent("industria.block.info.maxCurrent", this.maximumPower));
			if (cableType != null) info.add(new TranslationTextComponent("industria.block.info.burnedCableType", cableType.getName()));
			info.add(new TranslationTextComponent("industria.block.info.burnedCable", this.maximumPower));
		};
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		float current = network.getCurrent();
		
		if (this.maximumPower < current && worldIn.random.nextInt(300) == 0) {
			
			for (int x = -1; x < 2; x++) {
				for (int y = -1; y < 2; y++) {
					for (int z = -1; z < 2; z++) {
						BlockPos firePos = pos.offset(x, y, z);
						BlockState fireState = worldIn.getBlockState(firePos);
						
						if (fireState.isAir()) {
							
							for (Direction d : Direction.values()) {
								BlockPos burnBlockPos = firePos.relative(d);
								BlockState burnBlockState = worldIn.getBlockState(burnBlockPos);
								int flammability = burnBlockState.getFlammability(worldIn, burnBlockPos, d.getOpposite());
								if (d == Direction.DOWN && burnBlockState.canOcclude() && flammability == 0) flammability = 100;
								
								if (flammability > 0 ? worldIn.random.nextInt(299) < flammability : false) {
									
									worldIn.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
									
								}
							}
							
						}
						
					}
				}
			}

			if (worldIn.random.nextInt(100) == 0) {
				worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 1, Mode.BREAK);
			}
			
		}
		
	}
	
}
