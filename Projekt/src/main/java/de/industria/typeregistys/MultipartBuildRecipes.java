package de.industria.typeregistys;

import java.util.ArrayList;
import java.util.List;

import de.industria.Industria;
import de.industria.multipartbuilds.MultipartBuildBlastFurnace;
import de.industria.multipartbuilds.MultipartBuildBlender;
import de.industria.multipartbuilds.MultipartBuildCoalHeater;
import de.industria.multipartbuilds.MultipartBuildElectricHeater;
import de.industria.multipartbuilds.MultipartBuildFluidBath;
import de.industria.multipartbuilds.MultipartBuildGasHeater;
import de.industria.multipartbuilds.MultipartBuildOreWashingPlant;
import de.industria.multipartbuilds.MultipartBuildRaffinery;
import de.industria.multipartbuilds.MultipartBuildSchredder;
import de.industria.multipartbuilds.MultipartBuildSteamGenerator;
import de.industria.multipartbuilds.MultipartBuildThermalZentrifuge;
import de.industria.util.types.MultipartBuild;
import net.minecraft.util.ResourceLocation;

public class MultipartBuildRecipes {
	
	protected static List<MultipartBuildRecipe> multipartBuilds = new ArrayList<MultipartBuildRecipe>();
	
	public static final MultipartBuildRecipe BLENDER = register("blender", new MultipartBuildBlender());
	public static final MultipartBuildRecipe RAFFINERY = register("raffinery", new MultipartBuildRaffinery());
	public static final MultipartBuildRecipe SCHREDDER = register("schredder", new MultipartBuildSchredder());
	public static final MultipartBuildRecipe BLAST_FURNACE = register("blast_furnace", new MultipartBuildBlastFurnace());
	public static final MultipartBuildRecipe THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", new MultipartBuildThermalZentrifuge());
	public static final MultipartBuildRecipe ORE_WASHING_PLANT = register("ore_washing_plant", new MultipartBuildOreWashingPlant());
	public static final MultipartBuildRecipe ELECTRIC_HEATER = register("electric_heater", new MultipartBuildElectricHeater());
	public static final MultipartBuildRecipe GAS_HEATER = register("gas_heater", new MultipartBuildGasHeater());
	public static final MultipartBuildRecipe COAL_HEATER = register("coal_heater", new MultipartBuildCoalHeater());
	public static final MultipartBuildRecipe FLUID_BATH = register("fluid_bath", new MultipartBuildFluidBath());
	public static final MultipartBuildRecipe STEAM_GENERATOR = register("steam_generator", new MultipartBuildSteamGenerator());
	
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
