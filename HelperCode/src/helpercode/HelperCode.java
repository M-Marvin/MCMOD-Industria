package helpercode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import minecraftnbt.CompressedStreamTools;
import minecraftnbt.NBTBase;
import minecraftnbt.NBTTagCompound;
import minecraftnbt.NBTTagList;
import minecraftnbt.NBTTagString;

public class HelperCode {
	
	public static void main(String... args) throws URISyntaxException, IOException {
		
		File runFolder = new File(HelperCode.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1)).getParentFile();
		File worldFolderInput = new File(runFolder, "run/Neue Weltdfghfh");
		File worldFolderOutput = new File(runFolder, "run/RedTecWorld");
		
		System.out.println("World folder input: " + worldFolderInput);
		System.out.println("World folder output: " + worldFolderOutput);
		if (worldFolderInput.isDirectory()) {
			
			System.out.println("Start converting ...");
			
			scanAndTransferFolder(worldFolderInput, worldFolderOutput);
			
			System.out.println("Complete!");
			
		}
		
	}
	
	public static void scanAndTransferFolder(File sourceFolder, File targetFolder) throws IOException {
		if (!targetFolder.isDirectory()) {
			System.out.println("Create output folder " + targetFolder + " ... " + (targetFolder.mkdir() ? "OK" : "FAILED"));
		}
		for (String item : sourceFolder.list()) {
			File transferItem = new File(sourceFolder, item);
			File targetItem = new File(targetFolder, item);
			
			if (transferItem.isDirectory()) {
				scanAndTransferFolder(transferItem, targetItem);
			} else if (transferItem.isFile()) {
				scannAndTransferFile(transferItem, targetItem);
			}
		}
	}
	
	public static void scannAndTransferFile(File sourceFile, File targetFile) throws IOException {
		System.out.println("Found file " + sourceFile);
		
		String fileEnding = sourceFile.getName().split("\\.")[1];
		if (
				fileEnding.equalsIgnoreCase("nbt") ||
				fileEnding.equalsIgnoreCase("mca") ||
				fileEnding.equalsIgnoreCase("dat") ||
				fileEnding.equalsIgnoreCase("dat_old")
				) {
			if (tryScannNBT(sourceFile, targetFile)) return;
		}
		if (
				fileEnding.equalsIgnoreCase("json")
				) {
			if (tryScannJson(sourceFile, targetFile)) return;
		}
		if (!transferBinary(sourceFile, targetFile)) {
			System.err.println("Problematic file found!");
		}
		
	}
	
	public static final Gson GSON = new Gson();
	
	public static boolean transferBinary(File sourceFile, File targetFile) {
		try {
			Files.copy(sourceFile.toPath(), targetFile.toPath());
			return true;
		} catch (Exception e) {
			System.err.println("Failed to copy file!");
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean tryScannNBT(File sourceFile, File targetFile) {
		try {
			InputStream input = new FileInputStream(sourceFile);
			NBTTagCompound compound = CompressedStreamTools.readCompressed(input);
			input.close();
			
			NBTTagCompound compound2 = (NBTTagCompound) processNBT(compound);
			
			OutputStream output = new FileOutputStream(targetFile);
			CompressedStreamTools.writeCompressed(compound2, output);
			output.close();
			
			return true;
		} catch (Exception e) {
			System.err.println("Not a NBT file!");
			e.printStackTrace();
			return false;
		}
	}
	
	public static NBTBase processNBT(NBTBase source) {
		if (source instanceof NBTTagString stringEntry) {
			return new NBTTagString(processString(stringEntry.getString()));
		} else if (source instanceof NBTTagList listEntry) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < listEntry.tagCount(); i++) {
				list.appendTag(processNBT(listEntry.get(i)));
			}
			return list;
		} else if (source instanceof NBTTagCompound compoundEntry) {
			NBTTagCompound compound = new NBTTagCompound();
			for (String key : compoundEntry.getKeySet()) {
				compound.setTag(processString(key), processNBT(compoundEntry.getTag(key)));
			}
			return compound;
		} else {
			return source.copy();
		}
	}
	
	public static boolean tryScannJson(File sourceFile, File targetFile) {
		try {
			InputStreamReader input = new InputStreamReader(new FileInputStream(sourceFile));
			JsonObject json = GSON.fromJson(input, JsonObject.class);
			input.close();
			
			String scanString = json.toString();
			scanString = processString(scanString);
			
			OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(targetFile));
			output.write(scanString);
			output.close();
			
			return true;
		} catch (Exception e) {
			System.out.println("Not a JSON file!");
			e.printStackTrace();
			return false;
		}
	}
	
	public static String processString(String string) {
		return string.replace("redtec", "industria");
	}
	
}
