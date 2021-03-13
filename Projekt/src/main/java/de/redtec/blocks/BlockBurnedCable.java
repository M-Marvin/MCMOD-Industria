package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.redtec.typeregistys.ModSoundEvents;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class BlockBurnedCable extends BlockElektricWire {
	
	public BlockBurnedCable() {
		super("burned_cable", 10, 4);
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (hasOpenEnd(stateIn) && rand.nextInt(10) == 0) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
			ElectricityNetwork network = handler.getNetwork(pos);
			Voltage voltage = network.getVoltage();
			
			if (network.getCurrent() > 0) {
				
				int particleCount = voltage.getVoltage() / 10;

				if (particleCount > 0) {
					
					for (int i = 0; i < particleCount; i++) {
						
						
						IParticleData particle = ParticleTypes.CRIT;
						IParticleData particle1 = ParticleTypes.FLAME;
						IParticleData particle2 = ParticleTypes.SMOKE;
						
						double x = pos.getX() + 0.5d;
						double y = pos.getY() + 0.2d;
						double z = pos.getZ() + 0.5d;
						
						double xSpeed = rand.nextFloat() - 0.5F;
						double ySpeed = rand.nextFloat() - 0.5F;
						double zSpeed = rand.nextFloat() - 0.5F;
						
						double range = 2d;
						
						worldIn.addParticle(particle, x, y, z, xSpeed * range, ySpeed * range, zSpeed * range);
						worldIn.addParticle(particle1, x, y, z, xSpeed * range, ySpeed * range, zSpeed * range);
						worldIn.addParticle(particle2, x, y, z, xSpeed * range, ySpeed * range, zSpeed * range);
						
					}
					
					worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.SPARKING_CABLE, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.2F + 0.8F, false);
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.burnedCable", this.maximumPower));
		info.add(new TranslationTextComponent("redtec.block.info.maxCurrent", this.maximumPower));
		return info;
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		float current = network.getCurrent();
		
		if (this.maximumPower < current) {
			
			// TODO
			
		}
		
	}
	
}
