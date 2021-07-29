package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.entity.EntityFallingFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityType {
	
	public static final EntityType<EntityFallingFluid> FALLING_FLUID = register("falling_fluid", EntityType.Builder.<EntityFallingFluid>of(EntityFallingFluid::new, EntityClassification.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).setUpdateInterval(20));
	
	private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
		EntityType<T> entityType = builder.build(key);
		entityType.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.ENTITIES.register(entityType);
		return entityType;
	}
	
}
