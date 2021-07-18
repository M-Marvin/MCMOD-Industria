package de.industria.tileentity;

import de.industria.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityMBurnedCable extends TileEntity {
	
	protected Block cableBlock;
	
	public TileEntityMBurnedCable() {
		super(ModTileEntityType.BURNED_CABLE);
		this.cableBlock = ModItems.copper_cable;
	}
	
	public Block getCableBlock() {
		return cableBlock;
	}
	
	public void setCableBlock(Block cableBlock) {
		this.cableBlock = cableBlock;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (this.cableBlock != null) compound.putString("CableBlock", this.cableBlock.getRegistryName().toString());
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		if (nbt.contains("CableBlock")) this.cableBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("CableBlock")));
		super.load(state, nbt);
	}

}
