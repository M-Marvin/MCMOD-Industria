package de.industria.typeregistys;

import java.util.ArrayList;
import java.util.List;

import de.industria.Industria;
import de.industria.multipartbuilds.MultipartBuild;
import de.industria.multipartbuilds.MultipartBuildBlender;
import net.minecraft.util.ResourceLocation;

public class MultipartBuildRecipes {
	
	protected static List<MultipartBuildRecipe> multipartBuilds = new ArrayList<MultipartBuildRecipe>();
	
	public static final MultipartBuildRecipe BLENDER = register("blender", new MultipartBuildBlender());
	
	
	protected static MultipartBuildRecipe register(String name, MultipartBuild builder) {
		MultipartBuildRecipe recipe = new MultipartBuildRecipe(new ResourceLocation(Industria.MODID, name), builder);
		multipartBuilds.add(recipe);
		return recipe;
	}
	
	public static class MultipartBuildRecipe {
		protected ResourceLocation id;
		protected MultipartBuild structureBuilder;
		public MultipartBuildRecipe(ResourceLocation id, MultipartBuild structureBuilder) {
			this.id = id;
			this.structureBuilder = structureBuilder;
		}
		public ResourceLocation getId() {
			return id;
		}
		public MultipartBuild getStructureBuilder() {
			return structureBuilder;
		}
	}
	
	public static List<MultipartBuildRecipe> getRecipes() {
		return multipartBuilds;
	}
	
}
