package de.redtec.recipetypes;

import de.redtec.registys.ModRecipeTypes;
import de.redtec.registys.ModSerializer;
import de.redtec.tileentity.TileEntityMBlender;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class BlendingRecipe implements IRecipe<IInventory> {
	
	public ResourceLocation id;
	public ItemStack[] itemsIn;
	public FluidStack[] fluidsIn;
	public FluidStack fluidOut;
	public int mixingTime;
	
	public BlendingRecipe(ResourceLocation id, ItemStack[] itemsIn, FluidStack[] fluidsIn, FluidStack fluidOut, int mixingTime) {
		super();
		this.id = id;
		this.itemsIn = itemsIn;
		this.fluidsIn = fluidsIn;
		this.fluidOut = fluidOut;
		this.mixingTime = mixingTime;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		
		if (inv instanceof TileEntityMBlender) {
			
			// Check items
			ItemStack[] neededItems = this.itemsIn.clone();
			int i2 = 0;
			for (int i = 0; i < 3; i++) {
				ItemStack itemIn = inv.getStackInSlot(i);
				int i1;
				for (i1 = 0; i1 < neededItems.length; i1++) {
					if (neededItems[i1] == null) continue;
					if (neededItems[i1].getItem().equals(itemIn.getItem()) && neededItems[i1].getCount() <= itemIn.getCount()) {
						neededItems[i1] = null;
						i1 = -1;
						i2++;
						break;
					}
				}				
				// Too many items
				if (i1 != -1 && i2 < neededItems.length && !itemIn.isEmpty()) return false;
			}
			
			for (ItemStack item : neededItems) {
				// To low items
				if (item != null) return false;
			}
			
			// Check fluids
			FluidStack[] neededFluids = this.fluidsIn.clone();
			i2 = 0;
			for (int i = 0; i < 2; i++) {
				FluidStack fluidIn = i == 0 ? ((TileEntityMBlender) inv).fluidIn1 : ((TileEntityMBlender) inv).fluidIn2;
				int i1;
				for (i1 = 0; i1 < neededFluids.length; i1++) {
					if (neededFluids[i1] == null) continue;
					if (neededFluids[i1].getFluid().equals(fluidIn.getFluid()) && neededFluids[i1].getAmount() <= fluidIn.getAmount()) {
						neededFluids[i1] = null;
						i1 = -1;
						i2++;
						break;
					}
				}
				
				// Too many fluids
				if (i1 != -1 && i2 < neededFluids.length && !fluidIn.isEmpty()) return false;
			}
			
			for (FluidStack fluid : neededFluids) {
				// To low fluids
				if (fluid != null) return false;
			}
			
			return true;
			
		} else {
			
			// Not an blender instance
			return false;
			
		}
		
	}
	
	
	
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return this.fluidOut.getFluid().getAttributes().getBucket(this.fluidOut);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width == 1 && height == 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.fluidOut.getFluid().getAttributes().getBucket(this.fluidOut);
	}
	
	public FluidStack getRecipeOutputFluid() {
		return this.fluidOut.copy();
	}
	
	public int getMixingTime() {
		return this.mixingTime;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModSerializer.BLENDING;
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.BLENDING;
	}
	
}
