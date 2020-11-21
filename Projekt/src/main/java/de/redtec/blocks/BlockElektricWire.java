package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.registys.ModDamageSource;
import de.redtec.registys.ModSoundEvents;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import de.redtec.util.IElectricWire;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;

public class BlockElektricWire extends BlockWiring implements IElectricWire, IAdvancedBlockInfo {
	
	protected final int maximumPower;
	
	public BlockElektricWire(String name, int maximumPower, int size) {
		super(name, Material.WOOL, 0.2F, SoundType.CLOTH, size);
		this.maximumPower = maximumPower;
	}
	
	public int getMaximumPower() {
		return maximumPower;
	}
	
	@Override
	public boolean canConnectTo(BlockState wireState, World worldIn, BlockPos wirePos, BlockPos connectPos, Direction direction) {
		
		BlockState otherState = worldIn.getBlockState(connectPos);
		
		if (otherState.getBlock() instanceof IElectricWire) {
			return true;
		} else if (otherState.getBlock() instanceof IElectricConnective) {
			return ((IElectricConnective) otherState.getBlock()).canConnect(direction.getOpposite(), worldIn, connectPos, otherState);
		}
		
		return false;
		
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (hasOpenEnd(stateIn) && rand.nextInt(10) == 0) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
			ElectricityNetwork network = handler.getNetwork(pos);
			Voltage voltage = network.getVoltage();
			
			int particleCount = voltage.getVoltage() / 10;
			
			if (particleCount > 0) {
				
				for (int i = 0; i < particleCount; i++) {
					
					
					IParticleData particle = ParticleTypes.CRIT;
					
					double x = pos.getX() + 0.5d;
					double y = pos.getY() + 0.2d;
					double z = pos.getZ() + 0.5d;
					
					double xSpeed = rand.nextFloat() - 0.5F;
					double ySpeed = rand.nextFloat() - 0.5F;
					double zSpeed = rand.nextFloat() - 0.5F;
					
					double range = 2d;
					
					worldIn.addParticle(particle, x, y, z, xSpeed * range, ySpeed * range, zSpeed * range);
					
				}
				
				worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.SPARKING_CABLE, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.2F + 0.8F, false);
				
			}
			
		}
		
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		BlockState stateIn = worldIn.getBlockState(pos);
		
		if (hasOpenEnd(stateIn) && entityIn instanceof LivingEntity) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
			ElectricityNetwork network = handler.getNetwork(pos);
			Voltage voltage = network.getVoltage();
			
			int dammge = voltage.getVoltage() / 100;

			if (dammge > 0) entityIn.attackEntityFrom(ModDamageSource.ELCTRIC_SHOCK, dammge);
			
		}
		
	}

	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.maxCurrent", this.maximumPower));
		return info;
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		float current = network.getCurrent();
		
		if (this.maximumPower < current) {
			
			worldIn.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0F, Mode.DESTROY);
			worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			
		}
		
	}
	
}
