package de.redtec.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import de.redtec.RedTec;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

public class DriveManager {
	
	public static File getDriveFolder(ServerWorld world) {
		try {
			Path savePath = world.getServer().func_240776_a_(FolderName.GENERATED);
			File hardDriveFolder = new File(savePath.toFile().getCanonicalFile().getAbsolutePath() + "/" + RedTec.MODID + "/drives/");
			if (!hardDriveFolder.exists() || !hardDriveFolder.isDirectory()) {
				hardDriveFolder.mkdirs();
			}
			return hardDriveFolder;
		} catch (IOException e) {
			System.err.println("ERROR on get world folder!");
			e.printStackTrace();
			return null;
		}
	}
	
	public static String createNextDriveId(ServerWorld world) {
		File driveFolder = getDriveFolder(world);
		if (driveFolder != null) {
			int freeId = 0;
			for (String driveName : driveFolder.list()) {
				if (driveName.startsWith("D")) {
					int id = Integer.parseInt(driveName.split("D")[1]);
					if (id == freeId) freeId++;
				}
			}
			String driveName = new String("D" + freeId);
			new File(driveFolder, driveName).mkdir();
			return driveName;
		}
		return null;
	}
	
	public static void saveDataInDrive(String drivePath, String code, ServerWorld world) {
		
		try {
			File driveFolder = getDriveFolder(world);
			File filePath = new File(driveFolder, drivePath);
			if (!filePath.exists()) {
				filePath.createNewFile();
			}
			
			OutputStream outputStream = new FileOutputStream(filePath);
			BufferedWriter streamWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
			streamWriter.write(code);
			streamWriter.close();
			outputStream.close();
			
		} catch (IOException e) {
			System.err.println("ERROR on create drive file!");
			e.printStackTrace();
		}
		
	}
	
	public static String loadDataFromDrive(String drivePath, ServerWorld world) {
		
		try {
			
			File driveFolder = getDriveFolder(world);
			File filePath = new File(driveFolder, drivePath);
			
			if (filePath.exists()) {
				
				InputStream inputStream = new FileInputStream(filePath);
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream));
				String code = "";
				String line = "";
				while ((line = streamReader.readLine()) != null) {
					code = code + line + "\n";
				}
				streamReader.close();
				inputStream.close();
				
				return code;
				
			}
			
		} catch (IOException e) {
			System.err.println("ERROR on load drive file!");
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	public static boolean containsDrive(String path, String... drives) {
		String drive0 = path.substring(0, 2);
		for (String drive : drives) {
			if (drive0.equals(drive)) return true;
		}
		return false;
	}
	
}
