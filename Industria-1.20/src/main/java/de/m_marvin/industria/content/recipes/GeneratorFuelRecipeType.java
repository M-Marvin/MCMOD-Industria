package de.m_marvin.industria.content.recipes;

import com.google.gson.JsonObject;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.registries.ModRecipeTypes;
import de.m_marvin.industria.core.util.container.IFluidSlotContainer.FluidContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class GeneratorFuelRecipeType implements Recipe<FluidContainer> {
	
	protected final ResourceLocation id;
	protected final Fluid fluid;
	protected final int wattsPerMb;
	
	public GeneratorFuelRecipeType(ResourceLocation id, Fluid fluid, int wattsPerMb) {
		this.id = id;
		this.fluid = fluid;
		this.wattsPerMb = wattsPerMb;
	}
	
	public static GeneratorFuelRecipeType fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation fluidName = new ResourceLocation(json.get("fluid").getAsString());
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
		if (fluid == null) {
			Industria.LOGGER.warn("Could net deserialize recipe " + id + ", uknown fluid: " + fluidName);
			return null;
		}
		int wattsPerMb = json.get("wattsPerMB").getAsInt();
		return new GeneratorFuelRecipeType(id, fluid, wattsPerMb);
	}
	
	public static GeneratorFuelRecipeType fromNetwork(ResourceLocation id, FriendlyByteBuf buff) {
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(buff.readResourceLocation());
		int wattsPerMb = buff.readInt();
		return new GeneratorFuelRecipeType(id, fluid, wattsPerMb);
	}
	
	public static void toNetwork(FriendlyByteBuf buff, GeneratorFuelRecipeType recipe) {
		buff.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(recipe.fluid));
		buff.writeInt(recipe.wattsPerMb);
	}
	
	public Fluid getFluid() {
		return fluid;
	}
	
	public int getWattsPerMb() {
		return wattsPerMb;
	}
	
	@Override
	public boolean matches(FluidContainer pContainer, Level pLevel) {
		return pContainer.getFluid(0).getFluid() == this.fluid;
	}

	@Override
	public ItemStack assemble(FluidContainer pContainer, RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.GENERATOR_FUEL_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.GENERATOR_FUEL.get();
	}
	
}
