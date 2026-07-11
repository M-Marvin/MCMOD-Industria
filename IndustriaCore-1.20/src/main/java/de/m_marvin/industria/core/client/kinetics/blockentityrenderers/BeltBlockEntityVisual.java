package de.m_marvin.industria.core.client.kinetics.blockentityrenderers;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.core.client.flwinstances.ShiftedTextureInstance;
import de.m_marvin.industria.core.client.registries.FlwInstanceTypes;
import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.FlywheelUtility;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;

public class BeltBlockEntityVisual extends AbstractBlockEntityVisual<BeltBlockEntity> implements SimpleDynamicVisual {

	private ShiftedTextureInstance instance;
	private BakedModel bakedModel;
	
	public BeltBlockEntityVisual(VisualizationContext ctx, BeltBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		this.bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockEntity.getBlockState());
		
		Model model = FlywheelUtility.modelOfBlockForceBaked(blockEntity.getBlockState());
		this.instance = ctx.instancerProvider().instancer(FlwInstanceTypes.SHIFTED_TEXTURE, model).createInstance();
		this.instance.position(getVisualPosition()).zeroShift().setChanged();
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		consumer.accept(this.instance);
	}

	@Override
	public void updateLight(float partialTick) {
		relight(this.instance);
	}

	@Override
	public void beginFrame(Context ctx) {
		double rpm = this.blockEntity.getRPM(0);
		float animation = (float) (rpm * -0.333F * ClientTimer.getRenderTicks() / 1000) % 1F;
		if (animation < 0F) animation += 1F;
		this.instance.shift(this.bakedModel, 0F, animation * 0.5F, "belt").setChanged();
	}
	
	@Override
	protected void _delete() {
		this.instance.delete();
	}
	
}
