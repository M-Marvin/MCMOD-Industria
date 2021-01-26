package de.redtec.renderer;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public abstract class ItemSchredderToolModel extends EntityModel<Entity> {
	
	public abstract ModelRenderer getAxe1();
	
	public abstract ModelRenderer getAxe2();
	
	public abstract void setRotationState(float rotation);
	
}