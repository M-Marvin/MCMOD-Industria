package de.industria.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;import de.industria.ModItems;
import de.industria.util.handler.ModGameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMTransformatorCoil extends BlockBase {
	
	public BlockMTransformatorCoil() {
		super("transformator_coil", Material.IRON, 2F, SoundType.METAL);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(1, 0, 1, 15, 16, 15);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		try {

			int sizeX = 80;
			int sizeZ = 80;
			
			HashMap<String, List<String>> toCreateMap = new HashMap<String, List<String>>();
			
			String firstRow = null;
			for (int x = 1; x < sizeX; x++) {
				String mapBlockType = null;
				int index = 0;
				for (int z = 0; z < sizeZ; z++) {
					BlockPos pos2 = new BlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
					BlockState state2 = worldIn.getBlockState(pos2);
					
					if (z == 0 && !state2.isAir()) {
						if (state2.getBlock() == Blocks.OAK_SIGN) {
							SignTileEntity sign = (SignTileEntity) worldIn.getTileEntity(pos2);
							String line2 = sign.getText(1).getUnformattedComponentText();
							mapBlockType = sign.getText(0).getUnformattedComponentText() + line2;
						} else {
							mapBlockType = state2.getBlock().getRegistryName().getPath();
						}
						if (firstRow == null) {
							firstRow = mapBlockType;
							System.out.println("Set material row " + firstRow);
						}
						System.out.println("MapType " + mapBlockType);
					} else if (mapBlockType != null && firstRow == mapBlockType && !state2.isAir()) { 
						List<String> list = toCreateMap.getOrDefault(mapBlockType, new ArrayList<String>());
						list.add(state2.getBlock().getRegistryName().getPath());
						toCreateMap.put(mapBlockType, list);
					} else if (state2.getBlock() == Blocks.AIR) {
						
					} else if (state2.getBlock() == Blocks.DIAMOND_BLOCK) {
						
					} else if (state2.getBlock() == Blocks.LAPIS_BLOCK) {
					} else if (state2.getBlock() == Blocks.REDSTONE_BLOCK || state2 .getBlock() == Blocks.GOLD_BLOCK || state2 .getBlock() == Blocks.EMERALD_BLOCK) {
						if (mapBlockType != null && firstRow != null) {
							String materialName = toCreateMap.get(firstRow).get(index).replace("_block", "");
							String typeName = mapBlockType.replace("blackstone", "%").replace("sandstone", "%").replace("cobblestone", "cobbled_%").replace("stone", "%").replace("andesite", "%").replace("quatze", "%");
							
							String newBlockName = typeName.replace("%", materialName).replace("terracotta_brick", "brick").replace("netherrack_brick", "nether_brick").replace("warped_wart_brick", "blue_nether_brick").replace("nether_wart_brick", "red_nether_brick").replace("paintet_planks_wall", "paintet_planks_fence").replace("burned_planks_wall", "burned_planks_fence"); 
							
							List<String> list = toCreateMap.getOrDefault(mapBlockType, new ArrayList<String>());
							list.add(newBlockName);
							toCreateMap.put(mapBlockType, list);
						}
					}
					if (!state2.isAir() && z != 0) {
						index++;
					}
					
				}
			}
			
			toCreateMap.remove(firstRow);
			
			System.out.println("Mapping Complete, map size " + toCreateMap.size());
			
			StringBuilder stringb = new StringBuilder();
			for (String type : toCreateMap.keySet()) {
				for (String name : toCreateMap.get(type)) {
					stringb.append("		ModGameRegistry.registerBlock(ModItems."  + name + ", BUILDING_BLOCKS);\n");
					//generateFiles(name, type);
				}
			}
			System.out.println(stringb.toString());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ActionResultType.SUCCESS;
	}
	
	public static void generateFiles(String blockName, String type) {
		
		if (type.contains("_stairs")) {
			
		} else if (type.contains("_stairs")) {
			String stairsBlockState = "{\r\n" + 
					"  \"variants\": {\r\n" + 
					"    \"facing=east,half=bottom,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=bottom,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\"\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=bottom,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=bottom,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\"\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=bottom,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\"\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=top,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=top,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=top,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=top,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=east,half=top,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=bottom,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=bottom,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=bottom,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=bottom,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=bottom,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=top,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=top,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=top,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=top,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=north,half=top,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=bottom,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\"\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=bottom,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=bottom,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\"\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=bottom,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=bottom,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=top,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=top,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=top,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=top,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=south,half=top,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=bottom,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=bottom,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=bottom,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"y\": 90,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=bottom,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=bottom,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=top,shape=inner_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=top,shape=inner_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_inner\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=top,shape=outer_left\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=top,shape=outer_right\": {\r\n" + 
					"      \"model\": \"industria:block/%_outer\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 270,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    },\r\n" + 
					"    \"facing=west,half=top,shape=straight\": {\r\n" + 
					"      \"model\": \"industria:block/%\",\r\n" + 
					"      \"x\": 180,\r\n" + 
					"      \"y\": 180,\r\n" + 
					"      \"uvlock\": true\r\n" + 
					"    }\r\n" + 
					"  }\r\n" + 
					"}";
			String newBlockState = stairsBlockState.replace("%", blockName);
			
			String stairsModel1 = "{\r\n" + 
					"  \"parent\": \"minecraft:block/stairs\",\r\n" + 
					"  \"textures\": {\r\n" + 
					"    \"bottom\": \"minecraft:block/acacia_planks\",\r\n" + 
					"    \"top\": \"minecraft:block/acacia_planks\",\r\n" + 
					"    \"side\": \"minecraft:block/acacia_planks\"\r\n" + 
					"  }\r\n" + 
					"}";
			
			
		} else if (type.contains("_slab")) {
			
		} else if (type.contains("_wall")) {
			
		} else if (type.contains("_fence")) {
			
		} else {
			
		}
		
	}
	
}
