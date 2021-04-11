package de.industria.tileentity;

import java.util.Collection;
import java.util.Optional;

import de.industria.blocks.BlockMElectricFurnace;
import de.industria.gui.ContainerMElectricFurnace;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.world).getNetwork(this.pos);
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
			this.isWorking = canWork() && this.hasPower;
			
			if (isWorking != getBlockState().get(BlockMElectricFurnace.LIT)) this.world.setBlockState(pos, this.getBlockState().with(BlockMElectricFurnace.LIT, this.isWorking));
			
			if (this.isWorking) {
				
				FurnaceRecipe recipe = findRecipe();
				
				if (recipe != null) {
					
					if (this.cookTimeTotal == 0) {
						
						this.cookTimeTotal = recipe.getCookTime();
						this.cookTime = 0;
						
					} else if (this.cookTime <= this.cookTimeTotal) {
						
						if (this.hasPower) {
							
							if (this.cookTime >= this.cookTimeTotal) {
								
								this.decrStackSize(0, 1);
								ItemStack result = recipe.getRecipeOutput();
								
								if (this.getStackInSlot(1).isEmpty()) {
									this.setInventorySlotContents(1, result.copy());
								} else {
									this.getStackInSlot(1).grow(1);
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
			
			if (this.cookTime > 0 && (findRecipe() == null || network.canMachinesRun() != Voltage.NormalVoltage)) {
				
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
		Collection<IRecipe<?>> recipes = this.world.getRecipeManager().getRecipes();
		for (IRecipe<?> recipe : recipes) {
			if (recipe.getType() == IRecipeType.SMELTING) {
				if (recipe.getIngredients().get(0).test(this.getStackInSlot(0)) && ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), recipe.getRecipeOutput())) return (FurnaceRecipe) recipe;
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
			Optional<? extends IRecipe<?>> recipe = this.world.getRecipeManager().getRecipe(recipeId);
			if (recipe.get().getType() == IRecipeType.SMELTING) {
				int xp = (int) (((FurnaceRecipe) recipe.get()).getExperience() * this.usedRecipes.getInt(recipeId));
				player.giveExperiencePoints(xp);
			}
		}
		player.unlockRecipes(this.usedRecipes.keySet().toArray(new ResourceLocation[] {}));
		this.usedRecipes.clear();
		
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 1;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.electric_furnace");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("cookTime", this.cookTime);;
		compound.putInt("cookTimeTotal", this.cookTimeTotal);
		compound.putBoolean("hasPower", this.hasPower);
		CompoundNBT compoundnbt = new CompoundNBT();
		this.usedRecipes.forEach((p_235643_1_, p_235643_2_) -> {
			compoundnbt.putInt(p_235643_1_.toString(), p_235643_2_);
		});
		compound.put("RecipesUsed", compoundnbt);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.cookTime = compound.getInt("cookTime");
		this.cookTimeTotal = compound.getInt("cookTimeTotal");
		this.hasPower = compound.getBoolean("hasPower");
		CompoundNBT compoundnbt = compound.getCompound("RecipesUsed");
		for(String s : compoundnbt.keySet()) {
			this.usedRecipes.put(new ResourceLocation(s), compoundnbt.getInt(s));
		}
		super.read(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}
