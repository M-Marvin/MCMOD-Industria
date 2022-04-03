package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockBase extends Block {
	
	public BlockBase(String name, Properties properties) {
		super(properties);
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound));
		this.setRegistryName(Industria.MODID, name);
	}

	public BlockBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.of(material).strength(hardnessAndResistance).sound(sound).requiresCorrectToolForDrops());
		this.setRegistryName(Industria.MODID, name);
	}
	
	public BlockBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.of(material).strength(hardness, resistance).sound(sound).requiresCorrectToolForDrops());
		this.setRegistryName(Industria.MODID, name);
	}
	
	public static ToolType getDefaultToolType(Material material) {
		if (material == Material.STONE || material == Material.METAL) return ToolType.PICKAXE;
		if (material == Material.SAND || material == Material.DIRT) return ToolType.SHOVEL;
		if (material == Material.WOOD) return ToolType.AXE;
		if (material == Material.GRASS || material == Material.LEAVES) return ToolType.HOE;
		return null;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand handIn, BlockRayTraceResult rayTraceResult) {
		return ActionResultType.PASS;
	}
	
}
