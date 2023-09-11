package de.m_marvin.industria.content.recipes;

import com.google.gson.JsonObject;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.items.ConduitCoilItem;
import de.m_marvin.industria.content.registries.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class WireCoilWindingRecipeType implements CraftingRecipe {
	
	protected final ResourceLocation id;
	protected Item conduitItem;
	protected Item emptyCoil;
	protected Item wireCoil;
	
	public WireCoilWindingRecipeType(ResourceLocation id, Item conduitCoil, Item emptyCoil, Item wireCoil) {
		this.id = id;
		this.conduitItem = conduitCoil;
		this.emptyCoil = emptyCoil;
		this.wireCoil = wireCoil;
	}

	public static WireCoilWindingRecipeType fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation conduitItemName = new ResourceLocation(json.get("conduitItem").getAsString());
		Item conduitItem = ForgeRegistries.ITEMS.getValue(conduitItemName);
		ResourceLocation emptyCoilName = new ResourceLocation(json.get("emptyCoil").getAsString());
		Item emptyCoil = ForgeRegistries.ITEMS.getValue(emptyCoilName);
		ResourceLocation wireCoilName = new ResourceLocation(json.get("wireCoil").getAsString());
		Item wireCoil = ForgeRegistries.ITEMS.getValue(wireCoilName);
		
		if (conduitItem == null) {
			Industria.LOGGER.warn("Could net deserialize recipe " + id + ", uknown conduit item: " + conduitItemName);
			return null;
		}
		if (emptyCoil == null) {
			Industria.LOGGER.warn("Could net deserialize recipe " + id + ", uknown coil item: " + emptyCoilName);
			return null;
		}
		if (wireCoil == null) {
			Industria.LOGGER.warn("Could net deserialize recipe " + id + ", uknown wire coil item: " + wireCoilName);
			return null;
		}
		return new WireCoilWindingRecipeType(id, conduitItem, emptyCoil, wireCoil);
	}
	
	public static WireCoilWindingRecipeType fromNetwork(ResourceLocation id, FriendlyByteBuf buff) {
		Item conduitItem = ForgeRegistries.ITEMS.getValue(buff.readResourceLocation());
		Item emptyCoil = ForgeRegistries.ITEMS.getValue(buff.readResourceLocation());
		Item wireCoil = ForgeRegistries.ITEMS.getValue(buff.readResourceLocation());
		return new WireCoilWindingRecipeType(id, conduitItem, emptyCoil, wireCoil);
	}
	
	public static void toNetwork(FriendlyByteBuf buff, WireCoilWindingRecipeType recipe) {
		buff.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.conduitItem));
		buff.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.emptyCoil));
		buff.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.wireCoil));
	}
	
	@Override
	public boolean matches(CraftingContainer pContainer, Level pLevel) {
		int conduitCount = 0;
		int emptyCoils = 0;
		int wireCoils = 0;
		for (int i = 0; i < pContainer.getContainerSize(); i++) {
			ItemStack stack = pContainer.getItem(i);
			if (stack.getItem() == conduitItem) 
				conduitCount++;
			else if (stack.getItem() == emptyCoil) 
				emptyCoils++;
			else if (stack.getItem() == wireCoil) 
				wireCoils++;
			else if (!stack.isEmpty())
				return false;
		}
		if (conduitCount > 0) {
			return (emptyCoils == 1) != (wireCoils == 1);
		} else {
			return emptyCoils == 0 && wireCoils == 1;
		}
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer pContainer) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pContainer.getContainerSize(), ItemStack.EMPTY);
		
		int conduitCount = 0;
		ItemStack coilStack = ItemStack.EMPTY;
		for (int i = 0; i < pContainer.getContainerSize(); i++) {
			ItemStack stack = pContainer.getItem(i);
			if (stack.getItem() == conduitItem) 
				conduitCount++;
			else if (stack.getItem() == emptyCoil) 
				coilStack = stack;
			else if (stack.getItem() == wireCoil) 
				coilStack = stack;
		}
		
		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack item = pContainer.getItem(i);
			if (item.hasCraftingRemainingItem()) {
				nonnulllist.set(i, item.getCraftingRemainingItem());
			} else if (conduitCount == 0 && !coilStack.isEmpty() && item.getItem() == this.wireCoil) {
				if (coilStack.getItem() instanceof ConduitCoilItem coil) {
					int wiresOnCoil = coil.getConduitsOnCoil(coilStack);
					ItemStack conduitStack = new ItemStack(this.conduitItem);
					int wiresRemoved = Math.min(wiresOnCoil, this.conduitItem.getMaxStackSize(conduitStack));
					ItemStack coilRemaining = wiresRemoved >= wiresOnCoil ? new ItemStack(this.emptyCoil) : coil.getWithWires(wiresOnCoil - wiresRemoved);
					nonnulllist.set(i, coilRemaining);
				}
			}
		}
		
		return nonnulllist;
	}
	
	@Override
	public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
		int conduitCount = 0;
		ItemStack coilStack = ItemStack.EMPTY;
		for (int i = 0; i < pContainer.getContainerSize(); i++) {
			ItemStack stack = pContainer.getItem(i);
			if (stack.getItem() == conduitItem) 
				conduitCount++;
			else if (stack.getItem() == emptyCoil) 
				coilStack = stack;
			else if (stack.getItem() == wireCoil) 
				coilStack = stack;
			else if (!stack.isEmpty())
				return ItemStack.EMPTY;
		}
		
		if (conduitCount > 0 && !coilStack.isEmpty() && this.wireCoil instanceof ConduitCoilItem coilItem) {
			int wiresOnCoil = 0;
			if (coilStack.getItem() instanceof ConduitCoilItem coil) {
				wiresOnCoil = coil.getConduitsOnCoil(coilStack);
			}
			wiresOnCoil += conduitCount;
			if (wiresOnCoil <= coilItem.getMaxConduits()) {
				return coilItem.getWithWires(wiresOnCoil);
			}
		} else if (conduitCount == 0 && !coilStack.isEmpty()) {
			if (coilStack.getItem() instanceof ConduitCoilItem coil) {
				int wiresOnCoil = coil.getConduitsOnCoil(coilStack);
				ItemStack conduitStack = new ItemStack(this.conduitItem);
				int wiresRemoved = Math.min(wiresOnCoil, this.conduitItem.getMaxStackSize(conduitStack));
				conduitStack.setCount(wiresRemoved);
				return conduitStack;
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return new ItemStack(this.wireCoil);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		// TODO Auto-generated method stub
		return CraftingRecipe.super.getIngredients();
	}
	
	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.WIRE_COIL_WINDING_SERIALIZER.get();
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

}
