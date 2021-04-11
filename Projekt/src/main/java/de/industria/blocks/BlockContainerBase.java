package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public abstract class BlockContainerBase extends ContainerBlock {
	
	public BlockContainerBase(String name, Properties properties) {
		super(properties);
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}

	public BlockContainerBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public static ToolType getDefaultToolType(Material material) {
		if (material == Material.ROCK || material == Material.IRON) return ToolType.PICKAXE;
		if (material == Material.SAND || material == Material.EARTH) return ToolType.SHOVEL;
		if (material == Material.WOOD) return ToolType.AXE;
		if (material == Material.ORGANIC) return ToolType.HOE;
		return null;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) te);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
}
