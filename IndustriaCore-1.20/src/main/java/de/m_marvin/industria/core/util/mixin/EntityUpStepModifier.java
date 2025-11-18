package de.m_marvin.industria.core.util.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	
	@Inject(
			method = "maxUpStep()F",
			at = @At("HEAD")
	)
	public void maxUpStepModified(CallbackInfoReturnable<Float> callback) {
		float modifiedUpStep = CompoundBlock.performOnAllAndCombine(level(), getOnPosLegacy(), 
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
		if (modifiedUpStep != maxUpStep) callback.setReturnValue(modifiedUpStep);
	}
	
}
