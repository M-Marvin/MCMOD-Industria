package de.industria.typeregistys;

import java.util.HashMap;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientBindings {
	
	protected static HashMap<Item, ItemModelProvoider> itemModelBindings = new HashMap<Item, ModClientBindings.ItemModelProvoider>();
	
	public static void bindModelToitem(Item item, ResourceLocation texturePath, EntityModel<?> model) {
		itemModelBindings.put(item, new ItemModelProvoider(texturePath, model));
	}
	
	public static ItemModelProvoider getBindedModel(Item item) {
		return itemModelBindings.get(item);
	}
	
	public static class ItemModelProvoider {
		protected ResourceLocation textureLoc;
		protected EntityModel<?> model;
		public ResourceLocation getTextureLoc() {
			return textureLoc;
		}
		public EntityModel<?> getModel() {
			return model;
		}
		public ItemModelProvoider(ResourceLocation textureLoc, EntityModel<?> model) {
			super();
			this.textureLoc = textureLoc;
			this.model = model;
		}
	}
	
}
