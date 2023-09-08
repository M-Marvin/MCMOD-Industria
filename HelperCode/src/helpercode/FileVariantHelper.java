package helpercode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import de.m_marvin.commandlineparser.CommandLineParser;

public class FileVariantHelper {
	
	public static void main(String[] args) {
		
		CommandLineParser parser = new CommandLineParser();
		parser.addOption("gen_item_list", "", "Generate item list from texture files");
		parser.addOption("ignore_file_endings", "", "List of file endings ignored when generating item name list.");
		
		parser.parseInput(args);
		if (!parser.getOption("gen_item_list").isEmpty()) {
			System.out.println("Run gen item list ..." + parser.getOption("gen_item_list"));
			makeItemList(new File(parser.getOption("gen_item_list")), parser.getOption("ignore_file_endings").split(","));
		}
		
	}
	
	public static void makeItemList(File fileFolder, String[] ignoredEndings) {
		
		File outputFile = new File(fileFolder.getParent(), "/item_list.txt");
		Set<String> itemNames = new HashSet<>();
		File[] files = fileFolder.listFiles();
		
		for (File file : files) {
			String fileName = file.getName().split("\\.")[0];
			
			for (String s : ignoredEndings) {
				if (fileName.endsWith(s)) {
					fileName = fileName.substring(0, s.length());
					break;
				}
			}
			
			itemNames.add(fileName);
		}
		
		System.out.println("Found " + itemNames.size() + " item names in " + files.length + " files");
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			for (String s : itemNames) writer.write(s + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
