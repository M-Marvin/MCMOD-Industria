package de.industria.util.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlueprintFileManager {
	
	public static void saveNewStrukture(ServerWorld world, BlockPos cornerA, BlockPos cornerB, String fileName) {
		
		System.out.println("Save blueprint " + fileName);
		
		File schematicsFolder = world.getServer().getFile("schematics/");
		if (!schematicsFolder.isDirectory()) schematicsFolder.mkdir();
		boolean schemFormat = fileName.endsWith(".schem");
		File schematicFile = new File(schematicsFolder, fileName + (schemFormat ? "" : !fileName.endsWith(".nbt") ? ".nbt" : ""));
		
		if (schematicFile.exists()) System.out.println("Override existing schematic!");
		
		CompoundNBT fileNBT = new CompoundNBT();
		saveAreaToNBT(world, cornerA, cornerB, fileNBT, schemFormat);
		
		try {
			OutputStream outputStream = new FileOutputStream(schematicFile);
			CompressedStreamTools.writeCompressed(fileNBT, outputStream);
			outputStream.close();
		} catch (IOException e) {
			System.err.println("IOException on writing schematic blueprint!");
			e.printStackTrace();
		}
		
	}
	
	public static void saveAreaToNBT(ServerWorld world, BlockPos cornerA, BlockPos cornerB, CompoundNBT nbt, boolean schemFormat) {
		
		List<BlockState> palette = new ArrayList<BlockState>();
		List<TileEntity> blockEnitys = new ArrayList<TileEntity>();
		HashMap<BlockPos, Integer> blockMap = new HashMap<BlockPos, Integer>();
		
		int sizeX = Math.max(cornerA.getX(), cornerB.getX()) - Math.min(cornerA.getX(), cornerB.getX()) + 1;
		int sizeY = Math.max(cornerA.getY(), cornerB.getY()) - Math.min(cornerA.getY(), cornerB.getY()) + 1;
		int sizeZ = Math.max(cornerA.getZ(), cornerB.getZ()) - Math.min(cornerA.getZ(), cornerB.getZ()) + 1;
		BlockPos origin = new BlockPos(Math.min(cornerA.getX(), cornerB.getX()), Math.min(cornerA.getY(), cornerB.getY()), Math.min(cornerA.getZ(), cornerB.getZ()));
		
		for (int y = 0; y < sizeY; y++) {
			for (int x = 0; x < sizeX; x++) {
				for (int z = 0; z < sizeZ; z++) {
					BlockPos position = new BlockPos(x, y, z);
					BlockPos worldPos = origin.offset(position);
					BlockState state = world.getBlockState(worldPos);
					TileEntity blockEntity = world.getBlockEntity(position);
					
					if (blockEntity != null && state.hasTileEntity()) blockEnitys.add(blockEntity);
					if (!palette.contains(state)) palette.add(state);
					int id = palette.indexOf(state);
					blockMap.put(position, id);
				}
			}
		}
		
		if (schemFormat) {
			
			CompoundNBT paletteNbt = new CompoundNBT();
			int index = 0;
			for (BlockState stateEntry : palette) {
				paletteNbt.putInt(ItemStackHelper.getBlockStateString(stateEntry), index++);
			}
			nbt.put("Palette", paletteNbt);
			
			ListNBT blockEntities = new ListNBT();
			for (TileEntity blockEntity : blockEnitys) {
				CompoundNBT tenbt = new CompoundNBT();
				blockEntity.save(tenbt);
				tenbt.remove("x");
				tenbt.remove("y");
				tenbt.remove("z");
				ListNBT pos = new ListNBT();
				BlockPos tePos = blockEntity.getBlockPos().subtract(origin);
				pos.add(IntNBT.valueOf(tePos.getX()));
				pos.add(IntNBT.valueOf(tePos.getY()));
				pos.add(IntNBT.valueOf(tePos.getZ()));
				tenbt.put("Pos", pos);
				blockEntities.add(tenbt);
			}
			nbt.put("BlockEntities", blockEntities);
			
			byte[] blockData = new byte[sizeX * sizeY * sizeZ];
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					for (int x = 0; x < sizeX; x++) {
						BlockPos position = new BlockPos(x, y, z);
						int state = blockMap.get(position);
						blockData[x + z * sizeZ + y * sizeZ * sizeX] = (byte) state;
					}
				}
			}
			nbt.putByteArray("BlockData", blockData);
			
			nbt.putInt("Height", sizeY);
			nbt.putInt("Width", sizeX);
			nbt.putInt("Length", sizeZ);
			
			nbt.putInt("PaletteMax", blockData.length);
			nbt.putInt("Version", 2);
			nbt.putInt("DataVersion", 2586);
			
		} else {
			
			ListNBT paletteNbt = new ListNBT();
			for (BlockState stateEntry : palette) {
				paletteNbt.add(NBTUtil.writeBlockState(stateEntry));
			}
			nbt.put("palette", paletteNbt);
			
			ListNBT blocks = new ListNBT();
			for (Entry<BlockPos, Integer> blockEntry : blockMap.entrySet()) {
				CompoundNBT entry = new CompoundNBT();
				
				ListNBT pos = new ListNBT();
				pos.add(IntNBT.valueOf(blockEntry.getKey().getX()));
				pos.add(IntNBT.valueOf(blockEntry.getKey().getY()));
				pos.add(IntNBT.valueOf(blockEntry.getKey().getZ()));
				entry.put("pos", pos);
				
				entry.putInt("state", blockEntry.getValue());
				
				if (palette.get(blockEntry.getValue()).hasTileEntity()) {
					for (TileEntity entityEntry : blockEnitys) {
						if (entityEntry.getBlockPos().equals(blockEntry.getKey().offset(origin))) {
							CompoundNBT tenbt = new CompoundNBT();
							entityEntry.save(tenbt);
							tenbt.remove("x");
							tenbt.remove("y");
							tenbt.remove("z");
							entry.put("nbt", tenbt);
							break;
						}
					}
				}
				
				blocks.add(entry);
			}
			nbt.put("blocks", blocks);

			ListNBT size = new ListNBT();
			size.add(IntNBT.valueOf(sizeX));
			size.add(IntNBT.valueOf(sizeY));
			size.add(IntNBT.valueOf(sizeZ));
			nbt.put("size", size);

			nbt.put("entities", new ListNBT());
			nbt.putInt("DataVersion", 2586);
			
		}
		
	}
	
	public static String[] getAviableStructures(ServerWorld world) {
		
		File schematicsFolder = world.getServer().getFile("schematics/");
		String[] aviableFiles = schematicsFolder.list((d, s) -> {
			return (s.endsWith(".nbt") || s.endsWith(".schem"));
		});
		return aviableFiles;
		
	}
	
	public static HashMap<String, BlockPos> getBlueprintSize(ServerWorld world, String[] blueprintNames) {
		HashMap<String, BlockPos> map = new HashMap<>();
		for (String name : blueprintNames) {
			map.put(name, getBlueprintSize(world, name));
		}
		return map;
	}
	
	public static BlockPos getBlueprintSize(ServerWorld world, String blueprintName) {
		Schematic schematic = loadStructure(world, blueprintName);
		if (schematic == null) return new BlockPos(0, 0, 0);
		return new BlockPos(schematic.sizeX, schematic.sizeY, schematic.sizeZ);
	}
	
	public static Schematic loadStructure(ServerWorld world, String blueprintName) {
		
		File schematicFolder = world.getServer().getFile("schematics/");
		File blueprintFile = new File(schematicFolder, blueprintName);
		boolean schemFormat = blueprintName.endsWith(".schem");
		
		try {
			InputStream inputStream = new FileInputStream(blueprintFile);
			CompoundNBT fileNBT = CompressedStreamTools.readCompressed(inputStream);
			inputStream.close();
			
			return loadStructureFromNBT(fileNBT, schemFormat);
		} catch (IOException e) {
			System.err.println("IOException on writing schematic blueprint!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Schematic loadStructureFromNBT(CompoundNBT nbt, boolean schemFormat) {
		
		HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>();
		HashMap<BlockPos, CompoundNBT> blockEnitys = new HashMap<BlockPos, CompoundNBT>();
		HashMap<BlockPos, Integer> blockMap = new HashMap<BlockPos, Integer>();
		
		int sizeX = 0;
		int sizeY = 0;
		int sizeZ = 0;
		
		if (schemFormat) {

			sizeY = nbt.getInt("Height");
			sizeX = nbt.getInt("Width");
			sizeZ = nbt.getInt("Length");
			
			CompoundNBT paletteNbt = nbt.getCompound("Palette");
			try {
				for (String stateEntryString : paletteNbt.getAllKeys()) {
					int index = paletteNbt.getInt(stateEntryString);
					BlockStateParser parser = new BlockStateParser(new StringReader(stateEntryString), true);
					parser.parse(false);
					palette.put(index, parser.getState());
				}
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
				return null;
			}
						
			ListNBT blockEntities = nbt.getList("BlockEntities", 10);
			for (int i = 0; i < blockEntities.size(); i++) {
				CompoundNBT tenbt = blockEntities.getCompound(i);
				ListNBT posNbt = tenbt.getList("Pos", 3);
				tenbt.remove("Pos");
				BlockPos tepos = new BlockPos(posNbt.getInt(0), posNbt.getInt(1), posNbt.getInt(2));
				blockEnitys.put(tepos, tenbt);
			}
			
			byte[] blockData = nbt.getByteArray("BlockData");
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					for (int x = 0; x < sizeX; x++) {
						BlockPos position = new BlockPos(x, y, z);
						byte state = blockData[x + z * sizeZ + y * sizeZ * sizeX];
						blockMap.put(position, (int) state);
					}
				}
			}
						
		} else {

			ListNBT size = nbt.getList("size", 3);
			sizeX = size.getInt(0);
			sizeY = size.getInt(1);
			sizeZ = size.getInt(2);
			
			ListNBT paletteNbt = nbt.getList("palette", 10);
			for (int i = 0; i < paletteNbt.size(); i++) {
				CompoundNBT stateNbt = paletteNbt.getCompound(i);
				palette.put(i, NBTUtil.readBlockState(stateNbt));
			}
			
			ListNBT blocks = nbt.getList("blocks", 10);
			for (int i = 0; i < blocks.size(); i++) {
				CompoundNBT entry = blocks.getCompound(i);
				
				ListNBT pos = entry.getList("pos", 3);
				BlockPos position = new BlockPos(pos.getInt(0), pos.getInt(1), pos.getInt(2));
				int state = entry.getInt("state");
				blockMap.put(position, state);
				
				if (palette.get(state).hasTileEntity()) {
					CompoundNBT tenbt = entry.getCompound("nbt");
					blockEnitys.put(position, tenbt);
				}
			}
			
		}
		
		return new Schematic(sizeX, sizeY, sizeZ, palette, blockEnitys, blockMap);
		
	}
	
	public static class Schematic {
		
		public int sizeX;
		public int sizeY;
		public int sizeZ;
		public HashMap<Integer, BlockState> palette;
		public HashMap<BlockPos, CompoundNBT> blockEntityData;
		public HashMap<BlockPos, Integer> blockMap;
		
		public Schematic(int sizeX, int sizeY, int sizeZ, HashMap<Integer, BlockState> palette, HashMap<BlockPos, CompoundNBT> blockEntityData, HashMap<BlockPos, Integer> blockMap) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.sizeZ = sizeZ;
			this.palette = palette;
			this.blockEntityData = blockEntityData;
			this.blockMap = blockMap;
		}
		
	}
	
}
