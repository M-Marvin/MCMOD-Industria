package de.m_marvin.industria.core.entitymod.engine.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.registries.Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Entity.class)
public abstract class EntityUpStepModifier {

	@Shadow
	private float maxUpStep;
	
	@Shadow
	public abstract Level level();
	@Shadow
	public abstract BlockPos getOnPosLegacy();
	
	@Overwrite
	public float maxUpStep() {
		return CompoundBlock.performOnAllAndCombine(level(), getOnPosLegacy(), 
				() -> {
					BlockState stepOnState = level().getBlockState(getOnPosLegacy());
					if (stepOnState.is(Tags.Blocks.ENTITY_UP_STEP_ONE_BLOCK)) {
						return maxUpStep > 1.0F ? maxUpStep : 1.0F;
					} else if (stepOnState.is(Tags.Blocks.ENTITY_UP_STEP_HALF_BLOCK)) {
						return maxUpStep > 0.5F ? maxUpStep : 0.5F;
					} else {
						return maxUpStep;
					}
				}, 
				(compound, p) -> {
					if (p.getState().is(Tags.Blocks.ENTITY_UP_STEP_ONE_BLOCK)) {
						return maxUpStep > 1.0F ? maxUpStep : 1.0F;
					} else if (p.getState().is(Tags.Blocks.ENTITY_UP_STEP_HALF_BLOCK)) {
						return maxUpStep > 0.5F ? maxUpStep : 0.5F;
					} else {
						return maxUpStep;
					}
				}, 
				Math::max);		
	}
	
}
