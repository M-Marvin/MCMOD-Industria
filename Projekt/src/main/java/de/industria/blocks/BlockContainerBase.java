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
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}

	public BlockContainerBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound).harvestTool(getDefaultToolType(material)));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockContainerBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound).harvestTool(getDefaultToolType(material)));
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
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof IInventory) {
			InventoryHelper.dropContents(worldIn, pos, (IInventory) te);
		}
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
}
