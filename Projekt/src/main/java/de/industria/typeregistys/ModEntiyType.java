package de.industria.typeregistys;

import de.industria.Industria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntiyType {
	
	//public static final EntityType<EntitySteelMinecart> STEEL_MINECART = register("steel_minecart", EntityType.Builder.<EntitySteelMinecart>create(EntitySteelMinecart::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
	
	@SuppressWarnings("unused")
	private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
		EntityType<T> entityType = builder.build(key);
		entityType.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.ENTITIES.register(entityType);
		return entityType;
	}
	
}
