package de.industria.tileentity;

import java.util.Collection;
import java.util.Optional;

import de.industria.blocks.BlockMElectricFurnace;
import de.industria.gui.ContainerMElectricFurnace;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.ItemStackHelper;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMElectricFurnace extends TileEntityInventoryBase implements INamedContainerProvider, ISidedInventory, ITickableTileEntity {
	
	public int cookTime;
	public int cookTimeTotal;
	public boolean hasPower;
	public boolean isWorking;
	protected final Object2IntOpenHashMap<ResourceLocation> usedRecipes = new Object2IntOpenHashMap<>();
	
	public TileEntityMElectricFurnace() {
		super(ModTileEntityType.ELECTRIC_FURNACE, 2);
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMElectricFurnace(id, playerInv, this);
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.level).getNetwork(this.worldPosition);
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = canWork() && this.hasPower;
			
			if (isWorking != getBlockState().getValue(BlockMElectricFurnace.LIT)) this.level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(BlockMElectricFurnace.LIT, this.isWorking));
			
			if (this.isWorking) {
				
				FurnaceRecipe recipe = findRecipe();
				
				if (recipe != null) {
					
					if (this.cookTimeTotal == 0) {
						
						this.cookTimeTotal = recipe.getCookingTime();
						this.cookTime = 0;
						
					} else if (this.cookTime <= this.cookTimeTotal) {
						
						if (this.hasPower) {
							
							if (this.cookTime >= this.cookTimeTotal) {
								
								this.removeItem(0, 1);
								ItemStack result = recipe.getResultItem();
								
								if (this.getItem(1).isEmpty()) {
									this.setItem(1, result.copy());
								} else {
									this.getItem(1).grow(1);
								}
								
								this.cookTime = 0;
								this.cookTimeTotal = 0;
								this.setRecipeUsed(recipe);
								
								return;
								
							}
							
							this.cookTime++;
							
						}
						
					}
					
				}
				
			} 
			
			if (this.cookTime > 0 && (findRecipe() == null || network.canMachinesRun() != Voltage.HightVoltage)) {
				
				this.cookTime -= 2;
				if (this.cookTime < 0) {
					this.cookTime = 0;
					this.cookTimeTotal = 0;
				}
				
			}
			
		}
		
	}
	
	public boolean canWork() {
		return this.findRecipe() != null;
	}
	
	public FurnaceRecipe findRecipe() {
		Collection<IRecipe<?>> recipes = this.level.getRecipeManager().getRecipes();
		for (IRecipe<?> recipe : recipes) {
			if (recipe.getType() == IRecipeType.SMELTING) {
				if (recipe.getIngredients().get(0).test(this.getItem(0)) && ItemStackHelper.canMergeRecipeStacks(this.getItem(1), recipe.getResultItem())) return (FurnaceRecipe) recipe;
			}
		}
		return null;
	}
	
	public void setRecipeUsed(FurnaceRecipe recipe) {
		ResourceLocation id = recipe.getId();
		this.usedRecipes.addTo(id, 1);
	}
	
	public void onPlayerCollect(PlayerEntity player) {
		
		for (ResourceLocation recipeId : this.usedRecipes.keySet()) {
			Optional<? extends IRecipe<?>> recipe = this.level.getRecipeManager().byKey(recipeId);
			if (recipe.get().getType() == IRecipeType.SMELTING) {
				int xp = (int) (((FurnaceRecipe) recipe.get()).getExperience() * this.usedRecipes.getInt(recipeId));
				player.giveExperiencePoints(xp);
			}
		}
		player.awardRecipesByKey(this.usedRecipes.keySet().toArray(new ResourceLocation[] {}));
		this.usedRecipes.clear();
		
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 1;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.electric_furnace");
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("cookTime", this.cookTime);;
		compound.putInt("cookTimeTotal", this.cookTimeTotal);
		compound.putBoolean("hasPower", this.hasPower);
		CompoundNBT compoundnbt = new CompoundNBT();
		this.usedRecipes.forEach((p_235643_1_, p_235643_2_) -> {
			compoundnbt.putInt(p_235643_1_.toString(), p_235643_2_);
		});
		compound.put("RecipesUsed", compoundnbt);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.cookTime = compound.getInt("cookTime");
		this.cookTimeTotal = compound.getInt("cookTimeTotal");
		this.hasPower = compound.getBoolean("hasPower");
		CompoundNBT compoundnbt = compound.getCompound("RecipesUsed");
		for(String s : compoundnbt.getAllKeys()) {
			this.usedRecipes.put(new ResourceLocation(s), compoundnbt.getInt(s));
		}
		super.load(state, compound);
	}
	
}
