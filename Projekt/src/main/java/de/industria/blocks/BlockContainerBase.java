package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound));
		this.setRegistryName(Industria.MODID, name);
	}

	public BlockContainerBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public static ToolType getDefaultToolType(Material material) {
		if (material == Material.STONE || material == Material.METAL) return ToolType.PICKAXE;
		if (material == Material.SAND || material == Material.DIRT) return ToolType.SHOVEL;
		if (material == Material.WOOD) return ToolType.AXE;
		if (material == Material.GRASS) return ToolType.HOE;
		return null;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof IInventory) {
				InventoryHelper.dropContents(world, pos, (IInventory) te);
			}
			world.removeBlockEntity(pos);
		}
	}
	
}
