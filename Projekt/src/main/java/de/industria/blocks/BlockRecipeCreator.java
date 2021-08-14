package de.industria.blocks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.industria.gui.ContainerRecipeCreator;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockRecipeCreator extends BlockBase implements INamedContainerProvider {

	public BlockRecipeCreator() {
		super("recipe_creator", Material.WOOD, 1, 1, SoundType.WOOD);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand handIn, BlockRayTraceResult rayTraceResult) {
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) playerIn, this, pos);
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		RayTraceResult raytraceresult = getPlayerPOVHitResult(player.level, player, RayTraceContext.FluidMode.ANY);
		if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
			return null;
		} else {
			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
				BlockPos pos = new BlockPos(raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);  
				return new ContainerRecipeCreator(id, playerInv, pos);
			}
		}
		return null;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.recipe_creator");
	}
	
	public void createRecipe(World world, BlockPos pos, boolean shapeless, ItemStack[] recipe) {
		
		System.out.println("Create Recipe, Shapeless: " + shapeless);
		
		StringBuilder rb = new StringBuilder();
		
		if (!shapeless) {
			
			rb.append("{\r\n" + 
					"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
					"  \"pattern\": [\r\n");
			
			HashMap<Item, String> ingredientMap = new HashMap<Item, String>();
			
			int sizeX = 1;
			for (int i = 0; i < 3; i++) {
				if (!recipe[i * 3 + 2].isEmpty()) {
					sizeX = 3;
				} else if (!recipe[i * 3 + 1].isEmpty() && sizeX != 3) sizeX = 2;
				
			}
			int sizeY = 1;
			for (int i = 0; i < 3; i++) {
				if (!recipe[i + 2 + 3].isEmpty()) {
					sizeY = 3;
				} else if (!recipe[i + 1 * 3].isEmpty() && sizeY != 3) sizeY = 2;
			}
			
			StringBuilder sb = new StringBuilder();
			String[] keys = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
			for (int i = 0; i < 9; i++) {
				if (!recipe[i].isEmpty()) {
					if (!ingredientMap.containsKey(recipe[i].getItem())) {
						ingredientMap.put(recipe[i].getItem(), keys[i]);
					}
					sb.append(ingredientMap.get(recipe[i].getItem()));
				} else {
					sb.append(" ");
				}
			}
			
			String pattern = sb.toString();
			
			for (int i = 0; i < sizeY; i++) {
				rb.append("    \"");
				for (int x = 0; x < sizeX; x++) {
					rb.append(pattern.charAt(i * 3 + x));
				}
				rb.append("\"" + (i != sizeX - 1 ? "," : "") + "\r\n");
			}
			
			rb.append("  ],\r\n" + 
					"  \"key\": {\r\n");
			
			int size = ingredientMap.entrySet().size();
			int count = 0;
			for (Entry<Item, String> key : ingredientMap.entrySet()) {
				
				boolean last = ++count >= size;
				String jsonk =	"    \"" + key.getValue() + "\": {\r\n" + 
								"      \"item\": \"" + key.getKey().getRegistryName().toString() + "\"\r\n" + 
								"    }" + (last ? "" : ",") + "\r\n";
				rb.append(jsonk);
				
			}
			
			rb.append(
					"  },\r\n" + 
					"  \"result\": {\r\n" + 
					"    \"item\": \"" + recipe[9].getItem().getRegistryName().toString() + "\",\r\n" + 
					"    \"count\": " + recipe[9].getCount() + "\r\n" + 
					"  }\r\n" + 
					"}\r\n" + 
					"");
			
		} else {

			rb.append("{\r\n" + 
					"  \"type\": \"minecraft:crafting_shapeless\",\r\n" + 
					"  \"ingredients\": [\r\n");
			
			List<ItemStack> ingredientMap = new ArrayList<ItemStack>();
			
			
			for (int i = 0; i < 9; i++) {
				if (!recipe[i].isEmpty()) {
					ingredientMap.add(recipe[i]);
				}
			}
			
			int size = ingredientMap.size();
			int count = 0;
			for (ItemStack key : ingredientMap) {
				
				boolean last = ++count >= size;
				String jsonk =	"    {\r\n" + 
								"      \"item\": \"" + key.getItem().getRegistryName() + "\"\r\n" + 
								"    }" + (last ? "" : ",") + "\r\n";
				rb.append(jsonk);
				
			}
			
			rb.append(
					"  ],\r\n" + 
					"  \"result\": {\r\n" + 
					"    \"item\": \"" + recipe[9].getItem().getRegistryName().toString() + "\",\r\n" + 
					"    \"count\": " + recipe[9].getCount() + "\r\n" + 
					"  }\r\n" + 
					"}\r\n" + 
					"");
			
		}
		
		String json = rb.toString();
		
		System.out.println(json);
		
		String name = recipe[9].getItem().getRegistryName().getPath().toString();
		
		System.out.println("Save as " + name);
		
		try {
			Path savePath = world.getServer().getWorldPath(FolderName.DATAPACK_DIR);
			
			File recipeDataPack = new File(savePath.toFile().getCanonicalFile().getAbsolutePath() + "/recipe_creator/data/recipe_creator/recipes");
			if (!recipeDataPack.exists() || !recipeDataPack.isDirectory()) {
				recipeDataPack.mkdirs();
				File dataPackJson = new File(recipeDataPack.getParentFile().getParentFile().getParentFile(), "/pack.mcmeta");
				writeToFile(dataPackJson, "{\r\n" + 
						"  \"pack\": {\r\n" + 
						"    \"pack_format\": 1,\r\n" + 
						"    \"description\": \"Temporäres Datenpacket von Industria zur implementierung Ingame erstellter Rezepte.\"\r\n" + 
						"  }\r\n" + 
						"}");
			}
			File jsonFile = new File(recipeDataPack, "/crafting_rc_" + name + ".json");
			int counter = 2;
			while (jsonFile.exists()) {
				jsonFile = new File(recipeDataPack, "/crafting_rc_" + name + "_" + counter + ".json");
				counter ++;
			}
			
			writeToFile(jsonFile, json);
			
		} catch (IOException e) {
			System.err.println("ERROR on get world folder!");
			e.printStackTrace();
		}
		
	}
	
	protected void writeToFile(File file, String text) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			writer.write(text);
			writer.close();
			outputStream.close();
		} catch (IOException e) {
			System.err.println("Error on writer to temporary Datapack!");
			e.printStackTrace();
		}
	}
	
	protected static BlockRayTraceResult getPlayerPOVHitResult(World p_219968_0_, PlayerEntity p_219968_1_, RayTraceContext.FluidMode p_219968_2_) {
	  float f = p_219968_1_.xRot;
	  float f1 = p_219968_1_.yRot;
	  Vector3d vector3d = p_219968_1_.getEyePosition(1.0F);
	  float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
	  float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
	  float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
	  float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
	  float f6 = f3 * f4;
	  float f7 = f2 * f4;
	  double d0 = p_219968_1_.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();;
	  Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
	  return p_219968_0_.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, p_219968_2_, p_219968_1_));
	}
	
}
